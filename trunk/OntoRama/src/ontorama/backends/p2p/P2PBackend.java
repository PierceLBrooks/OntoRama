package ontorama.backends.p2p;

import java.net.URI;

import org.tockit.events.EventBroker;

import ontorama.backends.Backend;
import ontorama.backends.p2p.model.P2PEdge;
import ontorama.backends.p2p.model.P2PGraph;
import ontorama.backends.p2p.model.P2PNode;
import ontorama.backends.p2p.p2pmodule.P2PSender;
import ontorama.model.graph.GraphModificationException;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.query.Query;

/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface P2PBackend extends Backend {
    public P2PGraph search(Query query);
    public void assertEdge(P2PEdge edge, URI asserter) 
    	throws GraphModificationException, NoSuchRelationLinkException;

    public void assertNode(P2PNode node, URI asserter) 
    	throws GraphModificationException;

    public void rejectEdge(P2PEdge edge, URI rejecter) 
    	throws GraphModificationException, NoSuchRelationLinkException;

    public void rejectNode(P2PNode node, URI rejecter) 
    	throws GraphModificationException;
    
    public EventBroker getEventBroker();
    
    public P2PSender getSender();
    
    /// @todo not sure if this should be here
    public void searchRequest (String senderPeerID, String query);
    
	/// @todo not sure if this should be here
    public P2PGraph getP2PGraph();
    
    
}
