/*
 * Models a graph node.
 * User: nataliya
 * Date: Sep 16, 2002
 * Time: 12:08:52 PM
 */
package ontorama.model.graph;

import ontorama.model.GeneralNode;

import java.net.URI;

public interface Node extends GeneralNode {

    /**
     * set node name
     * @param name
     */
    public void setName(String name);

    /**
     * get node unique identifier
     * @return
     */
    public String getIdentifier();
    /**
     * set node unique identifier
     * @param identifier
     */
    public void setIdentifier(String identifier);

    /**
     * get creatorUri for this node. creatorUri is URI for a creator of this node.
     * @return creatorUri
     */
    public URI getCreatorUri ();
    
    /**
     * set creatorUri for this node.
     * @param creatorUri
     */
    public void setCreatorUri(URI creatorUri);

    public void setNodeType (NodeType nodeType);

    public NodeType getNodeType ();
    
    public void setDescription(String description);
    public String getDescription();

}
