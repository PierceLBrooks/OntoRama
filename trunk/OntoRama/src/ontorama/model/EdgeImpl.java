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
     * URI for this edge (unique identifier of creator)
     */
    private URI uri;



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

    public URI getUri() {
        return this.uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
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

    public String toString() {
        String str = "Edge from '" + this.fromNode + "' to '";
        str = str + this.toNode + "', edgeType = " + edgeType.getName();
        str = str + ", URI = " + this.uri;
        return str;
    }


}
