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
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.*;
import java.awt.event.*;

import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedList;

import ontorama.OntoramaConfig;

import ontorama.webkbtools.query.QueryEngine;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.query.QueryResult;
import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;

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

    private String termName;

    GraphBuilder graphBuilder;
    Graph graph;

    private QueryPanel queryPanel;

    public OntoRamaApp() {
        super("OntoRamaApp");

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        //termName = "root";
        termName = "comms#CommsObject";
        LinkedList wantedLinks = new LinkedList();
        wantedLinks.add(new Integer (OntoramaConfig.SUBTYPE));
        Query query = new Query (termName, wantedLinks);

        try {
            QueryEngine queryEngine = new QueryEngine (query);
            QueryResult queryResult = queryEngine.getQueryResult();

            graphBuilder = new GraphBuilder(queryResult);
            System.out.println("got graph = " + graph);
            graph = graphBuilder.getGraph();
        }
        catch (Exception e) {
            System.err.println("Unable to build graph: " + e);
            e.printStackTrace();
            System.exit(-1);
        }

		// create a query panel
        queryPanel = new QueryPanel();
        queryPanel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println ("---actionListener for queryPanel");
                //graphBuilder = new GraphBuilder(queryField.getText());
                //graph = graphBuilder.getGraph();
                // update views
            }
        } );

        // Create HyperView
        hyperView = new SimpleHyperView();

        hyperView.setGraph(graph);
        hyperView.setPreferredSize(new Dimension(500,400));

        // Create OntoTreeView
        treeView = new OntoTreeView(graph);
        JComponent treeViewPanel = treeView.getTreeViewPanel();
        treeViewPanel.setPreferredSize(new Dimension (500,200));

        //Add the scroll panes to a split pane.
        splitPane.setLeftComponent(hyperView);
        splitPane.setRightComponent(treeViewPanel);

        //Add the split pane to this frame.
        getContentPane().add(queryPanel,BorderLayout.NORTH);
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

