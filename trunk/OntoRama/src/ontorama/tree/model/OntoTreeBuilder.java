package ontorama.tree.model;

import ontorama.model.Edge;
import ontorama.model.Graph;
import ontorama.model.GraphNode;

import javax.swing.tree.TreeNode;
import java.util.Hashtable;
import java.util.Iterator;

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
    public OntoTreeBuilder(Graph graph) {
        this.graph = graph;

//        Iterator topLevelNodes = graph.getUnconnectedNodesList().iterator();
//        if (!topLevelNodes.hasNext()) {
            processNode(graph.getRootNode());
//        }
//        while (topLevelNodes.hasNext()) {
//            GraphNode topLevelNode = (GraphNode) topLevelNodes.next();
//            processNode(topLevelNode);
//        }
    }

    /**
     *
     */
    private void processNode (GraphNode topGraphNode) {
        Iterator outboundEdges = Edge.getOutboundEdges(topGraphNode);

        // take care of a case when we only have one node and no edges
        if (!outboundEdges.hasNext()) {
            OntoTreeNode ontoTreeNode = new OntoTreeNode(topGraphNode);
            ontoHash.put(topGraphNode, ontoTreeNode);
        }

        while (outboundEdges.hasNext()) {
            Edge edge = (Edge) outboundEdges.next();
            graphNodeToOntoTreeNode(topGraphNode, edge.getType());
        }

    }

    /**
     * Convert each GraphNode to OntoTreeNode
     */
    private void graphNodeToOntoTreeNode(GraphNode top, int relLink) {
        OntoTreeNode ontoTreeNode = new OntoTreeNode(top);
        ontoTreeNode.setRelLink(relLink);
        ontoHash.put(top, ontoTreeNode);

        Iterator outboundEdges = Edge.getOutboundEdges(top);
        while (outboundEdges.hasNext()) {
            Edge edge = (Edge) outboundEdges.next();
            GraphNode toGraphNode = (GraphNode) edge.getToNode();
            graphNodeToOntoTreeNode(toGraphNode, edge.getType());
        }
    }

    /**
     * Get TreeNode associated with given GraphNode
     * @param	graphNode
     * @return	treeNode
     */
    public static TreeNode getTreeNode(GraphNode graphNode) {
        TreeNode treeNode = (OntoTreeNode) ontoHash.get(graphNode);
        return treeNode;
    }

    /**
     * Get Iterator of TreeNodes for this tree
     * @return	iterator of treeNodes
     */
    public Iterator getIterator() {
        return (ontoHash.values()).iterator();
    }


}
