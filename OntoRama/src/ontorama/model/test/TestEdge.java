package ontorama.model.test;

import junit.framework.TestCase;
import ontorama.util.IteratorUtil;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.model.GraphNode;
import ontorama.model.Edge;
import ontorama.model.GraphImpl;
import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.RelationLinkDetails;

import java.util.*;

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

    private GraphNode node1;
    private GraphNode node2;
    private GraphNode node3;
    private GraphNode node4;
    private GraphNode node5;
    private GraphNode node6;

    private Edge edge1;
    private Edge edge2;
    private Edge edge3;
    private Edge edge4;
    private Edge edge5;
    private Edge edge6;
    private Edge edge7;

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


    /**
     *
     */
    public TestEdge(String name) {
        super(name);
    }


    /**
     *
     */
    protected void setUp() {
        GraphImpl.removeAllEdges();

        node1 = new GraphNode("node1");
        node2 = new GraphNode("node2");
        node3 = new GraphNode("node3");
        node4 = new GraphNode("node4");
        node5 = new GraphNode("node5");
        node6 = new GraphNode("node6");

        try {
            edge1 = new Edge(node1, node2, OntoramaConfig.getRelationLinkDetails()[1]);
            edge2 = new Edge(node1, node3, OntoramaConfig.getRelationLinkDetails()[1]);
            edge3 = new Edge(node1, node4, OntoramaConfig.getRelationLinkDetails()[2]);
            edge4 = new Edge(node1, node5, OntoramaConfig.getRelationLinkDetails()[3]);
            edge5 = new Edge(node2, node6, OntoramaConfig.getRelationLinkDetails()[1]);
            edge6 = new Edge(node3, node6, OntoramaConfig.getRelationLinkDetails()[2]);
            edge7 = new Edge(node4, node6, OntoramaConfig.getRelationLinkDetails()[3]);

            // create relation links set
            relLinksSet = createSet(OntoramaConfig.getRelationLinkDetails()[2], OntoramaConfig.getRelationLinkDetails()[3]);

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
    public void testGetType() {
        assertEquals(OntoramaConfig.getRelationLinkDetails()[1], edge1.getEdgeType());
        assertEquals(OntoramaConfig.getRelationLinkDetails()[1], edge2.getEdgeType());
        assertEquals(OntoramaConfig.getRelationLinkDetails()[2], edge3.getEdgeType());
        assertEquals(OntoramaConfig.getRelationLinkDetails()[3], edge4.getEdgeType());
        assertEquals(OntoramaConfig.getRelationLinkDetails()[1], edge5.getEdgeType());
        assertEquals(OntoramaConfig.getRelationLinkDetails()[2], edge6.getEdgeType());
        assertEquals(OntoramaConfig.getRelationLinkDetails()[3], edge7.getEdgeType());
    }

    //////////////******* Helper methods ********////////////////////


    /**
     * create a set of int's
     */
    private Set createSet(RelationLinkDetails det1, RelationLinkDetails det2) {
        Set set = new HashSet();
        set.add(det1);
        set.add(det2);
        return set;
    }
}
