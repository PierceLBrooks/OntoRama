package ontorama.model.tree;

import java.util.List;

import ontorama.model.graph.NodeType;
import ontorama.model.GeneralNode;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Nov 25, 2002
 * Time: 2:03:36 PM
 * To change this template use Options | File Templates.
 */
public interface TreeNode extends GeneralNode {

	public NodeType getNodeType();

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
     * check if node is a clone to a given node
     * @return true if nodes are clones
     */
    public boolean isClone (TreeNode node);

    /**
     * get children of this node
     * @return list of child nodes
     */
    public List getChildren ();
    public TreeEdge getEdge (TreeNode childNode);

    public void addChild (TreeNode childNode, TreeEdge childEdge);
    public void removeChild(TreeNode childNode, TreeEdge childEdge);


 	public void setParent(TreeNode parent);
 	public TreeNode getParent();

	/**
	 * Returns the distance to the root node.
	 * @return node depth
	 * @todo should be in the hyper model since it is only used there
	 */
	public int getDepth();

	/**
	 * set distance to the root node
	 * @param depth
	 * @todo should be in the hyper model since it is only used there
	 *	 */
	public void setDepth(int depth);

}
