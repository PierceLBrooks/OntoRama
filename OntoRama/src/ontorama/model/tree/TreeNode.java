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
     * get children of this node
     * @return list of outbound edges
     */
    public List getChildren ();

    public void addChild (TreeEdge childEdge);
    public boolean removeChild(TreeEdge childEdge);

 
 	public void setParent(TreeNode parent);
 	public TreeNode getParent();
    
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
    
}
