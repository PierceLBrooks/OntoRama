package ontorama.tree.model;

import ontorama.model.GraphNode;
import ontorama.util.Debug;
import ontorama.ontologyConfig.RelationLinkDetails;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.*;

/**
 * Description: OntoTreeNode is implementation of javax.swing.TreeNode
 * and contains GraphNode as a part of OntoTreeNode.
 *
 * Copyright:    Copyright (c) 2001
 * Company:     DSTC
 */

public class OntoTreeNode implements TreeNode {

    /**
     * Set to true if this node can have children
     * ( required by methods we need to implement for TreeNode interface)
     */
    private boolean allowChildren = true;

    /**
     * GraphNode that is a base for this OntoTreeNode
     */
    private GraphNode graphNode;

    /**
     * Observers list
     */
    private LinkedList observers = new LinkedList();

    /**
     *
     */
    private RelationLinkDetails relLink;

    /**
     * debug
     */
    private boolean debugOn = false;
    Debug debug = new Debug(this.debugOn);

    /**
     *  Constructor
     *  @param graphNode
     */
    public OntoTreeNode(GraphNode graphNode) {
        this.graphNode = graphNode;
    }

    /**
     *
     */
    public void setRelLink(RelationLinkDetails relLink) {
        this.relLink = relLink;
    }

    /**
     *
     */
    public RelationLinkDetails getRelLink() {
        return this.relLink;
    }

    /**
     * Add observer to the list of OntoTree observers.
     * @param observer
     */
    public void addOntoObserver(Object observer) {
        this.observers.add(observer);
    }

    /**
     * Update all views of change.
     * In this case - tell OntoTreeNode observers to update.
     * (OntoTreeView is an observer for each OntoTreeNode).
     */
    /*
   private void notifyTreeChange () {
       // get TreePath for this node and tell tree to update
       TreePath path = getTreePath();

       Iterator it = observers.iterator();
       while(it.hasNext()) {
           OntoNodeObserver cur = (OntoNodeObserver) it.next();
           cur.updateOntoTree( path );
       }
   }
   */

    /**
     * Recursive build of a stack of nodes from given node through to root
     * @param   node stack
     * @return updatedStack
     */
    private Stack buildPath(OntoTreeNode node, Stack stack) {

        stack.push(node);
        OntoTreeNode parent = (OntoTreeNode) node.getParent();
        if (parent != null) {
            Stack newStack = buildPath(parent, stack);
            return newStack;
        }
        return stack;
    }

    /**
     * Build TreePath for this node (path from root to the node)
     * @return  TreePath path
     */
    public TreePath getTreePath() {
        OntoTreeNode parent = (OntoTreeNode) this.getParent();

        Stack stack = new Stack();
        stack.push(this);
        if (this.getParent() != null) {
            // if this node is root - don't need to build stack
            stack = buildPath(parent, stack);
        }

        // now reverse stack as it is backwards: starting at
        // node and finishing at root
        Stack reverseStack = new Stack();
        while (!stack.isEmpty()) {
            reverseStack.push(stack.pop());
        }

        TreePath path = new TreePath(reverseStack.toArray());
        //debug.message("OntoTreeNode","getTreePath", "TreePath = " + path);
        return path;
    }

    /**
     * Set focus on this node
     */
    /*
   public void setFocus() {
       //System.out.println("OntoTreeNode setFocus()");
       this.graphNode.hasFocus();
   }
   */


    //////////////Implementation of TreeNode interface methods//////////////

    /**
     * Returns the child TreeNode at index childIndex.
     * @param childIndex
     * @return  child TreeNode
     * @todo    should return null if index is out of bounds????
     */
    public TreeNode getChildAt(int childIndex) {
        List outboundNodes = OntoTreeModel.graph.getOutboundEdgeNodesList(this.graphNode);
        GraphNode outboundGraphNode = (GraphNode) outboundNodes.get(childIndex);
        TreeNode ouboundTreeNode = OntoTreeBuilder.getTreeNode(outboundGraphNode);

        /*
        List childrenList = this.graphNode.getChildrenList();
        GraphNode childGraphNode = (GraphNode) childrenList.get(childIndex);
        TreeNode childNode = OntoTreeBuilder.getTreeNode(childGraphNode);
        */
        //debug.message("OntoTreeNode","getChildAt", "node = " + this.graphNode.getName() + " returning " + childNode + " for index " + childIndex);
        return ouboundTreeNode;
    }

    /**
     * Returns the number of children TreeNodes the receiver contains.
     * @return  int
     * @todo  remove getNumberOfChildren method
     */
    public int getChildCount() {
        List outboundNodes = OntoTreeModel.graph.getOutboundEdgeNodesList(this.graphNode);
        //System.out.println("node = " + this.graphNode.getName() + " returning "  + outboundNodes.size());

        //List childrenList = this.graphNode.getChildrenList();
        //debug.message("OntoTreeNode","getChildCount","node = " + this.graphNode.getName() + " returning "  + childrenList.size());
        return outboundNodes.size();
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

        Iterator inboundNodes = OntoTreeModel.graph.getInboundEdgeNodes(this.graphNode);
        if (inboundNodes.hasNext()) {
            GraphNode inboundGraphNode = (GraphNode) inboundNodes.next();
            return (OntoTreeBuilder.getTreeNode(inboundGraphNode));
        }

        /*
        Iterator parentsIterator = this.graphNode.getParents();
        if (parentsIterator.hasNext()) {
            GraphNode parentGraphNode = (GraphNode) parentsIterator.next();
            //debug.message("OntoTreeNode","getParent","node = " + this.graphNode.getName() + " returning "  + (TreeNode) OntoTreeBuilder.getTreeNode(parentGraphNode));
            return ( (TreeNode) OntoTreeBuilder.getTreeNode(parentGraphNode));
        }
        //debug.message("OntoTreeNode","getParent","node = " + this.graphNode.getName() + " returning null");
        */
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
        List outboundNodes = OntoTreeModel.graph.getOutboundEdgeNodesList(this.graphNode);
        return outboundNodes.indexOf(node);
        /*
        List childrenList = this.graphNode.getChildrenList();
        //debug.message("OntoTreeNode","getIndex","node = " + this.graphNode.getName() + " returning "  + childrenList.indexOf(node) + for TreeNode " +  (OntoTreeBuilder.getGraphNode(node)).getName());
        return childrenList.indexOf(node);
        */
    }

    /**
     * Returns true if the receiver allows children.
     * @return  boolean allowChildren
     */
    public boolean getAllowsChildren() {
        //debug.message("OntoTreeNode","getAllowsChildren"," node = " + this.graphNode.getName() + " returning "  + allowChildren);
        return allowChildren;
    }

    /**
     * Returns true if the receiver is a leaf.
     * @return  true if node is a leaf, false otherwise
     */
    public boolean isLeaf() {
        List outboundNodesList = OntoTreeModel.graph.getOutboundEdgeNodesList(this.getGraphNode());
        if (outboundNodesList.size() <= 0) {
            //System.out.println("isLeaf, node = " + this.graphNode.getName() + " returning true");
            // debug.message("OntoTreeNode","isLeaf","node = " + this.graphNode.getName() + " returning true");
            return true;
        }
        //System.out.println("isLeaf, node = " + this.graphNode.getName() + " returning false");

        //debug.message("OntoTreeNode","isLeaf","node = " + this.graphNode.getName() + " returning false");
        return false;
    }

    /**
     * Returns the children of the reciever as an Enumeration.
     * @return  children Enumeration
     */
    public Enumeration children() {
        Enumeration outboundNodesEnum = (Enumeration) OntoTreeModel.graph.getOutboundEdgeNodesList(this.graphNode);
        return outboundNodesEnum;
        /*
      Enumeration childrenEnum = (Enumeration) ( this.graphNode.getChildrenList());
      //debug.message("OntoTreeNode","children","node = " + this.graphNode.getName() + " returning" + childrenEnum);
      return childrenEnum;
      */
    }

    ///////////////End of TreeNode interface implementation/////////////////

    /**
     * Get GraphNode that is a part of this OntoTreeNode
     * @return  graphNode
     */
    public GraphNode getGraphNode() {
        return this.graphNode;
    }

    /**
     * toString. Pring out name of this node
     */
    public String toString() {
        //String str = "OntoTreeNode: " + this.graphNode.toString();
        return this.graphNode.getName();
    }


}
