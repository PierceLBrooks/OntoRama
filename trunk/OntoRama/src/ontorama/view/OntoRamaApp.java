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
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.Action;

import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.examplesConfig.OntoramaExample;

import ontorama.view.action.*;

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

public class OntoRamaApp extends JFrame implements ActionListener {
    /**
     * holds hyper view
     */
    private SimpleHyperView hyperView;

    /**
     * holds tree view
     */
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
     * ontorama menus
     */
    private JMenu _fileMenu;
    private ExamplesMenu _examplesMenu;
    private HistoryMenu _historyMenu;
    private JMenu _helpMenu;

    /**
     * back_forward toolbar
     */
    private JToolBar _backForwardToolBar;

    /**
     *
     */
    private DescriptionView descriptionViewPanel;

    /**
     *
     */
    private JMenuBar _menuBar;

    /**
     *
     */
    private JPanel _statusBar;
    private JLabel _statusLabel;
    private JProgressBar _progressBar;
    private Timer _timer;
    //private QueryEngineTask _worker;
    private QueryEngineThread _worker;

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
    Debug debug = new Debug(false);

    /**
     * actions
     */
    public Action _backAction;
    public Action _forwardAction;
    public Action _exitAction;
    public Action _aboutAction;

    /**
     *
     */
    private Graph _graph;

    /**
     *
     */
    private static final int TIMER_INTERVAL = 100;

    /**
     * @todo: introduce error dialogs for exception
     */
    public OntoRamaApp() {
        super("OntoRamaApp");

        initActions();

        _timer = new Timer(TIMER_INTERVAL, this);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        calculateAppPreferredSize();

        JPanel mainContentPanel = new JPanel(new BorderLayout());

        buildMenuBar();
        this.setJMenuBar(_menuBar);

        buildBackForwardToolBar();

        buildStatusBar();
        setStatusLabel("status bar is here");

        queryPanel = new QueryPanel(viewListener, this);

        /**
         * Create OntoTreeView
         */
        treeView = new OntoTreeView(viewListener);

        /**
         * Create HyperView
         */
        hyperView = new SimpleHyperView(viewListener);

        /** create description panel
         *  NOTE: description panel can't be created before hyper view and tree view
         *  because then a view that is created after description panel doesn't
         *  display clones for the first time a user clicks on a clone in one of the views
         */
        descriptionViewPanel = new DescriptionView(viewListener);

        //Add the scroll panes to a split pane.
        addComponentsToScrollPanel(hyperView, treeView);

        mainContentPanel.add(queryPanel, BorderLayout.NORTH);
        mainContentPanel.add(splitPane, BorderLayout.CENTER);
        mainContentPanel.add(descriptionViewPanel,BorderLayout.SOUTH);

        getContentPane().add(_backForwardToolBar, BorderLayout.NORTH);
        getContentPane().add(mainContentPanel, BorderLayout.CENTER);
        getContentPane().add(_statusBar, BorderLayout.SOUTH);

        pack();
        setSize(appWidth,appHeight);
        setLocation(centerAppWin());
        setVisible(true);


        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
              System.exit(0);
            }
        });

        termName = OntoramaConfig.ontologyRoot;
        Query query = new Query (termName);
        executeQuery(query);
    }

    /**
     *
     */
    private void initActions () {
      _backAction = new BackHistoryAction();
      _forwardAction = new ForwardHistoryAction();
      _exitAction = new ExitAction();
      _aboutAction = new AboutOntoRamaAction();
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
        super.repaint();
    }

    /**
     * find preferred sizes for application window.
     */
    private void calculateAppPreferredSize () {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.screenWidth = (int) screenSize.getWidth();
        this.screenHeight = (int) screenSize.getHeight();
        this.appWidth = (this.screenWidth * this.appWindowPercent) /100;
        this.appHeight = (this.screenHeight * this.appWindowPercent) /100;
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
    private void buildMenuBar () {
      _menuBar = new JMenuBar();

      _fileMenu = new JMenu("File");
      _fileMenu.setMnemonic(KeyEvent.VK_F);
      _fileMenu.add(_exitAction);

      _examplesMenu = new ExamplesMenu(this);

      _historyMenu = new HistoryMenu(this);

      _helpMenu = new JMenu("Help");
      _helpMenu.add(_aboutAction);

      _menuBar.add(_fileMenu);
      _menuBar.add(_examplesMenu);
      _menuBar.add(_historyMenu);
      _menuBar.add(_helpMenu);
    }

    /**
     *
     */
    private void buildBackForwardToolBar () {
      _backForwardToolBar = new JToolBar();

      JButton backButton = _backForwardToolBar.add(_backAction);
      _backForwardToolBar.add(backButton);

      JButton forwardButton = _backForwardToolBar.add(_forwardAction);
      _backForwardToolBar.add(forwardButton);
    }

    /**
     * actionPerformed for the timer action.
     * This method will wait untill worker thread is finished and
     * subsequently graph is build and then display it. While
     * waiting - update progress bar.
     */
    public void actionPerformed(ActionEvent evt) {
        //_progressBar.setValue(_worker.getCurrent());
        setStatusLabel(_worker.getMessage());
        //System.out.println("--- timer checking _worker.done() : " + _worker.done() + " _worker.isInterrupted()  = " + _worker.isInterrupted()  + ", isAlive = " + _worker.isAlive());
        if ( _worker.done() ) {
            _graph = _worker.getGraph();
            System.out.println(".....returned graph = " + _graph);
            _timer.stop();
            //_progressBar.setValue(_progressBar.getMinimum());
            _progressBar.setIndeterminate(false);
            //_progressBar.setValue(_progressBar.getMinimum());
            setStatusLabel("");
            if (_graph == null) {
              return;
            }
            updateViews();
        }
    }

    /**
     *
     */
    public boolean executeQuery (Query query) {
        debug.message(".............. EXECUTE QUERY for new graph ...................");
        _graph = null;

        System.out.println("\n\n\n---------------------------------------------------------------");
        System.out.println("          method executeQuery(query)\n\n\n");


        //_worker = new QueryEngineTask(query);
        //_worker.go();
        _worker = new QueryEngineThread(query);
        _worker.start();

        _timer.start();
        _progressBar.setIndeterminate(true);

//        System.out.println("returned from worker thread");
//
//        if (_graph == null) {
//          return false;
//        }
//
//        setStatusLabel("Query for " + query.getQueryTypeName());
        System.out.println("END of executeQuery method");
        System.out.println("---------------------------------------------------------------\n\n\n");
        return true;
    }

    /**
     *
     */
    private void updateViews () {
        hyperView.setGraph(_graph);
        treeView.setGraph(_graph);
        queryPanel.setQueryField(_graph.getRootNode().getName());
        descriptionViewPanel.clear();
        descriptionViewPanel.setFocus(_graph.getRootNode());

        //addComponentsToScrollPanel(hyperView, treeView);
        //addComponentsToScrollPanel(hyperViewPanel, treeViewPanel);

        hyperView.repaint();
        treeView.repaint();
        splitPane.repaint();

        setSelectedExampleMenuItem(OntoramaConfig.getCurrentExample());
        setSelectedHistoryMenuItem(OntoramaConfig.getCurrentExample());
        repaint();
    }

    /**
     *
     */
    protected void stopQuery () {
      System.out.println("\n\n\nSTOP QUERY");
      //_worker.stop();
      //_worker.interrupt();
      _worker.stopProcess();
    }

    /**
     *
     */
    public void appendHistoryMenu (Query query) {
      _historyMenu.appendHistory(query.getQueryTypeName(), OntoramaConfig.getCurrentExample());
    }

    /**
     *
     */
    protected void appendHistoryForGivenExample (String termName, OntoramaExample example) {
      _historyMenu.appendHistory(termName, example);
    }

    /**
     *
     */
    protected void setSelectedHistoryMenuItem (OntoramaExample example) {
      _historyMenu.setSelectedHistoryMenuItem(example);
    }

    /**
     *
     */
    protected void setSelectedExampleMenuItem (OntoramaExample example) {
      _examplesMenu.setSelectedExampleMenuItem(example);
    }

    /**
     *
     */
    protected boolean executeQueryForGivenExample (String termName, OntoramaExample example) {

      // reset details in OntoramaConfig
      OntoramaConfig.setCurrentExample(example);

      // create a new query
      Query query = new Query(termName);

      // get graph for this query and load it into app
      if (executeQuery(query)) {
        return true;
      }
      return false;
    }

    /**
     *
     */
    protected static void showErrorDialog (String message) {
      Frame[] frames = ontorama.view.OntoRamaApp.getFrames();
      ErrorPopupMessage errorPopup = new ErrorPopupMessage(message, frames[0]);
    }

    /**
     *
     */
    private void buildStatusBar () {
      _statusBar = new JPanel(new BorderLayout());

      _statusLabel = new JLabel();
      _progressBar = new JProgressBar(0, 100);
      //_progressBar = new JProgressBar();
      _progressBar.setIndeterminate(true);

      _statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
      _statusBar.add(_statusLabel, BorderLayout.WEST);
      _statusBar.add(_progressBar, BorderLayout.EAST);
    }

    /**
     *
     */
    private void setStatusLabel (String statusMessage) {
      _statusLabel.setText(statusMessage);
    }



    /**
     * main
     */
    public static void main(String[] args) {
        JFrame frame = new OntoRamaApp();
    }
}

