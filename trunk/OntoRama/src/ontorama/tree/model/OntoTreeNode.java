

package ontorama.tree.model;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Enumeration;
import java.util.Collection;

import javax.swing.tree.TreeNode;

import ontorama.model.GraphNode;

/**
 * Basic GraphNode for ontology viewers.
 */
public class OntoTreeNode implements TreeNode {

    /**
     * Set to true if this node can have children
     * ( required by methods we need to implement for TreeNode interface)
     */
    private boolean allowChildren = true;

    /**
     *
     */
    private GraphNode graphNode;

    public OntoTreeNode ( GraphNode graphNode ) {
        this.graphNode = graphNode;
    }


    /**
     * Implementation of TreeNode interface methods
     */
    /**
     * Returns the child TreeNode at index childIndex.
     * @param childIndex
     * @return  child TreeNode
     */
    public TreeNode getChildAt(int childIndex) {
        List childrenList = this.graphNode.getChildrenList();
        GraphNode childGraphNode = (GraphNode) childrenList.get(childIndex);
        TreeNode childNode = OntoTreeBuilder.getTreeNode(childGraphNode);
        System.out.println("getChildAt(index): , node = " + this.graphNode.getName() + " returning " + childNode + " for index " + childIndex );
        return childNode;
    }

    /**
     * Returns the number of children TreeNodes the receiver contains.
     * @param -
     * @return  int
     * @todo  remove getNumberOfChildren method
     */
    public int getChildCount() {
        List childrenList = this.graphNode.getChildrenList();
        System.out.println("getChildCount(): , node = " + this.graphNode.getName() + " returning "  + childrenList.size());

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
        Iterator parentsIterator = this.graphNode.getParents();
        if (parentsIterator.hasNext()) {
            GraphNode parentGraphNode = (GraphNode) parentsIterator.next();
            System.out.println("getParent(): , node = " + this.graphNode.getName() + " returning "  + (TreeNode) OntoTreeBuilder.getTreeNode(parentGraphNode));

            return ( (TreeNode) OntoTreeBuilder.getTreeNode(parentGraphNode));
        }
        System.out.println("getParent(): , node = " + this.graphNode.getName() + " returning null");

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
        List childrenList = this.graphNode.getChildrenList();
        System.out.println("getIndex(TreeNode): , node = " + this.graphNode.getName() +
                    " returning "  + childrenList.indexOf(node) +
                    " for TreeNode " + ((OntoTreeNode) node).getGraphNode().getName());
        return childrenList.indexOf(node);
    }

    /**
     * Returns true if the receiver allows children.
     */
    public boolean getAllowsChildren() {
        System.out.println("getAllowsChildren(): , node = " + this.graphNode.getName() +
                    " returning "  + allowChildren);
      return allowChildren;
    }

    /**
     * Returns true if the receiver is a leaf.
     * @param -
     * @return  true if node is a leaf, false otherwise
     */
    public boolean isLeaf() {
      if (this.getChildCount() < 0 ) {
        System.out.println("isLeaf(): , node = " + this.graphNode.getName() +
                    " returning true");
        return true;
      }
        System.out.println("isLeaf(): , node = " + this.graphNode.getName() +
                    " returning false");

      return false;
    }

    /**
     * Returns the children of the reciever as an Enumeration.
     */
    public Enumeration children() {
      Enumeration childrenEnum = (Enumeration) ( this.graphNode.getChildrenList());
        System.out.println("children(): , node = " + this.graphNode.getName() +
                    " returning" + childrenEnum);

      return childrenEnum;
    }

    /**
     * Get GraphNode that is a part of this OntoTreeNode
     */
    public GraphNode getGraphNode () {
        return this.graphNode;
    }

    /**
     * toString
     */
    public String toString () {
        //String str = "OntoTreeNode: " + this.graphNode.toString();
        return this.graphNode.getName();
    }

}