package ontorama.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ontorama.OntoramaConfig;
import ontorama.backends.Backend;
import ontorama.backends.filemanager.FileImporter;
import ontorama.model.graph.Graph;
import ontorama.model.graph.events.GraphLoadedEvent;
import ontorama.model.tree.Tree;
import ontorama.model.tree.TreeImpl;
import ontorama.model.tree.events.TreeChangedEvent;
import ontorama.model.tree.events.TreeLoadedEvent;
import ontorama.ui.events.*;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.query.QueryEngine;
import ontorama.ontotools.query.QueryResult;
import ontorama.ui.action.AboutOntoRamaAction;
import ontorama.ui.action.ExitAction;
import ontorama.ui.controller.QueryNodeEventHandler;
import ontorama.ui.controller.QueryStartEventHandler;
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
 * Main Application class. This class starts OntoRama application.
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
     * holds query panel
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
    private HistoryMenu _historyMenu;
    private JMenu _helpMenu;

    /**
     * application toolbar
     */
    private JToolBar _toolBar;

    /**
     * actions
     */
    public Action _exitAction;
    public Action _aboutAction;

    /**
     * desctiption ui panel contains details for each node
     * of focus (normally these are details not displayed in
     * the graph visualisations).
     */
    private DescriptionView _descriptionViewPanel;

    /**
     * status bar
     */
	private StatusBar _statusBar = new StatusBar();
    private Timer _timer;

    /**
     * holds thread that will do all the work: querying ont server
     * and building graph
     */
    private ThreadWorker _worker;

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
    private static int _leftSplitPanelWidthPercent = 65;

    /**
     * nodes list viewer, used to show unconnected nodes
     */
    private NodesListViewer _listViewer;
    
    private static EventBroker _modelEventBroker;
    private static EventBroker _viewsEventBroker;

    private Projection projection = new SphericalProjection(300, 0.8);

    /**
     * timer interval - interval for swing timer thread to check for
     * progress on worker thread and update status and progress bar.
     */
    private static final int TIMER_INTERVAL = 100;
    
    private String CONFIGURATION_SECTION_NAME = "MainWindow";

    private class ViewUpdateHandler implements EventBrokerListener {
        public void processEvent(Event e) {
            //TreeImpl tree = (TreeImpl) e.getSubject();
            updateViews();
        }
    }
    
	private class QueryEngineThreadStartEventHandler implements EventBrokerListener {
		public void processEvent(Event event) {
			QueryEngine queryEngine = (QueryEngine) event.getSubject();
			QueryEngineThreadStartEvent e = (QueryEngineThreadStartEvent) event;
			Query query = e.getQuery();
			_lastQuery = _query;
			_query = query;
			_worker = new QueryEngineThread(queryEngine, _query, _modelEventBroker);
			_modelEventBroker.removeSubscriptions(_viewsEventBroker);
			_worker.start();
			_timer.start();
			_statusBar.startProgressBar("Getting data from data source");
			_queryPanel.enableStopQueryAction(true);
		}
	}

    private class GraphIsLoadedEventHandler implements EventBrokerListener {
        public void processEvent(Event event) {
            Graph graph = (Graph) event.getSubject();
			_statusBar.stopProgressBar();
            _queryPanel.enableStopQueryAction(false);
            _queryPanel.enableQueryActions(true);
            _modelEventBroker.subscribe(
                _viewsEventBroker,
                TreeChangedEvent.class,
                Object.class);
            _graph = graph;
            _modelEventBroker.processEvent(new GraphLoadedEvent(_graph));
            _tree =
                new TreeImpl(_graph, _graph.getRootNode(), _modelEventBroker);
            _modelEventBroker.processEvent(new TreeLoadedEvent(_tree));
            _viewsEventBroker.subscribe(
                new ViewUpdateHandler(),
                TreeChangedEvent.class,
                Tree.class);
            updateViews();
        }
    }

    class QueryCancelledEventHandler implements EventBrokerListener {
        public void processEvent(Event event) {
            _worker.stopProcess();
			_statusBar.stopProgressBar();
            _queryPanel.enableStopQueryAction(false);
            
            if (_lastQuery != null) {
	            _query = _lastQuery;
	            _queryPanel.setQuery(_query);
	            _modelEventBroker.subscribe(
	                _viewsEventBroker,
	                TreeChangedEvent.class,
	                Object.class);
            }
        }
    }
    
    private class PutThinkingCapOnEventHandler implements EventBrokerListener {
    	public void  processEvent (Event event) {
    		String message = (String) event.getSubject();
			_statusBar.startProgressBar(message);
    	}
    }

	private class TakeThinkingCapOffEventHandler implements EventBrokerListener {
		public void  processEvent (Event event) {
			_statusBar.stopProgressBar();
		}
	}

	private class QueryIsFinishedEventHandler implements EventBrokerListener {
		public void processEvent(Event event) {
			QueryResult queryResult = (QueryResult) event.getSubject();
			_worker = new GraphCreatorThread(queryResult, _modelEventBroker);
			_worker.start();
			_timer.restart();
		}
	}

    public OntoRamaApp() {
        super("OntoRama");
        initActions();
        _modelEventBroker = new EventBroker();
        _viewsEventBroker = new EventBroker();

        /// @todo need to sort out what broker is responsible for handling what events

        new QueryNodeEventHandler(_modelEventBroker);
        new QueryNodeEventHandler(_viewsEventBroker);


        _modelEventBroker.subscribe(
				            new QueryStartEventHandler(_modelEventBroker),
				            QueryStartEvent.class,
				            Object.class);

        _modelEventBroker.subscribe(
				            new GraphIsLoadedEventHandler(),
				            GraphIsLoadedEvent.class,
				            Object.class);
        _viewsEventBroker.subscribe(
				            new GraphIsLoadedEventHandler(),
				            GraphIsLoadedEvent.class,
				            Object.class);

        _modelEventBroker.subscribe(
				            new QueryCancelledEventHandler(),
				            QueryCancelledEvent.class,
				            Object.class);
        _viewsEventBroker.subscribe(
				            new QueryCancelledEventHandler(),
				            QueryCancelledEvent.class,
				            Object.class);

		_modelEventBroker.subscribe(
							new PutThinkingCapOnEventHandler(),
							PutThinkingCapOnEvent.class,
							Object.class);
		_modelEventBroker.subscribe(
							new TakeThinkingCapOffEventHandler(),
							TakeThinkingCapOffEvent.class,
							Object.class);
							
		_modelEventBroker.subscribe(
							new QueryIsFinishedEventHandler(),
							QueryIsFinishedEvent.class,
							Object.class);

		_modelEventBroker.subscribe(
							new QueryEngineThreadStartEventHandler(),
							QueryEngineThreadStartEvent.class,
							Object.class);

        _timer = new Timer(TIMER_INTERVAL, this);
        initBackend();
        _splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        _splitPane.setResizeWeight(_leftSplitPanelWidthPercent / 100.0);
        buildMenuBar();
        setJMenuBar(_menuBar);

        _listViewer = new NodesListViewer(_viewsEventBroker);

        buildToolBar();

        new LoggingEventListener(
				            _modelEventBroker,
				            QueryCancelledEvent.class,
				            Object.class,
				            System.out);


        _descriptionViewPanel = new DescriptionView(_viewsEventBroker);
        _queryPanel = new QueryPanel(_viewsEventBroker);
        _treeView = new OntoTreeView(_viewsEventBroker);
        _hyperView = new SimpleHyperView(_viewsEventBroker, projection);

		JSplitPane leftSideSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    	leftSideSplitPane.add(_treeView);
		JPanel backendPanel = OntoramaConfig.getBackend().getPanel();
		if (backendPanel != null) {
			leftSideSplitPane.setResizeWeight(0.7);
			leftSideSplitPane.setOneTouchExpandable(true);
			leftSideSplitPane.add( backendPanel);
		}

        addComponentsToScrollPanel(_hyperView, leftSideSplitPane);
        JPanel mainContentPanel = new JPanel(new BorderLayout());

        mainContentPanel.add(_queryPanel, BorderLayout.NORTH);
        mainContentPanel.add(_splitPane, BorderLayout.CENTER);
        mainContentPanel.add(_descriptionViewPanel, BorderLayout.SOUTH);
        getContentPane().add(_toolBar, BorderLayout.NORTH);
        getContentPane().add(mainContentPanel, BorderLayout.CENTER);
        getContentPane().add(_statusBar, BorderLayout.SOUTH);

        pack();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle rect = new Rectangle(10, 10, (int)screenSize.getWidth() - 20, (int)screenSize.getHeight() - 20);
        ConfigurationManager.restorePlacement(CONFIGURATION_SECTION_NAME, this, rect);
        int divPos = ConfigurationManager.fetchInt(CONFIGURATION_SECTION_NAME, "mainDividerPos", (int) (this.getWidth() * 0.65) );
        _splitPane.setDividerLocation(divPos);
		this.setBounds(rect);
        setVisible(true);
    }

    /**
     * @todo this contructor and the next create two objects. The call to new
     * creates a second object in addition to the constructor itself. Probably
     * the default constructor should not rely on a loaded configuration, there
     * should be a load event for the configuration causing the needed updates.
     */
    public OntoRamaApp(String examplesConfigFilePath,
				        String ontoramaPropertiesPath,
				        String configFilePath)
    {
        OntoramaConfig.loadAllConfig(
            configFilePath,
            ontoramaPropertiesPath,
            examplesConfigFilePath);
        new OntoRamaApp();
    }
   
    private void initBackend() {
    	String backendName = OntoramaConfig.getBackendPackageName();
        if (backendName == null) {
        	ErrorDialog.showError(this, "Error", "No backends specified in ontorama.properties");
        	System.exit(1);
        }
		Backend backend = OntoramaConfig.instantiateBackend(backendName, this);
		System.out.println("\nOntoRamaApp::initBackend passing event broker " + _modelEventBroker + "\n");
		backend.setEventBroker(_modelEventBroker);
    }

    /**
     * Initialise actions
     */
    private void initActions() {
        _exitAction = new ExitAction();
        _aboutAction = new AboutOntoRamaAction();
    }

    /**
     * Add the scroll panes to a split pane
     */
    private void addComponentsToScrollPanel(JComponent leftComp, JComponent rightComp) {
        _splitPane.setLeftComponent(leftComp);
        _splitPane.setRightComponent(rightComp);
        _splitPane.setOneTouchExpandable(true);
    }

    private void buildMenuBar() {
        _menuBar = new JMenuBar();
        _fileMenu = new JMenu("File");
        _fileMenu.setMnemonic(KeyEvent.VK_F);
               
               
		if (!OntoramaConfig.SECURITY_RESTRICTED) {               
	        JMenu importMenu = new JMenu("Import");
	    	Action fileImportAction = new AbstractAction("from file backend...") {
	    		public void actionPerformed(ActionEvent e) {
	    			FileImporter importer = new FileImporter(_modelEventBroker);
	    			importer.doImport();
	    		}
	    	};
	    	importMenu.add(fileImportAction);
	    	
	    	
	    	Action proxySettingsMenuAction = new AbstractAction("Configure proxy settings") {
	    		public void actionPerformed(ActionEvent e) {
	    			new ProxySettingsDialog(OntoRamaApp.getMainFrame(), "Cofigure Proxies", true);
	    		}
	    	};
	    	
	    	
	    	
	    	_fileMenu.add(importMenu);
	
	    	_fileMenu.add(proxySettingsMenuAction);
		}
        
        _fileMenu.add(_exitAction);


        _historyMenu = new HistoryMenu(_modelEventBroker);

        _helpMenu = new JMenu("Help");
        _helpMenu.add(_aboutAction);

        _menuBar.add(_fileMenu);
        _menuBar.add(_historyMenu);

        JMenu backendsMenu = OntoramaConfig.getBackend().getMenu();

        _menuBar.add(backendsMenu);

        _menuBar.add(_helpMenu);
    }

    private void buildToolBar() {
        _toolBar = new JToolBar();
        JButton backButton = _toolBar.add(_historyMenu.getBackAction());
        _toolBar.add(backButton);
        JButton forwardButton = _toolBar.add(_historyMenu.getForwardAction());
        _toolBar.add(forwardButton);
        _toolBar.addSeparator();
        _toolBar.add(_listViewer);
        _toolBar.addSeparator();
        if (projection instanceof SphericalProjection) {
            final SphericalProjection sphProj =
                (SphericalProjection) projection;
            final JSlider focusSlider =
                new JSlider(
                    JSlider.HORIZONTAL,
                    1,
                    20,
                    (int) (sphProj.getRelativeFocus() * 10));
            focusSlider.getModel().addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    sphProj.setRelativeFocus(
                        focusSlider.getModel().getValue() / 10.0);
                    _hyperView.repaint();
                }
            });
            focusSlider.setMaximumSize(new Dimension(140, 100));
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
        _statusBar.setStatusLabel(_worker.getMessage());
		if (_worker.isStopped()) {
			_timer.stop();
			_modelEventBroker.processEvent(new QueryCancelledEvent(_query));
			return;
		}
		if (_worker.done()) {
			_timer.stop();
        	if (_worker instanceof QueryEngineThread) {
                QueryResult qr = (QueryResult) _worker.getResult();
                _modelEventBroker.processEvent(new QueryIsFinishedEvent(qr));
			} else if (_worker instanceof GraphCreatorThread) { 				
				Graph graph = (Graph) _worker.getResult();
				_modelEventBroker.processEvent(new GraphIsLoadedEvent(graph));
			}
        }
    }

    private void updateViews() {
        setGraphInViews(_graph);
        _descriptionViewPanel.clear();
        _descriptionViewPanel.focus(_graph.getRootNode());
        _hyperView.repaint();
        _treeView.repaint();
        _splitPane.repaint();
        if (_query != null) {
	    	_queryPanel.setQuery(_query);
	        _historyMenu.updateHistory(_query);
        }
        repaint();
    }

    private void setGraphInViews(Graph graph) {
        _hyperView.setTree(_tree);
        _treeView.setTree(_tree);
        _queryPanel.setGraph(graph);
        _descriptionViewPanel.setGraph(graph);
        _listViewer.setGraph(graph);
    }

    public static Frame getMainFrame() {
        Frame[] frames = ontorama.ui.OntoRamaApp.getFrames();
        if (frames.length == 0) {
            return null;
        }
        return frames[0];
    }

    protected void closeWindow() {
    	if (! OntoramaConfig.SECURITY_RESTRICTED) {
			ConfigurationManager.storePlacement(CONFIGURATION_SECTION_NAME, this);
			ConfigurationManager.storeInt(CONFIGURATION_SECTION_NAME, "mainDividerPos", _splitPane.getDividerLocation());
			ConfigurationManager.saveConfiguration();
    	}
        System.exit(0);
    }

    /**
     * main
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            new OntoRamaApp();
        } else if (args.length == 3) {
            new OntoRamaApp(args[0], args[1], args[2]);
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
