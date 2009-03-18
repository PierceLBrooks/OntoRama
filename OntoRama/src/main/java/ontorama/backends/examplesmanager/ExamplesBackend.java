package ontorama.backends.examplesmanager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JPanel;

import ontorama.OntoramaConfig;
import ontorama.backends.Backend;
import ontorama.backends.examplesmanager.gui.ExamplesMenu;
import ontorama.conf.ConfigParserException;
import ontorama.conf.DataFormatMapping;
import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeImpl;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Graph;
import ontorama.model.graph.GraphImpl;
import ontorama.model.graph.InvalidArgumentException;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeImpl;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.QueryFailedException;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.query.QueryEngine;
import ontorama.ontotools.query.QueryResult;
import ontorama.ui.ErrorDialog;
import ontorama.ui.HistoryElement;
import ontorama.ui.OntoRamaApp;
import ontorama.ui.events.GraphIsLoadedEvent;
import ontorama.ui.events.QueryCancelledEvent;
import ontorama.ui.events.QueryStartEvent;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

/**
 * This is backend to be used by test cases.
 */
public class ExamplesBackend implements Backend {
	
	private List<OntoramaExample> _examples = new ArrayList<OntoramaExample>();
	
	private OntoramaExample _curExample;
	private OntoramaExample _prevExample;

	private EventBroker _eventBroker;

	private final ExamplesMenu _menu;
	
	private boolean _isNewExample = false;
	
	private QueryEngine _lastQueryEngine;
	

	/**
	 * Handle a case where query is not completed for whatever reason - 
	 * we then need to return to the previous Example's settings. Note that we 
	 * do that only when we are processing query on a new example. If there is 
	 * a multiple queries on an existing example - we don't want to roll back to
	 * an older example.
	 * @todo perhaps  a better solution would be to not leave old graph displayed when querying?... but have a back button for it...
	 */
    private class QueryCancelledEventHandler implements EventBrokerListener {
        public void processEvent(Event event) {
        	if (_isNewExample) {
	        	_curExample = _prevExample;
	        	_menu.setSelectedExampleMenuItem(_curExample);
        	}
        	_isNewExample = false;
        }
    }
    
    private class GraphIsLoadedEventHandler implements EventBrokerListener {
        public void processEvent(Event event) {
        	_isNewExample = false;
        }
    }
    

	/**
	 * Constructor. Load all examples defined in the config file.
	 */
	public ExamplesBackend() {
		loadExamples();
		_menu = new ExamplesMenu(this);
	}

	public JMenu getMenu() {
		return _menu;
	}

	public JPanel getPanel() {
		return null;
	}
	
	public void setEventBroker(EventBroker viewsEventBroker) {
		_eventBroker = viewsEventBroker;
        _eventBroker.subscribe(new QueryCancelledEventHandler(),QueryCancelledEvent.class,Query.class);
        _eventBroker.subscribe(new GraphIsLoadedEventHandler(),GraphIsLoadedEvent.class,Object.class);
	}
	
	public Node createNode(String name, String fullName) {
		return new NodeImpl(name, fullName);
	}

	public Edge createEdge(Node fromNode, Node toNode, EdgeType edgeType) throws NoSuchRelationLinkException {
		return new EdgeImpl(fromNode, toNode, edgeType);
	}

	public Collection<DataFormatMapping> getDataFormats() {
		return null;
	}

	private void loadExamples() {
		try {
			String examplesConfigLocation = OntoramaConfig.examplesConfigLocation;
			InputStream examplesConfigStream = OntoramaConfig.streamReader.getInputStreamFromResource(examplesConfigLocation);
			XmlExamplesConfigParser examplesConfig = new XmlExamplesConfigParser(examplesConfigStream);
			_examples = examplesConfig.getExamplesList();
			_curExample = examplesConfig.getMainExample();
	
			_prevExample = _curExample;
			setCurrentExample(_curExample);
		} catch (ConfigParserException cpe) {
			cpe.printStackTrace();
			ErrorDialog.showError(OntoRamaApp.getMainFrame(), cpe, "Error", "ConfigParserException: " + cpe.getMessage());
		}
	}

	public void setCurrentExample(OntoramaExample example) {
        _isNewExample = true;
		_prevExample = _curExample;
		_curExample = example;
	}

	public List<OntoramaExample> getExamplesList() {
		return _examples;
	}

	public String getSourceUri() {
		return _curExample.getRelativeUri();
	}
	
	public String getOntologyRoot() {
		return _curExample.getRoot();
	}
	
	public Graph createGraph(QueryResult qr) throws InvalidArgumentException {
		return new GraphImpl(qr);
	}
	
	public void processQueryFromExampleMenu (OntoramaExample example) {
        // reset parser and source details corresponding to this example.       
        setCurrentExample(example);
        // create a new query
        Query query = new Query(example.getRoot());
        // get graph for this query and load it into app
        _eventBroker.processEvent(new QueryStartEvent(query));
	}

	protected void processQueryFromHistoryElement (OntoramaExample example, Query query) {
		// reset parser and source details corresponding to this example.
		setCurrentExample(example);
		// get graph for this query and load it into app
		_eventBroker.processEvent(new QueryStartEvent(query));
		_menu.setSelectedExampleMenuItem(_curExample);
	}

	public HistoryElement createHistoryElement(Query query, EventBroker eventBroker) {
		ExamplesHistoryElement res = new ExamplesHistoryElement(query, eventBroker, this, _curExample);
		return res;
	}


	public QueryEngine getQueryEngine() throws QueryFailedException {
		_lastQueryEngine = new QueryEngine( _curExample.getDataSource(), _curExample.getDataFormatMapping().getParser(), _curExample.getRelativeUri());
		return _lastQueryEngine;
	}

}
