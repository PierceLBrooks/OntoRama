package ontorama.model.tree;


import ontorama.model.graph.EdgeType;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 27/09/2002
 * Time: 12:31:02
 * To change this template use Options | File Templates.
 */
public interface Tree {
    /**
     * Returns root node of the tree.
     */
    public TreeNode getRootNode();

	public TreeNode addNode (TreeNode newNode, TreeNode parentTreeNode, EdgeType edgeType) throws TreeModificationException;

    public void removeNode(TreeNode nodeToRemove)  throws TreeModificationException ;

}
