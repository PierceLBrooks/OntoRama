/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 2, 2002
 * Time: 2:45:55 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.backends.p2p.model;

import ontorama.model.Graph;

import java.net.URI;
import java.util.List;

public interface P2PGraph extends Graph {
    public void addNodeAssertion(P2PNode node, URI userIdUri);
    public void rejectNode (P2PNode node, URI userIdUri);

    public void addEdgeAssertion (P2PEdge edge, URI userIdUri);
    public void rejectEdge (P2PEdge edge, URI userIdUri);

    public List getRejectedNodes ();
    public List getRejectedEdges ();
}
