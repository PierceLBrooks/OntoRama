package ontorama.backends;

import java.net.URI;

import ontorama.backends.p2p.model.P2PEdge;
import ontorama.backends.p2p.model.P2PGraph;
import ontorama.backends.p2p.model.P2PNode;
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
public interface Peer2PeerBackend extends Backend {
    public P2PGraph search(Query query);
    public void assertEdge(P2PEdge edge, URI asserter) 
    	throws GraphModificationException, NoSuchRelationLinkException;

    public void assertNode(P2PNode node, URI asserter) 
    	throws GraphModificationException;

    public void rejectEdge(P2PEdge edge, URI rejecter) 
    	throws GraphModificationException, NoSuchRelationLinkException;

    public void rejectNode(P2PNode node, URI rejecter) 
    	throws GraphModificationException;



    /**
     * @todo this a hack, couldn't figure out other way to get this panel to show from ontorama app.
     * @param show
     */
    public void showPanels (boolean show);
    
}
