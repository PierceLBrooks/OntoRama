package ontorama.tree.model;

import ontorama.model.*;
import ontorama.util.Debug;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.*;

/**
 * Description: OntoTreeNode is implementation of javax.swing.TreeNode
 * and contains Node as a part of OntoTreeNode.
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
     * Node that is a base for this OntoTreeNode
     */
    private Node graphNode;

    /**
     * Observers list
     */
    private LinkedList observers = new LinkedList();

    /**
     *
     */
    private EdgeType relLink;

    /**
     * debug
     */
    private boolean debugOn = false;
    Debug debug = new Debug(this.debugOn);

    /**
     *  Constructor
     *  @param graphNode
     */
    public OntoTreeNode(Node graphNode) {
        this.graphNode = graphNode;
    }

    /**
     *
     */
    public void setRelLink(EdgeType relLink) {
        this.relLink = relLink;
    }

    /**
     *
     */
    public EdgeType getRelLink() {
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
        return path;
    }


    //////////////Implementation of TreeNode interface methods//////////////

    /**
     * Returns the child TreeNode at index childIndex.
     * @param childIndex
     * @return  child TreeNode
     */
    public TreeNode getChildAt(int childIndex) {
        List outboundEdges = OntoTreeModel.graph.getOutboundEdgesDisplayedInGraph(this.graphNode);
        Edge outboundEdge = (Edge) outboundEdges.get(childIndex);
        Node outboundNode = outboundEdge.getToNode();
        TreeNode ouboundTreeNode = OntoTreeBuilder.getTreeNode(outboundNode);

        return ouboundTreeNode;
    }

    /**
     * Returns the number of children TreeNodes the receiver contains.
     * @return  int
     */
    public int getChildCount() {
        List outboundNodes = OntoTreeModel.graph.getOutboundEdgesDisplayedInGraph(this.graphNode);
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
        List inboundEdges = OntoTreeModel.graph.getInboundEdgesDisplayedInGraph(this.graphNode);
        Iterator it = inboundEdges.iterator();
        if (it.hasNext()) {
            Edge edge = (Edge) it.next();
            Node inboundNode = edge.getFromNode();
            return (OntoTreeBuilder.getTreeNode(inboundNode));
        }
        return null;
    }

    /**
     * Returns the index of node in the receivers children.
     * @param node
     * @return int index. If the receiver does not contain node, -1 will be returned.
     * NOTE:  not sure if implemented correctly - terminology in java TreeNode
     * interface is confusing (what do they mean by receiver? ). Assumed
     * that by 'receiver' they mean this node.
     */
    public int getIndex(TreeNode node) {
        List outboundEdges = OntoTreeModel.graph.getOutboundEdgesDisplayedInGraph(this.graphNode);
        Iterator it = outboundEdges.iterator();
        int index = -1;
        while (it.hasNext()) {
            Edge edge = (Edge) it.next();
            Node toNode = edge.getToNode();
            if (toNode.equals(node)) {
                index = outboundEdges.indexOf(edge);
            }
        }
        return index;
    }

    /**
     * Returns true if the receiver allows children.
     * @return  boolean allowChildren
     */
    public boolean getAllowsChildren() {
        return allowChildren;
    }

    /**
     * Returns true if the receiver is a leaf.
     * @return  true if node is a leaf, false otherwise
     */
    public boolean isLeaf() {
        List outboundEdgesList = OntoTreeModel.graph.getOutboundEdgesDisplayedInGraph(this.getGraphNode());
        if (outboundEdgesList.size() <= 0) {
            return true;
        }
        return false;
    }

    /**
     * Returns the children of the reciever as an Enumeration.
     * @return  children Enumeration
     */
    public Enumeration children() {
        Enumeration outboundEdgesEnum = (Enumeration) OntoTreeModel.graph.getOutboundEdgesDisplayedInGraph(this.graphNode);
        return outboundEdgesEnum;
    }

    ///////////////End of TreeNode interface implementation/////////////////

    /**
     * Get Node that is a part of this OntoTreeNode
     * @return  graphNode
     */
    public Node getGraphNode() {
        return this.graphNode;
    }

    /**
     * toString. Pring out name of this node
     */
    public String toString() {
        return this.graphNode.getName();
    }


}
