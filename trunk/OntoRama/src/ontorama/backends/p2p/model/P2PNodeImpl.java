package ontorama.backends.p2p.model;


import ontorama.model.graph.Node;
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
         * Create a new P2PNodeImpl with given name and fullName
         *
         * @param node
         */
        public P2PNodeImpl(Node node) {
            super(node.getName(),node.getIdentifier());
            this.setCreatorUri(node.getCreatorUri());
            this.setNodeType(node.getNodeType());
            this.asserters = new HashSet();
            this.rejecters = new HashSet();
        }


    /**
     * Create a new P2PNodeImpl with given name and asserter or rejecter
     *
     * @param name
     * @param asserter
     * @param rejecter
     */
	public P2PNodeImpl(String name, URI asserter, URI rejecter) {
		super(name, name);
		
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
     * Create a new P2PNodeImpl with given name and asserter or rejecter
     *
     * @param name
     * @param fullName
     * @param asserter
     * @param rejecter
     */
	public P2PNodeImpl(String name, String fullName, URI asserter, URI rejecter) {
		super(name,fullName);
		
		
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
