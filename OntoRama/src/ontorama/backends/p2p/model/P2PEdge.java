/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 2, 2002
 * Time: 2:39:01 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.backends.p2p.model;

import java.net.URI;
import java.util.Set;

public interface P2PEdge extends ontorama.model.graph.Edge {
    /**
     * Add an asserter for this edge. If the asserter is registered as a 
     * rejecter as well that is removed
     * 
     * @param userIdUri
     */
     public void addAssertion (URI userIdUri);


   /**
     * Add an rejecter for this edge. If the rejecter is registered as a 
     * asserter as well that is removed
     *
     * @param userIdUri
     */
      public void addRejection (URI userIdUri);

    /**
     * Returns the asserters
     *
     * @return a list of the asserter
     */
     public Set getAssertionsList ();


    /**
     * Returns the rejecter
     *
     * @return a list of the rejecters
     */
     public Set getRejectionsList ();
}
