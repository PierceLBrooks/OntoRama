package ontorama.backends.p2p;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import ontorama.backends.Backend;
import ontorama.backends.ExtendedGraph;
import ontorama.backends.GraphNode;
import ontorama.backends.Menu;
import ontorama.backends.NoSuchGraphNodeException;
import ontorama.backends.OntoRamaBackend;
import ontorama.backends.p2p.p2pmodule.ChangePanel;
import ontorama.backends.p2p.p2pmodule.P2PReciever;
import ontorama.backends.p2p.p2pmodule.P2PSender;
import ontorama.backends.p2p.p2pmodule.PeersPanel;
import ontorama.backends.p2p.p2pprotocol.CommunicationProtocolJxta;
import ontorama.backends.p2p.p2pprotocol.GroupExceptionInit;
import ontorama.backends.p2p.p2pprotocol.GroupExceptionThread;
import ontorama.backends.p2p.p2pprotocol.SearchResultElement;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.webkbtools.query.parser.rdf.RdfDamlParser;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.util.ParserException;

/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class P2PBackend implements Backend{
    private ExtendedGraph graph = null;
    private P2PSender sender = null;
    private OntoRamaBackend ontoRama = null;
    public List panels = null;


//Constructor 
    public P2PBackend(OntoRamaBackend ontoRama){
        this.graph = new ExtendedGraph();     
        this.ontoRama = ontoRama;
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
    
    public void searchRequest(String senderPeerID, String query){
        //First search in the model in this P2P module
        ExtendedGraph resultGraphP2P;
        try {
               List relationLinks = (List) new LinkedList();
               relationLinks.add(new Integer(1));
               relationLinks.add(new Integer(2));
               relationLinks.add(new Integer(4));
               relationLinks.add(new Integer(7));
               Query tmpQuery = new Query(query, relationLinks);
               
               resultGraphP2P = this.getExtendedGraph().search(tmpQuery);
               //TODO should be toRdf instead
               String rdfResultGraph;
               rdfResultGraph = resultGraphP2P.toRDFString();
               sender.sendSearchResponse(senderPeerID, rdfResultGraph);
          
               //TODO should be toRdf instead 
               ExtendedGraph resultGraphOntoRama = this.ontoRama.search(tmpQuery);
               String rdfResultOntoRama = resultGraphOntoRama.toRDFString();
               sender.sendSearchResponse(senderPeerID, rdfResultOntoRama);
        } catch (GroupExceptionThread e) {
              System.err.println("An error accured in SearchRequest");
              e.printStackTrace(); 
        }   
        
        
    }
    
    public ExtendedGraph search(Query query){
       //Emtpy the previus graph model and set it to what ontoRama returns
       //TODO this should be used when everything is working
       this.setExtendedGraph(this.ontoRama.search(query));
       ((ChangePanel) this.getPanels().get(1)).empty();
       //Ask the other peers what they got
         try {
              Vector result = this.sender.sendSearch(query.getQueryTypeName());
              Enumeration enum = result.elements();
              while (enum.hasMoreElements()){
                    RdfDamlParser parser = new RdfDamlParser();
                    SearchResultElement resultElement = (SearchResultElement) enum.nextElement();   
                    Reader resultPart = new StringReader(resultElement.getResultText()); 
                    ParserResult parserResult = parser.getResult(resultPart);
                    this.graph.add(parserResult.getNodesList(),parserResult.getEdgesList());
			  } 
            
            //Let the responses arrive for a while and then return the new graph
          	  this.graph.setRoot(query.getQueryTypeName());  
			} catch (NoSuchGraphNodeException e) {
				System.err.println("Could not find the root node");
				e.printStackTrace();
			} catch (ParserException e) {
              System.err.println("An error accured in search()");
              e.printStackTrace();
			} catch (GroupExceptionThread e) {
              System.err.println("An error accured in search()");
              e.printStackTrace();   
			} catch (IOException e) {
				System.err.println("An error accured in search()");
            	e.printStackTrace();   
			} 
       return this.getExtendedGraph();
     }
    


    public void assertRelation(GraphNode fromNode, GraphNode toNode, int edgeType,String nameSpaceForRelation){
        try {
             this.sender.sendPropagate(P2PSender.TAGPROPAGATEADD, null, "New relation from: " +
             fromNode.getFullName() + " to: " + toNode.getFullName() + " of type: " + edgeType);
 
        } catch (GroupExceptionThread e) {
               System.err.println("An error accured in assertRelation()");
               e.printStackTrace(); 
        }
        this.graph.addEdge(fromNode.getFullName(), toNode.getFullName(), edgeType,nameSpaceForRelation);
      }
    



    public void assertConcept(GraphNode fromNode, GraphNode node, int edgeType,String nameSpaceForRelation){
        try {
             this.sender.sendPropagate(P2PSender.TAGPROPAGATEADD, null, "New concept: " + node.getFullName() +
              " was added to: " + fromNode.getFullName() + " of type: " + edgeType);
 
        } catch (GroupExceptionThread e) {
               System.err.println("An error accured in assertConcept()");
               e.printStackTrace(); 
        }

        this.graph.addNode(node);
        //System.out.println("Add edge from " + fromUri + " to: " + node.getFullName());
        this.graph.addEdge(fromNode.getFullName(), node.getFullName(), edgeType,nameSpaceForRelation);    
      }
    


    public void rejectRelation(GraphNode fromNode, GraphNode toNode, int edgeType,String nameSpaceForRelation){
           try {
             this.sender.sendPropagate(P2PSender.TAGPROPAGATEDELETE, null, "Rejected relation from " +
             fromNode.getFullName() + " to " + toNode.getFullName() + " with type: " + edgeType);
 
        } catch (GroupExceptionThread e) {
               System.err.println("An error accured in rejectRelation()");
               e.printStackTrace(); 
        }
        this.graph.rejectEdge(fromNode.getFullName(), toNode.getFullName(), edgeType,nameSpaceForRelation);
    }
  
  
  
  
    public void updateConcept(GraphNode node){
        try {
             this.sender.sendPropagate(P2PSender.TAGPROPAGATEUPDATE, null, "Updated node: " 
             + node.getFullName());
 
        } catch (GroupExceptionThread e) {
               System.err.println("An error accured in updateConcept()");
               e.printStackTrace(); 
        }
 
         this.graph.updateNode(node);
    
    }
 
 
 
    
    public ExtendedGraph getExtendedGraph(){
        return this.graph;    
    }
    
    public void setExtendedGraph(ExtendedGraph graph){
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

    public String showGraph(){
		return this.graph.toRDFString();
    }

   	public List searchRejectedEdges() {
		return this.graph.getRejectedEdges();	
	}

}
