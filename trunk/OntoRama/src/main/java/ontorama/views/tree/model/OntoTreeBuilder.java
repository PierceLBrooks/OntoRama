package ontorama.views.tree.model;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import ontorama.model.tree.Tree;
import ontorama.model.tree.TreeEdge;
import ontorama.model.tree.TreeNode;

/**
 * Description: Build Tree of OntoTreeNodes from given graph
 * with given GraphNodes. This object is point of reference
 * between GraphNodes and OntoTreeNodes.
 * This is used by OntoTreeModel to build the TreeModel and
 * later on is used by OntoTreeNode for reference for getting
 * a TreeNode from given Node.
 *
 * Copyright:    Copyright (c) 2001
 * Company:     DSTC
 */

public class OntoTreeBuilder {

	/**
	 * mapping between the models
	 * keys - ontorama.model.Tree TreeNodes
	 * values - OntoTreeNodes
	 */
    private static Hashtable<TreeNode, OntoTreeNode> _ontoHash = new Hashtable<TreeNode, OntoTreeNode>();
    private Tree _tree;

    /**
     * Constructor
     * @param	graph
     */
    public OntoTreeBuilder(Tree tree) {
    	_tree = tree;

        processNode(_tree.getRootNode());
    }

    /**
     *
     */
    private void processNode (ontorama.model.tree.TreeNode topTreeNode) {
    	
    	List<TreeNode> childrenEdges = topTreeNode.getChildren();
    	Iterator<TreeNode> children = childrenEdges.iterator();

		if (!children.hasNext()) {
			OntoTreeNode ontoTreeNode = new OntoTreeNode (topTreeNode);
			_ontoHash.put(topTreeNode, ontoTreeNode);
		}    	
    	while (children.hasNext()) {
    		ontorama.model.tree.TreeNode curNode = children.next();
    		TreeEdge edge = topTreeNode.getEdge(curNode);
    		createOntoTreeNode (topTreeNode, edge);
    	}
    }

    /**
     * Convert each Node to OntoTreeNode
     */
    private void createOntoTreeNode(TreeNode top, TreeEdge edge) {
        OntoTreeNode ontoTreeNode = new OntoTreeNode(top);
        ontoTreeNode.setRelLink(edge.getEdgeType());
        _ontoHash.put(top, ontoTreeNode);

		Iterator<TreeNode> children = top.getChildren().iterator();
        while (children.hasNext()) {
        	TreeNode toModelTreeNode = children.next();
        	
            TreeEdge curEdge = top.getEdge(toModelTreeNode);

            createOntoTreeNode(toModelTreeNode, curEdge);
        }
    }

    /**
     * Get TreeNode associated with given Node
     * @param	graphNode
     * @return	treeNode
     */
    public static javax.swing.tree.TreeNode getTreeNode(TreeNode treeModelNode) {
		javax.swing.tree.TreeNode treeNode = _ontoHash.get(treeModelNode);
        return treeNode;
    }

}
