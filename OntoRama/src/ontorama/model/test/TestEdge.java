package ontorama.model.test;

import junit.framework.TestCase;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.model.*;
import ontorama.OntoramaConfig;

import java.util.*;
import java.net.URI;
import java.net.URISyntaxException;

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

public class TestEdge extends TestCase {

    private Node node1;
    private Node node2;
    private Node node3;
    private Node node4;
    private Node node5;
    private Node node6;

    private Edge edge1;
    private Edge edge2;
    private Edge edge3;
    private Edge edge4;
    private Edge edge5;
    private Edge edge6;
    private Edge edge7;

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
    public TestEdge(String name) {
        super(name);
    }


    /**
     *
     */
    protected void setUp() throws URISyntaxException {
        creatorUri1 = new URI("ontoMailto:someone@ontorama.org");
        creatorUri2 = new URI("ontoHttp://ontorama.ort/someone.html");


        node1 = new NodeImpl("node1");
        node2 = new NodeImpl("node2");
        node3 = new NodeImpl("node3");
        node4 = new NodeImpl("node4");
        node5 = new NodeImpl("node5");
        node6 = new NodeImpl("node6");

        try {
            edge1 = new EdgeImpl(node1, node2, OntoramaConfig.getEdgeType(edgeName_subtype));
            edge2 = new EdgeImpl(node1, node3, OntoramaConfig.getEdgeType(edgeName_subtype));
            edge3 = new EdgeImpl(node1, node4, OntoramaConfig.getEdgeType(edgeName_similar));
            edge4 = new EdgeImpl(node1, node5, OntoramaConfig.getEdgeType(edgeName_reverse));
            edge5 = new EdgeImpl(node2, node6, OntoramaConfig.getEdgeType(edgeName_subtype));
            edge6 = new EdgeImpl(node3, node6, OntoramaConfig.getEdgeType(edgeName_similar));
            edge7 = new EdgeImpl(node4, node6, OntoramaConfig.getEdgeType(edgeName_reverse));

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

    //////////////******* Helper methods ********////////////////////


    /**
     * create a set of edge types
     */
    private Set createSet(EdgeType det1, EdgeType det2) {
        Set set = new HashSet();
        set.add(det1);
        set.add(det2);
        return set;
    }
}