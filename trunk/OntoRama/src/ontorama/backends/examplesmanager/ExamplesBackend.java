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
import ontorama.model.graph.GraphImpl;
import ontorama.model.graph.GraphModificationException;
import ontorama.model.graph.NoTypeFoundInResultSetException;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeImpl;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.SourceException;
import ontorama.ontotools.query.QueryResult;
import ontorama.ui.ErrorPopupMessage;
import ontorama.ui.OntoRamaApp;

import org.tockit.events.EventBroker;

/**
 * This is backend to be used by test cases.
 * @author nataliya
 */
public class ExamplesBackend implements Backend {
	
	private List _dataFormats = OntoramaConfig.getDataFormatsMapping();

	private List _examples = new LinkedList();
	private OntoramaExample _curExample;

	private String _parserPackage;
	private String _sourcePackage;
	
	private EventBroker _eventBroker;
	
	/**
	 * URI for Ontology Source. It can be file or URL to CGI script.
	 * If file is used - it is important to formulate correct URI,
	 * for example:
	 * file:/H:/devel/OntoRama/test/wn_cat-children_rdf.html
	 * for file H:/devel/OntoRama/test/wn_cat-children_rdf.html
	 */	
	private String _sourceUri;
	
	/**
	 * default ontology root
	 */	
	private String _ontologyRoot;

	private ExamplesMenu _menu;

	/**
	 * Constructor for TestingBackend.
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
		_menu.setEventBroker(_eventBroker);
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
		return _parserPackage;
	}
	
	
	
	private void loadExamples() {
		try {
			String examplesConfigLocation = OntoramaConfig.examplesConfigLocation;
			InputStream examplesConfigStream = OntoramaConfig.streamReader.getInputStreamFromResource(examplesConfigLocation);
			XmlExamplesConfigParser examplesConfig = new XmlExamplesConfigParser(examplesConfigStream);
			_examples = examplesConfig.getExamplesList();
			_curExample = examplesConfig.getMainExample();
	
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
		_curExample = example;
		_sourceUri = example.getRelativeUri();
		_parserPackage = example.getDataFormatMapping().getParserName();
		_sourcePackage =  example.getSourcePackagePathSuffix();
		_ontologyRoot = example.getRoot();
	}

	public List getExamplesList() {
		return _examples;
	}


	/**
	 * @see ontorama.backends.Backend#getSourcePackageName()
	 */
	public String getSourcePackageName() {
		return _sourcePackage;
	}
	
	public String getSourceUri() {
		return _sourceUri;
	}
	
	public String getOntologyRoot() {
		return _ontologyRoot;
	}
	
	/**
	 * @todo this approach is a hack to make distillery work. need to rethink whole process
	 */
	public void overrideExampleRootAndUrl (String root, String url) {
		_sourceUri = url;
		_ontologyRoot = root;
		System.out.println("Overriden sourceUri = " + _sourceUri + ", root = " + _ontologyRoot);
		_curExample.setRoot(root);
		_curExample.setRelativeUri(url);
	}
	

	/**
	 * @see ontorama.backends.Backend#createGraph(ontorama.ontotools.query.QueryResult, org.tockit.events.EventBroker)
	 */
	public Graph createGraph(QueryResult qr, EventBroker eb)
												throws
													GraphModificationException,
													NoSuchRelationLinkException,
													NoTypeFoundInResultSetException {
		Graph res = new GraphImpl(qr, eb);
		return res;
	}

}
