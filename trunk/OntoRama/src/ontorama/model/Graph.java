package ontorama.model;

/**
 * Graph takes an acrylic graph and converts it into a tree.
 * Graph removers all duplicate parents by cloning the children
 *
 * Graph holds the root node of the graph, and a list of nodes in the graph.
 */


import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Hashtable;

import java.util.ConcurrentModificationException;

import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.util.Debug;


public class Graph implements GraphInterface {

    private List treeNodes = new LinkedList ();
    private GraphNode root;
    private GraphNode edgeRoot;

    /**
     * debug vars
     */
     Debug debug = new Debug(false);


    // for debugging purposes only, remove later!!!
    private Hashtable printedGraphNodes;

    //public Graph(Collection graphNodes, GraphNode root, GraphNode edgeRoot) {
    public Graph( GraphNode edgeRoot) {
        //this.nodes = nodes;
        //this.root = root;

        //this.root = root;
        this.edgeRoot = edgeRoot;

        // debug
        Iterator edges = Edge.edges.iterator();
        while (edges.hasNext()) {
            Edge cur = (Edge) edges.next();
            debug.message("Graph","constructor","*****" + cur);
            if ( (cur.getFromNode().getName().equals("node3"))  || (cur.getToNode().getName().equals("node3")) ) {
                GraphNode node = cur.getFromNode();
                Iterator it = Edge.getOutboundEdgeNodes(node);
                debug.message("Graph","constructor","\t" + cur.getFromNode());
                debug.message("Graph","constructor","\t it.hasNext = " + it.hasNext());
            }
        }


        //System.out.println("-------graph size: " + graphNodes.size() + "------------------------------");

        //convertIntoTree();
        //System.out.println("converted graph into tree");
        //makeTreeList(root);

        // calculate the depth of each node
        //root.calculateDepths();


        //System.out.println("-------check if tree returned: " + checkIfTree(root) + "------------------------------");
        //System.out.println("-------tree size: " + getSize() + "------------------------------");

//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
        System.out.println("before convertIntoTree testIfTree(): " + testIfTree(edgeRoot));
        System.out.println("before convertIntoTree number of edges: " + Edge.getIteratorSize( Edge.edges.iterator()));

        try {
            convertIntoTree(edgeRoot);
            edgeRoot.calculateDepths();
        }
        catch (NoSuchRelationLinkException e ) {
            System.out.println("NoSuchRelationLinkException: " + e.getMessage());
            System.exit(-1);
        }
        System.out.println("after convertIntoTree testIfTree(): " + testIfTree(edgeRoot));
        System.out.println("after convertIntoTree number of edges: " + Edge.getIteratorSize( Edge.edges.iterator()));
        System.out.println();
        System.out.println();
        System.out.println();
    }

    /**
     * Returns the root node of the graph.
     */
//    public GraphNode getRootNode() {
//        return root;
//    }

    /**
     * Returns the root node of the graph.
     */
    //public GraphNode getEdgeRootNode() {
    public GraphNode getRootNode() {
        return edgeRoot;
    }

    /**
     * Returns and iterator of nodes.
     */
//    public Iterator iterator() {
//        return treeNodes.iterator();
//    }

    /**
     * Return collection of nodes
     */
    public List getList() {
        return treeNodes;
    }

    /**
     * Returns size of the graph (number of nodes)
     */
    public int getSize () {
        return treeNodes.size();
    }

//    /**
//     * Removes all duplicate parents by cloning the children.
//     */
//    private void convertIntoTree() {
//        List queue = new LinkedList();
//        queue.add(root);
//        while( !queue.isEmpty() ) {
//            GraphNode cur = (GraphNode)queue.remove(0);
//            List childrenList = cur.getChildrenList();
//            int count = 0;
//            int size = childrenList.size();
//            while( count < size ) {
//                GraphNode child = (GraphNode)childrenList.get( count );
//                if(child.getNumberOfParents() > 1) {
//                    //System.out.println("node with multiple parents: " + child);
//                    GraphNode clone = child.deepCopy();
//                    cur.removeChild( child );
//                    cur.addChild( clone );
//                    child.removeParent(cur);
//                    clone.setParent(cur);
//                }
//                count++;
//                queue.add( child );
//            }
//        }
//    }

//    /**
//     * Build a list of nodes including all clones.
//     * At this stage we should have a tree
//     * @param   root
//     * @return  -
//     */
//     public void makeTreeList (GraphNode root) {
//
//        Iterator it = root.getChildrenIterator();
//        treeNodes.add(root);
//        while (it.hasNext()) {
//            GraphNode child = (GraphNode) it.next();
//            makeTreeList(child);
//        }
//     }

//    /**
//     * debug method
//     */
//    private void printTree (GraphNode node) {
//
//      Iterator children = node.getChildrenIterator();
//      int depth = node.getDepth();
//      String tabs = "";
//      for(int i = 0; i < depth; i++) {
//        tabs = tabs + "\t";
//      }
//      tabs = tabs + "> ";
//      System.out.println(tabs + node.getName());
//      while (children.hasNext()) {
//        GraphNode child = (GraphNode) children.next();
//        printTree(child);
//
//      }
//    }

//    /**
//     *
//     */
//    private void printXmlTree (GraphNode node) {
//
//      printedGraphNodes = new Hashtable ();
//
//      System.out.println("<ontology top=\"" + node.getName().replace('#','_') + "\">");
//      printXmlGraphNode (node);
//      System.out.println("</ontology>");
//    }

//    /**
//     * debug method
//     */
//    private void printXmlGraphNode (GraphNode node) {
//
//      String tabs = "\t";
//
//      //if ( printedGraphNodes.containsKey(node)) {
//       // return;
//      //}
//      System.out.println(tabs + "<node name=\"" + node.getName().replace('#','_') + "\">");
//      printedGraphNodes.put(node,node);
//
//      Iterator children = node.getChildrenIterator();
//      while (children.hasNext()) {
//        GraphNode child = (GraphNode) children.next();
//        System.out.println ( tabs + tabs + "<child name=\"" + child.getName().replace('#','_') + "\"/>");
//      }
//      System.out.println(tabs + "</node>");
//      children = node.getChildrenIterator();
//      while (children.hasNext()) {
//        GraphNode child = (GraphNode) children.next();
//        if ( (child.getChildrenList()).size() > 0) {
//          printXmlGraphNode(child);
//        }
//      }
//    }

//    /**
//     * debug method
//     */
//    private boolean checkIfTree (GraphNode node) {
//      boolean isTree = true;
//      Iterator children = node.getChildrenIterator();
//      while (children.hasNext()) {
//        GraphNode child = (GraphNode) children.next();
//        isTree = checkIfTree (child);
//        if(child.getNumberOfParents() > 1) {
//          System.out.println("GraphNode with more than one parent: " + child);
//          return false;
//        }
//      }
//      return isTree;
//    }

    /**
     *
     */
    private boolean testIfTree (GraphNode root) {
        LinkedList queue = new LinkedList();
        boolean isTree = true;

        queue.add(root);

        while ( !queue.isEmpty()) {
            GraphNode nextQueueNode = (GraphNode) queue.remove(0);
            Iterator allOutboundNodes = Edge.getOutboundEdgeNodes(nextQueueNode);

            while (allOutboundNodes.hasNext()) {
                GraphNode curNode = (GraphNode) allOutboundNodes.next();
                queue.add(curNode);

                Iterator inboundEdges = Edge.getInboundEdges(curNode);
                int count = 0;
                while (inboundEdges.hasNext()) {
                    count++;
                    inboundEdges.next();
                }
                if (count > 1) {
                    isTree = false;
                }
            }
        }
        return isTree;
    }

    /**
     *
     */
    private void convertIntoTree (GraphNode root) throws NoSuchRelationLinkException {
        LinkedList queue = new LinkedList();
        boolean isTree = true;

        queue.add(root);

        while ( !queue.isEmpty()) {
            GraphNode nextQueueNode = (GraphNode) queue.remove(0);

            debug.message("Graph", "convertIntoTree", "--- processing node " + nextQueueNode.getName() + " -----");

            Iterator allOutboundNodes = Edge.getOutboundEdgeNodes(nextQueueNode);

            while (allOutboundNodes.hasNext()) {
                GraphNode curNode = (GraphNode) allOutboundNodes.next();
                queue.add(curNode);
                int[] typeCount = getTypeCount(Edge.getInboundEdges(curNode));
                for (int i = 0; i < typeCount.length; i++) {
                    while (typeCount[i] > 1) {
                        debug.message("Graph", "convertIntoTree", "current node " + curNode.getName() + " has multiple inbound edges, num = " + typeCount[i] + " for type = " + i);

                        // clone the node
                        GraphNode cloneNode = curNode.makeClone();

                        // add edge from cloneNode to a NodeParent with this rel type and
                        // remove edge from curNode to a NodeParent with this rel type

                        Iterator inboundEdges = Edge.getInboundEdges(curNode, i);
                        if (inboundEdges.hasNext()) {
                            Edge firstEdge = (Edge) inboundEdges.next();
                            Edge newEdge = new Edge (firstEdge.getFromNode(), cloneNode, i);
                            Edge.removeEdge(firstEdge);
                        }

                        // copy/clone all structure below
                        deepCopy(curNode, cloneNode);

                        // indicate that we processed one edge
                        typeCount[i]--;
                    }
                }

            }
        }
    }


    /**
     *
     */
    private void deepCopy (GraphNode node, GraphNode cloneNode ) throws NoSuchRelationLinkException {

        Iterator outboundEdgesIterator = Edge.getOutboundEdges(node);

        while (outboundEdgesIterator.hasNext()) {
            Edge curEdge = (Edge) outboundEdgesIterator.next();
            GraphNode toNode = curEdge.getToNode();
            GraphNode cloneToNode = toNode.makeClone();
            Edge newEdge = new Edge(cloneNode,cloneToNode,curEdge.getType());
            deepCopy(toNode, cloneToNode);
        }
    }

    /**
     *
     */
    private int[] getTypeCount (Iterator it) {
        int[] typeCount = new int[ontorama.OntoramaConfig.getRelationLinksSet().size()];
        for (int i = 0; i < typeCount.length; i++ ) {
            typeCount[i] = 0;
        }

        while (it.hasNext()) {
            Edge curEdge = (Edge) it.next();
            int type = curEdge.getType();
            typeCount[type]++;
        }
        return typeCount;
    }

}