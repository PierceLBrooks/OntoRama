package ontorama.views.tree.model;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.TreeNode;

import ontorama.model.graph.EdgeType;
import ontorama.model.tree.Tree;
import ontorama.model.tree.TreeEdge;

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
    private static Hashtable _ontoHash = new Hashtable();
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
    	
    	List childrenEdges = topTreeNode.getChildren();
    	Iterator children = childrenEdges.iterator();

		if (!children.hasNext()) {
			OntoTreeNode ontoTreeNode = new OntoTreeNode (topTreeNode);
			_ontoHash.put(topTreeNode, ontoTreeNode);
		}    	
    	while (children.hasNext()) {
    		ontorama.model.tree.TreeNode curNode = (ontorama.model.tree.TreeNode) children.next();
    		TreeEdge edge = topTreeNode.getEdge(curNode);
    		createOntoTreeNode (topTreeNode, edge);
    	}
    }

    /**
     * Convert each Node to OntoTreeNode
     */
    private void createOntoTreeNode(ontorama.model.tree.TreeNode top, TreeEdge edge) {
        OntoTreeNode ontoTreeNode = new OntoTreeNode(top);
        ontoTreeNode.setRelLink(edge.getEdgeType());
        _ontoHash.put(top, ontoTreeNode);

		Iterator children = top.getChildren().iterator();
        while (children.hasNext()) {
        	ontorama.model.tree.TreeNode toModelTreeNode = (ontorama.model.tree.TreeNode) children.next();
        	
            TreeEdge curEdge = top.getEdge(toModelTreeNode);
            EdgeType curEdgeType = curEdge.getEdgeType();

            createOntoTreeNode(toModelTreeNode, curEdge);
        }
    }

    /**
     * Get TreeNode associated with given Node
     * @param	graphNode
     * @return	treeNode
     */
    public static TreeNode getTreeNode(ontorama.model.tree.TreeNode treeModelNode) {
        TreeNode treeNode = (OntoTreeNode) _ontoHash.get(treeModelNode);
        return treeNode;
    }

}
