package ontorama.backends.p2p.model;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Node;
import ontorama.model.graph.EdgeImpl;

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
    private HashSet asserters;

    /**
     * Store the rejecters
     */
    private HashSet rejecters;

    /**
     * Create a new P2PEdgeImpl with given name and asserter or rejecter
     *
     * @param fromNode
     * @param toNode
     * @param edgeType
     */
	public P2PEdgeImpl(Node fromNode, Node toNode, EdgeType edgeType) throws NoSuchRelationLinkException {
		super(fromNode,toNode,edgeType);
        this.asserters = new HashSet();
        this.rejecters = new HashSet();
	}

    /**
     * Create a new P2PEdgeImpl with given name and asserter or rejecter
     *
     * @param fromNode
     * @param toNode
     * @param edgeType
     * @param asserter
     * @param rejecter
     */
	public P2PEdgeImpl(Node fromNode, Node toNode, EdgeType edgeType,URI asserter, URI rejecter) throws NoSuchRelationLinkException {
		super(fromNode,toNode,edgeType);

		this.asserters = new HashSet();
		this.rejecters = new HashSet();
		if (asserter != null) {
			this.asserters.add(asserter);
		}
		if (rejecter != null) {
			this.rejecters.add(rejecter);		
		}
	}


    /**
     * Add an asserter for this edge. If the asserter is registered as a 
     * rejecter as well that is removed
     * 
     * @param userIdUri
     */
     public void addAssertion (URI userIdUri) {
		if (this.rejecters.contains(userIdUri)) {
			this.rejecters.remove(userIdUri);
		} 		    	
		this.asserters.add(userIdUri);	
    }
    
   /**
     * Add an rejecter for this edge. If the rejecter is registered as a 
     * asserter as well that is removed
     *
     * @param userIdUri
     */
      public void addRejection (URI userIdUri) {
		if (this.asserters.contains(userIdUri)) {
			this.asserters.remove(userIdUri);
		} 	
		this.rejecters.add(userIdUri);		    	
    }
    

    /**
     * Returns the asserters
     *
     * @return a list of the asserter
     */
     public Set getAssertionsList () {
    	return this.asserters;
    }
    
    /**
     * Returns the rejecter
     *
     * @return a list of the rejecters
     */
     public Set getRejectionsList () {
    	return this.rejecters;
    }
}
