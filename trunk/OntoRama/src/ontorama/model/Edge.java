package ontorama.model;

import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.RelationLinkDetails;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Description: Edge between nodes. Edges correspong to relation links between concept types.
 * Copyright:    Copyright (c) 2001
 * Company: DSTC
 * @version 1.0
 */

public class Edge {

    /**
     * inboundNode
     */
    GraphNode fromNode;

    /**
     * outboundNodes
     */
    GraphNode toNode;

    /**
     * edgeType
     */
    RelationLinkDetails edgeType;

    /**
     * list holding all edges
     */
    public static List edges = new LinkedList();


    /**
     *
     */
    public Edge(GraphNode fromNode, GraphNode toNode, RelationLinkDetails edgeType) throws NoSuchRelationLinkException {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.edgeType = edgeType;
        registerEdge(this);
    }

    /**
     *
     */
    public GraphNode getFromNode() {
        return this.fromNode;
    }

    /**
     *
     */
    public GraphNode getToNode() {
        return this.toNode;
    }

    /**
     *
     */
    public RelationLinkDetails getEdgeType() {
        return this.edgeType;
    }

    /**
     *
     */
    private GraphNode getEdgeNode(boolean isFromNode) {
        if (isFromNode == true) {
            return this.fromNode;
        }
        return this.toNode;
    }

    /**
     *
     */
    private static void registerEdge(Edge edge) {
        boolean isInList = false;
        Iterator it = edges.iterator();
        while (it.hasNext()) {
            Edge cur = (Edge) it.next();
            if ((edge.getFromNode().equals(cur.getFromNode())) &&
                    (edge.getToNode().equals(cur.getToNode())) &&
                    (edge.getEdgeType() == cur.getEdgeType())) {
                // this edge is already registered
                //System.out.println("edge is already registered: " + edge);
                isInList = true;
            }
        }
        if (!isInList) {
            Edge.edges.add(edge);
        }
    }

    /**
     *
     */
    public static void removeEdge(Edge remEdge) {
        Edge.edges.remove(remEdge);
    }

    /**
     *
     */
    public static void removeAllEdges() {
        Edge.edges.clear();
    }

    /**
     *
     */
    public static Edge getEdge(GraphNode fromNode, GraphNode toNode, RelationLinkDetails edgeType) {
        Iterator it = Edge.getOutboundEdges(fromNode, edgeType);
        //Iterator it = Edge.getOutboundEdgeNodes(fromNode, relLink);
        while (it.hasNext()) {
            Edge curEdge = (Edge) it.next();
            //GraphNode curNode = (GraphNode) it.next();
            GraphNode curNode = curEdge.getToNode();
            if (curNode.equals(toNode)) {
                return curEdge;
            }
        }
        return null;
    }

    /**
     *
     */
    public static Iterator getOutboundEdges(GraphNode node) {
        return getEdges(node, true);
    }

    /**
     *
     */
    public static Iterator getInboundEdges(GraphNode node) {
        return getEdges(node, false);
    }


    /**
     *
     * @param  node GraphNode
     *         flag - true if we want to get list of outbound edges,
     *         false of we want to get a list of inbound edges.
     * @return iterator of Edges
     */
    private static Iterator getEdges(GraphNode node, boolean flag) {
        List result = new LinkedList();
        Iterator it = edges.iterator();
        while (it.hasNext()) {
            Edge cur = (Edge) it.next();
            GraphNode graphNode = cur.getEdgeNode(flag);
            if (graphNode.equals(node)) {
                result.add(cur);
            }
        }
        return result.iterator();
    }

    /**
     *
     * @param  node GraphNode
     *         int relationType
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.      *
     * @return iterator of Edges
     */
    private static Iterator getEdges(GraphNode node, RelationLinkDetails relationType, boolean flag) {
        List result = new LinkedList();
        Iterator nodeEdgesIt = getEdges(node, flag);
        while (nodeEdgesIt.hasNext()) {
            Edge cur = (Edge) nodeEdgesIt.next();
            if (cur.getEdgeType() == relationType) {
                result.add(cur);
            }
        }
        return result.iterator();
    }

    /**
     *
     */
    public static Iterator getOutboundEdges(GraphNode node, RelationLinkDetails edgeType) {
        return getEdges(node, edgeType, true);
    }

    /**
     *
     */
    public static Iterator getInboundEdges(GraphNode node, RelationLinkDetails edgeType) {
        return getEdges(node, edgeType, false);
    }


    /**
     *
     * @return iterator of Nodes
     */
    public static Iterator getOutboundEdgeNodes(GraphNode node, Set relationLinks) {
        return getEdgeNodes(node, relationLinks, true);
    }

    /**
     *
     * @return iterator of Nodes
     */
    public static Iterator getInboundEdgeNodes(GraphNode node, Set relationLinks) {
        return getEdgeNodes(node, relationLinks, false);
    }

    /**
     *
     * @param  node GraphNode
     *         Set relationLinks
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.
     * @return iterator of Nodes
     */
    private static Iterator getEdgeNodes(GraphNode node, Set relationLinks, boolean flag) {
        List result = new LinkedList();
        Iterator nodeEdgesIt = getEdges(node, flag);
        while (nodeEdgesIt.hasNext()) {
            Edge cur = (Edge) nodeEdgesIt.next();
            Iterator it = relationLinks.iterator();
            while (it.hasNext()) {
                RelationLinkDetails curRel = (RelationLinkDetails) it.next();
                if (cur.getEdgeType().equals(curRel)) {
                    result.add(cur.getEdgeNode(!flag));
                }
            }
//            while (it.hasNext()) {
//                Integer curRel = (Integer) it.next();
//                if (cur.getEdgeType() == curRel.intValue()) {
//                    result.add(cur.getEdgeNode(!flag));
//                }
//            }
        }
        return result.iterator();
    }

    /**
     *
     */
    public static Iterator getOutboundEdgeNodes(GraphNode node, RelationLinkDetails edgeType) {
        return getEdgeNodes(node, edgeType, true);
    }

    /**
     *
     */
    public static Iterator getInboundEdgeNodes(GraphNode node, RelationLinkDetails edgeType) {
        return getEdgeNodes(node, edgeType, false);
    }


    /**
     *
     * @param  node GraphNode
     *         int relationType
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.      *
     * @return iterator of Nodes
     */
    private static Iterator getEdgeNodes(GraphNode node, RelationLinkDetails edgeType, boolean flag) {
        List result = new LinkedList();
        Iterator nodeEdgesIt = getEdges(node, flag);
        while (nodeEdgesIt.hasNext()) {
            Edge cur = (Edge) nodeEdgesIt.next();
            if (cur.getEdgeType().equals(edgeType)) {
                result.add(cur.getEdgeNode(!flag));
            }
        }
        return result.iterator();
    }

    /**
     *
     */
    public static Iterator getOutboundEdgeNodes(GraphNode node) {
        return getEdgeNodes(node, true);
    }

    /**
     *
     */
    public static Iterator getInboundEdgeNodes(GraphNode node) {
        return getEdgeNodes(node, false);
    }


    /**
     *
     * @param  node GraphNode
     *         int relationType
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.      *
     * @return iterator of Nodes
     */
    private static Iterator getEdgeNodes(GraphNode node, boolean flag) {
        return getEdgeNodesList(node, flag).iterator();
    }

    /**
     *
     */
    public static List getOutboundEdgeNodesList(GraphNode node) {
        return getEdgeNodesList(node, true);
    }

    /**
     *
     */
    public static List getInboundEdgeNodesList(GraphNode node) {
        return getEdgeNodesList(node, false);
    }


    /**
     *
     * @param  node GraphNode
     *         int relationType
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.      *
     * @return list of Nodes
     */
    private static List getEdgeNodesList(GraphNode node, boolean flag) {
        List result = new LinkedList();
        Iterator nodeEdgesIt = getEdges(node, flag);
        while (nodeEdgesIt.hasNext()) {
            Edge cur = (Edge) nodeEdgesIt.next();
            result.add(cur.getEdgeNode(!flag));
        }
        return result;
    }

    /**
     * Convenience method that returns iterator size
     * @param   it iterator
     * @return  int size
     * @todo  perhaps this method should 'live' in util package
     */
    public static int getIteratorSize(Iterator it) {
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count = count + 1;
        }
        return count;
    }


    /**
     *
     */
    public String toString() {
        String str = "Edge from '" + this.fromNode.getName() + "' to '" + this.toNode.getName() + "', edgeType = " + edgeType;
        //String str = "Edge from '" + this.fromNode.getName() + "' = " + this.fromNode +  " to '" + this.toNode.getName() + "' = " + this.toNode + ", edgeType = " + edgeType;
        return str;
    }

    /**
     *
     */
    public static void printAllEdges() {
        Iterator it = edges.iterator();
        while (it.hasNext()) {
            Edge edge = (Edge) it.next();
            System.out.println(edge);
        }
    }


}
