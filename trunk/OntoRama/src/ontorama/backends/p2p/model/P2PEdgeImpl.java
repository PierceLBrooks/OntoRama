package ontorama.backends.p2p.model;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;

import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Node;

/**
 * Description: EdgeImpl between nodes. Edges correspond to relation links between concept types.
 * Copyright:    Copyright (c) 2001
 * Company: DSTC
 * @version 1.0
 */

public class P2PEdgeImpl implements P2PEdge {
	
	private P2PNode fromNode;
	private P2PNode toNode;
	private EdgeType edgeType;
	
	private URI creatorUri;
	
    /**
     * Store the asserters
     */
    private HashSet assertions;

    /**
     * Store the rejecters
     */
    private HashSet rejections;

    /**
     * Create a new P2PEdgeImpl with given name and asserter or rejecter
     */
	public P2PEdgeImpl(P2PNode fromNode, P2PNode toNode, EdgeType edgeType)  {
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.edgeType = edgeType;
        this.assertions = new HashSet();
        this.rejections = new HashSet();
	}

    /**
     * Add an asserter for this edge. If the asserter is registered as a 
     * rejecter as well that is removed
     */
     public void addAssertion (URI userIdUri) {
		if (this.rejections.contains(userIdUri)) {
			this.rejections.remove(userIdUri);
		} 		    	
		this.assertions.add(userIdUri);	
    }
    
   /**
     * Add an rejecter for this edge. If the rejecter is registered as a 
     * asserter as well that is removed
     */
      public void addRejection (URI userIdUri) {
		if (this.assertions.contains(userIdUri)) {
			this.assertions.remove(userIdUri);
		} 	
		this.rejections.add(userIdUri);		    	
    }
    

    /**
     * Returns the asserters
     */
     public Collection getAssertions () {
    	return this.assertions;
    }
    
    /**
     * Returns the rejecter
     */
     public Collection getRejections () {
    	return this.rejections;
    }
    
    
	/**
	 * @see ontorama.model.graph.Edge#getCreatorUri()
	 */
	public URI getCreatorUri() {
		return this.creatorUri;
	}

	/**
	 * @see ontorama.model.graph.Edge#getEdgeType()
	 */
	public EdgeType getEdgeType() {
		return this.edgeType;
	}

	/**
	 * @see ontorama.model.graph.Edge#getFromNode()
	 */
	public Node getFromNode() {
		return this.fromNode;
	}

	/**
	 * @see ontorama.model.graph.Edge#getToNode()
	 */
	public Node getToNode() {
		return this.toNode;
	}

	/**
	 * @see ontorama.model.graph.Edge#setCreatorUri(java.net.URI)
	 */
	public void setCreatorUri(URI creatorUri) {
		this.creatorUri = creatorUri;
	}

	/**
	 * @see ontorama.model.graph.Edge#setFromNode(ontorama.model.graph.Node)
	 */
	public void setFromNode(Node node) {
		this.fromNode = (P2PNode) node;
	}

	/**
	 * @see ontorama.model.graph.Edge#setToNode(ontorama.model.graph.Node)
	 */
	public void setToNode(Node node) {
		this.toNode = (P2PNode) node;
	}

}
