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
     *
     */
    public Node getFromNode() {
        return this.fromNode;
    }

    /**
     *
     */
    public Node getToNode() {
        return this.toNode;
    }

    public EdgeType getEdgeType() {
        return this.edgeType;
    }

    public Node getEdgeNode(boolean isFromNode) {
        if (isFromNode == true) {
            return this.fromNode;
        }
        return this.toNode;
    }

    public void setCreatorUri(URI creatorUri) {
        this.creatorUri = creatorUri;
    }

    public URI getCreatorUri() {
        return this.creatorUri;
    }

    public String toString() {
        String str = "Edge from '" + this.fromNode + "' to '";
        str = str + this.toNode + "', edgeType = " + edgeType.getName();
        str = str + ", creatorUri = " + this.creatorUri;
        return str;
    }


}
