
package ontorama.tree.model;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import ontorama.model.Graph;
import ontorama.model.GraphNode;

public class OntoTreeBuilder {

    private static Hashtable ontoHash = new Hashtable();
    private Graph graph;

    /**
     *
     */
    public OntoTreeBuilder (Graph graph) {
        this.graph = graph;
        graphToOntoTree();
    }

    /**
     * Convert each GraphNode to OntoTreeNode
     */
    private void graphToOntoTree() {
        Iterator it = this.graph.iterator();
        while (it.hasNext()) {
            GraphNode curNode = (GraphNode) it.next();
            TreeNode treeNode = new OntoTreeNode (curNode);
            //System.out.println("graph node = " + curNode);
            //System.out.println("tree node = " + treeNode);
            ontoHash.put(curNode,treeNode);
        }
    }
    /**
     *
     */
    public static TreeNode getTreeNode (GraphNode graphNode) {
        TreeNode treeNode = (OntoTreeNode) ontoHash.get(graphNode);
        return treeNode;
    }
    /**
     *
     */
     /*
   public static GraphNode getGraphNode (TreeNode treeNode) {
        Enumeration en = ontoHash.keys();
        while (en.hasMoreElements()) {
            GraphNode graphNode = (GraphNode) en.nextElement();
            if ( treeNode.equals( (TreeNode) ontoHash.get(graphNode)) ) {
                return graphNode;
            }
        }
        return null;
    }
    */
}