package ontorama.tree.model;

import ontorama.model.*;
import ontorama.OntoramaConfig;

import javax.swing.tree.TreeNode;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Enumeration;

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

    private static Hashtable ontoHash = new Hashtable();
    private Graph graph;

    /**
     * Constructor
     * @param	graph
     */
    public OntoTreeBuilder(Graph graph) {
        this.graph = graph;

        processNode(graph.getRootNode());
    }

    /**
     *
     */
    private void processNode (Node topGraphNode) {
        List outboundEdgesList = graph.getOutboundEdgesDisplayedInGraph(topGraphNode);
        Iterator outboundEdges =outboundEdgesList.iterator();

        int countRootGraphEdges = 0;
        while (outboundEdges.hasNext()) {
            Edge edge = (Edge) outboundEdges.next();
            EdgeType edgeType = edge.getEdgeType();
            graphNodeToOntoTreeNode(topGraphNode, edgeType);
            countRootGraphEdges++;
        }
        // take care of a case when we only have one node and no _graphEdges
        if (countRootGraphEdges == 0) {
            OntoTreeNode ontoTreeNode = new OntoTreeNode(topGraphNode);
            ontoHash.put(topGraphNode, ontoTreeNode);
        }

    }

    /**
     * Convert each Node to OntoTreeNode
     */
    private void graphNodeToOntoTreeNode(Node top, EdgeType edgeType) {
        OntoTreeNode ontoTreeNode = new OntoTreeNode(top);
        ontoTreeNode.setRelLink(edgeType);
        ontoHash.put(top, ontoTreeNode);

        Iterator outboundEdges = graph.getOutboundEdgesDisplayedInGraph(top).iterator();
        while (outboundEdges.hasNext()) {
            Edge edge = (Edge) outboundEdges.next();
            EdgeType curEdgeType = edge.getEdgeType();

            Node toGraphNode = edge.getToNode();
            graphNodeToOntoTreeNode(toGraphNode, curEdgeType);
        }
    }

    /**
     * Get TreeNode associated with given Node
     * @param	graphNode
     * @return	treeNode
     */
    public static TreeNode getTreeNode(Node graphNode) {
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
