package ontorama.model.tree;

import java.util.List;

import ontorama.model.graph.Node;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Nov 25, 2002
 * Time: 2:03:36 PM
 * To change this template use Options | File Templates.
 */
public interface TreeNode {

    public Node getGraphNode ();

    /**
     * get clones list
     * @return list of tree nodes that are clones to this node
     */
    public List getClones ();

    /**
     * add a clone TreeNode
     * @param cloneNode
     */
    public void addClone (TreeNode cloneNode);

    /**
     * set children edges for this node
     * @param childEdges - list of outbound edges
     */
    public void setChildren (List childEdges);

    /**
     * get children of this node
     * @return list of outbound edges
     */
    public List getChildren ();

    public void addChild (TreeEdge childEdge);
    public boolean removeChild(TreeEdge childEdge);

    /**
     * set this node's parent
     * @param parentEdge - inbound edge
     */
    public void setParent (TreeEdge parentEdge);

    /**
     * get node's parent
     * @return inbound edge
     */
    public TreeEdge getParent ();
}
