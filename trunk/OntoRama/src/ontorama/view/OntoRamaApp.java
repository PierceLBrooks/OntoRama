package ontorama.view;


import java.net.URL;
import java.io.IOException;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JTree;
//import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;


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
    /**
     * holds hyper view
     */
    private SimpleHyperView hyperView;

    /**
     * holds tree view
     */
    private JComponent treeView;

    /**
     * holds name of term user is serching for
     */
    private String termName;

    /**
     * graphBuilder
     */
    private GraphBuilder graphBuilder;

    /**
     * holds graph
     */
    private Graph graph;

    /**
     *
     */
    private QueryPanel queryPanel;

    /**
     * split panel will contain hyper view and tree view
     */
    private JSplitPane splitPane;

    /**
     * left side of split panel holds hyper view.
     * leftSplitPanelWidthPercent allocates percentage of space for
     * the hyper view. the rest of the split panel will be taken up
     * by tree view
     */
    int leftSplitPanelWidthPercent = 80;

    /**
     * height and width of main window
     */
    private int appHeight = 600;
    private int appWidth = 700;

    /**
     * location of split panel's divider
     */
    private int dividerBarLocation = -1;

    /**
     * screen width and height
     */
    private int screenWidth;
    private int screenHeight;

    /**
     * what part of a screen this app window should take (percentage)
     */
    private int appWindowPercent = 80;

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

        // find preferred sizes for application window.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.screenWidth = (int) screenSize.getWidth();
        this.screenHeight = (int) screenSize.getHeight();
        this.appWidth = (this.screenWidth * this.appWindowPercent) /100;
        this.appHeight = (this.screenHeight * this.appWindowPercent) /100;

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

        //Add the scroll panes to a split pane.
        setSplitPanelSizes(appWidth, appHeight);
        splitPane.setLeftComponent(hyperView);
        splitPane.setRightComponent(treeView);
        splitPane.setOneTouchExpandable(true);

        // create description panel
        DescriptionView descriptionViewPanel = new DescriptionView(graph);
        //JScrollPane descriptionViewScrollPanel = new JScrollPane(descriptionViewPanel,
                               //JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                               //JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollPane descriptionViewScrollPanel = new JScrollPane(descriptionViewPanel);


        //Add the split pane to this frame.
        getContentPane().add(queryPanel,BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        // Add description panel to this frame
        getContentPane().add(descriptionViewScrollPanel,BorderLayout.SOUTH);

        pack();
        setSize(appWidth,appHeight);
        setLocation(centerAppWin());
        setVisible(true);
    }

    /**
     * repaint method. takes care of repositioning divider bar in split panel
     * @todo  not sure how to work out if divider location bar have been moved by a user.
     */
    public void repaint () {
        int curAppWidth = getContentPane().getWidth();
        int curAppHeight = getContentPane().getHeight();

        // recalculate percentage for leftSplitPanelWidthPercent to
        // account for user specified position of divider bar
        int currentDividerBarLocation = splitPane.getDividerLocation();
        if (this.dividerBarLocation != currentDividerBarLocation) {
            System.out.println("*****this.dividerBarLocation != currentDividerBarLocation: " + this.dividerBarLocation + ", " + currentDividerBarLocation);

            double scale = (double) curAppWidth/(double) this.appWidth;
            double scaledDividerLocation = ((double) this.dividerBarLocation * scale);

            System.out.println("current divider bar location = " + currentDividerBarLocation + ", scaled location = " + scaledDividerLocation);

            double scaledDividerPercent = (scaledDividerLocation * 100) / curAppWidth;
            System.out.println("scaled percent = " + scaledDividerPercent);

            int newLeftPanelPercent = (currentDividerBarLocation * 100) / this.appWidth;
            System.out.println("newLeftPanelPercent = " + newLeftPanelPercent + ", calculated new divider position: " + calculateLeftPanelWidth(curAppWidth, newLeftPanelPercent));
            if ( ((newLeftPanelPercent - scaledDividerPercent) > 10) || ((scaledDividerPercent - newLeftPanelPercent) > 10) ) {
                this.leftSplitPanelWidthPercent = newLeftPanelPercent;
            }
        }
        System.out.println("this.leftSplitPanelWidthPercent = " + this.leftSplitPanelWidthPercent);

        setSplitPanelSizes(curAppWidth, curAppHeight);
        this.appWidth = curAppWidth;
        this.appHeight = curAppHeight;

    }

    /**
     * Calculate width of left panel in the split panel
     */
    private int calculateLeftPanelWidth (int appWidth, int percent) {
        return ( (appWidth * percent)/100 );
    }

    /**
     * Set sizes of left and right component in the split panel
     */
    private void setSplitPanelSizes (int applicationWidth, int applicationHeigth) {

        int splitPaneWidth = applicationWidth;
        int splitPaneHeight = (applicationHeigth * 70)/100;

        int dividerBarWidth = splitPane.getDividerSize();

        int leftPanelWidth = calculateLeftPanelWidth(applicationWidth, this.leftSplitPanelWidthPercent );
        int rigthPanelWidth = applicationWidth - leftPanelWidth;

        hyperView.setPreferredSize(new Dimension(leftPanelWidth - dividerBarWidth, splitPaneHeight));
        treeView.setPreferredSize(new Dimension(rigthPanelWidth - dividerBarWidth, splitPaneHeight));

        splitPane.setPreferredSize(new Dimension(splitPaneWidth, splitPaneHeight));

        this.dividerBarLocation = leftPanelWidth;

        splitPane.setDividerLocation(this.dividerBarLocation);
    }

    /**
     * get position for center of the screen
     */
    private Point centerAppWin () {
      int xDiff = this.screenWidth - this.appWidth;
      int yDiff = this.screenHeight - this.appHeight;
      return new Point(xDiff/4, yDiff/4);
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

    /**
     * main
     */
    public static void main(String[] args) {
        JFrame frame = new OntoRamaApp();

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}

