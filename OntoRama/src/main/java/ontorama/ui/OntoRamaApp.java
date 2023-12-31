package ontorama.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ontorama.OntoramaConfig;
import ontorama.backends.Backend;
import ontorama.backends.filemanager.FileImporter;
import ontorama.model.graph.Graph;
import ontorama.model.tree.Tree;
import ontorama.model.tree.TreeImpl;
import ontorama.model.tree.events.TreeChangedEvent;
import ontorama.model.tree.events.TreeLoadedEvent;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.query.QueryEngine;
import ontorama.ontotools.query.QueryResult;
import ontorama.ui.action.AboutOntoRamaAction;
import ontorama.ui.action.ExitAction;
import ontorama.ui.controller.QueryNodeEventHandler;
import ontorama.ui.controller.QueryStartEventHandler;
import ontorama.ui.events.GraphIsLoadedEvent;
import ontorama.ui.events.QueryCancelledEvent;
import ontorama.ui.events.QueryEngineThreadStartEvent;
import ontorama.ui.events.QueryIsFinishedEvent;
import ontorama.ui.events.QueryStartEvent;
import ontorama.ui.events.UpdateViewsWithQueryCancelledEvent;
import ontorama.views.hyper.view.Projection;
import ontorama.views.hyper.view.SimpleHyperView;
import ontorama.views.hyper.view.SphericalProjection;
import ontorama.views.textDescription.view.DescriptionView;
import ontorama.views.tree.view.OntoTreeView;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import com.pagosoft.plaf.PlafOptions;

/**
 * Main Application class. This class starts OntoRama application.
 */
@SuppressWarnings("serial")
public class OntoRamaApp extends JFrame {
    
    private SimpleHyperView _hyperView;
    private OntoTreeView _treeView;
    public QueryPanel _queryPanel;

    /**
     * split panel will contain hyper ui and tree ui
     */
    private JSplitPane _splitPane;

    private JMenuBar _menuBar;

    /*
     * ontorama menus
     */
    private JMenu _fileMenu;
    private HistoryMenu _historyMenu;
    private JMenu _helpMenu;

    private JToolBar _toolBar;

    /*
     * actions
     */
    public Action _exitAction;
    public Action _aboutAction;

    /**
     * Description UI panel contains details for each node
     * of focus (normally these are details not displayed in
     * the graph visualizations).
     */
    private DescriptionView _descriptionViewPanel;

	private final StatusBar _statusBar = new StatusBar();

    /**
     * holds current query
     */
    private Query _query;

    /**
     * previous successful query
     */
    private Query _lastQuery = null;

    /**
     * currently displayed tree
     */
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

    private final Projection projection = new SphericalProjection(300, 0.8);
   
    private final String CONFIGURATION_SECTION_NAME = "MainWindow";

    private class ViewUpdateHandler implements EventBrokerListener {
        public void processEvent(Event e) {
            TreeImpl tree = (TreeImpl) e.getSubject();
            updateViews(tree.getGraph());
        }
    }
    
	private class QueryEngineThreadStartEventHandler implements EventBrokerListener {
		public void processEvent(Event event) {
			QueryEngine queryEngine = (QueryEngine) event.getSubject();
			QueryEngineThreadStartEvent e = (QueryEngineThreadStartEvent) event;
			Query query = e.getQuery();
			_lastQuery = _query;
			_query = query;
			_statusBar.startProgressBar("Getting data from data source");
			QueryEngineThread worker = new QueryEngineThread(queryEngine, _query, _viewsEventBroker);
			_modelEventBroker.removeSubscriptions(_viewsEventBroker);
			worker.start();
			_queryPanel.enableStopQueryAction(true);
		}
	}

    private class GraphIsLoadedEventHandler implements EventBrokerListener {
        public void processEvent(Event event) {
            Graph graph = (Graph) event.getSubject();
            assert graph != null : "We expected to get a new graph";
            assert graph.getRootNode() != null : "The new graph should have a root node";
			_statusBar.stopProgressBar();
            _queryPanel.enableStopQueryAction(false);
            _queryPanel.enableQueryActions(true);
            _modelEventBroker.subscribe(_viewsEventBroker, TreeChangedEvent.class, Object.class);
            _tree = new TreeImpl(graph, graph.getRootNode());
            _modelEventBroker.processEvent(new TreeLoadedEvent(_tree));
            _viewsEventBroker.subscribe( new ViewUpdateHandler(), TreeChangedEvent.class, Tree.class);
			_tree.getEventBroker().subscribe(_viewsEventBroker, TreeChangedEvent.class, Tree.class);
            updateViews(graph);
        }
    }

	/**
	 * This event handler is only interested in this event in order to 
	 * reset UI and revert to the previous query. 
	 */
    class UpdateViewsWithQueryCancelledEventHandler implements EventBrokerListener {
        public void processEvent(Event event) {
			_statusBar.stopProgressBar();
            _queryPanel.enableStopQueryAction(false);
            
            if (_lastQuery != null) {
	            _query = _lastQuery;
	            _queryPanel.setQuery(_query);
	            _modelEventBroker.subscribe( _viewsEventBroker, TreeChangedEvent.class, Object.class);
            }
        }
    }
    
    private class QueryIsFinishedEventHandler implements EventBrokerListener {
		public void processEvent(Event event) {
			QueryResult queryResult = (QueryResult) event.getSubject();
			_statusBar.startProgressBar("Building data model");
			GraphCreatorThread worker = new GraphCreatorThread(queryResult, _modelEventBroker, _viewsEventBroker);
			worker.start();
		}
	}

    public OntoRamaApp() {
        super("OntoRama");
        initActions();
        _modelEventBroker = new EventBroker();
        _viewsEventBroker = new EventBroker();

        /// @todo need to sort out what broker is responsible for handling what events
        // only views are using this event
        new QueryNodeEventHandler(_viewsEventBroker);

		_viewsEventBroker.subscribe(
							new QueryStartEventHandler(_viewsEventBroker),
							QueryStartEvent.class,
							Object.class);

		_viewsEventBroker.subscribe(
							new QueryEngineThreadStartEventHandler(),
							QueryEngineThreadStartEvent.class,
							Object.class);
							
		_viewsEventBroker.subscribe(
							new QueryIsFinishedEventHandler(),
							QueryIsFinishedEvent.class,
							Object.class);

        _viewsEventBroker.subscribe(
				            new GraphIsLoadedEventHandler(),
				            GraphIsLoadedEvent.class,
				            Object.class);

        _viewsEventBroker.subscribe(
				            new UpdateViewsWithQueryCancelledEventHandler(),
							UpdateViewsWithQueryCancelledEvent.class,
				            Query.class);

		new ExtendedLoggingEventListener(
							_modelEventBroker,
							QueryCancelledEvent.class,
							Object.class,
							System.out);
		new ExtendedLoggingEventListener(
							_viewsEventBroker,
							QueryCancelledEvent.class,
							Object.class,
							System.out);

        initBackend();
        _splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        _splitPane.setResizeWeight(_leftSplitPanelWidthPercent / 100.0);
        buildMenuBar();
        setJMenuBar(_menuBar);

        _listViewer = new NodesListViewer(_viewsEventBroker);

        buildToolBar();

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
            @Override
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
     * TODO this constructor and the next create two objects. The call to new
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
		backend.setEventBroker(_viewsEventBroker);
    }

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
	    	Action fileImportAction = new AbstractAction("Load file...") {
	    		public void actionPerformed(ActionEvent e) {
	    			FileImporter importer = new FileImporter(_viewsEventBroker);
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
                    SwingConstants.HORIZONTAL,
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

    private void updateViews(Graph graph) {
        setGraphInViews(graph);
        _descriptionViewPanel.clear();
        _descriptionViewPanel.focus(graph.getRootNode());
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
        Frame[] frames = Frame.getFrames();
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

    public static void main(final String[] args) {
        String lnf = OntoramaConfig.getLookAndFeel();
        try {
            if ("PGS".equals(lnf)) {
                PlafOptions.setAsLookAndFeel();
            } else {
                UIManager.setLookAndFeel(lnf);
            }
        } catch (Exception e) {
            Logger.getLogger(OntoRamaApp.class.getName()).log(Level.WARNING,
                                                              "Failed to load look and feel '" + lnf + "'", e);
        }
        if (args.length == 0) {
        	SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
		            new OntoRamaApp();
				}
        	});
        } else if (args.length == 3) {
        	SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
		            new OntoRamaApp(args[0], args[1], args[2]);
				}
        	});
        } else {
            String usage = " Usage: \n";
            usage += "OntoRamaApp [relative/path/to/config.xml relative/path/to/ontorama.properties relative/path/to/examplesConfig.xml] ";
            usage += "To use default OntoRama settings - no need for command line arguments\n";
            usage += "To load alternative OntoRama setting - specify relative paths to config.xml, ontorama.properties and examplesConfig.xml\n";
            System.err.println(usage);
            System.exit(-1);
        }
    }
}
