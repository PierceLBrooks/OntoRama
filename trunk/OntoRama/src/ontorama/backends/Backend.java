package ontorama.backends;

import java.net.URI;
import java.util.List;

import ontorama.backends.p2p.model.P2PEdge;
import ontorama.backends.p2p.model.P2PGraph;
import ontorama.backends.p2p.model.P2PNode;
import ontorama.model.util.GraphModificationException;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface Backend{
    public P2PGraph search(Query query);
    public void assertEdge(P2PEdge edge, URI asserter) 
    	throws GraphModificationException, NoSuchRelationLinkException; 

    public void assertNode(P2PNode node, URI asserter) 
    	throws GraphModificationException; 

    public void rejectEdge(P2PEdge edge, URI rejecter) 
    	throws GraphModificationException, NoSuchRelationLinkException;

    public void rejectNode(P2PNode node, URI rejecter) 
    	throws GraphModificationException;

    public List getPanels();
    public Menu getMenu();
 }