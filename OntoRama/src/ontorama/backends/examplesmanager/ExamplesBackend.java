package ontorama.backends.examplesmanager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenu;

import ontorama.OntoramaConfig;
import ontorama.backends.Backend;
import ontorama.backends.examplesmanager.gui.ExamplesMenu;
import ontorama.conf.ConfigParserException;
import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeImpl;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Graph;
import ontorama.model.graph.GraphCyclesDisallowedException;
import ontorama.model.graph.GraphImpl;
import ontorama.model.graph.GraphModificationException;
import ontorama.model.graph.NoTypeFoundInResultSetException;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeImpl;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.SourceException;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.query.QueryResult;
import ontorama.ui.ErrorPopupMessage;
import ontorama.ui.OntoRamaApp;
import ontorama.ui.events.QueryCancelledEvent;
import ontorama.ui.events.QueryEndEvent;
import ontorama.ui.events.QueryStartEvent;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

/**
 * This is backend to be used by test cases.
 * @author nataliya
 */
public class ExamplesBackend implements Backend {
	
	private List _dataFormats = OntoramaConfig.getDataFormatsMapping();

	private List _examples = new LinkedList();
	
	private OntoramaExample _curExample;
	private OntoramaExample _prevExample;

	private EventBroker _eventBroker;

	private ExamplesMenu _menu;
	
	private boolean _isNewExample = false;

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
    
    private class QueryEndEventHandler implements EventBrokerListener {
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

	/**
	 * @see ontorama.backends.Backend#getMenu()
	 */
	public JMenu getMenu() {
		return _menu;
	}

	/**
	 * @see ontorama.backends.Backend#getPanels()
	 */
	public List getPanels() {
		return null;
	}

	/**
	 * @see ontorama.backends.Backend#setEventBroker(org.tockit.events.EventBroker)
	 */
	public void setEventBroker(EventBroker eventBroker) {
		_eventBroker = eventBroker;
        _eventBroker.subscribe(new QueryCancelledEventHandler(),QueryCancelledEvent.class,Query.class);
        _eventBroker.subscribe(new QueryEndEventHandler(),QueryEndEvent.class,Object.class);
	}

	/**
	 * @see ontorama.backends.Backend#createNode(java.lang.String, java.lang.String)
	 */
	public Node createNode(String name, String fullName) {
		return new NodeImpl(name, fullName);
	}

	/**
	 * @see ontorama.backends.Backend#createEdge(ontorama.model.graph.Node, ontorama.model.graph.Node, ontorama.model.graph.EdgeType)
	 */
	public Edge createEdge(Node fromNode, Node toNode, EdgeType edgeType) throws NoSuchRelationLinkException {
		return new EdgeImpl(fromNode, toNode, edgeType);
	}

	/**
	 * @see ontorama.backends.Backend#getDataFormats()
	 */
	public Collection getDataFormats() {
		return null;
	}

	/**
	 * @see ontorama.backends.Backend#getParser()
	 */
	public String getParser() {
		return _curExample.getDataFormatMapping().getParserName();
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
		}
		catch (SourceException sourceExc) {
			sourceExc.printStackTrace();
			new ErrorPopupMessage("Unable to read properties or configuration file " + ". Error: " + sourceExc.getMessage(), OntoRamaApp.getMainFrame());
		} catch (ConfigParserException cpe) {
			cpe.printStackTrace();
			new ErrorPopupMessage("ConfigParserException: " + cpe.getMessage(), OntoRamaApp.getMainFrame());
		} catch (IOException e) {
			e.printStackTrace();
			new ErrorPopupMessage(e.getMessage(), OntoRamaApp.getMainFrame());
		}
	}

	public void setCurrentExample(OntoramaExample example) {
        _isNewExample = true;
		_prevExample = _curExample;
		_curExample = example;
	}

	public List getExamplesList() {
		return _examples;
	}

	/**
	 * @see ontorama.backends.Backend#getSourcePackageName()
	 */
	public String getSourcePackageName() {
		return _curExample.getSourcePackagePathSuffix();
	}
	
	public String getSourceUri() {
		return _curExample.getRelativeUri();
	}
	
	public String getOntologyRoot() {
		return _curExample.getRoot();
	}
	
	/**
	 * @todo this approach is a hack to make distillery work. need to rethink whole process
	 */
	public void overrideExampleRootAndUrl (String root, String url) {
		System.out.println("Overriden sourceUri = " + url + ", root = " + root);
		_curExample.setRoot(root);
		_curExample.setRelativeUri(url);
	}
	
	/**
	 * @see ontorama.backends.Backend#createGraph(ontorama.ontotools.query.QueryResult, org.tockit.events.EventBroker)
	 */
	public Graph createGraph(QueryResult qr, EventBroker eb)
										throws	GraphModificationException,	NoSuchRelationLinkException,
										NoTypeFoundInResultSetException, GraphCyclesDisallowedException {
		Graph res = new GraphImpl(qr, eb);
		return res;
	}
	
	public void processQueryFromExampleMenu (OntoramaExample example) {
	
        // reset parser and source details corresponding to this example.       
        setCurrentExample(example);

        // create a new query
        Query query = new Query(example.getRoot(), OntoramaConfig.getEdgeTypesList(), getSourcePackageName(), getParser(), getSourceUri());

        // get graph for this query and load it into app
        _eventBroker.processEvent(new QueryStartEvent(query));
	}

}
