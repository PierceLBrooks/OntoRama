package ontorama.backends;

import junit.framework.TestCase;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

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
 * Test Graph 
 * - number of nodes and edges are correct
 * - test a node for properties, clones and depth
 * - test an edge for inbound and outbound connections
 *
 * not tested methods:
 * - all private methods
 * - convertIntoTree
 * - testIfTree
 *
 * @think how can we check if nodes that are created are what we expect...
 */

public class TestGraph extends TestCase {

    private Graph graph;

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
		this.graph = new Graph();		
    }

    /**
     *
     * not sure how to test graph, so for now will test if there are nodes
     * and edges created as expected and if they have correct settings.   *
     */
    protected void setUp() throws NoSuchRelationLinkException,
            NoSuchRelationLinkException {
        // set up some edges and nodes, so we can see if they are cleared properly
        GraphNode tmpNode1 = new GraphNode("tmpNode1");
        GraphNode tmpNode2 = new GraphNode("tmpNode2");
        GraphNode tmpNode3 = new GraphNode("tmpNode3");
        EdgeObject tmpEdge1 = new EdgeObject(tmpNode1, tmpNode2, 1,"http://undefined.se");
        EdgeObject tmpEdge2 = new EdgeObject(tmpNode1, tmpNode3, 1,"http://undefined.se");
		
		this.graph.addNode(tmpNode1);
		this.graph.addNode(tmpNode2);
		this.graph.addNode(tmpNode3);
		
		this.graph.getEdge().addEdge(tmpEdge1);
		this.graph.getEdge().addEdge(tmpEdge2);
		
		GraphNode tmpNode10 = new GraphNode("root");
		GraphNode tmpNode11 = new GraphNode("node1");
		GraphNode tmpNode12 = new GraphNode("node2");
		GraphNode tmpNode13 = new GraphNode("node3");
		GraphNode tmpNode14 = new GraphNode("node1.1");
		GraphNode tmpNode15 = new GraphNode("node1.2");
		GraphNode tmpNode16 = new GraphNode("node4");
		GraphNode tmpNode17 = new GraphNode("node5");

		LinkedList tmpList = new LinkedList();
		tmpList.add(new String(rootNodeDescr));
		tmpNode10.setProperty(typePropertyName, "http://undefined.se",tmpList);
		tmpList = new LinkedList();
		tmpList.add(new String(node1Descr));
		tmpNode11.setProperty(typePropertyName, "http://undefined.se",tmpList);

		this.graph.addNode(tmpNode10);
		this.graph.addNode(tmpNode11);
		this.graph.addNode(tmpNode12);
		this.graph.addNode(tmpNode13);
		this.graph.addNode(tmpNode14);
		this.graph.addNode(tmpNode15);
		this.graph.addNode(tmpNode16);
		this.graph.addNode(tmpNode17);
		
		

		this.graph.addEdge(tmpNode10.getFullName(),tmpNode11.getFullName(),1,"http://undefined.se");
		this.graph.addEdge(tmpNode10.getFullName(),tmpNode12.getFullName(),2,"http://undefined.se");
		this.graph.addEdge(tmpNode10.getFullName(),tmpNode13.getFullName(),1,"http://undefined.se");
		this.graph.addEdge(tmpNode11.getFullName(),tmpNode14.getFullName(),1,"http://undefined.se");
		this.graph.addEdge(tmpNode11.getFullName(),tmpNode15.getFullName(),2,"http://undefined.se");
		this.graph.addEdge(tmpNode12.getFullName(),tmpNode15.getFullName(),1,"http://undefined.se");
		this.graph.addEdge(tmpNode16.getFullName(),tmpNode17.getFullName(),1,"http://undefined.se");
		this.graph.addEdge(tmpNode17.getFullName(),tmpNode16.getFullName(),1,"http://undefined.se");
		
		try {
			this.graph.setRoot("root");
		} catch (NoSuchGraphNodeException e) {
			System.err.println("Could not find the root node");
		}
		
        node1 = getNodeChildByName(graph.getRootNode(), "node1");
        node1_2 = getNodeChildByName(node1, "node1.2");
        
        this.graph.setDepth();
    }

    
    
    /**
     * test graph root
     */
    public void testGraphRoot() throws NoSuchRelationLinkException {
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
        //??? is the number 11 correct?!
        assertEquals("number of nodes should equal number of added nodes",
                11, nodesList.size());
    }

    /**
     * test properties, clones and depth for node1
     */
    public void testNode1() throws NoSuchRelationLinkException {
		//the model must be represented as a tree
		this.testConvertIntoTree();
		
        List propList = node1.getProperty(typePropertyName);
        assertEquals("description property for node1", node1Descr, propList.get(0));
        assertEquals("depth of node1", 1, node1.getDepth());
        assertEquals("does node1 have clones", false, node1.hasClones());
    }

    /**
     * test clones and depth for node1_2
     */
    public void testNode1_2() {
		//the model must be represented as a tree
		this.testConvertIntoTree();

        assertEquals("depth for node1_2", 2, node1_2.getDepth());
        assertEquals("does node1_2 have clones", true, node1_2.hasClones());
    }

    /**
     *
     */
    public void testEdgesSize() {
        int size = this.graph.getEdge().getIteratorSize(this.graph.getEdge().getEdgeIterator());
        assertEquals("9 edges in the graph", 10, size);
    }

    /**
     * check if outbound edges for node1 are what they should be
     */
    public void testOutboundEdgesForNode1() {
        assertEquals("outbound edges for node1 ", 2, this.graph.getEdge().getOutboundEdgeNodesList(node1).size());

        Iterator outboundEdges = this.graph.getEdge().getOutboundEdges(node1);
        while (outboundEdges.hasNext()) {
            EdgeObject cur = (EdgeObject) outboundEdges.next();
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
        Iterator inboundEdges = this.graph.getEdge().getInboundEdges(node1);
        assertEquals("inbound edges for node1", 1, this.graph.getEdge().getInboundEdgeNodesList(node1).size());

        if (inboundEdges.hasNext()) {
            EdgeObject inEdge = (EdgeObject) inboundEdges.next();
            // should be edge from root with type 1
            assertEquals("root", inEdge.getFromNode().getName());
            assertEquals(1, inEdge.getType());
        }
    }

    /**
     * test convertIntoTree
     */
    public void testConvertIntoTree() {
		
		try {
			this.graph.convertIntoTree(this.graph.getRootNode());
			assertEquals("This should be a tree", true, this.graph.testIfTree(this.graph.getRootNode()));

		} catch (NoSuchRelationLinkException e) {
			System.err.println("Error no such relationlink exception");
		}
		
    }

    ///////////////////***** Helper methods *****///////////////////////

    /**
     *
     */
    private GraphNode getNodeChildByName(GraphNode parent, String name) {
        Iterator outboundNodes = this.graph.getEdge().getOutboundEdgeNodes(parent);
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