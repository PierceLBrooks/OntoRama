
package ontorama.tree.view;

import java.util.Iterator;

import javax.swing.JTree;
//import javax.swing.tree.DefaultMutableTreeNode;
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


public class OntoTreeView implements OntoNodeObserver {

    private JScrollPane treeView;
    private JTree tree;
    private OntoTreeModel ontoTreeModel;


    public OntoTreeView(Graph graph) {
        this.ontoTreeModel = ontoTreeModel;

        //System.out.println("TreeRamaApp, graph size = " + graph.getSize());
        OntoTreeModel ontoTreeModel = new OntoTreeModel(graph);

        addOntoObservers(ontoTreeModel);

        try {
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName() );
        }
        catch (Exception e) {
        }


        //final JTree tree = new JTree(ontoTreeModel);
        this.tree = new JTree(ontoTreeModel);

        this.tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

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

    }

    /**
     *
     */
    public void updateOnto (Object obj) {
        TreePath path = (TreePath) obj;
        this.tree.makeVisible(path);
        System.out.println("---OntoTreeView, method updateOnto");
    }

    /**
     *
     */
    private void addOntoObservers (OntoTreeModel treeModel) {
        Iterator it = treeModel.getOntoTreeIterator();
        while (it.hasNext()) {
            OntoTreeNode ontoTreeNode = (OntoTreeNode) it.next();
            ontoTreeNode.addOntoObserver(this);
        }
    }

    /**
     *
     */
    public JComponent getTreeViewPanel () {
        return (JComponent) this.treeView;
    }
}

