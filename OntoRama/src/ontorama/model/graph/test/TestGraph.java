package ontorama.model.graph.test;import java.util.Iterator;import java.util.LinkedList;import java.util.List;import junit.framework.TestCase;import ontorama.OntoramaConfig;import ontorama.model.graph.Edge;import ontorama.model.graph.EdgeImpl;import ontorama.model.graph.Graph;import ontorama.model.graph.GraphImpl;import ontorama.model.graph.NoTypeFoundInResultSetException;import ontorama.model.graph.Node;import ontorama.model.graph.NodeImpl;import ontorama.model.graph.AddUnconnectedNodeIsDisallowedException;import ontorama.model.graph.GraphModificationException;import ontorama.ontotools.TestWebkbtoolsPackage;import ontorama.ontotools.NoSuchRelationLinkException;import ontorama.ontotools.query.Query;import ontorama.ontotools.query.QueryResult;import org.tockit.events.EventBroker;/** * <p>Title: </p> * <p>Description: </p> * <p>Copyright: Copyright (c) 2002</p> * <p>Company: DSTC</p> * @author nataliya * @version 1.0 * * Test Graph created from QueryResult: * - number of nodes and _graphEdges are correct * - test a node for properties, clones and depth * - test an edge for inbound and outbound connections * * not tested methods: * - all private methods * - printXml method * */public class TestGraph extends TestCase {    private ontorama.model.graph.Graph graph;    List _nodesList = new LinkedList();    List _edgesList = new LinkedList();    private ontorama.model.graph.Node node1;    private ontorama.model.graph.Node node1_2;    ontorama.model.graph.Node gn, gn1, gn2, gn3, gn4, gn5, gn31;    ontorama.model.graph.Edge e, e1, e2, e3, e4 ,e5, e31;    /**     *     */    public TestGraph(String name) {        super(name);    }    /**     *     * not sure how to test graph, so for now will test if there are nodes     * and _graphEdges created as expected and if they have correct settings.   *     */    protected void setUp() throws NoSuchRelationLinkException,            ontorama.model.graph.NoTypeFoundInResultSetException {        // create queryResult        Query query = new Query("root");        gn = new ontorama.model.graph.NodeImpl("root");        gn1 = new ontorama.model.graph.NodeImpl("node1");        gn2 = new ontorama.model.graph.NodeImpl("node2");        gn3 = new ontorama.model.graph.NodeImpl("node3");        gn4 = new ontorama.model.graph.NodeImpl("node1.1");        gn5 = new ontorama.model.graph.NodeImpl("node1.2");        gn31 = new ontorama.model.graph.NodeImpl("node3.1");        // create ont types not traceble to root, so we can test        // if GraphBuilder will ignore them or not        ontorama.model.graph.Node gn6 = new ontorama.model.graph.NodeImpl("node4");        ontorama.model.graph.Node gn7 = new ontorama.model.graph.NodeImpl("node5");        _nodesList.add(gn);        _nodesList.add(gn1);        _nodesList.add(gn2);        _nodesList.add(gn3);        _nodesList.add(gn4);        _nodesList.add(gn5);        _nodesList.add(gn6);        _nodesList.add(gn7);        _nodesList.add(gn31);        e = new ontorama.model.graph.EdgeImpl(gn, gn1,  OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype));        e1 = new ontorama.model.graph.EdgeImpl(gn, gn2, OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_similar));        e2 = new ontorama.model.graph.EdgeImpl(gn, gn3, OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype));        e3 = new ontorama.model.graph.EdgeImpl(gn1, gn5, OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_similar));        e4 = new ontorama.model.graph.EdgeImpl(gn2, gn5, OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype));        e5 = new ontorama.model.graph.EdgeImpl(gn6, gn7, OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype));        e31 = new ontorama.model.graph.EdgeImpl(gn3, gn31, OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype));        _edgesList.add(e);        _edgesList.add(e1);        _edgesList.add(e2);        _edgesList.add(e3);        _edgesList.add(e4);        _edgesList.add(e5);        _edgesList.add(e31);        QueryResult queryResult = new QueryResult(query, _nodesList, _edgesList);        graph = new ontorama.model.graph.GraphImpl(queryResult, new EventBroker());        System.out.println("nodes list = " + graph.getNodesList());        System.out.println("edges list = " + graph.getEdgesList());        node1 = getNodeByName(graph.getNodesList(), "node1");        node1_2 = getNodeByName(graph.getNodesList(), "node1.2");    }    /**     * test graph root     */    public void testGraphRoot() {        ontorama.model.graph.Node rootNode = graph.getRootNode();        assertEquals("root", rootNode.getName());    }    /**     * test if nodes list is correct     */    public void testGetNodesList() {        List nodesList = graph.getNodesList();        assertEquals("number of nodes should equal number of ontTypes ",                _nodesList.size(), nodesList.size());    }    /**     * test properties, clones and depth for node1     */    public void testNode1() {        assertEquals("depth of node1", 1, node1.getDepth());        assertEquals("does node1 have clones", false, node1.hasClones());    }    /**     * test clones and depth for node1_2     */    public void testNode1_2() {        assertEquals("depth for node1_2", 2, node1_2.getDepth());        assertEquals("does node1_2 have clones", true, node1_2.hasClones());    }    /**     *     */    public void testEdgesSize() {        List edgesList = graph.getEdgesList();        assertEquals("5 _graphEdges in the graph", _edgesList.size(), edgesList.size());    }    /**     * check if outbound _graphEdges for node1 are what they should be     */    public void testOutboundEdgesForNode1()  throws NoSuchRelationLinkException {        assertEquals("outbound _graphEdges for node1 ", 1, graph.getOutboundEdges(node1).size());        Iterator outboundEdges = graph.getOutboundEdges(node1).iterator();        while (outboundEdges.hasNext()) {            ontorama.model.graph.Edge cur = (ontorama.model.graph.Edge) outboundEdges.next();            if ((cur.getToNode().getName()).equals("node1.1")) {                // should be edge to node1.1 with type 1                assertEquals( OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype), cur.getEdgeType());            }            if ((cur.getToNode().getName()).equals("node1.2")) {                // should be edge to node1.2 with type2                assertEquals( OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_similar), cur.getEdgeType());            }        }    }    /**     * check inbound _graphEdges for node1     */    public void testInboundEdgesForNode1() throws NoSuchRelationLinkException {        Iterator inboundEdges = graph.getInboundEdges(node1).iterator();        assertEquals("inbound _graphEdges for node1", 1, graph.getInboundEdges(node1).size());        if (inboundEdges.hasNext()) {            ontorama.model.graph.Edge inEdge = (ontorama.model.graph.Edge) inboundEdges.next();            // should be edge from root with type 1            assertEquals("root", inEdge.getFromNode().getName());            assertEquals( OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype), inEdge.getEdgeType());        }    }    public void testAddNode()  throws GraphModificationException {        ontorama.model.graph.Node newNode = new ontorama.model.graph.NodeImpl("newNode");        try {            graph.addNode(newNode);            fail("Should raise AddUnconnectedNodeIsDisallowedException ");        }        catch (AddUnconnectedNodeIsDisallowedException e) {        }    }    public void testAddEdgeForGivenEdge () throws NoSuchRelationLinkException, GraphModificationException {        ontorama.model.graph.Node newNode = new ontorama.model.graph.NodeImpl("newNode");        ontorama.model.graph.Edge newEdge = new ontorama.model.graph.EdgeImpl(gn3, newNode, OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype));        graph.addEdge(newEdge);        testingAddEdge(newEdge);    }    public void testAddEdgeForGivenNodes () throws NoSuchRelationLinkException, GraphModificationException {        ontorama.model.graph.Node newNode = new ontorama.model.graph.NodeImpl("newNode");        graph.addEdge(gn3, newNode, OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype));        ontorama.model.graph.Edge edge = graph.getEdge(gn3, newNode, OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype));        assertEquals("graph should return a valid edge for the newly created edge, not null", true, (edge != null));    }//    public void testAddEdgeForExistingNodes () throws NoSuchRelationLinkException, GraphModificationException {//        Edge newEdge = new EdgeImpl(gn3, gn2, OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype));//        graph.addEdge(newEdge);//        // since gn3 is now having multiple parents - root and gn2, graph should//        // clone gn3 and we should end up with edges root->gn3 (already existing)//        // and g2->gn3 (cloned). also need to check if gn3's substructure has//        // been cloned properly//        Edge edge_root_gn2 = graph.getEdge(graph.getRootNode(), gn3, OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype));//        Edge edge_gn2_gn3 = graph.getEdge(gn2, gn3, OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype));//        assertEquals("expect to find edge from root to gn2 ", true, (edge_root_gn2 != null));//        assertEquals("expect to find edge from gn2 to gn3 ", true, (edge_gn2_gn3 != null) );////        assertEquals("gn3 should have a clone ", 1, gn3.getClones().size());//        assertEquals("gn31 should have a clone ", 1, gn31.getClones().size());//    }//    ///////////////////***** Helper methods *****///////////////////////    private void testingAddEdge (ontorama.model.graph.Edge edge) {        boolean edgeIsInGraph = graph.getEdgesList().contains(edge);        assertEquals("new edge should be in the graph edges list", true, edgeIsInGraph);        boolean toNodeIsInGraph = graph.getNodesList().contains(edge.getToNode());        boolean fromNodeIsInGraph = graph.getNodesList().contains(edge.getFromNode());        assertEquals("toNode " + edge.getToNode().getName() + " for new edge should be in the graph nodes list", true, toNodeIsInGraph);        assertEquals("fromNode " + edge.getFromNode().getName() + " for new edge should be in the graph nodes list", true, fromNodeIsInGraph);    }    /**     *     */    private ontorama.model.graph.Node getNodeByName(List nodesList, String name) {        Iterator it = nodesList.iterator();        ontorama.model.graph.Node resultNode = null;        while (it.hasNext()) {            ontorama.model.graph.Node cur = (ontorama.model.graph.Node) it.next();            if ((cur.getName()).equals(name)) {                resultNode = cur;            }        }        return resultNode;    }}