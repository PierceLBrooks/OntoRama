
package ontorama.tree.view;

import java.util.Iterator;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JComponent;
import javax.swing.UIManager;

import ontorama.model.Graph;

import ontorama.tree.model.OntoTreeModel;
import ontorama.tree.model.OntoTreeNode;
import ontorama.tree.model.OntoNodeObserver;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class OntoTreeView implements OntoNodeObserver {

    private JScrollPane treeView;
    private JTree tree;

    /**
     * Constructor
     */
    public OntoTreeView(Graph graph) {
        OntoTreeModel ontoTreeModel = new OntoTreeModel(graph);

        // sort out observers
        addOntoObservers(ontoTreeModel);

        try {
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName() );
        }
        catch (Exception e) {
        }

        //final JTree tree = new JTree(ontoTreeModel);
        this.tree = new JTree(ontoTreeModel);
        this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        this.tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                OntoTreeNode node = (OntoTreeNode) tree.getLastSelectedPathComponent();
                System.out.println("******** node " + node);
                node.setFocus();
                if (node == null) return;
            }
        });

        this.treeView = new JScrollPane(tree);
        tree.putClientProperty("JTree.lineStyle", "Angled");

        // testing if communication from GraphNode to OntoTreeView is working
        /*
        Iterator it = ontoTreeModel.getOntoTreeIterator();
        while (it.hasNext()) {
            OntoTreeNode ontoTreeNode = (OntoTreeNode) it.next();
            int depth = (ontoTreeNode.getGraphNode()).getDepth();
            System.out.println("depth for node " + (ontoTreeNode.getGraphNode()).getName() + " is " + depth);
            if (depth ==2 ) {
                ontoTreeNode.setFocus();
            }
            //if ( ((ontoTreeNode.getGraphNode()).getName()).equals("node3.2")) {
                //ontoTreeNode.setFocus();
            //}
        }
        */
    }

    /**
     * Update tree view by focusing on given object
     * @param   TreePath path
     */
    public void updateOntoTree (TreePath path) {
        this.tree.makeVisible(path);
        System.out.println("---OntoTreeView, method updateOntoTree for path" + path);
        this.tree.addSelectionPath(path);
    }

    /**
     * Return TreeView as a JComponent
     * @param   -
     * @return  treeView
     */
    public JComponent getTreeViewPanel () {
        return (JComponent) this.treeView;
    }

    /**
     * Add this model as an observer to all OntoTreeNodes
     * @param OntoTreeModel ontoTreeModel
     */
    private void addOntoObservers (OntoTreeModel ontoTreeModel) {
        Iterator it = ontoTreeModel.getOntoTreeIterator();
        while (it.hasNext()) {
            OntoTreeNode ontoTreeNode = (OntoTreeNode) it.next();
            ontoTreeNode.addOntoObserver(this);
        }
    }
}

