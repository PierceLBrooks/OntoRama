package ontorama.model;

import ontorama.ontologyConfig.RelationLinkDetails;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

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
    Node fromNode;

    /**
     * outboundNodes
     */
    NodeImpl toNode;

    /**
     * edgeType
     */
    RelationLinkDetails edgeType;


    /**
     *
     */
    public EdgeImpl(Node fromNode, NodeImpl toNode, RelationLinkDetails edgeType) throws NoSuchRelationLinkException {
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
    public NodeImpl getToNode() {
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
    public Node getEdgeNode(boolean isFromNode) {
        if (isFromNode == true) {
            return this.fromNode;
        }
        return this.toNode;
    }


    /**
     *
     */
    public String toString() {
        String str = "EdgeImpl from '" + this.fromNode + "' to '" + this.toNode + "', edgeType = " + edgeType.getLinkName();
        //String str = "EdgeImpl from '" + this.fromNode.getName() + "' = " + this.fromNode +  " to '" + this.toNode.getName() + "' = " + this.toNode + ", edgeType = " + edgeType;
        return str;
    }


}
