/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Sep 16, 2002
 * Time: 11:50:51 AM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model;

import java.net.URI;

public interface Edge {
    public Node getFromNode();

    public Node getToNode();

    public EdgeType getEdgeType();

    public void setCreatorUri (URI cretorUri);
    public URI getCreatorUri ();
}
