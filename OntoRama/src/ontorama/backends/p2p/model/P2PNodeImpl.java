package ontorama.backends.p2p.model;


import ontorama.model.graph.NodeImpl;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;

/**
 * Basic P2PNodeImpl for ontology viewers.
 *
 * <p>
 * Copyright:    Copyright (c) 2002
 * <br>
 * Company:     DSTC
 */
public class P2PNodeImpl extends NodeImpl implements P2PNode {

    /**
     * Store the asserters
     */
    private HashSet asserters;

    /**
     * Store the rejecters
     */
    private HashSet rejecters;

    /**
     * Create a new P2PNodeImpl with given name and fullName
     *
     * @param name
     * @param fullName
     */
	public P2PNodeImpl(String name, String fullName) {
		super(name,fullName);
		this.asserters = new HashSet();
		this.rejecters = new HashSet();
	}


    /**
     * Add an asserter for this node. If the asserter is registered as a 
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
     * Add an rejecter for this node. If the rejecter is registered as a 
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
    public Collection getAssertions () {
    	return this.asserters;
    }
    
    /**
     * Returns the rejecter
     *
     * @return a list of the rejecters
     */
    public Collection getRejectionsList () {
    	return this.rejecters;
    }
}
