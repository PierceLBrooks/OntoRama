package ontorama.ui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ontorama.OntoramaConfig;
import ontorama.backends.Backend;
import ontorama.conf.examplesConfig.OntoramaExample;
import ontorama.model.graph.Graph;
import ontorama.model.graph.events.GraphLoadedEvent;
import ontorama.model.tree.Tree;
import ontorama.model.tree.TreeImpl;
import ontorama.model.tree.events.TreeChangedEvent;
import ontorama.model.tree.events.TreeLoadedEvent;
import ontorama.ui.events.*;
import ontorama.ontotools.query.Query;
import ontorama.ui.action.AboutOntoRamaAction;
import ontorama.ui.action.ExitAction;
import ontorama.ui.controller.GeneralQueryEventHandler;
import ontorama.util.Debug;
import ontorama.views.hyper.view.Projection;
import ontorama.views.hyper.view.SimpleHyperView;
import ontorama.views.hyper.view.SphericalProjection;
import ontorama.views.textDescription.view.DescriptionView;
import ontorama.views.tree.view.OntoTreeView;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;
import org.tockit.events.LoggingEventListener;

/**
 * Main Application class. This class start OntoRama application.
 */
public class OntoRamaApp extends JFrame implements ActionListener {
	private static final double FOCAL_RADIUS_CHANGE = 1.1;
	/**
	 * holds hyper ui
	 */
	private SimpleHyperView _hyperView;
	/**
	 * holds tree ui
	 */
	private OntoTreeView _treeView;
	/**
	 * hold query panel
	 */
	public QueryPanel _queryPanel;
	/**
	 * split panel will contain hyper ui and tree ui
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
	private JToolBar _toolBar;
	/**
	 * actions
	 */
	public Action _exitAction;
	public Action _aboutAction;

	/**
	 * desctiption ui panel contains concept properties details
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
	private static Graph _graph;

	private static Tree _tree;

	/**
	 * left side of split panel holds hyper ui.
	 * leftSplitPanelWidthPercent allocates percentage of space for
	 * the hyper ui. the rest of the split panel will be taken up
	 * by tree ui
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
	private int _mainSplitPaneWidth;
	private int _mainSplitPaneHeight;
	/**
	 * nodes list viewer, used to show unconnected nodes
	 */
	private NodesListViewer _listViewer;
	private static Vector _backends;
	private static EventBroker _modelEventBroker;
	private static EventBroker _viewsEventBroker;

	private Projection projection = new SphericalProjection(300, 0.8);

	/**
	 * debugging
	 */
	Debug _debug = new Debug(false);

	/**
	 * timer interval - interval for swing timer thread to check for
	 * progress on worker thread and update status and progress bar.
	 */
	private static final int TIMER_INTERVAL = 100;


	private class ViewUpdateHandler implements EventBrokerListener {
		public void processEvent(Event e) {
            System.out.println("processEvent ViewUpdateHandler for new tree");
			TreeImpl tree = (TreeImpl) e.getSubject();
			updateViews();
		}
	}


    private class QueryStartEventHandler implements EventBrokerListener {
        public void processEvent (Event event) {
            System.out.println("QueryStartEventHandler, processEvent()");
            Query query = (Query) event.getSubject();
            _lastQuery = _query;
            _query = query;
            _worker = new QueryEngineThread(_query, _modelEventBroker);
            System.out.println("_modelEventBroker.removeSubscriptions(_viewsEventBroker)");
            _modelEventBroker.removeSubscriptions(_viewsEventBroker);
            _worker.start();
            _timer.start();
            _progressBar.setIndeterminate(true);
            _queryPanel.enableStopQueryAction(true);
        }
    }

    private class QueryEndEventHandler implements EventBrokerListener {
        public void processEvent (Event event) {
            System.out.println("QueryEndEventHandler, processEvent()");
        	Graph graph = (Graph) event.getSubject();
        	_progressBar.setIndeterminate(false);
        	setStatusLabel("");
            _queryPanel.enableStopQueryAction(false);
            System.out.println("_modelEventBroker resubscribe _viewsEventBroker");
        	_modelEventBroker.subscribe(_viewsEventBroker, TreeChangedEvent.class, Object.class);
        	if (graph == null) {
        		return;
        	}
        	_graph = graph;
        	_modelEventBroker.processEvent(new GraphLoadedEvent(_graph));
        	_tree = new TreeImpl(_graph, _graph.getRootNode(), _modelEventBroker);
        	_modelEventBroker.processEvent(new TreeLoadedEvent(_tree));
        	_viewsEventBroker.subscribe(new ViewUpdateHandler(),TreeChangedEvent.class,Tree.class);
        	updateViews();
        }
    }

    class QueryCancelledEventHandler implements EventBrokerListener {
        public void processEvent (Event event) {
            System.out.println("QueryCancelledEventHandler processEvent()");
            _worker.stopProcess();
            _query = _lastQuery;
            _queryPanel.setQuery(_query);
            _progressBar.setIndeterminate(false);
            setStatusLabel("");
            _queryPanel.enableStopQueryAction(false);
            System.out.println("_modelEventBroker resubscribe _viewsEventBroker");
        	_modelEventBroker.subscribe(_viewsEventBroker, TreeChangedEvent.class, Object.class);
        }
    }

	/**
	 *
	 */
	public OntoRamaApp() {
		super("OntoRama");
		initActions();
		_modelEventBroker = new EventBroker();
		_viewsEventBroker = new EventBroker();

        System.out.println("_modelEventBroker.subscribe(_viewsEventBroker,...)");
        System.out.println("_viewsEventBroker.subscribe(_modelEventBroker, ...)");

        new GeneralQueryEventHandler(_modelEventBroker);
        new GeneralQueryEventHandler(_viewsEventBroker);

        _viewsEventBroker.subscribe(new QueryStartEventHandler(), QueryStartEvent.class, Object.class);
         _modelEventBroker.subscribe(new QueryStartEventHandler(), QueryStartEvent.class, Object.class);

		_modelEventBroker.subscribe(new QueryEndEventHandler(), QueryEndEvent.class, Object.class);

        _modelEventBroker.subscribe(new QueryCancelledEventHandler(), QueryCancelledEvent.class, Object.class);
        _viewsEventBroker.subscribe(new QueryCancelledEventHandler(), QueryCancelledEvent.class, Object.class);

        System.out.println("_modelEventBroker = " + _modelEventBroker);
        System.out.println("_viewsEventBroker = " + _viewsEventBroker);


		_timer = new Timer(TIMER_INTERVAL, this);
		initBackends();
		_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		calculateAppPreferredSize();
		buildMenuBar();
		setJMenuBar(_menuBar);

		_listViewer = new NodesListViewer(_viewsEventBroker);

		buildToolBar();
		buildStatusBar();
		setStatusLabel("status bar is here");

        new LoggingEventListener(_modelEventBroker,GeneralQueryEvent.class,Object.class,System.out);

        new LoggingEventListener(_modelEventBroker,QueryCancelledEvent.class,Object.class,System.out);


		_descriptionViewPanel = new DescriptionView(_viewsEventBroker);
		_queryPanel = new QueryPanel(_viewsEventBroker);
		_treeView = new OntoTreeView(_viewsEventBroker);
		_hyperView = new SimpleHyperView(_viewsEventBroker, projection);

		addComponentsToScrollPanel(_hyperView, _treeView);
		JPanel mainContentPanel = new JPanel(new BorderLayout());

		mainContentPanel.add(_queryPanel, BorderLayout.NORTH);
		mainContentPanel.add(_splitPane, BorderLayout.CENTER);
		mainContentPanel.add(_descriptionViewPanel, BorderLayout.SOUTH);
		getContentPane().add(_toolBar, BorderLayout.NORTH);
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

		if (!OntoramaConfig.loadBlank()) {
			_progressBar.setIndeterminate(true);
            if ( (OntoramaConfig.ontologyRoot.equals("null")) || (OntoramaConfig.ontologyRoot.length() == 0)) {
                _query = new Query();
            }
            else {
			    _query = new Query(OntoramaConfig.ontologyRoot,	OntoramaConfig.getEdgeTypesList());
            }
			//executeQuery(_query);
            _modelEventBroker.processEvent(new QueryStartEvent(_query));
		}
	}

	/**
	 *
	 */
	public OntoRamaApp(	String examplesConfigFilePath, String ontoramaPropertiesPath, String configFilePath) {
		OntoramaConfig.loadAllConfig( configFilePath, ontoramaPropertiesPath, examplesConfigFilePath);
		new OntoRamaApp();
	}

	/**
	 *
	 * @param examplesConfigFilePath
	 * @param ontoramaPropertiesPath
	 * @param configFilePath
	 * @param exampleRoot
	 * @param exampleURL
	 * @todo this approach is a hack to make distillery work. need to rethink whole process
	 */
	public OntoRamaApp( String examplesConfigFilePath, String ontoramaPropertiesPath,
                        String configFilePath, String exampleRoot, String exampleURL) {
		OntoramaConfig.loadAllConfig( configFilePath, ontoramaPropertiesPath, examplesConfigFilePath);
		OntoramaConfig.overrideExampleRootAndUrl(exampleRoot, exampleURL);
		new OntoRamaApp();
	}

//    /**
//     *
//     * @todo hack - to get file backend working (search function). shouldn't have to export graph.
//     */
//    public static Graph getCurrentGraph () {
//        return _graph;
//    }

	private void initBackends() {
		_backends = new Vector();
		List backendsList = OntoramaConfig.getBackends();
		Iterator it = backendsList.iterator();
		while (it.hasNext()) {
			String backendName = (String) it.next();
			try {
				Backend curBackend = (Backend) Class.forName(backendName).newInstance();
				curBackend.setEventBroker(_modelEventBroker);
				_backends.add(curBackend);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				new ErrorPopupMessage("Couldn't find class for backendName " + backendName,	this);
			} catch (InstantiationException instExc) {
				instExc.printStackTrace();
				new ErrorPopupMessage("Couldn't instantiate backendName " + backendName,this);
			} catch (IllegalAccessException illegalAccExc) {
				illegalAccExc.printStackTrace();
				new ErrorPopupMessage("Couldn't load backend "+ backendName + " (Illegal Access Exception)", this);
			}
            catch (Exception e) {
                e.printStackTrace();
                new ErrorPopupMessage("Couldn't load backend " + backendName + ": " + e.getMessage(), this);
            }
		}
	}

	/**
	 * Initialise actions
	 */
	private void initActions() {
		_exitAction = new ExitAction();
		_aboutAction = new AboutOntoRamaAction();
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
		double scaledDividerLocation =
			((double) this._dividerBarLocation * scale);
		int newLeftPanelPercent =
			(currentDividerBarLocation * 100) / this._appWidth;
		double scaledDividerPercent =
			(scaledDividerLocation * 100) / curAppWidth;
		if (((calculateLeftPanelWidth(curAppWidth, newLeftPanelPercent)
			- scaledDividerLocation)
			> 25)
			|| ((scaledDividerLocation
				- calculateLeftPanelWidth(curAppWidth, newLeftPanelPercent))
				> 25)) {
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
	private void setSplitPanelSizes(
		int applicationWidth,
		int applicationHeigth) {
		_mainSplitPaneWidth = applicationWidth;
		_mainSplitPaneHeight = (applicationHeigth * 70) / 100;
		int dividerBarWidth = _splitPane.getDividerSize();
		int leftPanelWidth = calculateLeftPanelWidth(applicationWidth,this._leftSplitPanelWidthPercent);
		int rigthPanelWidth = applicationWidth - leftPanelWidth;
		_hyperView.setPreferredSize(new Dimension(leftPanelWidth - dividerBarWidth,	_mainSplitPaneHeight));
		_treeView.setPreferredSize(new Dimension(rigthPanelWidth - dividerBarWidth,	_mainSplitPaneHeight));
		_splitPane.setPreferredSize(new Dimension(_mainSplitPaneWidth, _mainSplitPaneHeight));
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
	private void addComponentsToScrollPanel(
		JComponent leftComp,
		JComponent rightComp) {
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

		_examplesMenu = new ExamplesMenu(_modelEventBroker);

		_historyMenu = new HistoryMenu(_modelEventBroker);

		_helpMenu = new JMenu("Help");
		_helpMenu.add(_aboutAction);

		_menuBar.add(_fileMenu);
		_menuBar.add(_examplesMenu);
		_menuBar.add(_historyMenu);

		if (!_backends.isEmpty()) {
			JMenu backendsMenu = new JMenu("Backends");
			backendsMenu.setMnemonic(KeyEvent.VK_B);
			Enumeration backendsEnum = _backends.elements();
			while (backendsEnum.hasMoreElements()) {
				Backend backend = (Backend) backendsEnum.nextElement();
				JMenu backendMenu = backend.getJMenu();
				backendsMenu.add(backendMenu);
			}
			_menuBar.add(backendsMenu);
		}
		_menuBar.add(_helpMenu);
	}

	/**
	 *
	 */
	private void buildToolBar() {
		_toolBar = new JToolBar();
		JButton backButton = _toolBar.add(_historyMenu.getBackAction());
		_toolBar.add(backButton);
		JButton forwardButton = _toolBar.add(_historyMenu.getForwardAction());
		_toolBar.add(forwardButton);
		_toolBar.addSeparator();
		_toolBar.add(_listViewer);
		_toolBar.addSeparator();
		if(projection instanceof SphericalProjection) {
			final SphericalProjection sphProj = (SphericalProjection) projection;
			final JSlider focusSlider = new JSlider(JSlider.HORIZONTAL, 1, 20, (int)(sphProj.getRelativeFocus() * 10));
			focusSlider.getModel().addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					sphProj.setRelativeFocus(focusSlider.getModel().getValue() / 10.0);
					_hyperView.repaint();
				}
			});
			focusSlider.setMaximumSize(new Dimension(140,100));
			_toolBar.add(new JLabel("Focal Depth:"));
			_toolBar.add(focusSlider);
		}
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
				_modelEventBroker.processEvent(new QueryEndEvent(graph));
			}
			_timer.stop();
		}
	}

	/**
	 *
	 */
	private void updateViews() {
		setGraphInViews(_graph);
		_queryPanel.setQuery(_query);
		_descriptionViewPanel.clear();
		_descriptionViewPanel.focus(_graph.getRootNode());
		_hyperView.repaint();
		_treeView.repaint();
		_splitPane.repaint();
		setSelectedExampleMenuItem(OntoramaConfig.getCurrentExample());
		enableDisableDynamicFields();
        appendHistoryMenu(_query);
		repaint();
	}

	/**
	 *
	 */
	private void setGraphInViews(Graph graph) {
		//_hyperView.setGraph(graph);
		//_treeView.setGraph(graph);
		_hyperView.setTree(_tree);
		_treeView.setTree(_tree);
		_queryPanel.setGraph(graph);
		_descriptionViewPanel.setGraph(graph);
		_listViewer.setGraph(graph);
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
	public void appendHistoryMenu(Query query) {
        System.out.println("append history for given query " + query);
		_historyMenu.appendHistory(query, OntoramaConfig.getCurrentExample());
	}

	/**
	 *
	 */
	protected void setSelectedExampleMenuItem(OntoramaExample example) {
		_examplesMenu.setSelectedExampleMenuItem(example);
	}

	/**
	 *
	 */
	public static void showErrorDialog(String message) {
		Frame parentFrame = getMainFrame();
		new ErrorPopupMessage(message, parentFrame);
	}

    /**
     *
     */
	public static Frame getMainFrame() {
		Frame[] frames = ontorama.ui.OntoRamaApp.getFrames();
		if (frames.length == 0) {
			return null;
		}
		return frames[0];
	}

	/**
	 *
	 */
	private void buildStatusBar() {
		_statusBar = new JPanel(new BorderLayout());
		_statusLabel = new JLabel();
		_progressBar = new JProgressBar();
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
	public static Vector getBackends() {
		return _backends;
	}
	/**
	 * main
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			new OntoRamaApp();
		} else if (args.length == 3) {
			new OntoRamaApp(args[0], args[1], args[2]);
		} else if (args.length == 5) {
			/// @todo need some errorchecking here, or should pass parameters with options: --root=Root and
			/// then split them into proper parameteres
			new OntoRamaApp(args[0], args[1], args[2], args[3], args[4]);
		} else {
			String usage = " Usage: \n";
			usage =
				usage
					+ "OntoRamaApp [relative/path/to/config.xml relative/path/to/ontorama.properties relative/path/to/examplesConfig.xml] ";
			usage = usage + "[ontologyRoot ontologyURL]";
			usage =
				usage
					+ "To use default OntoRama settings - no need for command line arguments\n";
			usage =
				usage
					+ "To load alternative OntoRama setting - specify relative paths to config.xml, ontorama.properties and examplesConfig.xml\n";
			System.err.println(usage);
			System.exit(-1);
		}
	}
}
