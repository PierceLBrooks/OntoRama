package ontorama.model.graph;

import java.net.URI;

import ontorama.model.GeneralNode;

/**
 * Models a graph node.
 */
public interface Node extends GeneralNode {

    /**
     * get node unique identifier
     */
    public String getIdentifier();
    /**
     * set node unique identifier
     */
    public void setIdentifier(String identifier);

    /**
     * get creatorUri for this node. creatorUri is URI for a creator of this node.
     */
    public URI getCreatorUri ();
    
    public void setCreatorUri(URI creatorUri);

    public void setNodeType (NodeType nodeType);

    public NodeType getNodeType ();
    
    public void setDescription(String description);
    public String getDescription();

}
