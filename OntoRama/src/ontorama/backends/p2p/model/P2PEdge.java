/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 2, 2002
 * Time: 2:39:01 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.backends.p2p.model;

import ontorama.model.Edge;

import java.net.URI;
import java.util.List;

public interface P2PEdge extends Edge {
    public void addAssertion (URI userIdUri);
    public void addRejection (URI userIdUri);
    public List getAssertionsList ();
    public List getRejectionsList ();
}
