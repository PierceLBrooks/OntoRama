package ontorama.model.tree;

import ontorama.model.graph.Node;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Nov 25, 2002
 * Time: 2:03:36 PM
 * To change this template use Options | File Templates.
 */
public interface TreeNode {

    public void setGraphNode (Node graphNode);

    /**
     * set clones for this tree node
     * @param clones - list of tree nodes
     */
    public void setClones (List clones);

    /**
     * get clones list
     * @return list of tree nodes that are clones to this node
     */
    public List getClones ();

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
