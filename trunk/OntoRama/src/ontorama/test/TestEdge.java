package ontorama.test;

import junit.framework.*;


import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import ontorama.model.GraphNode;
import ontorama.model.Edge;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class TestEdge extends TestCase {

    private GraphNode node1;
    private GraphNode node2;
    private GraphNode node3;
    private GraphNode node4;
    private GraphNode node5;

    private Edge edge1;
    private Edge edge2;
    private Edge edge3;
    private Edge edge4;

    /**
     *
     */
    public TestEdge(String name) {
        super(name);
    }

	/**
	 *
	 */
	public static Test suite() {
	  TestSuite suite= new TestSuite();
	  suite.addTest(new TestEdge("testEdgeListSize"));
	  suite.addTest(new TestEdge("testEdge1"));
	  suite.addTest(new TestEdge("testEdge4"));
	  return suite;
	}


    /**
     *
     */
    protected void setUp() {
		Edge.clearEdgesList();

        node1 = new GraphNode("node1");
        node2 = new GraphNode("node2");
        node3 = new GraphNode("node3");
        node4 = new GraphNode("node4");
        node5 = new GraphNode("node5");

        try {
            edge1 = new Edge(node1,node2,1);
            edge2 = new Edge(node1,node3,1);
            edge3 = new Edge(node1,node4,2);
            edge4 = new Edge(node1,node5,3);
        }
        catch (NoSuchRelationLinkException e) {
            System.err.println("NoSuchRelationLinkException: " + e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * test number of edges in the list
     */
    public void testEdgeListSize() {
        assertEquals(4, Edge.edges.size());
    }

    /**
     * test first edge in the list
     */
    public void testEdge1() {
        assertEquals(node1, ( (Edge) Edge.edges.get(0)).getFromNode());
        assertEquals(node2, ( (Edge) Edge.edges.get(0)).getToNode());
        assertEquals(1, ( (Edge) Edge.edges.get(0)).getType());
    }

    /**
     * test last edge4 in the list
     */
    public void testEdge4 () {
        assertEquals(node1, ( (Edge) Edge.edges.get(3)).getFromNode());
        assertEquals(node5, ( (Edge) Edge.edges.get(3)).getToNode());
        assertEquals(3, ( (Edge) Edge.edges.get(3)).getType());
    }

    /**
     *
     */

    /**
     * @make this automated
     */
     /*
    public void testEdgeList () {
        List edgesList = Edge.edges;
        Iterator it = edgesList.iterator();
        while (it.hasNext()) {
            Edge cur = (Edge) it.next();
            System.out.println("---" + cur);
        }
    }
    */

    /**
     *
     */
     public void printIterator (Iterator it) {
        while (it.hasNext()) {
            Object obj = it.next();
            System.out.println(obj);
        }
     }
}
