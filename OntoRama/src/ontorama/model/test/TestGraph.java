package ontorama.model.test;

import junit.framework.TestCase;
import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.query.QueryResult;
import ontorama.webkbtools.util.NoSuchPropertyException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.model.*;

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
 * - number of nodes and edges are correct
 * - test a node for properties, clones and depth
 * - test an edge for inbound and outbound connections
 *
 * not tested methods:
 * - all private methods
 * - printXml method
 *
 * @think how can we check if nodes that are created are what we expect...
 */

public class TestGraph extends TestCase {

    private Graph graph;

    private LinkedList ontTypesList;

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
     * and edges created as expected and if they have correct settings.   *
     */
    protected void setUp() throws NoSuchRelationLinkException,
            NoSuchPropertyException,
            NoTypeFoundInResultSetException {
        // set up some edges and nodes, so we can see if they are cleared properly
        GraphNode tmpNode1 = new GraphNode("tmpNode1");
        GraphNode tmpNode2 = new GraphNode("tmpNode2");
        GraphNode tmpNode3 = new GraphNode("tmpNode3");
        Edge tmpEdge1 = new Edge(tmpNode1, tmpNode2, 1);
        Edge tmpEdge2 = new Edge(tmpNode1, tmpNode3, 1);


        // create queryResult
        Query query = new Query("root");
        OntologyType ontType = new OntologyTypeImplementation("root");
        OntologyType ontType1 = new OntologyTypeImplementation("node1");
        OntologyType ontType2 = new OntologyTypeImplementation("node2");
        OntologyType ontType3 = new OntologyTypeImplementation("node3");
        OntologyType ontType4 = new OntologyTypeImplementation("node1.1");
        OntologyType ontType5 = new OntologyTypeImplementation("node1.2");
        // create ont types not traceble to root, so we can test
        // if GraphBuilder will ignore them or not. We will not include
        // these into ontTypesList as at the moment we are ignoring them.
        OntologyType ontType6 = new OntologyTypeImplementation("node4");
        OntologyType ontType7 = new OntologyTypeImplementation("node5");

        ontType.addRelationType(ontType1, 1);
        ontType.addRelationType(ontType2, 2);
        ontType.addRelationType(ontType3, 1);
        ontType1.addRelationType(ontType4, 1);
        ontType1.addRelationType(ontType5, 2);
        ontType2.addRelationType(ontType5, 1);
        ontType6.addRelationType(ontType7, 1);

//    LinkedList rootDescrPropValue = new LinkedList();
//    rootDescrPropValue.add(rootNodeDescr);
//
//    LinkedList node1DescrPropValue = new LinkedList();
//    node1DescrPropValue.add(node1DescrPropValue);
//
        ontType.addTypeProperty(typePropertyName, rootNodeDescr);
        ontType1.addTypeProperty(typePropertyName, node1Descr);

        ontTypesList = new LinkedList();
        ontTypesList.add(ontType);
        ontTypesList.add(ontType1);
        ontTypesList.add(ontType2);
        ontTypesList.add(ontType3);
        ontTypesList.add(ontType4);
        ontTypesList.add(ontType5);

        QueryResult queryResult = new QueryResult(query, ontTypesList.iterator());

        graph = new GraphImpl(queryResult);

        node1 = getNodeChildByName(graph.getRootNode(), "node1");
        node1_2 = getNodeChildByName(node1, "node1.2");
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

        // adding 1 to ontTypesList size to account for cloned node1.2
        assertEquals("number of nodes should equal number of ontTypes + 1",
                ontTypesList.size() + 1, nodesList.size());
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
        List edgesList = Edge.edges;
        assertEquals("6 edges in the graph", 6, edgesList.size());
    }

    /**
     * check if outbound edges for node1 are what they should be
     */
    public void testOutboundEdgesForNode1() {
        assertEquals("outbound edges for node1 ", 2, Edge.getOutboundEdgeNodesList(node1).size());

        Iterator outboundEdges = Edge.getOutboundEdges(node1);
        while (outboundEdges.hasNext()) {
            Edge cur = (Edge) outboundEdges.next();
            if ((cur.getToNode().getName()).equals("node1.1")) {
                // should be edge to node1.1 with type 1
                assertEquals(1, cur.getType());
            }
            if ((cur.getToNode().getName()).equals("node1.2")) {
                // should be edge to node1.2 with type2
                assertEquals(2, cur.getType());
            }
        }
    }

    /**
     * check inbound edges for node1
     */
    public void testInboundEdgesForNode1() {
        Iterator inboundEdges = Edge.getInboundEdges(node1);
        assertEquals("inbound edges for node1", 1, Edge.getInboundEdgeNodesList(node1).size());

        if (inboundEdges.hasNext()) {
            Edge inEdge = (Edge) inboundEdges.next();
            // should be edge from root with type 1
            assertEquals("root", inEdge.getFromNode().getName());
            assertEquals(1, inEdge.getType());
        }
    }

    ///////////////////***** Helper methods *****///////////////////////

    /**
     *
     */
    private GraphNode getNodeChildByName(GraphNode parent, String name) {
        Iterator outboundNodes = Edge.getOutboundEdgeNodes(parent);
        GraphNode resultNode = null;
        while (outboundNodes.hasNext()) {
            GraphNode cur = (GraphNode) outboundNodes.next();
            //System.out.println("---------cur name = " + cur.getName());
            if ((cur.getName()).equals(name)) {
                resultNode = cur;
            }
        }
        //System.out.println("resultNode = " + resultNode);
        return resultNode;
    }
}