package ontorama.model;

import ontorama.webkbtools.util.NoSuchRelationLinkException;

import java.net.URI;

/**
 * Description: EdgeImpl between nodes. Edges correspong to relation links between concept types.
 * Copyright:    Copyright (c) 2001
 * Company: DSTC
 * @version 1.0
 */

public class EdgeImpl implements Edge {

    /**
     * inboundNode
     */
    private Node fromNode;

    /**
     * outboundNodes
     */
    private Node toNode;

    /**
     * edgeType
     */
    private EdgeType edgeType;

    /**
     * cretor URI for this edge - unique identifier of the creator
     */
    private URI creatorUri;





    /**
     *
     */
    public EdgeImpl(Node fromNode, Node toNode, EdgeType edgeType) throws NoSuchRelationLinkException {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.edgeType = edgeType;
    }

    /**
     * get a 'start' node for this edge.
     * @return fromNode
     */
    public Node getFromNode() {
        return this.fromNode;
    }

    /**
     * get an 'end' node for this edge.
     * @return toNode
     */
    public Node getToNode() {
        return this.toNode;
    }

    /**
     * get edge type.
     * @return edgeType
     */
    public EdgeType getEdgeType() {
        return this.edgeType;
    }

    public Node getEdgeNode(boolean isFromNode) {
        if (isFromNode == true) {
            return this.fromNode;
        }
        return this.toNode;
    }

    /**
     * set URI of creator of this edge.
     * @param creatorUri
     */
    public void setCreatorUri(URI creatorUri) {
        this.creatorUri = creatorUri;
    }

    /**
     * get URI of creator of this edge.
     * @return
     */
    public URI getCreatorUri() {
        return this.creatorUri;
    }

    public String toString() {
        String str = "Edge from '" + this.fromNode + "' to '";
        str = str + this.toNode + "', edgeType = " + edgeType.getName() + "(URI:" + edgeType.getNamespace() + ")";
        str = str + ", creatorUri = " + this.creatorUri;
        return str;
    }

    public void setToNode(Node node) {
               this.toNode = node;
        }

    public void setFromNode(Node node) {
           this.fromNode = node;
    }
}
