package ontorama.backends.p2p.model;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;

import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.EdgeImpl;
import ontorama.model.graph.Node;

/**
 * Description: EdgeImpl between nodes. Edges correspond to relation links between concept types.
 * Copyright:    Copyright (c) 2001
 * Company: DSTC
 * @version 1.0
 */

public class P2PEdgeImpl extends EdgeImpl implements P2PEdge {
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
	public P2PEdgeImpl(Node fromNode, Node toNode, EdgeType edgeType) throws NoSuchRelationLinkException {
		super(fromNode,toNode,edgeType);
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
}
