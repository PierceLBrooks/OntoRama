package ontorama.backends.p2p;import java.io.File;import java.io.IOException;import java.io.StringWriter;import java.io.Writer;import java.net.URI;import java.util.*;import javax.swing.JMenu;import javax.swing.JPanel;import ontorama.OntoramaConfig;import ontorama.backends.Peer2PeerBackend;import ontorama.backends.BackendSearch;import ontorama.backends.p2p.controller.EdgeAddedEventHandler;import ontorama.backends.p2p.controller.GroupIsLeftEventHandler;import ontorama.backends.p2p.controller.GroupJoinedEventHandler;import ontorama.backends.p2p.controller.JoinGroupEventHandler;import ontorama.backends.p2p.controller.LeaveGroupEventHandler;import ontorama.backends.p2p.controller.NewGroupEventHandler;import ontorama.backends.p2p.controller.NodeAddedEventHandler;import ontorama.backends.p2p.controller.GraphLoadedEventHandler;import ontorama.backends.p2p.events.GroupIsLeftEvent;import ontorama.backends.p2p.events.GroupJoinedEvent;import ontorama.backends.p2p.events.JoinGroupEvent;import ontorama.backends.p2p.events.LeaveGroupEvent;import ontorama.backends.p2p.events.NewGroupEvent;import ontorama.backends.p2p.gui.P2PJMenu;import ontorama.backends.p2p.gui.P2PMainPanel;import ontorama.backends.p2p.model.*;import ontorama.backends.p2p.p2pmodule.P2PReciever;import ontorama.backends.p2p.p2pmodule.P2PSender;import ontorama.backends.p2p.p2pprotocol.CommunicationProtocolJxta;import ontorama.backends.p2p.p2pprotocol.GroupExceptionInit;import ontorama.backends.p2p.p2pprotocol.GroupExceptionThread;import ontorama.backends.p2p.p2pprotocol.GroupReferenceElement;import ontorama.backends.p2p.p2pprotocol.SearchResultElement;import ontorama.conf.DataFormatMapping;import ontorama.model.graph.EdgeType;import ontorama.model.graph.Graph;import ontorama.model.graph.GraphModificationException;import ontorama.model.graph.Edge;import ontorama.model.graph.InvalidArgumentException;import ontorama.model.graph.Node;import ontorama.ontotools.*;import ontorama.ontotools.query.Query;import ontorama.ontotools.query.QueryEngine;import ontorama.ontotools.query.QueryResult;import ontorama.ontotools.writer.ModelWriter;import ontorama.ontotools.writer.ModelWriterException;import ontorama.ontotools.writer.rdf.RdfP2PWriter;import org.tockit.events.EventBroker;/** * @author henrika * * To change this generated comment edit the template variable "typecomment": * Window>Preferences>Java>Templates. * To enable and disable the creation of type comments go to * Window>Preferences>Java>Code Generation. */public class P2PBackend implements Peer2PeerBackend{	    private P2PGraph graph = null;    private P2PSender sender = null;    private EventBroker _eventBroker;    /// @todo need to change this to something meaninfull    private static final String _defaultUserUri = "mailto:user@p2p.ontorama.org";    private P2PMainPanel mainPanel;    /**     * keys - p2p edges,  values - graph edges     */    private Hashtable _graphEdgesMapping;    /**     * keys - p2p nodes, values - graph nodes     */    private Hashtable _graphNodesMapping;    private static String parserPackage;    private static String sourcePackage = "ontorama.ontotools.source.StringSource";        private List _dataFormatMapping = new LinkedList();        private String p2pFileExtension;       	/**	 * 	 */	public P2PBackend() {		_dataFormatMapping = OntoramaConfig.getDataFormatsMapping();		DataFormatMapping mapping = OntoramaConfig.getDataFormatMapping("P2P-RDF");		parserPackage = mapping.getParserName();		p2pFileExtension = mapping.getFileExtention();				/// @todo not sure if we should be creating an empty graph here if we don't have any		/// capabilities to display it. we should either not create an empty graph OR		/// have some way to display this empty graph so user can add edges and nodes to it.		this.graph = new P2PGraphImpl();						mainPanel = new P2PMainPanel(this);				System.out.println("p2p backend constructor, p2pbackend = " + this);					activate();	} 		public void activate() {		System.out.println("P2PBackend::activate()");		this.emptyLocalCache();				try {		 	CommunicationProtocolJxta tmpComm = new CommunicationProtocolJxta(new P2PReciever(this));		    this.sender = new P2PSender(tmpComm, this);		    this.sender.sendPropagate(P2PSender.TAGPROPAGATEINIT, null, "New Peer went online");		    		    this.mainPanel.getGroupsPanel().updateGroups();		}catch (GroupExceptionThread e) {		    System.err.println("GroupExceptionThread: " + e.getMessage());		    e.printStackTrace();				}catch (GroupExceptionInit e) {			System.err.println("An error accured in P2PBackends constructor");			e.printStackTrace();		}	}		public String getFileExtension() {		return this.p2pFileExtension;	}    private void emptyLocalCache() {        this.removeFilesInFolder(".jxta/cm");    }    private void removeFilesInFolder(String folderPath) {        File file = new File(folderPath);        if (file != null) {            String[] contents = file.list();            if (contents != null) {                int size = contents.length;                for (int i = 0;i<size;i++) {                    file = new File(folderPath + "/" + contents[i]);                    if (file.isDirectory()) {                        this.removeFilesInFolder(file.getAbsolutePath());                        file.delete();                    } else {                        file.delete();                    }                }            }        }    }    public void setEventBroker(EventBroker eventBroker) {        _eventBroker = eventBroker;        new NodeAddedEventHandler(_eventBroker, this);        new EdgeAddedEventHandler(_eventBroker, this);        new GraphLoadedEventHandler(_eventBroker, this);        		_eventBroker.subscribe(new GroupJoinedEventHandler(this.mainPanel.getPeerPanel()), 											GroupJoinedEvent.class, GroupReferenceElement.class);        		_eventBroker.subscribe(new GroupJoinedEventHandler(this.mainPanel.getGroupsPanel()), 											GroupJoinedEvent.class, GroupReferenceElement.class);      	_eventBroker.subscribe(new GroupIsLeftEventHandler(this.mainPanel.getPeerPanel()),    										GroupIsLeftEvent.class, GroupReferenceElement.class);    	_eventBroker.subscribe(new GroupIsLeftEventHandler(this.mainPanel.getGroupsPanel()),    										GroupIsLeftEvent.class, GroupReferenceElement.class);													_eventBroker.subscribe(new JoinGroupEventHandler(this.sender), 											JoinGroupEvent.class, GroupReferenceElement.class);		_eventBroker.subscribe(new NewGroupEventHandler(this.sender),											NewGroupEvent.class, GroupReferenceElement.class);																						          	_eventBroker.subscribe(new LeaveGroupEventHandler(this.sender),    										LeaveGroupEvent.class, GroupReferenceElement.class);    }    /**     * used when we recieve an external search request, should ask the file backend OR get the information     * from OntoRama soemhow     * @param senderPeerID     * @param query     */    public void searchRequest(String senderPeerID, String query){        //First search in the model in this P2P module        P2PGraph resultGraphP2P;        try {            Query tmpQuery = new Query(query);            tmpQuery.setDepth(2);            resultGraphP2P = (this.getP2PGraph()).search(tmpQuery);            String rdfResultGraph;            ModelWriter modelWriter = new RdfP2PWriter();            Writer _writer = new StringWriter();            modelWriter.write(resultGraphP2P, _writer);            rdfResultGraph = _writer.toString();            //Sends back the response from the P2PBackend            sender.sendSearchResponse(senderPeerID, rdfResultGraph);            P2PGraph resultGraphOntoRama = BackendSearch.searchLocal(tmpQuery);            modelWriter = new RdfP2PWriter();            _writer = new StringWriter();            modelWriter.write(resultGraphOntoRama, _writer);            String rdfResultOntoRama = _writer.toString();            sender.sendSearchResponse(senderPeerID, rdfResultOntoRama);        } catch (GroupExceptionThread e) {              System.err.println("An error accured in SearchRequest");              e.printStackTrace();        } catch (ModelWriterException e) {            e.printStackTrace();  //To change body of catch statement use Options | File Templates.        }    }    /**     * This one is called form the SearchPanel and should ONLY search the P2P network     * @param query     * @return resulting p2p graph     */    public P2PGraph search(Query query) {        P2PGraph retVal = new P2PGraphImpl();       //Emtpy the previus graph model and set it to what ontoRama returns       //TODO this should be used when everything is working//       ((ChangePanel) this.getPanel().get(1)).empty();       mainPanel.getChangePanel().empty();       //Ask the other peers what they got         try {              Vector result = this.sender.sendSearch(query.getQueryTypeName());              Enumeration enum = result.elements();              while (enum.hasMoreElements()){                    SearchResultElement resultElement = (SearchResultElement) enum.nextElement();                    String sourceText = resultElement.getResultText();                    QueryResult qr = getQueryResult(sourceText, query);                    if (qr != null ) {                        this.graph.add(qr);                    }			  }             retVal.add(this.graph.search(query)) ;            //Let the responses arrive for a while and then return the new graph			} catch (GroupExceptionThread e) {              System.err.println("An error accured in search()");              e.printStackTrace();			} catch (IOException e) {				System.err.println("An error accured in search()");            	e.printStackTrace();			} catch (GraphModificationException e) {				System.err.println("An error accured in search()");            	e.printStackTrace();			} catch (NoSuchRelationLinkException e) {				System.err.println("An error accured in search()");            	e.printStackTrace();			}       return retVal;     }    public static QueryResult getQueryResult(String sourceText, Query query) {   	        try {            QueryEngine qe = new QueryEngine( sourcePackage, parserPackage, sourceText);            return qe.getQueryResult(query);        }        catch (QueryFailedException e) {            /// shouldn't get this one in the current context            e.printStackTrace();        }        catch (NoSuchTypeInQueryResult e) {            /// is not applicable in p2p context, we just want to get some query result with lists of nodes and edges            e.printStackTrace();        }        catch (CancelledQueryException e) {            /// not applicabel in p2p context - there is no ability to cancel a query            e.printStackTrace();        }        return null;    }        public void assertEdge(P2PEdge edge, URI asserter) throws GraphModificationException, NoSuchRelationLinkException{        try {			this.sender.sendPropagate(P2PSender.TAGPROPAGATEADD, null,             	"New relation from: "             	+ edge.getFromNode().getIdentifier()             	+ " to: " + edge.getToNode().getIdentifier() + " of type: " + edge.getEdgeType());			this.graph.assertEdge(edge, asserter);        } catch (GroupExceptionThread e) {               System.err.println("An error accured in assertRelation()");               e.printStackTrace();		} catch (GraphModificationException e) {			throw e;		} catch (NoSuchRelationLinkException e) {			throw e;        }      }    public void assertNode(P2PNode node, URI asserter) throws GraphModificationException{        try {            String asserterStr = "";            if (asserter == null) {                asserterStr = _defaultUserUri;            }            else {                asserterStr = asserter.toString();            }             this.sender.sendPropagate(P2PSender.TAGPROPAGATEADD, null,             		"New node was added: "             		+ node.getIdentifier()             		+ " by : " + asserterStr);			this.graph.assertNode(node,asserter);        } catch (GroupExceptionThread e) {               System.err.println("An error accured in assertConcept()");               e.printStackTrace();		} catch (GraphModificationException e) {			throw e;		}      }    public void rejectNode(P2PNode node, URI rejecter) throws GraphModificationException{        try {            String rejectorStr = "";            if (rejecter == null) {                rejectorStr = _defaultUserUri;            }            else {                rejectorStr = rejecter.toString();            }             this.sender.sendPropagate(P2PSender.TAGPROPAGATEADD, null,             		"New node was rejected: "             		+ node.getIdentifier()             		+ " by : " + rejectorStr);			this.graph.rejectNode(node,rejecter);        } catch (GroupExceptionThread e) {               System.err.println("An error accured in assertConcept()");               e.printStackTrace();		} catch (GraphModificationException e) {			throw e;		}      }    public void rejectEdge(P2PEdge edge, URI rejecter) throws GraphModificationException, NoSuchRelationLinkException{		try {			this.sender.sendPropagate(P2PSender.TAGPROPAGATEDELETE, null,             		"Rejected relation from "             		+ edge.getFromNode().getIdentifier() + " to "             		+ edge.getToNode().getIdentifier()             		+ " with type: " + edge.getEdgeType());			this.graph.rejectEdge(edge,rejecter);        } catch (GroupExceptionThread e) {               System.err.println("An error accured in rejectRelation()");               e.printStackTrace();		} catch (GraphModificationException e) {			throw e;		} catch (NoSuchRelationLinkException e) {			throw e;        }    }    public P2PGraph getP2PGraph(){        return this.graph;    }    public void setP2PGraph(P2PGraph graph){        this.graph = graph;    }    public void peerDiscovery(String groupName){        this.sender.peerDiscovery(groupName);    }    public JPanel getPanel(){        return (JPanel) this.mainPanel;    }    public JMenu getMenu() {        return new P2PJMenu(this);    }    public String showGraph(){        String retVal = null;        try {            ModelWriter modelWriter = new RdfP2PWriter();            Writer _writer = new StringWriter();            modelWriter.write(this.graph, _writer);            retVal = _writer.toString();        } catch (ModelWriterException e) {            e.printStackTrace();  //To change body of catch statement use Options | File Templates.        }        return retVal;    }//    public void showPanels(boolean show) {//        this.mainPanel.showP2PPanel(show);//    }//    public void buildP2PGraph (Graph graph) {//        this.graph = new P2PGraphImpl();//        _graphNodesMapping = new Hashtable();//        _graphEdgesMapping = new Hashtable();//        Iterator graphEdges = graph.getEdgesList().iterator();//        while (graphEdges.hasNext()) {//            Edge curEdge = (Edge) graphEdges.next();//            Node fromNode = curEdge.getFromNode();//            Node toNode = curEdge.getToNode();//            try {//                makeP2PNode(fromNode);//                makeP2PNode(toNode);//                makeP2PEdge(curEdge);//            }//            catch (NoSuchRelationLinkException e) {//                /// ignore here because we would have caught it while building graph//            }//            catch (GraphModificationException e) {//                /// @todo what to do with this exception?//                e.printStackTrace();//            }//        }//    }    private void makeP2PNode (Node node) throws GraphModificationException {        Enumeration e = _graphNodesMapping.keys();        while (e.hasMoreElements()) {            P2PNode p2pNode = (P2PNode) e.nextElement();            Node graphNode = (Node) _graphNodesMapping.get(p2pNode);            if (graphNode.equals(node)) {                return;            }        }        P2PNode p2pNode = (P2PNode) createNode(node.getName(), node.getIdentifier());        _graphNodesMapping.put(p2pNode, node);        this.graph.assertNode(p2pNode, node.getCreatorUri());    }    private void makeP2PEdge (Edge edge) throws NoSuchRelationLinkException, GraphModificationException {        Enumeration e = _graphEdgesMapping.keys();        while (e.hasMoreElements()) {            P2PEdge p2pEdge = (P2PEdge) e.nextElement();            Edge graphEdge = (Edge) _graphEdgesMapping.get(p2pEdge);            if (graphEdge.equals(edge)) {                return;            }        }        P2PEdge p2pEdge = (P2PEdge) createEdge(edge.getFromNode(), edge.getToNode(), edge.getEdgeType());        _graphEdgesMapping.put(p2pEdge, edge);        this.graph.assertEdge(p2pEdge, edge.getCreatorUri());    }	/**	 * @see ontorama.backends.Backend#createNode(java.lang.String, java.lang.String)	 */	public Node createNode(String name, String fullName) {		P2PNode node = new P2PNodeImpl(name, fullName);		return node;	}	/**	 * @see ontorama.backends.Backend#createEdge(ontorama.model.graph.Node, ontorama.model.graph.Node, ontorama.model.graph.EdgeType)	 */	public Edge createEdge(Node fromNode, Node toNode, EdgeType edgeType)										throws NoSuchRelationLinkException {		P2PEdge edge = new P2PEdgeImpl((P2PNode) fromNode, (P2PNode) toNode, edgeType);		return edge;	}	/**	 * @see ontorama.backends.Backend#getDataFormats()	 */	public Collection getDataFormats() {		return _dataFormatMapping;	}		/**	 * @see ontorama.backends.Backend#createGraph(ontorama.ontotools.query.QueryResult, org.tockit.events.EventBroker)	 */	public Graph createGraph(QueryResult qr, EventBroker eb) throws InvalidArgumentException {		Graph res = new P2PGraphImpl(qr); 			return res;	}	/**	 * Returns the sender.	 * @return P2PSender	 */	public P2PSender getSender() {		return this.sender;	}	/**	 * @see ontorama.backends.Backend#executeQuery(ontorama.ontotools.query.Query)	 */	public QueryResult executeQuery(Query query) throws QueryFailedException, CancelledQueryException, NoSuchTypeInQueryResult {		/// @todo implement!!!//		P2PQueryEngine queryEngine = new P2PQueryEngine( sourcePackage, parserPackage);//		QueryResult queryResult = queryEngine.getQueryResult(query);//		return queryResult;System.out.println("\n\nP2PBackend::executeQuery");		return null;	}		public QueryEngine getQueryEngine() {		/// @todo implement!!!		return null;	}				public EventBroker getEventBroker() {		return _eventBroker;	}	/**	 * @see ontorama.backends.Backend#setQueryEngine(ontorama.ontotools.query.QueryEngine)	 */	public void setQueryEngine(QueryEngine queryEngine) {		/// @todo implement!!!	}}