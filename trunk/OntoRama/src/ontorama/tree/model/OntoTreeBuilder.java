
package ontorama.tree.model;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import ontorama.model.Graph;
import ontorama.model.Edge;
import ontorama.model.GraphNode;

/**
 * Description: Build Tree of OntoTreeNodes from given graph
 * with given GraphNodes. This object is point of reference
 * between GraphNodes and OntoTreeNodes.
 * This is used by OntoTreeModel to build the TreeModel and
 * later on is used by OntoTreeNode for reference for getting
 * a TreeNode from given GraphNode.
 *
 * @todo: This probably can be done other way - by cycling through all
 * TreeNodes and comparing given GraphNode to OntoTreeNode.getGraphNode
 * untill match is found.
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
        Iterator outboundEdges = Edge.getOutboundEdges(graph.getEdgeRootNode());
        while (outboundEdges.hasNext()) {
            Edge edge = (Edge) outboundEdges.next();
            graphToOntoTree(graph.getEdgeRootNode(), edge.getType());
        }
        //graphToOntoTree(graph.getEdgeRootNode());
    }

    /**
     * Convert each GraphNode to OntoTreeNode
     */
    private void graphToOntoTree(GraphNode top, int relLink) {
        OntoTreeNode ontoTreeNode = new OntoTreeNode (top);
        ontoTreeNode.setRelLink(relLink);
        ontoHash.put(top,ontoTreeNode);

        Iterator outboundEdges = Edge.getOutboundEdges(top);
        while (outboundEdges.hasNext()) {
            Edge edge = (Edge) outboundEdges.next();
            GraphNode toGraphNode = (GraphNode) edge.getToNode();
            graphToOntoTree(toGraphNode, edge.getType());
        }

//        Iterator outboundNodes = Edge.getOutboundEdgeNodes(top);
//        while (outboundNodes.hasNext()) {
//            GraphNode gn = (GraphNode) outboundNodes.next();
//            graphToOntoTree(gn);
//        }

//        Iterator it = this.graph.iterator();
//        while (it.hasNext()) {
//            GraphNode curNode = (GraphNode) it.next();
//            TreeNode treeNode = new OntoTreeNode (curNode);
//            ontoHash.put(curNode,treeNode);
//        }
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

    /**
     *
     */

}
