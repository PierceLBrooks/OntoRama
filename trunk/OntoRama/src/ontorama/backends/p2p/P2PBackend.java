package ontorama.backends.p2p;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.Enumeration;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import ontorama.backends.Backend;
import ontorama.backends.Menu;
import ontorama.backends.BackendSearch;
import ontorama.backends.p2p.model.P2PEdge;
import ontorama.backends.p2p.model.P2PGraph;
import ontorama.backends.p2p.model.P2PGraphImpl;
import ontorama.backends.p2p.model.P2PNode;
import ontorama.backends.p2p.p2pmodule.ChangePanel;
import ontorama.backends.p2p.p2pmodule.P2PReciever;
import ontorama.backends.p2p.p2pmodule.P2PSender;
import ontorama.backends.p2p.p2pmodule.PeersPanel;
import ontorama.backends.p2p.p2pprotocol.CommunicationProtocolJxta;
import ontorama.backends.p2p.p2pprotocol.GroupExceptionInit;
import ontorama.backends.p2p.p2pprotocol.GroupExceptionThread;
import ontorama.backends.p2p.p2pprotocol.SearchResultElement;
import ontorama.backends.p2p.gui.P2PJMenu;
import ontorama.backends.p2p.controller.NodeAddedEventHandler;
import ontorama.model.util.GraphModificationException;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.webkbtools.query.parser.rdf.RdfDamlParser;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.util.ParserException;

import javax.swing.*;

import org.tockit.events.EventBroker;

/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class P2PBackend implements Backend{
    private P2PGraph graph = null;
    private P2PSender sender = null;
    //private BackendSearch ontoRama = null;
    public List panels = null;

    private EventBroker _eventBroker;

    private static final String _defaultUserUri = "mailto:dummyUser@p2p.ontorama.org";


//Constructor
    public P2PBackend(){
        this.graph = new P2PGraphImpl();     
        this.panels = new LinkedList();
        this.panels.add(0, new PeersPanel(this));
        this.panels.add(1, new ChangePanel());

        try {
                CommunicationProtocolJxta tmpComm = new CommunicationProtocolJxta(new P2PReciever(this));
                this.sender = new P2PSender(tmpComm, this);
                this.sender.sendPropagate(P2PSender.TAGPROPAGATEINIT, null, "New Peer went online");
        }catch (GroupExceptionThread e) {
        
                                     
        }catch (GroupExceptionInit e) {
              System.err.println("An error accured in P2PBackends constructor");
              e.printStackTrace();   
        }
     
      }

    public void setEventBroker(EventBroker eventBroker) {
        _eventBroker = eventBroker;
        new NodeAddedEventHandler(_eventBroker, this);
        System.out.println("p2p event broker: " + _eventBroker);
    }

   public void searchRequest(String senderPeerID, String query){
        //First search in the model in this P2P module
        P2PGraph resultGraphP2P;
        try {
               List relationLinks = (List) new LinkedList();
               relationLinks.add(new Integer(1));
               relationLinks.add(new Integer(2));
               relationLinks.add(new Integer(4));
               relationLinks.add(new Integer(7));
               Query tmpQuery = new Query(query, relationLinks);
               
               resultGraphP2P = ((P2PGraphImpl) this.getP2PGraph()).search(tmpQuery);
               //TODO should be toRdf instead
               String rdfResultGraph;
               rdfResultGraph = resultGraphP2P.toRDFString();
               sender.sendSearchResponse(senderPeerID, rdfResultGraph);
          
               //TODO should be toRdf instead 
               P2PGraph resultGraphOntoRama = BackendSearch.search(tmpQuery);
               String rdfResultOntoRama = resultGraphOntoRama.toRDFString();
               sender.sendSearchResponse(senderPeerID, rdfResultOntoRama);
        } catch (GroupExceptionThread e) {
              System.err.println("An error accured in SearchRequest");
              e.printStackTrace(); 
        }   
    }
    
    public P2PGraph search(Query query) {
       //Emtpy the previus graph model and set it to what ontoRama returns
       //TODO this should be used when everything is working
       this.setP2PGraph(BackendSearch.search(query));
       ((ChangePanel) this.getPanels().get(1)).empty();
       //Ask the other peers what they got
         try {
              Vector result = this.sender.sendSearch(query.getQueryTypeName());
              Enumeration enum = result.elements();
              while (enum.hasMoreElements()){
                    RdfDamlParser parser = new RdfDamlParser();
                    SearchResultElement resultElement = (SearchResultElement) enum.nextElement();   
                    Reader resultPart = new StringReader(resultElement.getResultText()); 
                    ParserResult parserResult;
					parserResult = parser.getResult(resultPart);
					this.graph.add(parserResult);
			  } 
            
            //Let the responses arrive for a while and then return the new graph
			} catch (GroupExceptionThread e) {
              System.err.println("An error accured in search()");
              e.printStackTrace();   
			} catch (IOException e) {
				System.err.println("An error accured in search()");
            	e.printStackTrace();   
			} catch (ParserException e) {
				System.err.println("An error accured in search()");
            	e.printStackTrace();   
			} catch (GraphModificationException e) {
				System.err.println("An error accured in search()");
            	e.printStackTrace();   
			} catch (NoSuchRelationLinkException e) {
				System.err.println("An error accured in search()");
            	e.printStackTrace();   
			} 
       return this.getP2PGraph();
     }
    


    public void assertEdge(P2PEdge edge, URI asserter) throws GraphModificationException, NoSuchRelationLinkException{
        try {
			this.sender.sendPropagate(P2PSender.TAGPROPAGATEADD, null, 
             	"New relation from: " 
             	+ edge.getFromNode().getIdentifier() 
             	+ " to: " + edge.getToNode().getIdentifier() + " of type: " + edge.getEdgeType());
 
			this.graph.assertEdge(edge, asserter);
        } catch (GroupExceptionThread e) {
               System.err.println("An error accured in assertRelation()");
               e.printStackTrace(); 
		} catch (GraphModificationException e) {
			throw e;
		} catch (NoSuchRelationLinkException e) {
			throw e;
        }
		
      }
    



    public void assertNode(P2PNode node, URI asserter) throws GraphModificationException{
        try {
            String asserterStr = "";
            if (asserter == null) {
                asserterStr = _defaultUserUri;
            }
            else {
                asserterStr = asserter.toString();
            }
             this.sender.sendPropagate(P2PSender.TAGPROPAGATEADD, null,
             		"New node was added: " 
             		+ node.getIdentifier() 
             		+ " by : " + asserterStr);
			this.graph.assertNode(node,asserter);
        } catch (GroupExceptionThread e) {
               System.err.println("An error accured in assertConcept()");
               e.printStackTrace(); 
		} catch (GraphModificationException e) {
			throw e;
		}
      }
    

    public void rejectNode(P2PNode node, URI rejecter) throws GraphModificationException{
        try {
            String rejectorStr = "";
            if (rejecter == null) {
                rejectorStr = _defaultUserUri;
            }
            else {
                rejectorStr = rejecter.toString();
            }
             this.sender.sendPropagate(P2PSender.TAGPROPAGATEADD, null,
             		"New node was rejected: " 
             		+ node.getIdentifier() 
             		+ " by : " + rejectorStr);
			this.graph.rejectNode(node,rejecter);
        } catch (GroupExceptionThread e) {
               System.err.println("An error accured in assertConcept()");
               e.printStackTrace(); 
		} catch (GraphModificationException e) {
			throw e;
		}
      }


    public void rejectEdge(P2PEdge edge, URI rejecter) throws GraphModificationException, NoSuchRelationLinkException{
		try {
			this.sender.sendPropagate(P2PSender.TAGPROPAGATEDELETE, null, 
             		"Rejected relation from " 
             		+ edge.getFromNode().getIdentifier() + " to " 
             		+ edge.getToNode().getIdentifier() 
             		+ " with type: " + edge.getEdgeType());
			this.graph.rejectEdge(edge,rejecter);
        } catch (GroupExceptionThread e) {
               System.err.println("An error accured in rejectRelation()");
               e.printStackTrace(); 
		} catch (GraphModificationException e) {
			throw e;
		} catch (NoSuchRelationLinkException e) {
			throw e;
        }
    }
  
    
    public P2PGraph getP2PGraph(){
        return this.graph;    
    }
    
    public void setP2PGraph(P2PGraph graph){
        this.graph = graph;
    }
    
    public void peerDiscovery(String groupName){
        this.sender.peerDiscovery(groupName);
        
    }
 
    public List getPanels(){
             return this.panels;
    }
    
    public Menu getMenu(){
        return (Menu) new P2PMenu(this.sender, this);
    }

    public JMenu getJMenu() {
        return new P2PJMenu(this, this.sender);
    }

    public String showGraph(){
		return this.graph.toRDFString();
    }

}
