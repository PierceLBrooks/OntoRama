/*
 * Models a graph node.
 * User: nataliya
 * Date: Sep 16, 2002
 * Time: 12:08:52 PM
 */
package ontorama.model;

import java.util.List;
import java.net.URI;

public interface Node {
    /**
     * Return GraphNodes name/title.
     * @return name
     */
    public String getName();

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
     * Return true if Node has clones.
     * @return
     */
    public boolean hasClones();

    /**
     * Return a list of all clones.
     * @return clones list
     */
    public List getClones();

    /**
     * Adds a new clone o this Node.
     * @param  clone - Node that is clone for this Node
     */
    public void addClone (Node clone);

    /**
     * Make a clone for this Node (make a new Node with the same
     * name and add new node (clone) to appropriate lists of clones)
     * @return cloneNode
     */
    public Node makeClone();

    /**
     * add list of new clones to this node's clones list
     * @param clones
     */
    public void addClones (List clones);

    /**
     * Returns the distance to the root node.
     * @return node depth
     */
    public int getDepth();

    /**
     * set distance to the root node
     * @param depth
     */
    public void setDepth(int depth);

    /**
     * returns true if this node is folded.
     * @return isFolded
     * @todo perhaps this shouldn't be in the model
     */
    public boolean getFoldedState();
    /**
     * set node's folded state
     * @param isFolded
     * @todo perhaps this shouldn't be in the model
     */
    public void setFoldState(boolean isFolded);

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

}
