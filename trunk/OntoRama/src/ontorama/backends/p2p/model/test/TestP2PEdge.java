package ontorama.backends.p2p.model.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import junit.framework.TestCase;

import ontorama.OntoramaConfig;
import ontorama.backends.p2p.model.P2PEdge;
import ontorama.backends.p2p.model.P2PEdgeImpl;
import ontorama.backends.p2p.model.P2PNode;
import ontorama.backends.p2p.model.P2PNodeImpl;
import ontorama.ontotools.NoSuchRelationLinkException;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 *
 * Not testing following methods:
 * - all private methods
 * - clearAllEdges method
 * - removeEdge method
 * - removeAllEdges method
 */

public class TestP2PEdge extends TestCase {

    private P2PNode node1;
    private P2PNode node2;
    private P2PNode node3;
    private P2PNode node4;
    private P2PNode node5;
    private P2PNode node6;

    private P2PEdge edge1;
    private P2PEdge edge2;
    private P2PEdge edge3;
    private P2PEdge edge4;
    private P2PEdge edge5;
    private P2PEdge edge6;
    private P2PEdge edge7;

    private static final String edgeName_subtype = "subtype";
    private static final String edgeName_similar = "similar";
    private static final String edgeName_reverse = "reverse";

    private Set relLinksSet;

    private LinkedList outboundEdgesListForNode1 = new LinkedList();
    private LinkedList inboundEdgesListForNode6 = new LinkedList();

    private LinkedList outboundEdgesListForNode1Relation1 = new LinkedList();
    private LinkedList inboundEdgesListForNode6Relation1 = new LinkedList();

    private LinkedList outboundNodesListForNode1RelLinkSet = new LinkedList();
    private LinkedList inboundNodesListForNode6RelLinkSet = new LinkedList();

    private LinkedList outboundNodesListForNode1Relation1 = new LinkedList();
    private LinkedList inboundNodesListForNode6Relation2 = new LinkedList();

    private LinkedList outboundNodesListForNode1 = new LinkedList();
    private LinkedList inboundNodesListForNode6 = new LinkedList();

    private URI creatorUri1;
    private URI creatorUri2;


    /**
     *
     */
    public TestP2PEdge(String name) {
        super(name);
    }


    /**
     *
     */
    protected void setUp() throws URISyntaxException {
        creatorUri1 = new URI("ontoMailto:someone@ontorama.org");
        creatorUri2 = new URI("ontoHttp://ontorama.ort/someone.html");


        node1 = new P2PNodeImpl("node1",creatorUri1,null);
        node2 = new P2PNodeImpl("node2",creatorUri1,null);
        node3 = new P2PNodeImpl("node3",creatorUri1,null);
        node4 = new P2PNodeImpl("node4",null,creatorUri2);
        node5 = new P2PNodeImpl("node5",null,creatorUri2);
        node6 = new P2PNodeImpl("node6",null,creatorUri2);

        try {
            edge1 = new P2PEdgeImpl(node1, node2, OntoramaConfig.getEdgeType(edgeName_subtype),creatorUri1,null);
            edge2 = new P2PEdgeImpl(node1, node3, OntoramaConfig.getEdgeType(edgeName_subtype),creatorUri1,null);
            edge3 = new P2PEdgeImpl(node1, node4, OntoramaConfig.getEdgeType(edgeName_similar),creatorUri1,null);
            edge4 = new P2PEdgeImpl(node1, node5, OntoramaConfig.getEdgeType(edgeName_reverse),null,creatorUri2);
            edge5 = new P2PEdgeImpl(node2, node6, OntoramaConfig.getEdgeType(edgeName_subtype),null,creatorUri2);
            edge6 = new P2PEdgeImpl(node3, node6, OntoramaConfig.getEdgeType(edgeName_similar),null,creatorUri2);
            edge7 = new P2PEdgeImpl(node4, node6, OntoramaConfig.getEdgeType(edgeName_reverse),null,creatorUri2);

            // create relation links set
            relLinksSet = createSet(OntoramaConfig.getEdgeType(edgeName_similar), OntoramaConfig.getEdgeType(edgeName_reverse));

            // populate linked lists
            outboundEdgesListForNode1.add(edge1);
            outboundEdgesListForNode1.add(edge2);
            outboundEdgesListForNode1.add(edge3);
            outboundEdgesListForNode1.add(edge4);

            inboundEdgesListForNode6.add(edge5);
            inboundEdgesListForNode6.add(edge6);
            inboundEdgesListForNode6.add(edge7);

            outboundEdgesListForNode1Relation1.add(edge1);
            outboundEdgesListForNode1Relation1.add(edge2);

            inboundEdgesListForNode6Relation1.add(edge5);

            outboundNodesListForNode1RelLinkSet.add(node4);
            outboundNodesListForNode1RelLinkSet.add(node5);

            inboundNodesListForNode6RelLinkSet.add(node3);
            inboundNodesListForNode6RelLinkSet.add(node4);

            outboundNodesListForNode1Relation1.add(node2);
            outboundNodesListForNode1Relation1.add(node3);

            inboundNodesListForNode6Relation2.add(node3);

            outboundNodesListForNode1.add(node2);
            outboundNodesListForNode1.add(node3);
            outboundNodesListForNode1.add(node4);
            outboundNodesListForNode1.add(node5);

            inboundNodesListForNode6.add(node2);
            inboundNodesListForNode6.add(node3);
            inboundNodesListForNode6.add(node4);

            edge1.setCreatorUri(creatorUri1);
            edge2.setCreatorUri(creatorUri2);
        } catch (NoSuchRelationLinkException e) {
            System.err.println("NoSuchRelationLinkException: " + e.getMessage());
            System.exit(-1);
        }
    }



    /**
     * test method getFromNode
     */
    public void testGetFromNode() {
        assertEquals(node1, edge1.getFromNode());
        assertEquals(node1, edge2.getFromNode());
        assertEquals(node1, edge3.getFromNode());
        assertEquals(node1, edge4.getFromNode());
        assertEquals(node2, edge5.getFromNode());
        assertEquals(node3, edge6.getFromNode());
        assertEquals(node4, edge7.getFromNode());
    }

    /**
     * test method getToNode
     */
    public void testGetToNode() {
        assertEquals(node2, edge1.getToNode());
        assertEquals(node3, edge2.getToNode());
        assertEquals(node4, edge3.getToNode());
        assertEquals(node5, edge4.getToNode());
        assertEquals(node6, edge5.getToNode());
        assertEquals(node6, edge6.getToNode());
        assertEquals(node6, edge7.getToNode());
    }

    /**
     * test method getEdgeType
     */
    public void testGetType() throws NoSuchRelationLinkException {
        assertEquals(OntoramaConfig.getEdgeType(edgeName_subtype), edge1.getEdgeType());
        assertEquals(OntoramaConfig.getEdgeType(edgeName_subtype), edge2.getEdgeType());
        assertEquals(OntoramaConfig.getEdgeType(edgeName_similar), edge3.getEdgeType());
        assertEquals(OntoramaConfig.getEdgeType(edgeName_reverse), edge4.getEdgeType());
        assertEquals(OntoramaConfig.getEdgeType(edgeName_subtype), edge5.getEdgeType());
        assertEquals(OntoramaConfig.getEdgeType(edgeName_similar), edge6.getEdgeType());
        assertEquals(OntoramaConfig.getEdgeType(edgeName_reverse), edge7.getEdgeType());
    }

    public void testGetCreator() {
        assertEquals("creatorUri for edge1", creatorUri1, edge1.getCreatorUri());
        assertEquals("creatorUri for edge2", creatorUri2, edge2.getCreatorUri());
    }

    public void testSetCreator() throws URISyntaxException {
        URI creatorUri = new URI("http://onotrama.org/user/");
        edge3.setCreatorUri(creatorUri);
        assertEquals("creatorUri for edge3", creatorUri, edge3.getCreatorUri());
    }



	public void testGetAssertionList() {
		HashSet set = new HashSet();
		set.add(creatorUri1);
		
		assertEquals("getAssertion for edge1", set, (HashSet) edge1.getAssertionsList());
		assertEquals("getAssertion for edge2", set, (HashSet) edge2.getAssertionsList());
		assertEquals("getAssertion for edge3", set, (HashSet) edge3.getAssertionsList());
	}

	public void testGetRejectionList() {
		HashSet set = new HashSet();
		set.add(creatorUri2);
		
		assertEquals("getRejections for edge4", set, (HashSet) edge4.getRejectionsList());
		assertEquals("getRejections for edge5", set, (HashSet) edge5.getRejectionsList());
		assertEquals("getRejections for edge6", set, (HashSet) edge6.getRejectionsList());
		assertEquals("getRejections for edge7", set, (HashSet) edge7.getRejectionsList());
	}

	public void testAddAssertion() {
		HashSet set = new HashSet();		
		set.add(creatorUri1);
		
		edge1.addAssertion(creatorUri1);
		assertEquals("addAssertion for edge1", set, (HashSet) edge1.getAssertionsList());
		
		set.add(creatorUri2);
		edge2.addAssertion(creatorUri2);
		assertEquals("addAssertion for edge2", set, (HashSet) edge2.getAssertionsList());
	}

	public void testAddRejection() {
		HashSet set = new HashSet();	

		set.add(creatorUri2);
		edge4.addRejection(creatorUri2);
		assertEquals("addRejection for edge2", set, (HashSet) edge4.getRejectionsList());

		set.add(creatorUri1);
		edge4.addRejection(creatorUri1);
		assertEquals("addRejection for edge1", set, (HashSet) edge4.getRejectionsList());
	}
	
	
	
    //////////////******* Helper methods ********////////////////////


    /**
     * create a set of edge types
     */
    private Set createSet(ontorama.model.graph.EdgeType det1, ontorama.model.graph.EdgeType det2) {
        Set set = new HashSet();
        set.add(det1);
        set.add(det2);
        return set;
    }
}
