package ontorama.view;

import ontorama.OntoramaConfig;
import ontorama.hyper.view.simple.SimpleHyperView;
import ontorama.model.Graph;
import ontorama.model.GraphNode;
import ontorama.ontologyConfig.examplesConfig.OntoramaExample;
import ontorama.textDescription.view.DescriptionView;
import ontorama.tree.view.OntoTreeView;
import ontorama.util.Debug;
import ontorama.util.event.ViewEventListener;
import ontorama.view.action.*;
import ontorama.webkbtools.query.Query;
import org.tockit.events.EventBroker;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Main Application class. This class start OntoRama application.
 */
public class OntoRamaApp extends JFrame implements ActionListener {
    /**
     * holds hyper view
     */
    private SimpleHyperView _hyperView;

    /**
     * holds tree view
     */
    private OntoTreeView _treeView;

    /**
     * hold query panel
     */
    public QueryPanel _queryPanel;

    /**
     * split panel will contain hyper view and tree view
     */
    private JSplitPane _splitPane;

    /**
     * ontorama menu bar
     */
    private JMenuBar _menuBar;

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
     * actions
     */
    public Action _backAction;
    public Action _forwardAction;
    public Action _exitAction;
    public Action _aboutAction;
    public StopQueryAction _stopQueryAction;

    /**
     * desctiption view panel contains concept properties details
     */
    private DescriptionView _descriptionViewPanel;

    /**
     * status bar
     */
    private JPanel _statusBar;
    private JLabel _statusLabel;
    private JProgressBar _progressBar;
    private Timer _timer;

    /**
     * holds thread that will do all the work: querying ont server
     * and building graph
     */
    //private QueryEngineTask _worker;
    private QueryEngineThread _worker;

    /**
     * holds current query
     */
    private Query _query;

    /**
     *
     */
    private Query _lastQuery = null;

    /**
     * currently displayed graph
     */
    private Graph _graph;

    /**
     * left side of split panel holds hyper view.
     * leftSplitPanelWidthPercent allocates percentage of space for
     * the hyper view. the rest of the split panel will be taken up
     * by tree view
     */
    int _leftSplitPanelWidthPercent = 65;

    /**
     * height and width of main window
     */
    private int _appHeight = 600;
    private int _appWidth = 700;

    /**
     * location of split panel's divider
     */
    private int _dividerBarLocation = -1;

    /**
     * screen width and height
     */
    private int _screenWidth;
    private int _screenHeight;

    /**
     * what part of a screen this app window should take (percentage)
     */
    private int _appWindowPercent = 95;

    /**
     * view listener
     */
    private ViewEventListener _viewListener = new ViewEventListener();

    /**
     * nodes list viewer, used to show unconnected nodes
     */
    private NodesListViewer _listViewer;

    /**
     * debugging
     */
    Debug _debug = new Debug(false);

    /**
     * timer interval - interval for swing timer thread to check for
     * progress on worker thread and update status and progress bar.
     */
    private static final int TIMER_INTERVAL = 100;

    /**
     *
     */
    public OntoRamaApp() {
        super("OntoRamaApp");

        initActions();

        _timer = new Timer(TIMER_INTERVAL, this);

        _splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        calculateAppPreferredSize();

        buildMenuBar();
        setJMenuBar(_menuBar);

        buildBackForwardToolBar();

        buildStatusBar();
        setStatusLabel("status bar is here");

        EventBroker eventBroker = new EventBroker();

        _queryPanel = new QueryPanel(_viewListener, this, eventBroker);


        _treeView = new OntoTreeView(_viewListener, eventBroker);
        _hyperView = new SimpleHyperView(_viewListener, eventBroker);

        //Add the scroll panes to a split pane.
        addComponentsToScrollPanel(_hyperView, _treeView);

        /** create description panel
         *  NOTE: description panel can't be created before hyper view and tree view
         *  because then a view that is created after description panel doesn't
         *  display clones for the first time a user clicks on a clone in one of the views
         */
        _descriptionViewPanel = new DescriptionView(_viewListener, eventBroker);

        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.add(_queryPanel, BorderLayout.NORTH);
        mainContentPanel.add(_splitPane, BorderLayout.CENTER);
        mainContentPanel.add(_descriptionViewPanel, BorderLayout.SOUTH);

        getContentPane().add(_backForwardToolBar, BorderLayout.NORTH);
        getContentPane().add(mainContentPanel, BorderLayout.CENTER);
        getContentPane().add(_statusBar, BorderLayout.SOUTH);

        pack();
        setSize(_appWidth, _appHeight);
        setLocation(centerAppWin());
        setVisible(true);

        enableDisableDynamicFields();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        //Query query = new Query(OntoramaConfig.ontologyRoot);
        _query = new Query(OntoramaConfig.ontologyRoot, OntoramaConfig.getRelationLinksList());
        executeQuery(_query);
    }

    /**
     * Initialise actions
     */
    private void initActions() {
        _backAction = new BackHistoryAction();
        _forwardAction = new ForwardHistoryAction();
        _exitAction = new ExitAction();
        _aboutAction = new AboutOntoRamaAction();
        _stopQueryAction = new StopQueryAction(this);
        _stopQueryAction.setEnabled(false);
    }

    /**
     * repaint method. takes care of repositioning divider bar in split panel
     * @todo  not sure how to work out if divider location bar have been moved by a user.
     */
    public void repaint() {
        int curAppWidth = getContentPane().getWidth();
        int curAppHeight = getContentPane().getHeight();

        // recalculate percentage for _leftSplitPanelWidthPercent to
        // account for user specified position of divider bar
        int currentDividerBarLocation = _splitPane.getDividerLocation();
        if (_dividerBarLocation != currentDividerBarLocation) {
            //System.out.println("*****this._dividerBarLocation != currentDividerBarLocation: " + this._dividerBarLocation + ", " + currentDividerBarLocation);
        }
        double scale = (double) curAppWidth / (double) this._appWidth;

        double scaledDividerLocation = ((double) this._dividerBarLocation * scale);
        int newLeftPanelPercent = (currentDividerBarLocation * 100) / this._appWidth;
        double scaledDividerPercent = (scaledDividerLocation * 100) / curAppWidth;
        if (((calculateLeftPanelWidth(curAppWidth, newLeftPanelPercent) - scaledDividerLocation) > 25)
                || ((scaledDividerLocation - calculateLeftPanelWidth(curAppWidth, newLeftPanelPercent)) > 25)) {
            if (((newLeftPanelPercent - scaledDividerPercent) > 10)
                    || ((scaledDividerPercent - newLeftPanelPercent) > 10)) {
                _leftSplitPanelWidthPercent = newLeftPanelPercent;
            }
        }
        setSplitPanelSizes(curAppWidth, curAppHeight);
        _appWidth = curAppWidth;
        _appHeight = curAppHeight;
        super.repaint();
    }

    /**
     * find preferred sizes for application window.
     */
    private void calculateAppPreferredSize() {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        _screenWidth = (int) screenSize.getWidth();
        _screenHeight = (int) screenSize.getHeight();
        _appWidth = (_screenWidth * _appWindowPercent) / 100;
        _appHeight = (_screenHeight * _appWindowPercent) / 100;
    }

    /**
     * Calculate width of left panel in the split panel
     */
    private int calculateLeftPanelWidth(int _appWidth, int percent) {
        return ((_appWidth * percent) / 100);
    }

    /**
     * Set sizes of left and right component in the split panel
     */
    private void setSplitPanelSizes(int applicationWidth, int applicationHeigth) {

        int _splitPaneWidth = applicationWidth;
        int _splitPaneHeight = (applicationHeigth * 70) / 100;

        int dividerBarWidth = _splitPane.getDividerSize();

        int leftPanelWidth = calculateLeftPanelWidth(applicationWidth, this._leftSplitPanelWidthPercent);
        int rigthPanelWidth = applicationWidth - leftPanelWidth;

        _hyperView.setPreferredSize(new Dimension(leftPanelWidth - dividerBarWidth, _splitPaneHeight));
        _treeView.setPreferredSize(new Dimension(rigthPanelWidth - dividerBarWidth, _splitPaneHeight));

        _splitPane.setPreferredSize(new Dimension(_splitPaneWidth, _splitPaneHeight));

        _dividerBarLocation = leftPanelWidth;

        _splitPane.setDividerLocation(_dividerBarLocation);
    }

    /**
     * get position for center of the screen
     */
    private Point centerAppWin() {
        int xDiff = _screenWidth - _appWidth;
        int yDiff = _screenHeight - _appHeight;
        return new Point(xDiff / 4, yDiff / 4);
    }

    /**
     * Add the scroll panes to a split pane
     */
    private void addComponentsToScrollPanel(JComponent leftComp, JComponent rightComp) {
        setSplitPanelSizes(_appWidth, _appHeight);
        _splitPane.setLeftComponent(leftComp);
        _splitPane.setRightComponent(rightComp);
        _splitPane.setOneTouchExpandable(true);
    }

    /**
     *
     */
    private void buildMenuBar() {
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
    private void buildBackForwardToolBar() {
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
        Graph graph = null;
        setStatusLabel(_worker.getMessage());
        if ((_worker.done()) || (_worker.isStopped())) {
            if (_worker.done()) {
                graph = _worker.getGraph();
                System.out.println(".....returned graph = " + graph);
            }
            _timer.stop();
            _progressBar.setIndeterminate(false);
            setStatusLabel("");
            _stopQueryAction.setEnabled(false);
            if (graph == null) {
                return;
            }
            _graph = graph;
            updateViews();
            showUnconnectedNodes();
        }
    }

    /**
     * @todo shouldn't return true or false as since introducing threads
     * we return from this method before query finished executing
     */
    public boolean executeQuery(Query query) {
        _lastQuery = _query;
        _query = query;
        _debug.message(".............. EXECUTE QUERY for new graph ...................");

        System.out.println("\n\n\n---------------------------------------------------------------");
        System.out.println("          method executeQuery(query)\n\n\n");

        //_worker = new QueryEngineTask(query);
        //_worker.go();
        _worker = new QueryEngineThread(_query);
        _worker.start();

        _timer.start();
        _progressBar.setIndeterminate(true);
        _stopQueryAction.setEnabled(true);

        System.out.println("END of executeQuery method");
        System.out.println("---------------------------------------------------------------\n\n\n");
        return true;
    }

    /**
     *
     */
    private void updateViews() {
        _hyperView.setGraph(_graph);
        _treeView.setGraph(_graph);
        //_queryPanel.setQueryField(graph.getRootNode().getName());
        _queryPanel.setQuery(_query);
        _descriptionViewPanel.clear();
        _descriptionViewPanel.focus(_graph.getRootNode());

        _hyperView.repaint();
        _treeView.repaint();
        _splitPane.repaint();

        setSelectedExampleMenuItem(OntoramaConfig.getCurrentExample());
        // don't following method, remove later.
        //setSelectedHistoryMenuItem(OntoramaConfig.getCurrentExample());

        enableDisableDynamicFields();

        repaint();
    }


    /**
     * enable/disable components that should be only shown
     * for dynamically loaded ontologies
     */
    private void enableDisableDynamicFields() {
        if (OntoramaConfig.isSourceDynamic) {
            _queryPanel.enableDepth();
            _descriptionViewPanel.enableDynamicFields();
        } else {
            _queryPanel.disableDepth();
            _descriptionViewPanel.disableDynamicFields();
        }
    }

    /**
     *
     */
    public void stopQuery() {
        System.out.println("\n\n\nSTOP QUERY");
        //_worker.stop();
        //_worker.interrupt();
        _worker.stopProcess();
        _query = _lastQuery;
        _queryPanel.setQuery(_query);
    }

    /**
     *
     */
    public void appendHistoryMenu(Query query) {
        //		_historyMenu.appendHistory(query.getQueryTypeName(),
        //			OntoramaConfig.getCurrentExample());
        _historyMenu.appendHistory(query, OntoramaConfig.getCurrentExample());
    }

    /**
     *
     */
    protected void appendHistoryForGivenExample(String termName, OntoramaExample example) {
        //protected void appendHistoryForGivenExample(Query query,OntoramaExample example) {
        _historyMenu.appendHistory(termName, example);
    }

//	/**
//	 *
//	 */
//	protected void setSelectedHistoryMenuItem(OntoramaExample example) {
//		_historyMenu.setSelectedHistoryMenuItem(example);
//	}

    /**
     *
     */
    protected void setSelectedExampleMenuItem(OntoramaExample example) {
        _examplesMenu.setSelectedExampleMenuItem(example);
    }

    /**
     *
     */
    protected boolean executeQueryForGivenExample(String termName, OntoramaExample example) {

        // reset details in OntoramaConfig
        OntoramaConfig.setCurrentExample(example);

        // create a new query
        Query query = new Query(termName, OntoramaConfig.getRelationLinksList());

        // get graph for this query and load it into app
        if (executeQuery(query)) {
            return true;
        }
        return false;
    }

    /**
     * @todo	do we need two methods: executeQueryForGivenExample and executeQueryForHistoryElement
     * probably not - think of a better way to handle this.
     */
    protected boolean executeQueryForHistoryElement(HistoryElement historyElement) {

        // reset details in OntoramaConfig
        OntoramaConfig.setCurrentExample(historyElement.getExample());

        // get graph for this query and load it into app
        if (executeQuery(historyElement.getQuery())) {
            return true;
        }
        return false;
    }

    /**
     *
     */
    private void showUnconnectedNodes() {
        List unconnectedNodes = _graph.getUnconnectedNodesList();
        if (unconnectedNodes.size() != 0) {
            //_listViewer.setNodesList( unconnectedNodes);
            closeUnconnectedNodesView();
            _listViewer = new NodesListViewer(this, unconnectedNodes);
            _listViewer.showList(true);
        }
    }

    /**
     *
     */
    private void closeUnconnectedNodesView() {
        if (_listViewer != null) {
            _listViewer.dispose();
            _listViewer = null;
        }
    }

    /**
     *
     */
    protected void resetGraphRoot(GraphNode newRootNode) {
        _graph.setRoot(newRootNode);
        updateViews();
    }

    /**
     *
     */
    public static void showErrorDialog(String message) {
        Frame[] frames = ontorama.view.OntoRamaApp.getFrames();
        ErrorPopupMessage errorPopup = new ErrorPopupMessage(message, frames[0]);
    }

    /**
     *
     */
    private void buildStatusBar() {
        _statusBar = new JPanel(new BorderLayout());

        _statusLabel = new JLabel();
        //_progressBar = new JProgressBar(0, 100);
        _progressBar = new JProgressBar();
        _progressBar.setIndeterminate(true);

        _statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        _statusBar.add(_statusLabel, BorderLayout.WEST);
        _statusBar.add(_progressBar, BorderLayout.EAST);
    }

    /**
     *
     */
    private void setStatusLabel(String statusMessage) {
        _statusLabel.setText(statusMessage);
    }

    /**
     * main
     */
    public static void main(String[] args) {
        JFrame frame = new OntoRamaApp();
    }
}
