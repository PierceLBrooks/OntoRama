/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 2, 2002
 * Time: 2:38:29 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.backends.p2p.model;

import ontorama.model.graph.Node;

import java.net.URI;
import java.util.Collection;

public interface P2PNode extends Node {


    /**
     * Add an asserter for this node. If the asserter is registered as a 
     * rejecter as well that is removed
     * 
     * @param userIdUri
     */
     public void addAssertion (URI userIdUri);


    /**
     * Add an rejecter for this node. If the rejecter is registered as a 
     * asserter as well that is removed
     *
     * @param userIdUri
     */
     public void addRejection (URI userIdUri);

    
    /**
     * Returns the assertions.
     */
    public Collection getAssertions ();
	
	
    /**
     * Returns the rejections.
     */
    public Collection getRejectionsList ();
}
