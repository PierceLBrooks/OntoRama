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

    private GraphNode node1;
    private GraphNode node1_2;


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
        GraphNode tmpNode1 = new GraphNode("tmpNode1");
        GraphNode tmpNode2 = new GraphNode("tmpNode2");
        GraphNode tmpNode3 = new GraphNode("tmpNode3");
        EdgeIterface tmpEdge1 = new EdgeImpl(tmpNode1, tmpNode2, OntoramaConfig.getRelationLinkDetails()[1]);
        EdgeIterface tmpEdge2 = new EdgeImpl(tmpNode1, tmpNode3, OntoramaConfig.getRelationLinkDetails()[1]);


        // create queryResult
        Query query = new Query("root");
        GraphNode gn = new GraphNode("root");
        GraphNode gn1 = new GraphNode("node1");
        GraphNode gn2 = new GraphNode("node2");
        GraphNode gn3 = new GraphNode("node3");
        GraphNode gn4 = new GraphNode("node1.1");
        GraphNode gn5 = new GraphNode("node1.2");
        // create ont types not traceble to root, so we can test
        // if GraphBuilder will ignore them or not. We will not include
        // these into ontTypesList as at the moment we are ignoring them.
        GraphNode gn6 = new GraphNode("node4");
        GraphNode gn7 = new GraphNode("node5");

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

        EdgeIterface e = new EdgeImpl(gn, gn1, OntoramaConfig.getRelationLinkDetails()[1]);
        EdgeIterface e1 = new EdgeImpl(gn, gn2, OntoramaConfig.getRelationLinkDetails()[2]);
        EdgeIterface e2 = new EdgeImpl(gn, gn3, OntoramaConfig.getRelationLinkDetails()[1]);
        EdgeIterface e3 = new EdgeImpl(gn1, gn5, OntoramaConfig.getRelationLinkDetails()[2]);
        EdgeIterface e4 = new EdgeImpl(gn2, gn5, OntoramaConfig.getRelationLinkDetails()[1]);
        EdgeIterface e5 = new EdgeImpl(gn6, gn7, OntoramaConfig.getRelationLinkDetails()[1]);
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
        GraphNode rootNode = graph.getRootNode();
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
    public void testOutboundEdgesForNode1() {
        assertEquals("outbound _graphEdges for node1 ", 1, graph.getOutboundEdgeNodesList(node1).size());

        Iterator outboundEdges = graph.getOutboundEdges(node1);
        while (outboundEdges.hasNext()) {
            EdgeIterface cur = (EdgeIterface) outboundEdges.next();
            if ((cur.getToNode().getName()).equals("node1.1")) {
                // should be edge to node1.1 with type 1
                assertEquals(OntoramaConfig.getRelationLinkDetails()[1], cur.getEdgeType());
            }
            if ((cur.getToNode().getName()).equals("node1.2")) {
                // should be edge to node1.2 with type2
                assertEquals(OntoramaConfig.getRelationLinkDetails()[2], cur.getEdgeType());
            }
        }
    }

    /**
     * check inbound _graphEdges for node1
     */
    public void testInboundEdgesForNode1() {
        Iterator inboundEdges = graph.getInboundEdges(node1);
        assertEquals("inbound _graphEdges for node1", 1, graph.getInboundEdgeNodesList(node1).size());

        if (inboundEdges.hasNext()) {
            EdgeIterface inEdge = (EdgeIterface) inboundEdges.next();
            // should be edge from root with type 1
            assertEquals("root", inEdge.getFromNode().getName());
            assertEquals(OntoramaConfig.getRelationLinkDetails()[1], inEdge.getEdgeType());
        }
    }



    ///////////////////***** Helper methods *****///////////////////////

    /**
     *
     */
    private GraphNode getNodeByName(List nodesList, String name) {
        Iterator it = nodesList.iterator();
        GraphNode resultNode = null;
        while (it.hasNext()) {
            GraphNode cur = (GraphNode) it.next();
            if ((cur.getName()).equals(name)) {
                resultNode = cur;
            }
        }
        return resultNode;
    }
}