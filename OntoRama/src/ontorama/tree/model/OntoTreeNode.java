

package ontorama.tree.model;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Enumeration;
import java.util.Collection;

import javax.swing.tree.TreeNode;

import ontorama.model.GraphNode;
import ontorama.model.NodeObserver;

/**
 * Basic GraphNode for ontology viewers.
 */
public class OntoTreeNode implements TreeNode, NodeObserver {

    /**
     * Set to true if this node can have children
     * ( required by methods we need to implement for TreeNode interface)
     */
    private boolean allowChildren = true;

    /**
     *
     */
    private GraphNode graphNode;

    /**
     *
     */
    public OntoTreeNode ( GraphNode graphNode ) {
        this.graphNode = graphNode;
        this.graphNode.addObserver(this);
    }


    /**
     * Update method called from observable (GraphNode)
     */
    public void update( Object obj ) {
        System.out.println("OntoTreeNode method update: " + this.graphNode.getName());
    }

    /**
     *
     */
    public void hasFocus() {
        this.graphNode.hasFocus();
    }

    //////////////Implementation of TreeNode interface methods//////////////

    /**
     * Returns the child TreeNode at index childIndex.
     * @param childIndex
     * @return  child TreeNode
     */
    public TreeNode getChildAt(int childIndex) {
        //List childrenList = this.graphNode.getChildrenList();

        GraphNode graphNode = OntoTreeBuilder.getGraphNode(this);
        List childrenList = graphNode.getChildrenList();

        GraphNode childGraphNode = (GraphNode) childrenList.get(childIndex);
        TreeNode childNode = OntoTreeBuilder.getTreeNode(childGraphNode);
        //System.out.println("getChildAt(index): , node = " + graphNode.getName() + " returning " + childNode + " for index " + childIndex );
        return childNode;
    }

    /**
     * Returns the number of children TreeNodes the receiver contains.
     * @param -
     * @return  int
     * @todo  remove getNumberOfChildren method
     */
    public int getChildCount() {
        //List childrenList = this.graphNode.getChildrenList();

        GraphNode graphNode = OntoTreeBuilder.getGraphNode(this);
        List childrenList = graphNode.getChildrenList();

        //this.graphNode.hasFocus();

        //System.out.println("getChildCount(): , node = " + graphNode.getName() + " returning "  + childrenList.size());

        return childrenList.size();
    }

    /**
     * Returns the parent TreeNode of the receiver.
     * This method is provided for TreeNode implementation, and should
     * be used ONLY after graph is converted into tree.
     * Since we assuming that we are dealing with a tree - we return first node
     * from parents list
     * @return parent TreeNode
     */
    public TreeNode getParent() {
        //Iterator parentsIterator = this.graphNode.getParents();

        GraphNode graphNode = OntoTreeBuilder.getGraphNode(this);
        Iterator parentsIterator = graphNode.getParents();



        if (parentsIterator.hasNext()) {
            GraphNode parentGraphNode = (GraphNode) parentsIterator.next();
            //System.out.println("getParent(): , node = " + graphNode.getName() + " returning "  + (TreeNode) OntoTreeBuilder.getTreeNode(parentGraphNode));

            return ( (TreeNode) OntoTreeBuilder.getTreeNode(parentGraphNode));
        }
        //System.out.println("getParent(): , node = " + graphNode.getName() + " returning null");

        return null;
    }

    /**
     * Returns the index of node in the receivers children.
     * @param node
     * @return int index. If the receiver does not contain node, -1 will be returned.
     * @todo  not sure if implemented correctly - terminology in java TreeNode
     * interface is confusing (what do they mean by receiver? ). Assumed
     * that by 'receiver' they mean this node.
     */
    public int getIndex(TreeNode node) {
        //List childrenList = this.graphNode.getChildrenList();

        GraphNode graphNode = OntoTreeBuilder.getGraphNode(this);
        List childrenList = graphNode.getChildrenList();

        //System.out.println("getIndex(TreeNode): , node = " + graphNode.getName() +
        //            " returning "  + childrenList.indexOf(node) +
        //            " for TreeNode " +  (OntoTreeBuilder.getGraphNode(node)).getName());
        return childrenList.indexOf(node);
    }

    /**
     * Returns true if the receiver allows children.
     */
    public boolean getAllowsChildren() {
        GraphNode graphNode = OntoTreeBuilder.getGraphNode(this);
        //System.out.println("getAllowsChildren(): , node = " + graphNode.getName() +
        //            " returning "  + allowChildren);
      return allowChildren;
    }

    /**
     * Returns true if the receiver is a leaf.
     * @param -
     * @return  true if node is a leaf, false otherwise
     */
    public boolean isLeaf() {
        GraphNode graphNode = OntoTreeBuilder.getGraphNode(this);
      if (this.getChildCount() < 0 ) {
        //System.out.println("isLeaf(): , node = " + graphNode.getName() +
        //            " returning true");
        return true;
      }
        //System.out.println("isLeaf(): , node = " + graphNode.getName() +
        //            " returning false");

      return false;
    }

    /**
     * Returns the children of the reciever as an Enumeration.
     */
    public Enumeration children() {
        GraphNode graphNode = OntoTreeBuilder.getGraphNode(this);
      Enumeration childrenEnum = (Enumeration) ( graphNode.getChildrenList());
        //System.out.println("children(): , node = " + graphNode.getName() +
        //            " returning" + childrenEnum);

      return childrenEnum;
    }

    ///////////////End of TreeNode interface implementation/////////////////

    /**
     * Get GraphNode that is a part of this OntoTreeNode
     */
    //public GraphNode getGraphNode () {
    //    return this.graphNode;
    //}

    /**
     * toString
     */
    public String toString () {
        GraphNode graphNode = OntoTreeBuilder.getGraphNode(this);
        return graphNode.getName();
        //String str = "OntoTreeNode: " + this.graphNode.toString();
        //return this.graphNode.getName();
    }

}