
package ontorama.tree.model;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import ontorama.model.Graph;
import ontorama.model.GraphNode;

/**
 * Description: Build Tree of OntoTreeNodes from given graph
 * with given GraphNodes
 *
 * Copyright:    Copyright (c) 2001
 * Company:     DSTC
 */

public class OntoTreeBuilder {

    private static Hashtable ontoHash = new Hashtable();
    private Graph graph;

    /**
     * Constructor
	 * @param	graph
     */
    public OntoTreeBuilder (Graph graph) {
        this.graph = graph;
        graphToOntoTree();
    }

    /**
     * Convert each GraphNode to OntoTreeNode
     */
    private void graphToOntoTree() {
        Iterator it = this.graph.iterator();
        while (it.hasNext()) {
            GraphNode curNode = (GraphNode) it.next();
            TreeNode treeNode = new OntoTreeNode (curNode);
            //System.out.println("graph node = " + curNode);
            //System.out.println("tree node = " + treeNode);
            ontoHash.put(curNode,treeNode);
        }
    }

    /**
     * Get TreeNode associated with given GraphNode
	 * @param	graphNode
	 * @return	treeNode
     */
    public static TreeNode getTreeNode (GraphNode graphNode) {
        TreeNode treeNode = (OntoTreeNode) ontoHash.get(graphNode);
        return treeNode;
    }
    /**
     * Get Iterator of TreeNodes for this tree
	 * @param	-
	 * @return	iterator of treeNodes
     */
    public Iterator getIterator () {
        return (ontoHash.values()).iterator();
    }
}
