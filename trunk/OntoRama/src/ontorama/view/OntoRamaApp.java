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
import ontorama.webkbtools.util.NoSuchRelationLinkException;

import ontorama.model.Graph;
import ontorama.model.GraphNode;
import ontorama.model.GraphBuilder;
import ontorama.model.NoTypeFoundInResultSetException;

import ontorama.hyper.view.simple.SimpleHyperView;

import ontorama.tree.model.OntoTreeModel;
import ontorama.tree.model.OntoTreeNode;
import ontorama.tree.view.OntoTreeView;

import ontorama.textDescription.view.DescriptionView;


public class OntoRamaApp extends JFrame {

    private SimpleHyperView hyperView;
    //private OntoTreeView treeView;
    private JComponent treeView;

    private String termName;

    GraphBuilder graphBuilder;
    Graph graph;

    private QueryPanel queryPanel;

    private JSplitPane splitPane;

    int leftSplitPanelWidthPercent = 80;

    private int appHeight = 600;
    private int appWidth = 700;
    private int dividerBarLocation = -1;

    /**
     * @todo: introduce error dialogs for exception
     */
    public OntoRamaApp() {
        super("OntoRamaApp");



        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        termName = OntoramaConfig.ontologyRoot;

        //termName = "root";
        //termName = "comms#CommsObject";
        //termName = "comms_CommsObject";

        //LinkedList wantedLinks = new LinkedList();
        //wantedLinks.add(new Integer (OntoramaConfig.SUBTYPE));
        //wantedLinks.add (new Integer (OntoramaConfig.SUPERTYPE));
        //Query query = new Query (termName, wantedLinks);

        Query query = new Query (termName);

        try {
            QueryEngine queryEngine = new QueryEngine (query);
            QueryResult queryResult = queryEngine.getQueryResult();

            graphBuilder = new GraphBuilder(queryResult);
            graph = graphBuilder.getGraph();
            //System.out.println(graph.printXml());
        }
        catch (NoTypeFoundInResultSetException noTypeExc) {
            System.err.println(noTypeExc);
            System.exit(-1);
        }
        catch (NoSuchRelationLinkException noRelExc) {
            System.err.println(noRelExc);
            System.exit(-1);
        }
        catch (Exception e) {
            System.err.println("Unable to build graph: " + e);
            e.printStackTrace();
            System.exit(-1);
        }

		// create a query panel
        queryPanel = new QueryPanel();
        queryPanel.setQueryField(termName);
        queryPanel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println ("---actionListener for queryPanel");
                //graphBuilder = new GraphBuilder(queryField.getText());
                //graph = graphBuilder.getGraph();
                // update views
            }
        } );
        JButton makeSVG = new JButton("Run Spring and force test");
        makeSVG.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent a) {
                testSpringAndForceAlgorthms();
            }});
        queryPanel.add( makeSVG );
        // Create HyperView
        hyperView = new SimpleHyperView();
        hyperView.setGraph(graph);
        hyperView.saveCanvasToFile( "hyperView" );

        // Create OntoTreeView
        treeView = (new OntoTreeView(graph)).getTreeViewPanel();
        //JComponent treeViewPanel = treeView.getTreeViewPanel();

        //Add the scroll panes to a split pane.
        setSplitPanelSizes(appWidth, appHeight);
        splitPane.setLeftComponent(hyperView);
        splitPane.setRightComponent(treeView);
        splitPane.setOneTouchExpandable(true);
//        int splitPaneWidth = appWidth;
//        int splitPaneHeight = (appHeight * 70)/100;
//
//        int rigthPanelWidthPercent = 100 - leftSplitPanelWidthPercent;
//        int leftPanelWidth = (appWidth *leftSplitPanelWidthPercent)/100;
//        int rigthPanelWidth = (appWidth * leftSplitPanelWidthPercent)/100;
//
//        hyperView.setPreferredSize(new Dimension(leftPanelWidth - 5, splitPaneHeight - 5));
//        treeViewPanel.setPreferredSize(new Dimension(rigthPanelWidth - 5, splitPaneHeight - 5));
//        splitPane.setLeftComponent(hyperView);
//        splitPane.setRightComponent(treeViewPanel);
//        splitPane.setPreferredSize(new Dimension(splitPaneWidth, splitPaneHeight));
//        splitPane.setOneTouchExpandable(true);
//        splitPane.setDividerLocation(leftPanelWidth);


        // create description panel
        DescriptionView descriptionViewPanel = new DescriptionView(graph);

        //Add the split pane to this frame.
        getContentPane().add(queryPanel,BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);
        getContentPane().add(descriptionViewPanel,BorderLayout.SOUTH);

        pack();
        setSize(appWidth,appHeight);
        setVisible(true);
    }

    public void repaint () {
        int curAppWidth = getContentPane().getWidth();
        int curAppHeight = getContentPane().getHeight();


        // recalculate percentage for leftSplitPanelWidthPercent to
        // account for user specified position of divider bar
        int currentDividerBarLocation = splitPane.getDividerLocation();
        if (this.dividerBarLocation != currentDividerBarLocation) {
            System.out.println("*****this.dividerBarLocation != currentDividerBarLocation: " + this.dividerBarLocation + ", " + currentDividerBarLocation);

            //System.out.println("repaint: lastAppWidth = " + this.appWidth + ", last divider location = " + splitPane.getDividerLocation() + ", curAppWidth = " + curAppWidth);
            double scale = (double) curAppWidth/(double) this.appWidth;
            //System.out.println("scale = " + scale);
            //System.out.println("lastAppWidth * scale = " + ((double) this.appWidth * scale));
            //System.out.println("new divider bar scale = " + ( (double) this.leftSplitPanelWidthPercent * scale));

                    double scaledDividerLocation = ((double) this.dividerBarLocation * scale);
                    System.out.println("current divider bar location = " + currentDividerBarLocation + ", scaled location = " + scaledDividerLocation);

            int newLeftPanelPercent = (currentDividerBarLocation * 100) / this.appWidth;
            System.out.println("newLeftPanelPercent = " + newLeftPanelPercent);

            System.out.println("calculated new divider position: " + calculateLeftPanelWidth(curAppWidth, newLeftPanelPercent));
        //if ( ( (newLeftPanelPercent - this.leftSplitPanelWidthPercent) > 3) || ((this.leftSplitPanelWidthPercent - newLeftPanelPercent) > 3) ){
            if ( ((calculateLeftPanelWidth(curAppWidth, newLeftPanelPercent)-scaledDividerLocation) > 25) ||
                     ((scaledDividerLocation- calculateLeftPanelWidth(curAppWidth, newLeftPanelPercent)) > 25)) {

                //this.leftSplitPanelWidthPercent = newLeftPanelPercent;
            }
        }
        System.out.println("this.leftSplitPanelWidthPercent = " + this.leftSplitPanelWidthPercent);

        setSplitPanelSizes(curAppWidth, curAppHeight);
        this.appWidth = curAppWidth;
        this.appHeight = curAppHeight;

    }

    private int calculateLeftPanelWidth (int appWidth, int percent) {
        return ( (appWidth * percent)/100 );
    }

    private void setSplitPanelSizes (int applicationWidth, int applicationHeigth) {

        int splitPaneWidth = applicationWidth;
        int splitPaneHeight = (applicationHeigth * 70)/100;

        int dividerBarWidth = splitPane.getDividerSize();

        int leftPanelWidth = calculateLeftPanelWidth(applicationWidth, this.leftSplitPanelWidthPercent );
        int rigthPanelWidth = applicationWidth - leftPanelWidth;
        //int leftPanelWidth = (applicationWidth * this.leftSplitPanelWidthPercent)/100;
        //int rigthPanelWidthPercent = 100 - leftSplitPanelWidthPercent;
        //int rigthPanelWidth = (applicationWidth * this.leftSplitPanelWidthPercent)/100;

        hyperView.setPreferredSize(new Dimension(leftPanelWidth - dividerBarWidth, splitPaneHeight));
        treeView.setPreferredSize(new Dimension(rigthPanelWidth - dividerBarWidth, splitPaneHeight));

        splitPane.setPreferredSize(new Dimension(splitPaneWidth, splitPaneHeight));

        this.dividerBarLocation = leftPanelWidth;

        splitPane.setDividerLocation(this.dividerBarLocation);
        //System.out.println("leftPanelWidth = " + leftPanelWidth + ", splitPaneWidth = " + splitPaneWidth);
    }

    /**
     * Method to test layouting usin spring and force algorthms
     */
    private void testSpringAndForceAlgorthms() {
        for( int i = 0; i < 10; i++) {
            //generate spring length values between 50 - 250;
            double springLength = (Math.random() * 200) + 51;
            //generate stiffness values between 0 - .99999
            double stiffness = Math.random();
            //generate electric_charge values between 0 - 1000;
            double electric_charge = (Math.random() * 1000) + 1;
            hyperView.testSpringAndForceAlgorthms(springLength, stiffness, electric_charge);
            hyperView.saveCanvasToFile( springLength+"_"+stiffness+"_"+electric_charge);
        }
        System.out.println("Test Finished...");
    }

    public static void main(String[] args) {
        JFrame frame = new OntoRamaApp();

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        //frame.pack();
        //frame.setSize(600,600);
        //frame.setVisible(true);
    }
}

