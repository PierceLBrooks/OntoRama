package ontorama.view.tree;

import javax.swing.JTree;
//import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreeSelectionModel;
import java.net.URL;
import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JFrame;
import javax.swing.JComponent;
import java.awt.*;
import java.awt.event.*;

import java.util.Iterator;
import java.util.Collection;

import ontorama.model.Graph;
import ontorama.model.GraphNode;
import ontorama.model.GraphBuilder;

import ontorama.tree.model.OntoTreeModel;
import ontorama.tree.model.OntoTreeNode;
import ontorama.tree.view.OntoTreeView;


public class TreeRamaApp extends JFrame {
    private JEditorPane htmlPane;
    private static boolean DEBUG = false;
    private URL helpURL;

    //Optionally play with line styles.  Possible values are
    //"Angled", "Horizontal", and "None" (the default).
    private boolean playWithLineStyle = false;
    private String lineStyle = "Angled";

    public TreeRamaApp() {
        super("TreeRamaApp");

        //Create a tree that allows one selection at a time.
        GraphBuilder graphBuilder = new GraphBuilder();
        Graph graph = graphBuilder.getGraph();

        OntoTreeView tree = new OntoTreeView(graph);

        /*
        //System.out.println("TreeRamaApp, graph size = " + graph.getSize());
        OntoTreeModel ontoTreeModel = new OntoTreeModel(graph);

        final JTree tree = new JTree(ontoTreeModel);


        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreeNode node = (OntoTreeNode) tree.getLastSelectedPathComponent();

                if (node == null) return;

                //Object nodeInfo = node.getUserObject();
            }
        });

        if (playWithLineStyle) {
            tree.putClientProperty("JTree.lineStyle", lineStyle);
        }
        */

        //Create the scroll pane and add the tree to it.
        //JScrollPane treeView = new JScrollPane(tree);
        JComponent treeView = tree.getTreeViewPanel();
        //JScrollPane treeView = tree.getTreeViewPanel();

        //Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(treeView);

        //Dimension minimumSize = new Dimension(100, 300);
        //treeView.setMinimumSize(minimumSize);
        Dimension prefferredSize = new Dimension (500,300);
        treeView.setPreferredSize(prefferredSize);

        //Add the split pane to this frame.
        getContentPane().add(treeView, BorderLayout.CENTER);

    }

    public static void main(String[] args) {
        JFrame frame = new TreeRamaApp();

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.pack();
        frame.setVisible(true);
    }
}

