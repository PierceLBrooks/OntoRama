package ontorama.view;


import java.net.URL;
import java.io.IOException;
import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

import java.awt.*;
import java.awt.event.*;
import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import ontorama.OntoramaConfig;

import ontorama.webkbtools.query.QueryEngine;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.query.QueryResult;
import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.util.ParserException;

import ontorama.model.Graph;
import ontorama.model.GraphNode;
import ontorama.model.Edge;
import ontorama.model.NoTypeFoundInResultSetException;

import ontorama.hyper.view.simple.SimpleHyperView;

import ontorama.tree.model.OntoTreeModel;
import ontorama.tree.model.OntoTreeNode;
import ontorama.tree.view.OntoTreeView;

import ontorama.textDescription.view.DescriptionView;

import ontorama.util.event.ViewEventListener;
import ontorama.util.Debug;

public class OntoRamaApp extends JFrame {
    /**
     * holds hyper view
     */
    private SimpleHyperView hyperView;

    /**
     * holds tree view
     */
    //private JComponent treeView;
    private OntoTreeView treeView;

    /**
     * holds name of term user is serching for
     */
    private String termName;

    /**
     * holds graph
     */
    private Graph graph;


    /**
     *
     */
    public QueryPanel queryPanel;

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
    int leftSplitPanelWidthPercent = 70;

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
    private int appWindowPercent = 85;

    /**
     *
     */
    ViewEventListener viewListener = new ViewEventListener();

    /**
     *
     */
    private OntoRamaMenu menu;

    /**
     *
     */
    //private OntoRamaToolBar toolBar;
    private JToolBar toolBar;

    /**
     *
     */
    private DescriptionView descriptionViewPanel;

    /**
     *
     */
    Debug debug = new Debug(false);

    /**
     * @todo: introduce error dialogs for exception
     */
    public OntoRamaApp() {
        super("OntoRamaApp");

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        termName = OntoramaConfig.ontologyRoot;
        Query query = new Query (termName);
        graph = getGraphFromQuery(query);

        /**
         * find preferred sizes for application window.
         */
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.screenWidth = (int) screenSize.getWidth();
        this.screenHeight = (int) screenSize.getHeight();
        this.appWidth = (this.screenWidth * this.appWindowPercent) /100;
        this.appHeight = (this.screenHeight * this.appWindowPercent) /100;

        /**
         * create Menu Bar
         */
        menu = new OntoRamaMenu(this, viewListener);
        this.setJMenuBar(menu.getMenuBar());

        /**
         * create tool bar
         */
        //toolBar = new OntoRamaToolBar();
        toolBar = menu.getToolBar();
        JPanel combinedToolBarQueryPanel = new JPanel(new BorderLayout());
        combinedToolBarQueryPanel.add(toolBar,BorderLayout.NORTH);

        /**
         * Create OntoTreeView
         */
        //treeView = (new OntoTreeView(graph, viewListener)).getTreeViewPanel();
        treeView = new OntoTreeView(graph, viewListener);
        treeView.setGraph(graph);


        /**
         * Create HyperView
         */
        hyperView = new SimpleHyperView(viewListener);
        hyperView.setGraph(graph);

        /**
         * create a query panel
         */
        queryPanel = new QueryPanel(hyperView, viewListener, this);
        queryPanel.setQueryField(termName);
        combinedToolBarQueryPanel.add(queryPanel, BorderLayout.CENTER);

        /** create description panel
         *  NOTE: description panel can't be created before hyper view and tree view
         *  because then a view that is created after description panel doesn't
         *  display clones for the first time a user clicks on a clone in one of the views
         */
        descriptionViewPanel = new DescriptionView(graph, viewListener);
        //JScrollPane descriptionViewScrollPanel = new JScrollPane(descriptionViewPanel,
                               //JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                               //JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        descriptionViewPanel.setFocus(graph.getRootNode());
        JScrollPane descriptionViewScrollPanel = new JScrollPane(descriptionViewPanel);

        //Add the scroll panes to a split pane.
        addComponentsToScrollPanel(hyperView, treeView);

        //Add the split pane to this frame.
        //getContentPane().add(queryPanel,BorderLayout.NORTH);
        getContentPane().add(combinedToolBarQueryPanel, BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        // Add description panel to this frame
        getContentPane().add(descriptionViewScrollPanel,BorderLayout.SOUTH);

        pack();
        setSize(appWidth,appHeight);
        setLocation(centerAppWin());
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
              closeMainApp();
            }
        });

    }

    /**
     *
     */
    public void closeMainApp () {
      System.exit(0);
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
            //System.out.println("*****this.dividerBarLocation != currentDividerBarLocation: " + this.dividerBarLocation + ", " + currentDividerBarLocation);
        }
        double scale = (double) curAppWidth/(double) this.appWidth;

        double scaledDividerLocation = ((double) this.dividerBarLocation * scale);
        int newLeftPanelPercent = (currentDividerBarLocation * 100) / this.appWidth;
        double scaledDividerPercent = (scaledDividerLocation * 100) / curAppWidth;
        if ( ((calculateLeftPanelWidth(curAppWidth, newLeftPanelPercent)-scaledDividerLocation) > 25) ||
                 ((scaledDividerLocation- calculateLeftPanelWidth(curAppWidth, newLeftPanelPercent)) > 25)) {
          if ( ((newLeftPanelPercent - scaledDividerPercent) > 10) || ((scaledDividerPercent - newLeftPanelPercent) > 10) ) {
              this.leftSplitPanelWidthPercent = newLeftPanelPercent;
          }
        }
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
     * Add the scroll panes to a split pane
     */
    private void addComponentsToScrollPanel (JComponent leftComp, JComponent rightComp) {
        setSplitPanelSizes(this.appWidth, this.appHeight);
        this.splitPane.setLeftComponent(leftComp);
        this.splitPane.setRightComponent(rightComp);
        this.splitPane.setOneTouchExpandable(true);
    }

    /**
     *
     */
    public Graph getGraphFromQuery (Query query) {

      Graph graph = null;
      try {
          QueryEngine queryEngine = new QueryEngine (query);
          QueryResult queryResult = queryEngine.getQueryResult();

          //GraphBuilder graphBuilder = new GraphBuilder(queryResult);
          graph = new Graph(queryResult);
          //graph = graphBuilder.getGraph();
          //System.out.println(graph.printXml());
      }
      catch (NoTypeFoundInResultSetException noTypeExc) {
          System.err.println(noTypeExc);
          showErrorDialog(noTypeExc.getMessage());
          //System.exit(-1);
      }
      catch (NoSuchRelationLinkException noRelExc) {
          System.err.println(noRelExc);
          showErrorDialog(noRelExc.getMessage());
          //System.exit(-1);
      }
      catch (IOException ioExc) {
        System.out.println(ioExc);
        ioExc.printStackTrace();
        showErrorDialog(ioExc.getMessage());
      }
      catch (ParserException parserExc) {
        System.out.println(parserExc);
        parserExc.printStackTrace();
        showErrorDialog(parserExc.getMessage());
      }
      catch (ClassNotFoundException classExc) {
        System.out.println(classExc);
        classExc.printStackTrace();
        showErrorDialog("Sorry, couldn't find one of the classes you specified in config.xml");
      }
      catch (IllegalAccessException iae) {
        System.out.println(iae);
        iae.printStackTrace();
        showErrorDialog(iae.getMessage());
      }
      catch (InstantiationException instExc) {
        System.out.println(instExc);
        instExc.printStackTrace();
        showErrorDialog(instExc.getMessage());
      }

      catch (Exception e) {
          System.err.println();
          e.printStackTrace();
          showErrorDialog("Unable to build graph: " + e.getMessage());
          //System.exit(-1);
      }
      return graph;
    }

    /**
     *
     */
    public Query buildNewQuery () {
        debug.message(".............. buildNewQuery  for " + queryPanel.getQueryField() + " ...................");

        List wantedLinks = queryPanel.getWantedRelationLinks();

        debug.message("wanted links list: " + wantedLinks);
        debug.message("building graph with root = " + queryPanel.getQueryField());

        Query query = new Query (queryPanel.getQueryField(), wantedLinks);

        return query;
    }

    /**
     *
     */
    public boolean executeQuery (Query query) {
        debug.message(".............. EXECUTE QUERY for new graph ...................");

        graph = getGraphFromQuery(query);
        if (graph == null) {
          return false;
        }

        hyperView.setGraph(graph);
        treeView.setGraph(graph);
        queryPanel.setQueryField(graph.getRootNode().getName());
        descriptionViewPanel.clear();
        descriptionViewPanel.setFocus(graph.getRootNode());
        //menu.appendHistory(query.getQueryTypeName(), OntoramaConfig.getCurrentExample());

        addComponentsToScrollPanel(hyperView, treeView);

        hyperView.repaint();
        treeView.repaint();
        splitPane.repaint();
        return true;
    }

    /**
     *
     */
    public void appendHistoryMenu (Query query) {
      menu.appendHistory(query.getQueryTypeName(), OntoramaConfig.getCurrentExample());
    }

    /**
     *
     */
    public void showErrorDialog (String message) {
      message = "Sorry, " + message;
      JOptionPane optionPane = new JOptionPane(message,JOptionPane.ERROR_MESSAGE);
      optionPane.showMessageDialog(this,message);
    }


    /**
     * main
     */
    public static void main(String[] args) {
        JFrame frame = new OntoRamaApp();
    }
}

