package ontorama.test;

import junit.framework.*;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

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
      Edge.clearEdgesList();

      node1 = new GraphNode("node1");
      node2 = new GraphNode("node2");
      node3 = new GraphNode("node3");
      node4 = new GraphNode("node4");
      node5 = new GraphNode("node5");
      node6 = new GraphNode("node6");

      try {
          edge1 = new Edge(node1,node2,1);
          edge2 = new Edge(node1,node3,1);
          edge3 = new Edge(node1,node4,2);
          edge4 = new Edge(node1,node5,3);
          edge5 = new Edge(node2,node6,1);
          edge6 = new Edge(node3,node6,2);
          edge7 = new Edge(node4, node6, 3);

          // create relation links set
          relLinksSet = createSet(2, 3);

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
      assertEquals(7, Edge.edges.size());
    }

    /**
     *
     */
    public void testEdgeList () {
      List edgesList = Edge.edges;
      for (int i=0; i <= edgesList.size(); i++) {
        if (i == 0) {
          assertEquals(edge1, (Edge) Edge.edges.get(i));
          continue;
        }
        if (i == 1) {
          assertEquals(edge2, (Edge) Edge.edges.get(i));
          continue;
        }
        if (i == 2) {
          assertEquals(edge3, (Edge) Edge.edges.get(i));
          continue;
        }
        if (i == 3) {
          assertEquals(edge4, (Edge) Edge.edges.get(i));
          continue;
        }
        if (i == 4) {
          assertEquals(edge5, (Edge) Edge.edges.get(i));
          continue;
        }
        if (i == 5) {
          assertEquals(edge6, (Edge) Edge.edges.get(i));
          continue;
        }
        if (i == 6) {
          assertEquals(edge7, (Edge) Edge.edges.get(i));
          continue;
        }
      }
    }

    /**
     * test method getFromNode
     */
    public void testGetFromNode () {
      assertEquals (node1, edge1.getFromNode());
      assertEquals (node1, edge2.getFromNode());
      assertEquals (node1, edge3.getFromNode());
      assertEquals (node1, edge4.getFromNode());
      assertEquals (node2, edge5.getFromNode());
      assertEquals (node3, edge6.getFromNode());
      assertEquals (node4, edge7.getFromNode());
    }

    /**
     * test method getToNode
     */
    public void testGetToNode () {
      assertEquals(node2, edge1.getToNode());
      assertEquals(node3, edge2.getToNode());
      assertEquals(node4, edge3.getToNode());
      assertEquals(node5, edge4.getToNode());
      assertEquals(node6, edge5.getToNode());
      assertEquals(node6, edge6.getToNode());
      assertEquals(node6, edge7.getToNode());
    }

    /**
     * test method getType
     */
    public void testGetType () {
      assertEquals( 1, edge1.getType());
      assertEquals( 1, edge2.getType());
      assertEquals( 2, edge3.getType());
      assertEquals( 3, edge4.getType());
      assertEquals( 1, edge5.getType());
      assertEquals( 2, edge6.getType());
      assertEquals( 3, edge7.getType());
    }

    /**
     * test method getOutboundEdges
     */
    public void testGetOutboundEdges () {
      assertEquals(outboundEdgesListForNode1.size(), getIteratorSize(Edge.getOutboundEdges(node1)));
      compareListToIterator(outboundEdgesListForNode1, Edge.getOutboundEdges(node1));
    }

    /**
     * test method getInboundEdges
     */
    public void testGetInboundEdges () {
      assertEquals(inboundEdgesListForNode6.size(), getIteratorSize(Edge.getInboundEdges(node6)));
      compareListToIterator(inboundEdgesListForNode6, Edge.getInboundEdges(node6));
    }

    /**
     * test method getOutboundEdges (node, type)
     */
    public void testOutboundEdgesForRelationType () {
      assertEquals(outboundEdgesListForNode1Relation1.size(), getIteratorSize(Edge.getOutboundEdges(node1, 1)));
      compareListToIterator(outboundEdgesListForNode1Relation1, Edge.getOutboundEdges(node1, 1));
    }

    /**
     * test method getInboundEdges (node, type)
     */
    public void testInboundEdgesForRelationType() {
      assertEquals(inboundEdgesListForNode6Relation1.size(), getIteratorSize(Edge.getInboundEdges(node6, 1)) );
      compareListToIterator(inboundEdgesListForNode6Relation1, Edge.getInboundEdges(node6, 1));
    }

    /**
     * test method getOutboundEdgeNodes(GraphNode node, Set relationLinks)
     */
    public void testGetOutboundEdgeNodesForRelationLinksSet () {
      Iterator outboundNodes = Edge.getOutboundEdgeNodes(node1,relLinksSet);
      assertEquals(outboundNodesListForNode1RelLinkSet.size(), getIteratorSize( outboundNodes) );

      outboundNodes = Edge.getOutboundEdgeNodes(node1,relLinksSet);
      compareListToIterator(outboundNodesListForNode1RelLinkSet, outboundNodes);
    }

    /**
     * test method getInboundEdgeNodes(GraphNode node, Set relationLinks)
     */
    public void testGetInboundEdgeNodesForRelationLinksSet () {
      Iterator inboundNodes = Edge.getInboundEdgeNodes(node6, relLinksSet);
      assertEquals(inboundNodesListForNode6RelLinkSet.size(), getIteratorSize(inboundNodes));

      inboundNodes = Edge.getInboundEdgeNodes(node6, relLinksSet);
      compareListToIterator(inboundNodesListForNode6RelLinkSet, inboundNodes);
    }

    /**
     * test method getOutboundEdgeNodes (GraphNode node, int relationType)
     */
    public void testGetOutboundEdgeNodesForRelationType () {
      Iterator outboundNodes = Edge.getOutboundEdgeNodes(node1, 1);
      assertEquals(outboundNodesListForNode1Relation1.size(), getIteratorSize(outboundNodes) );

      outboundNodes = Edge.getOutboundEdgeNodes(node1, 1);
      compareListToIterator(outboundNodesListForNode1Relation1, outboundNodes);
    }

    /**
     * test method getInboundEdgeNodes (GraphNode node, int relationType)
     */
    public void testGetInboundEdgeNodesForRelationType () {
      Iterator inboundNodes = Edge.getInboundEdgeNodes(node6, 2);
      assertEquals(inboundNodesListForNode6Relation2.size(), getIteratorSize(inboundNodes));

      inboundNodes = Edge.getInboundEdgeNodes(node6, 2);
      compareListToIterator(inboundNodesListForNode6Relation2, inboundNodes);
    }

    /**
     * test method getOutboundEdgeNodes
     */
    public void testGetOutboundEdgeNodes () {
      Iterator outboundNodes = Edge.getOutboundEdgeNodes(node1);
      assertEquals(outboundNodesListForNode1.size(), getIteratorSize(outboundNodes));

      outboundNodes = Edge.getOutboundEdgeNodes(node1);
      compareListToIterator(outboundNodesListForNode1, outboundNodes);
    }

    /**
     * test method getInboundEdgeNodes
     */
    public void testGetInboundEdgeNodes () {
      Iterator inboundNodes = Edge.getInboundEdgeNodes(node6);
      assertEquals(inboundNodesListForNode6.size(), getIteratorSize(inboundNodes));

      inboundNodes = Edge.getInboundEdgeNodes(node6);
      compareListToIterator(inboundNodesListForNode6, inboundNodes);
    }

    /**
     * test method getOutboundEdgeNodesList
     */
    public void testGetOutboundEdgeNodesList () {
      Iterator outboundNodes = Edge.getOutboundEdgeNodes(node1);
      assertEquals(outboundNodesListForNode1.size(), getIteratorSize(outboundNodes) );

      outboundNodes = Edge.getOutboundEdgeNodes(node1);
      compareListToIterator(outboundNodesListForNode1, outboundNodes);
    }

    /**
     * test method getInboundEdgeNodesList
     */
    public void testGetInboundEdgeNodesList () {
      Iterator inboundNodes = Edge.getInboundEdgeNodes(node6);
      assertEquals(inboundNodesListForNode6.size(), getIteratorSize(inboundNodes));

      inboundNodes = Edge.getInboundEdgeNodes(node6);
      compareListToIterator(inboundNodesListForNode6, inboundNodes);
    }


    //////////////******* Helper methods ********////////////////////

    /**
     *
     */
    private int getIteratorSize (Iterator it) {
      return IteratorUtil.getIteratorSize(it);
    }

    /**
     * compare contents of given iterator and given list
     *
     * The idea is: if method we want to check returns an iterator,
     * we build list of expected objects and then go through iterator
     * and check if objects in corresponding places are equal
     */
    private void compareListToIterator (List list, Iterator iterator) {
      int count = 0;
      while (iterator.hasNext()) {
        Object cur = iterator.next();
        //System.out.println("list item = " + list.get(count) + ", iterator item = " + cur);
        assertEquals(list.get(count), cur);
        count++;
      }
    }

    /**
     * create a set of int's
     */
    private Set createSet (int int1, int int2) {
      Set set = new HashSet();
      set.add(new Integer(int1));
      set.add(new Integer(int2));
      return set;
    }
}
