package ontorama.model.test;

import junit.framework.TestCase;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.query.QueryResult;
import ontorama.webkbtools.util.NoSuchPropertyException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.model.*;
import ontorama.OntoramaConfig;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 *
 * Test Graph created from QueryResult:
 * - number of nodes and _graphEdges are correct
 * - test a node for properties, clones and depth
 * - test an edge for inbound and outbound connections
 *
 * not tested methods:
 * - all private methods
 * - printXml method
 *
 * @todo how can we check if nodes that are created are what we expect...
 */

public class TestGraph extends TestCase {

    private Graph graph;

    List _nodesList = new LinkedList();
    List _edgesList = new LinkedList();

    private String typePropertyName = "Description";

    private String rootNodeDescr = "root element here";
    private String node1Descr = "ontType1 for node1";

    private Node node1;
    private Node node1_2;

    private static final String edgeName_subtype = "subtype";
    private static final String edgeName_similar = "similar";
    private static final String edgeName_reverse = "reverse";


    /**
     *
     */
    public TestGraph(String name) {
        super(name);
    }

    /**
     *
     * not sure how to test graph, so for now will test if there are nodes
     * and _graphEdges created as expected and if they have correct settings.   *
     */
    protected void setUp() throws NoSuchRelationLinkException,
            NoSuchPropertyException,
            NoTypeFoundInResultSetException {
        // set up some _graphEdges and nodes, so we can see if they are cleared properly
        Node tmpNode1 = new NodeImpl("tmpNode1");
        Node tmpNode2 = new NodeImpl("tmpNode2");
        Node tmpNode3 = new NodeImpl("tmpNode3");
        Edge tmpEdge1 = new EdgeImpl(tmpNode1, tmpNode2, OntoramaConfig.getRelationLinkDetails(edgeName_subtype));
        Edge tmpEdge2 = new EdgeImpl(tmpNode1, tmpNode3, OntoramaConfig.getRelationLinkDetails(edgeName_subtype));


        // create queryResult
        Query query = new Query("root");
        Node gn = new NodeImpl("root");
        Node gn1 = new NodeImpl("node1");
        Node gn2 = new NodeImpl("node2");
        Node gn3 = new NodeImpl("node3");
        Node gn4 = new NodeImpl("node1.1");
        Node gn5 = new NodeImpl("node1.2");
        // create ont types not traceble to root, so we can test
        // if GraphBuilder will ignore them or not. We will not include
        // these into ontTypesList as at the moment we are ignoring them.
        Node gn6 = new NodeImpl("node4");
        Node gn7 = new NodeImpl("node5");

        List prop1 = new LinkedList();
        prop1.add(rootNodeDescr);
        gn.setProperty(typePropertyName, prop1);

        List prop2 = new LinkedList();
        prop2.add(node1Descr);
        gn1.setProperty(typePropertyName,prop2);

        _nodesList.add(gn);
        _nodesList.add(gn1);
        _nodesList.add(gn2);
        _nodesList.add(gn3);
        _nodesList.add(gn4);
        _nodesList.add(gn5);
        _nodesList.add(gn6);
        _nodesList.add(gn7);

        Edge e = new EdgeImpl(gn, gn1, OntoramaConfig.getRelationLinkDetails(edgeName_subtype));
        Edge e1 = new EdgeImpl(gn, gn2, OntoramaConfig.getRelationLinkDetails(edgeName_similar));
        Edge e2 = new EdgeImpl(gn, gn3, OntoramaConfig.getRelationLinkDetails(edgeName_subtype));
        Edge e3 = new EdgeImpl(gn1, gn5, OntoramaConfig.getRelationLinkDetails(edgeName_similar));
        Edge e4 = new EdgeImpl(gn2, gn5, OntoramaConfig.getRelationLinkDetails(edgeName_subtype));
        Edge e5 = new EdgeImpl(gn6, gn7, OntoramaConfig.getRelationLinkDetails(edgeName_subtype));
        _edgesList.add(e);
        _edgesList.add(e1);
        _edgesList.add(e2);
        _edgesList.add(e3);
        _edgesList.add(e4);
        _edgesList.add(e5);

        QueryResult queryResult = new QueryResult(query, _nodesList, _edgesList);

        graph = new GraphImpl(queryResult);
        System.out.println("nodes list = " + graph.getNodesList());
        System.out.println("edges list = " + graph.getEdgesList());

        node1 = getNodeByName(graph.getNodesList(), "node1");
        node1_2 = getNodeByName(graph.getNodesList(), "node1.2");
    }

    /**
     * test graph root
     */
    public void testGraphRoot() throws NoSuchPropertyException {
        Node rootNode = graph.getRootNode();
        assertEquals("root", rootNode.getName());
        List propList = rootNode.getProperty(typePropertyName);
        assertEquals(rootNodeDescr, propList.get(0));
    }

    /**
     * test if nodes list is correct
     */
    public void testGetNodesList() {
        List nodesList = graph.getNodesList();
        assertEquals("number of nodes should equal number of ontTypes ",
                _nodesList.size(), nodesList.size());
    }

    /**
     * test properties, clones and depth for node1
     */
    public void testNode1() throws NoSuchPropertyException {
        List propList = node1.getProperty(typePropertyName);
        assertEquals("description property for node1", node1Descr, propList.get(0));
        assertEquals("depth of node1", 1, node1.getDepth());
        assertEquals("does node1 have clones", false, node1.hasClones());
    }

    /**
     * test clones and depth for node1_2
     */
    public void testNode1_2() {
        assertEquals("depth for node1_2", 2, node1_2.getDepth());
        assertEquals("does node1_2 have clones", true, node1_2.hasClones());
    }

    /**
     *
     */
    public void testEdgesSize() {
        List edgesList = graph.getEdgesList();
        assertEquals("5 _graphEdges in the graph", 5, edgesList.size());
    }

    /**
     * check if outbound _graphEdges for node1 are what they should be
     */
    public void testOutboundEdgesForNode1()  throws NoSuchRelationLinkException {
        assertEquals("outbound _graphEdges for node1 ", 1, graph.getOutboundEdgeNodesList(node1).size());

        Iterator outboundEdges = graph.getOutboundEdges(node1);
        while (outboundEdges.hasNext()) {
            Edge cur = (Edge) outboundEdges.next();
            if ((cur.getToNode().getName()).equals("node1.1")) {
                // should be edge to node1.1 with type 1
                assertEquals(OntoramaConfig.getRelationLinkDetails(edgeName_subtype), cur.getEdgeType());
            }
            if ((cur.getToNode().getName()).equals("node1.2")) {
                // should be edge to node1.2 with type2
                assertEquals(OntoramaConfig.getRelationLinkDetails(edgeName_similar), cur.getEdgeType());
            }
        }
    }

    /**
     * check inbound _graphEdges for node1
     */
    public void testInboundEdgesForNode1() throws NoSuchRelationLinkException {
        Iterator inboundEdges = graph.getInboundEdges(node1);
        assertEquals("inbound _graphEdges for node1", 1, graph.getInboundEdgeNodesList(node1).size());

        if (inboundEdges.hasNext()) {
            Edge inEdge = (Edge) inboundEdges.next();
            // should be edge from root with type 1
            assertEquals("root", inEdge.getFromNode().getName());
            assertEquals(OntoramaConfig.getRelationLinkDetails(edgeName_subtype), inEdge.getEdgeType());
        }
    }



    ///////////////////***** Helper methods *****///////////////////////

    /**
     *
     */
    private Node getNodeByName(List nodesList, String name) {
        Iterator it = nodesList.iterator();
        Node resultNode = null;
        while (it.hasNext()) {
            Node cur = (Node) it.next();
            if ((cur.getName()).equals(name)) {
                resultNode = cur;
            }
        }
        return resultNode;
    }
}