package ontorama.view;

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
import javax.swing.JPanel;
import javax.swing.JComponent;
import java.awt.*;
import java.awt.event.*;

import java.util.Iterator;
import java.util.Collection;

import ontorama.model.Graph;
import ontorama.model.GraphNode;
import ontorama.model.GraphBuilder;

import ontorama.hyper.view.simple.SimpleHyperView;

import ontorama.tree.model.OntoTreeModel;
import ontorama.tree.model.OntoTreeNode;
import ontorama.tree.view.OntoTreeView;


public class OntoRamaApp extends JFrame {

    private SimpleHyperView hyperView;
    private OntoTreeView treeView;

    public OntoRamaApp() {
        super("OntoRamaApp");

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        //Create a tree that allows one selection at a time.
        GraphBuilder graphBuilder = new GraphBuilder();
        Graph graph = graphBuilder.getGraph();

        // Create hyper view
        hyperView = new SimpleHyperView();

        //JPanel hyperViewPanel = new JPanel();
        hyperView.setGraph(graph);
        hyperView.setPreferredSize(new Dimension(400,400));
        //hyperViewPanel.add(hyperView);

        // Create OntoTreeView
        treeView = new OntoTreeView(graph);
        //Create the scroll pane and add the tree to it.
        JScrollPane treeViewPanel = treeView.getTreeViewPanel();
        //Dimension prefferredSize = new Dimension (500,200);
        //treeViewPanel.setPreferredSize(prefferredSize);

        //Add the scroll panes to a split pane.
        splitPane.setLeftComponent(hyperView);
        splitPane.setRightComponent(treeViewPanel);


        //Add the split pane to this frame.
        getContentPane().add(splitPane, BorderLayout.CENTER);

    }

    public static void main(String[] args) {
        JFrame frame = new OntoRamaApp();

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.pack();
        frame.setVisible(true);
    }
}

