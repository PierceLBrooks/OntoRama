package ontorama.backends.p2p;


import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import javax.swing.JMenu;
import javax.swing.JPanel;

import ontorama.OntoramaConfig;
import ontorama.backends.BackendSearch;
import ontorama.backends.p2p.controller.EdgeAddedEventHandler;
import ontorama.backends.p2p.controller.EdgeRemovedEventHandler;
import ontorama.backends.p2p.controller.GroupIsLeftEventHandler;
import ontorama.backends.p2p.controller.GroupJoinedEventHandler;
import ontorama.backends.p2p.controller.JoinGroupEventHandler;
import ontorama.backends.p2p.controller.LeaveGroupEventHandler;
import ontorama.backends.p2p.controller.NewGroupEventHandler;
import ontorama.backends.p2p.controller.NodeAddedEventHandler;
import ontorama.backends.p2p.controller.NodeRemovedEventHandler;
import ontorama.backends.p2p.events.GroupIsLeftEvent;
import ontorama.backends.p2p.events.GroupJoinedEvent;
import ontorama.backends.p2p.events.JoinGroupEvent;
import ontorama.backends.p2p.events.LeaveGroupEvent;
import ontorama.backends.p2p.events.NewGroupEvent;
import ontorama.backends.p2p.gui.P2PJMenu;
import ontorama.backends.p2p.gui.P2PMainPanel;
import ontorama.backends.p2p.model.*;
import ontorama.backends.p2p.p2pmodule.P2PReciever;
import ontorama.backends.p2p.p2pmodule.P2PSender;
import ontorama.backends.p2p.p2pmodule.XmlMessageCreatorException;
import ontorama.backends.p2p.p2pmodule.XmlMessageProcessor;
import ontorama.backends.p2p.p2pprotocol.CommunicationProtocol;
import ontorama.backends.p2p.p2pprotocol.CommunicationProtocolJxta;
import ontorama.backends.p2p.p2pprotocol.GroupExceptionInit;
import ontorama.backends.p2p.p2pprotocol.GroupExceptionThread;
import ontorama.backends.p2p.p2pprotocol.SearchResultElement;
import ontorama.conf.DataFormatMapping;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Graph;
import ontorama.model.graph.GraphModificationException;
import ontorama.model.graph.Edge;
import ontorama.model.graph.InvalidArgumentException;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeType;
import ontorama.model.graph.events.GraphChangedEvent;
import ontorama.model.graph.events.GraphNodeAddedEvent;
import ontorama.ontotools.*;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.query.QueryEngine;
import ontorama.ontotools.query.QueryResult;
import ontorama.ontotools.writer.ModelWriter;
import ontorama.ontotools.writer.ModelWriterException;
import ontorama.ontotools.writer.rdf.RdfP2PWriter;
import ontorama.ui.HistoryElement;

import org.tockit.events.EventBroker;
import org.tockit.events.LoggingEventListener;

/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class P2PBackendImpl implements P2PBackend {
	
    private P2PGraph graph = null;
    private P2PSender sender = null;
    
    private CommunicationProtocol _communicationProtocol = null;

    private EventBroker _eventBroker;

    /// @todo need to change this to something meaninfull
    private static final String _defaultUserUriString = "mailto:user@p2p.ontorama.org";
    private URI _defaultUserUri;

    private P2PMainPanel mainPanel;

    private static String parserPackage;
    private static String sourcePackage = "ontorama.ontotools.source.StringSource";
    
    private List _dataFormatMapping = new LinkedList();
    
    private String p2pFileExtension;
       
	/**
	 * 
	 */
	public P2PBackendImpl() {
		_dataFormatMapping = OntoramaConfig.getDataFormatsMapping();
		DataFormatMapping mapping = OntoramaConfig.getDataFormatMapping("P2P-RDF");
		parserPackage = mapping.getParserName();
		p2pFileExtension = mapping.getFileExtention();
		
		mainPanel = new P2PMainPanel(this);
		
		System.out.println("p2p backend constructor, p2pbackend = " + this);
			
		activate();
		
		try {
			_defaultUserUri = new URI(_defaultUserUriString);
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
	} 
	

	public void activate() {
		System.out.println("P2PBackend::activate()");
		
		this.emptyLocalCache();
		
		try {
		 	_communicationProtocol = new CommunicationProtocolJxta(new P2PReciever(this));
		    this.sender = new P2PSender(_communicationProtocol, this);
		    this.sender.sendPropagate(P2PSender.TAGPROPAGATEINIT, null, "New Peer went online");
		    
		    this.mainPanel.getGroupsPanel().updateGroups();
		    
		}catch (GroupExceptionThread e) {
		    System.err.println("GroupExceptionThread: " + e.getMessage());
		    e.printStackTrace();
		
		}catch (GroupExceptionInit e) {
			System.err.println("An error accured in P2PBackends constructor");
			e.printStackTrace();
		}
	}
	
	public String getFileExtension() {
		return this.p2pFileExtension;
	}

    private void emptyLocalCache() {
        this.removeFilesInFolder(".jxta/cm");
    }

    private void removeFilesInFolder(String folderPath) {
        File file = new File(folderPath);
        if (file != null) {
            String[] contents = file.list();
            if (contents != null) {
                int size = contents.length;
                for (int i = 0;i<size;i++) {
                    file = new File(folderPath + "/" + contents[i]);
                    if (file.isDirectory()) {
                        this.removeFilesInFolder(file.getAbsolutePath());
                        file.delete();
                    } else {
                        file.delete();
                    }
                }
            }
        }
    }


    public void setEventBroker(EventBroker eventBroker) {
        _eventBroker = eventBroker;

    	/// @todo not sure if we should be creating an empty graph here if we don't have any
    	/// capabilities to display it. we should either not create an empty graph OR
    	/// have some way to display this empty graph so user can add edges and nodes to it.
    	this.graph = new P2PGraphImpl(_eventBroker);


        new NodeAddedEventHandler(_eventBroker, this);
        new EdgeAddedEventHandler(_eventBroker, this);
		new NodeRemovedEventHandler(_eventBroker, this);
		new EdgeRemovedEventHandler(_eventBroker, this);
        
        
    	new LoggingEventListener(
    						_eventBroker,
    						GraphNodeAddedEvent.class,
    						Object.class,
    						System.out);
    	new LoggingEventListener(
    						_eventBroker,
    						GraphChangedEvent.class,
    						Object.class,
    						System.out);
        
        
		_eventBroker.subscribe(new GroupJoinedEventHandler(this.mainPanel.getPeerPanel()), 
											GroupJoinedEvent.class, GroupItemReference.class);        
		_eventBroker.subscribe(new GroupJoinedEventHandler(this.mainPanel.getGroupsPanel()), 
											GroupJoinedEvent.class, GroupItemReference.class);  

    	_eventBroker.subscribe(new GroupIsLeftEventHandler(this.mainPanel.getPeerPanel()),
    										GroupIsLeftEvent.class, GroupItemReference.class);
    	_eventBroker.subscribe(new GroupIsLeftEventHandler(this.mainPanel.getGroupsPanel()),
    										GroupIsLeftEvent.class, GroupItemReference.class);

											
		_eventBroker.subscribe(new JoinGroupEventHandler(this.sender), 
											JoinGroupEvent.class, GroupItemReference.class);
		_eventBroker.subscribe(new NewGroupEventHandler(this.sender),
											NewGroupEvent.class, GroupItemReference.class);																						      
    	_eventBroker.subscribe(new LeaveGroupEventHandler(this.sender),
    										LeaveGroupEvent.class, GroupItemReference.class);
    }

    /**
     * used when we recieve an external search request, should ask the file backend OR get the information
     * from OntoRama soemhow
     * @param senderPeerID
     * @param query
     */
    public void searchRequest(String senderPeerID, String query){
        //First search in the model in this P2P module
        P2PGraph resultGraphP2P;
        try {
            Query tmpQuery = new Query(query);
            tmpQuery.setDepth(2);

            resultGraphP2P = (this.getP2PGraph()).search(tmpQuery);
            String rdfResultGraph;

            ModelWriter modelWriter = new RdfP2PWriter();
            Writer _writer = new StringWriter();
            modelWriter.write(resultGraphP2P, _writer);
            rdfResultGraph = _writer.toString();
            //Sends back the response from the P2PBackend
            sender.sendSearchResponse(senderPeerID, rdfResultGraph);


            P2PGraph resultGraphOntoRama = BackendSearch.searchLocal(tmpQuery, _eventBroker);
            modelWriter = new RdfP2PWriter();
            _writer = new StringWriter();
            modelWriter.write(resultGraphOntoRama, _writer);
            String rdfResultOntoRama = _writer.toString();
            sender.sendSearchResponse(senderPeerID, rdfResultOntoRama);
        } catch (GroupExceptionThread e) {
              System.err.println("An error accured in SearchRequest");
              e.printStackTrace();
        } catch (ModelWriterException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }

    /**
     * This one is called form the SearchPanel and should ONLY search the P2P network
     * @param query
     * @return resulting p2p graph
     */
    public P2PGraph search(Query query) {
        P2PGraph retVal = new P2PGraphImpl(_eventBroker);
       //Emtpy the previus graph model and set it to what ontoRama returns
       //TODO this should be used when everything is working
       mainPanel.getChangePanel().empty();
       //Ask the other peers what they got
         try {
              Collection result = this.sender.sendSearch(query.getQueryTypeName());
              Iterator it = result.iterator();
              while (it.hasNext()){
                    SearchResultElement resultElement = (SearchResultElement) it.next();
                    String sourceText = resultElement.getResultText();
                    QueryResult qr = getQueryResult(sourceText, query);
                    if (qr != null ) {
                        this.graph.add(qr);
                    }
			  }
             retVal.add(this.graph.search(query)) ;
            //Let the responses arrive for a while and then return the new graph
			} catch (GroupExceptionThread e) {
              System.err.println("An error accured in search()");
              e.printStackTrace();
			} catch (IOException e) {
				System.err.println("An error accured in search()");
            	e.printStackTrace();
			} catch (GraphModificationException e) {
				System.err.println("An error accured in search()");
            	e.printStackTrace();
			} catch (NoSuchRelationLinkException e) {
				System.err.println("An error accured in search()");
            	e.printStackTrace();
			}
       return retVal;
     }

    public static QueryResult getQueryResult(String sourceText, Query query) {   	
        try {
            QueryEngine qe = new QueryEngine( sourcePackage, parserPackage, sourceText);
            return qe.getQueryResult(query);
        }
        catch (QueryFailedException e) {
            /// shouldn't get this one in the current context
            e.printStackTrace();
        }
        catch (NoSuchTypeInQueryResult e) {
            /// is not applicable in p2p context, we just want to get some query result with lists of nodes and edges
            e.printStackTrace();
        }
        catch (CancelledQueryException e) {
            /// not applicabel in p2p context - there is no ability to cancel a query
            e.printStackTrace();
        }
        return null;
    }
    

    public void assertEdge(P2PEdge edge, URI asserter) 
    						throws GraphModificationException, NoSuchRelationLinkException{
    	URI asserterUri = asserter;
        try {
			if (asserterUri == null) {
				asserterUri = _defaultUserUri;
			}
			Change edgeChange = new EdgeChange(edge.getFromNode().getIdentifier(), 
										edge.getToNode().getIdentifier(), 
										edge.getEdgeType().getName(), 
										Change.ASSERT, asserterUri.toString());
			String message = XmlMessageProcessor.createMessage(edgeChange);
			this.sender.sendPropagate(P2PSender.TAGPROPAGATEADD, null, message);

			this.graph.assertEdge(edge, asserterUri);
        } catch (GroupExceptionThread e) {
               System.err.println("An error accured in assertRelation()");
               e.printStackTrace();
		} catch (GraphModificationException e) {
			throw e;
		} catch (NoSuchRelationLinkException e) {
			throw e;
        }
		catch (XmlMessageCreatorException e) {
			// @todo not sure what to do with this exception
			e.printStackTrace();
		}
        
        

      }




    public void assertNode(P2PNode node, URI asserter) 
    								throws GraphModificationException{
        URI asserterUri = asserter;
        try {
            if (asserterUri == null) {
                asserterUri = _defaultUserUri;
            }
            System.out.println("\n\nP2PBackend::assertNode sending propagate for node " + node.getName());
             
             NodeType nodeType = node.getNodeType();
             Change nodeChange = new NodeChange(node.getIdentifier(), nodeType.getName(), Change.ASSERT, asserterUri.toString());
             String message = XmlMessageProcessor.createMessage(nodeChange);
			this.sender.sendPropagate(P2PSender.TAGPROPAGATEADD, null, message);
			this.graph.assertNode(node,asserterUri);
        } catch (GroupExceptionThread e) {
               System.err.println("An error accured in assertConcept()");
               e.printStackTrace();
		} catch (GraphModificationException e) {
			throw e;
		}
		catch (XmlMessageCreatorException e) {
			// @todo not sure what to do with this exception
			e.printStackTrace();
		}
      }


    public void rejectNode(P2PNode node, URI rejector) throws GraphModificationException{
        URI rejectorUri = rejector;
        try {
			System.out.println("\n\nP2PBackend::rejectNode sending propagate for node " + node.getName());
            if (rejectorUri == null) {
                rejectorUri = _defaultUserUri;
            }
			NodeType nodeType = node.getNodeType();
			Change nodeChange = new NodeChange(node.getIdentifier(), nodeType.getName(), Change.REJECT, rejectorUri.toString());
			String message = XmlMessageProcessor.createMessage(nodeChange);
			this.sender.sendPropagate(P2PSender.TAGPROPAGATEDELETE, null, message);
             		
			this.graph.rejectNode(node,rejectorUri);
        } catch (GroupExceptionThread e) {
               e.printStackTrace();
		} catch (GraphModificationException e) {
			throw e;
		}
		catch (XmlMessageCreatorException e) {
			e.printStackTrace();
			// @todo not sure what to do with this exception
		}
      }


    public void rejectEdge(P2PEdge edge, URI rejector) throws GraphModificationException, NoSuchRelationLinkException{
		URI rejectorUri = rejector;
		try {
			System.out.println("\n\nP2PBackend::rejectEdge sending propagate for edge " + edge);
			if (rejectorUri == null) {
				rejectorUri = _defaultUserUri;
			}

			Change edgeChange = new EdgeChange(edge.getFromNode().getIdentifier(), 
										edge.getToNode().getIdentifier(), 
										edge.getEdgeType().getName(), 
										Change.ASSERT, rejectorUri.toString());
			String message = XmlMessageProcessor.createMessage(edgeChange);
			this.sender.sendPropagate(P2PSender.TAGPROPAGATEDELETE, null, message);
             		
			this.graph.rejectEdge(edge,rejectorUri);
        } catch (GroupExceptionThread e) {
               System.err.println("An error accured in rejectRelation()");
               e.printStackTrace();
		} catch (GraphModificationException e) {
			throw e;
		} catch (NoSuchRelationLinkException e) {
			throw e;
        }
        catch (XmlMessageCreatorException e) {
        	e.printStackTrace();
        	// @todo not sure what to do with the exception here
        }
    }


    public P2PGraph getP2PGraph(){
        return this.graph;
    }



//	// dont' need this one me thinks since noone calls it
//    public void peerDiscovery(String groupName){
//        this.sender.peerDiscovery(groupName);
//
//    }

    public JPanel getPanel(){
        return (JPanel) this.mainPanel;
    }

    public JMenu getMenu() {
        return new P2PJMenu(this);
    }

    public String showGraph(){
        String retVal = null;
        try {
            ModelWriter modelWriter = new RdfP2PWriter();
            Writer _writer = new StringWriter();
            modelWriter.write(this.graph, _writer);

            retVal = _writer.toString();
        } catch (ModelWriterException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        return retVal;
    }


	/**
	 * @see ontorama.backends.Backend#createNode(java.lang.String, java.lang.String)
	 */
	public Node createNode(String name, String fullName) {
		P2PNode node = new P2PNodeImpl(name, fullName);
		return node;
	}

	/**
	 * @see ontorama.backends.Backend#createEdge(ontorama.model.graph.Node, ontorama.model.graph.Node, ontorama.model.graph.EdgeType)
	 */
	public Edge createEdge(Node fromNode, Node toNode, EdgeType edgeType)
										throws NoSuchRelationLinkException {
		P2PEdge edge = new P2PEdgeImpl((P2PNode) fromNode, (P2PNode) toNode, edgeType);
		return edge;
	}
	/**
	 * @see ontorama.backends.Backend#getDataFormats()
	 */
	public Collection getDataFormats() {
		return _dataFormatMapping;
	}
	
	/**
	 * @see ontorama.backends.Backend#createGraph(ontorama.ontotools.query.QueryResult, org.tockit.events.EventBroker)
	 */
	public Graph createGraph(QueryResult qr, EventBroker eb) throws InvalidArgumentException {
		Graph res = new P2PGraphImpl(qr, eb); 
		System.out.println("\n\n\nCREATE GRAPH\n\n\n");
		return res;
	}


	/**
	 * Returns the sender.
	 * @return P2PSender
	 */
	public P2PSender getSender() {
		return this.sender;
	}

	/**
	 * @see ontorama.backends.Backend#executeQuery(ontorama.ontotools.query.Query)
	 */
	public QueryResult executeQuery(Query query) throws QueryFailedException, CancelledQueryException, NoSuchTypeInQueryResult {
		/// @todo implement!!!
//		P2PQueryEngine queryEngine = new P2PQueryEngine( sourcePackage, parserPackage);
//		QueryResult queryResult = queryEngine.getQueryResult(query);
//		return queryResult;
System.out.println("\n\nP2PBackend::executeQuery");
		return null;
	}
	
	public QueryEngine getQueryEngine() {
		/// @todo implement!!!
		return null;
	}
	
	
	
	public EventBroker getEventBroker() {
		return _eventBroker;
	}

	/**
	 * @see ontorama.backends.Backend#setQueryEngine(ontorama.ontotools.query.QueryEngine)
	 */
	public void setQueryEngine(QueryEngine queryEngine) {
		/// @todo implement!!!
	}

	/**
	 * @see ontorama.backends.Backend#createHistoryElement(ontorama.ontotools.query.Query, org.tockit.events.EventBroker)
	 */
	public HistoryElement createHistoryElement(Query query,EventBroker eventBroker) {
		/// @todo implement!!!!
		return null;
	}

}