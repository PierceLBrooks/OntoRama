package ontorama.model.graph;

import java.net.URI;

/**
 * Description: EdgeImpl between nodes. Edges corresponding to relation links between concept types.
 * Copyright:    Copyright (c) 2001
 * Company: DSTC
 * @version 1.0
 */

public class EdgeImpl implements Edge {

    private Node fromNode;

    private Node toNode;

    private final EdgeType edgeType;

    /**
     * creator URI for this edge - unique identifier of the creator
     */
    private URI creatorUri;


    public EdgeImpl(Node fromNode, Node toNode, EdgeType edgeType) {
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

    @Override
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
    
    
	@Override
    public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		Edge edgeObj = (Edge) obj;
		if ((this.fromNode.equals(edgeObj.getFromNode())) &&
				(this.toNode.equals(edgeObj.getToNode())) &&
				(this.edgeType.equals(edgeObj.getEdgeType()))) {
			return true;
		}
		return false;
	}
	
	@Override
    public int hashCode() {
		int result = fromNode.hashCode() + toNode.hashCode() + edgeType.hashCode();
		return result;
	}

}
