
package ontorama.tree.view;

import javax.swing.JTree;
//import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JComponent;

import ontorama.model.Graph;

import ontorama.tree.model.OntoTreeModel;
import ontorama.tree.model.OntoTreeNode;


public class OntoTreeView {

    private JScrollPane treeView;


    public OntoTreeView(Graph graph) {

        //System.out.println("TreeRamaApp, graph size = " + graph.getSize());
        OntoTreeModel ontoTreeModel = new OntoTreeModel(graph);

        final JTree tree = new JTree(ontoTreeModel);


        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                OntoTreeNode node = (OntoTreeNode) tree.getLastSelectedPathComponent();
                System.out.println("******** node " + node);
                node.hasFocus();

                if (node == null) return;
            }
        });

        treeView = new JScrollPane(tree);

        //if (playWithLineStyle) {
        //    tree.putClientProperty("JTree.lineStyle", lineStyle);
        //}
    }

    public JComponent getTreeViewPanel () {
        return (JComponent) treeView;
    }
}

