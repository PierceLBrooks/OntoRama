package ontorama.tree.model;

import ontorama.model.Graph;
import ontorama.model.Node;
import ontorama.model.Edge;
import ontorama.model.Node;
import ontorama.ontologyConfig.RelationLinkDetails;

import javax.swing.tree.TreeNode;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Description: Build Tree of OntoTreeNodes from given graph
 * with given GraphNodes. This object is point of reference
 * between GraphNodes and OntoTreeNodes.
 * This is used by OntoTreeModel to build the TreeModel and
 * later on is used by OntoTreeNode for reference for getting
 * a TreeNode from given Node.
 *
 * @todo This probably can be done other way - by cycling through all
 * TreeNodes and comparing given Node to OntoTreeNode.getGraphNode
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

        processNode(graph.getRootNode());
    }

    /**
     *
     */
    private void processNode (Node topGraphNode) {
        Iterator outboundEdges = graph.getOutboundEdges(topGraphNode);

        // take care of a case when we only have one node and no _graphEdges
        if (!outboundEdges.hasNext()) {
            OntoTreeNode ontoTreeNode = new OntoTreeNode(topGraphNode);
            ontoHash.put(topGraphNode, ontoTreeNode);
        }

        while (outboundEdges.hasNext()) {
            Edge edge = (Edge) outboundEdges.next();
            graphNodeToOntoTreeNode(topGraphNode, edge.getEdgeType());
        }

    }

    /**
     * Convert each Node to OntoTreeNode
     */
    private void graphNodeToOntoTreeNode(Node top, RelationLinkDetails relLinkType) {
        OntoTreeNode ontoTreeNode = new OntoTreeNode(top);
        ontoTreeNode.setRelLink(relLinkType);
        ontoHash.put(top, ontoTreeNode);

        Iterator outboundEdges = graph.getOutboundEdges(top);
        while (outboundEdges.hasNext()) {
            Edge edge = (Edge) outboundEdges.next();
            Node toGraphNode = edge.getToNode();
            graphNodeToOntoTreeNode(toGraphNode, edge.getEdgeType());
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
