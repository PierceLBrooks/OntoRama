package ontorama.views.tree.model;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import ontorama.model.graph.EdgeType;

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
    private ontorama.model.tree.TreeNode _treeNode;

//    /**
//     * Observers list
//     */
//    private LinkedList observers = new LinkedList();

    /**
     *
     */
    private EdgeType _edgeType;


    /**
     *  Constructor
     *  @param graphNode
     */
    public OntoTreeNode(ontorama.model.tree.TreeNode treeNode) {
        _treeNode = treeNode;
    }

    /**
     *
     */
    public void setRelLink(EdgeType edgeType) {
        _edgeType = edgeType;
    }

    /**
     *
     */
    public EdgeType getEdgeType() {
        return _edgeType;
    }

//    /**
//     * Add observer to the list of OntoTree observers.
//     * @param observer
//     */
//    public void addOntoObserver(Object observer) {
//        this.observers.add(observer);
//    }

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
    	
    	List children = _treeNode.getChildren();
    	ontorama.model.tree.TreeNode node = (ontorama.model.tree.TreeNode) children.get(childIndex);
    	TreeNode ontoTreeNode = OntoTreeBuilder.getTreeNode(node);
	
        return ontoTreeNode;
    }

    /**
     * Returns the number of children TreeNodes the receiver contains.
     * @return  int
     */
    public int getChildCount() {
        List children = _treeNode.getChildren();
        return children.size();
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
		ontorama.model.tree.TreeNode modelNodeParent = _treeNode.getParent();
		if (modelNodeParent != null ) {
        	return OntoTreeBuilder.getTreeNode(modelNodeParent);
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
        List children = _treeNode.getChildren();
        Iterator it = children.iterator();
        int index = -1;
        while (it.hasNext()) {
            ontorama.model.tree.TreeNode curNode = (ontorama.model.tree.TreeNode) it.next();
            TreeNode ontoTreeNode = OntoTreeBuilder.getTreeNode(curNode);
            if (ontoTreeNode.equals(node)) {
                index = children.indexOf(curNode);
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
        List childrenList = _treeNode.getChildren();
        if (childrenList.size() <= 0) {
            return true;
        }
        return false;
    }

    /**
     * Returns the children of the reciever as an Enumeration.
     * @return  children Enumeration
     */
    public Enumeration children() {
        List childrenEdges = _treeNode.getChildren();
        Iterator it = childrenEdges.iterator();
        Vector result = new Vector();
        while (it.hasNext()) {
            ontorama.model.tree.TreeNode curNode = (ontorama.model.tree.TreeNode) it.next();
            OntoTreeNode treeNode = (OntoTreeNode) OntoTreeBuilder.getTreeNode(curNode);
            result.add(treeNode);
        }
        return result.elements();
    }


    ///////////////End of TreeNode interface implementation/////////////////


    /**
     * Get Node that is a part of this OntoTreeNode
     * @return  graphNode
     */
    public ontorama.model.tree.TreeNode getModelTreeNode() {
        return _treeNode;
    }

    /**
     * toString. Pring out name of this node
     */
    public String toString() {
        return _treeNode.getName();
    }


}
