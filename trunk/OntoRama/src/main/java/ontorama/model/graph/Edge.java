/*
 * Models a relationship between two nodes. Each edge has a start node and an end node.
 * Edges can be of different types.
 * User: nataliya
 * Date: Sep 16, 2002
 * Time: 11:50:51 AM
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model.graph;

import java.net.URI;

public interface Edge {
    /**
     * get a 'start' node for this edge.
     * @return fromNode
     */
    public Node getFromNode();
    
    /**
     * set a 'start' node for this edge
     * @param node
     */
    public void setFromNode(Node node);

    /**
     * get an 'end' node for this edge.
     * @return toNode
     */
    public Node getToNode();
    
    /**
     * set an 'end' node for this edge
     */
    public void setToNode(Node node);

    /**
     * get edge type.
     * @return edgeType
     */
    public EdgeType getEdgeType();

    /**
     * set URI of creator of this edge.
     * @param creatorUri
     */
    public void setCreatorUri (URI creatorUri);
    /**
     * get URI of creator of this edge.
     * @return
     */
    public URI getCreatorUri ();
}
