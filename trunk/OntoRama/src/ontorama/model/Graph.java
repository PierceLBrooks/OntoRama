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


public class Graph implements GraphInterface {

    private Collection nodes;
    private GraphNode root;

    // for debugging purposes only, remove later!!!
    private Hashtable printedGraphNodes;

    public Graph(Collection nodes, GraphNode root) {
        this.nodes = nodes;
        this.root = root;

        System.out.println("-------------------------------------");
        System.out.println("--------------tree-----------------------");
        printXmlTree(root);
        System.out.println("-------------------------------------");
        System.out.println("-------------------------------------");

        convertIntoTree();
        // calculate the depth of each node
        root.calculateDepths();

        System.out.println("-------check if tree returned: " + checkIfTree(root) + "------------------------------");

    }

    /**
     * Returns the root noed of the graph.
     */
    public GraphNode getRootNode() {
        return root;
    }

    /**
     * Returns and iterator of nodes.
     */
    public Iterator iterator() {
        return nodes.iterator();
    }

    /**
     * Test method for printing out all nodes.
     */
    //public void printGraphNodeNames() {
    //    if( root != null ) {
    //        root.printGraphNodeNames();
    //    }
    //}

    /**
     * Removes all duplicate parents by cloning the children.
     */
    private void convertIntoTree() {
        List queue = new LinkedList();
        queue.add(root);
        while( !queue.isEmpty() ) {
            GraphNode cur = (GraphNode)queue.remove(0);
            List childrenList = cur.getChildrenList();
            int count = 0;
            int size = childrenList.size();
            while( count < size ) {
                GraphNode child = (GraphNode)childrenList.get( count );
                if(child.getNumberOfParents() > 1) {
                  //System.out.println("node with multiple parents: " + child);
                    GraphNode clone = child.deepCopy();
                    cur.removeChild( child );
                    cur.addChild( clone );
                    child.removeParent(cur);
                    clone.setParent(cur);
                }
                count++;
                queue.add( child );
            }
        }
    }

    /**
     * debug method
     */
    private void printTree (GraphNode node) {

      Iterator children = node.getChildrenIterator();
      int depth = node.getDepth();
      String tabs = "";
      for(int i = 0; i < depth; i++) {
        tabs = tabs + "\t";
      }
      tabs = tabs + "> ";
      System.out.println(tabs + node.getName());
      while (children.hasNext()) {
        GraphNode child = (GraphNode) children.next();
        printTree(child);

      }
    }

    private void printXmlTree (GraphNode node) {

      printedGraphNodes = new Hashtable ();

      System.out.println("<ontology top=\"" + node.getName().replace('#','_') + "\">");
      printXmlGraphNode (node);
      System.out.println("</ontology>");
    }

    /**
     * debug method
     */
    private void printXmlGraphNode (GraphNode node) {

      String tabs = "\t";

      //if ( printedGraphNodes.containsKey(node)) {
       // return;
      //}
      System.out.println(tabs + "<node name=\"" + node.getName().replace('#','_') + "\">");
      printedGraphNodes.put(node,node);

      Iterator children = node.getChildrenIterator();
      while (children.hasNext()) {
        GraphNode child = (GraphNode) children.next();
        System.out.println ( tabs + tabs + "<child name=\"" + child.getName().replace('#','_') + "\"/>");
      }
      System.out.println(tabs + "</node>");
      children = node.getChildrenIterator();
      while (children.hasNext()) {
        GraphNode child = (GraphNode) children.next();
        if ( (child.getChildrenList()).size() > 0) {
          printXmlGraphNode(child);
        }
      }
    }

    /**
     * debug method
     */
    private boolean checkIfTree (GraphNode node) {
      boolean isTree = true;
      Iterator children = node.getChildrenIterator();
      while (children.hasNext()) {
        GraphNode child = (GraphNode) children.next();
        isTree = checkIfTree (child);
        if(child.getNumberOfParents() > 1) {
          System.out.println("GraphNode with more than one parent: " + child);
          return false;
        }

      }
      return isTree;
    }


}