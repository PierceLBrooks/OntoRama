package ontorama.views.tree.model;


import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import ontorama.model.tree.Tree;

/**
 * Description: OntoTreeModel takes graph of GraphNodes and
 * converts each NodeImpl into OntoTreeNode, effectively
 * building a TreeModel of TreeNodes
 *
 * Copyright:    Copyright (c) 2001
 * Company:     DSTC
 */
public class OntoTreeModel implements TreeModel {

    
    /**
     * Tree base for this OntoTreeModel
     */
    protected Tree _tree;
    

    /**
     * OntoTreeBuilder object that will hold references
     * between GraphNodes and TreeNodes
     */
    private OntoTreeBuilder ontoTreeBuilder;

    /**
     * Constructor
     */
    public OntoTreeModel(Tree tree) {
    	_tree = tree;
        this.ontoTreeBuilder = new OntoTreeBuilder(_tree);
    }
    

    ///////////////////Implementation of methods for TreeModel////////////////

    /**
     * Returns the root of the tree. Returns null only if the tree has no nodes.
     * @return the root of the tree
     */
    public Object getRoot() {
    	ontorama.model.tree.TreeNode rootModelNode = _tree.getRootNode(); 
        return OntoTreeBuilder.getTreeNode(rootModelNode);
    }

    /**
     * Returns the child of parent at index 'index' in the parent's child array.
     * parent must be a node previously obtained from this data source.
     * This should not return null if index is a valid index for parent
     * (that is index >= 0 && index < getChildCount(parent)).
     * @param parent - a node in the tree, obtained from this data source
     * @return  the child of parent at index 'index'
     */
    public Object getChild(Object parent, int index) {
        TreeNode parentNode = (OntoTreeNode) parent;
        OntoTreeNode childNode = (OntoTreeNode) parentNode.getChildAt(index);
        return childNode;
    }

    /**
     * Returns the number of children of parent.
     * Returns 0 if the node is a leaf or if it has no children.
     * parent must be a node previously obtained from this data source.
     * @param  parent - a node in the tree, obtained from this data source
     * @return the number of children of the node parent
     */
    public int getChildCount(Object parent) {
        return ((TreeNode) parent).getChildCount();
    }

    /**
     * Returns true if node is a leaf.
     * It is possible for this method to return false even if node has no children.
     * A directory in a filesystem, for example, may contain no files;
     * the node representing the directory is not a leaf, but it also has no children.
     * @param node - a node in the tree, obtained from this data source
     * @return  true if node is a leaf
     */
    public boolean isLeaf(Object node) {
        OntoTreeNode treeNode = (OntoTreeNode) node;
        return treeNode.isLeaf();
    }

    /**
     * Messaged when the user has altered the value for the item
     * identified by path to newValue. If newValue signifies a truly new
     * value the model should post a treeNodesChanged event.
     * @param  path - path to the node that the user has altered.
     *         newValue - the new value from the TreeCellEditor.
     * @todo   not sure if implementation is correct, CHECK!!!
     * @todo   hasn't finished this - not sure how to post treeNodesChanged event !!!!
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        // get value if object in the tree path
        // assuming that its still old value there
        // this assumption may be WRONG!?
        Object oldValue =  path.getLastPathComponent();
        if (! oldValue.equals(newValue)) {
            // post treeNodesChanged event
        }
    }

    /**
     * Returns the index of child in parent.
     */
    public int getIndexOfChild(Object parent, Object child) {
        return (((TreeNode) parent).getIndex((TreeNode) child));
    }

    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     * @param  l - the listener to add
     */
    public void addTreeModelListener(TreeModelListener l) {
    }

    /**
     * Removes a listener previously added with addTreeModelListener().
     * @param l - the listener to remove
     */
    public void removeTreeModelListener(TreeModelListener l) {
    }

    ////////////////////End of TreeModel implementation///////////////////
}