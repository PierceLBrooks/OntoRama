package ontorama.backends.filemanager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JPanel;

import ontorama.OntoramaConfig;
import ontorama.backends.Backend;
import ontorama.backends.filemanager.gui.FileJMenu;
import ontorama.conf.DataFormatMapping;
import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeImpl;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Graph;
import ontorama.model.graph.GraphImpl;
import ontorama.model.graph.InvalidArgumentException;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeImpl;
import ontorama.ui.ErrorDialog;
import ontorama.ui.HistoryElement;
import ontorama.ui.OntoRamaApp;
import ontorama.ui.events.GeneralQueryEvent;
import ontorama.model.graph.events.GraphLoadedEvent;
import ontorama.ontotools.CancelledQueryException;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.NoSuchTypeInQueryResult;
import ontorama.ontotools.QueryFailedException;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.query.QueryEngine;
import ontorama.ontotools.query.QueryResult;
import ontorama.ontotools.writer.ModelWriter;
import ontorama.ontotools.writer.ModelWriterException;
import ontorama.ontotools.writer.rdf.RdfModelWriter;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;


/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FileBackend implements Backend {
    private Graph _graph = null;
    private EventBroker _eventBroker;
    private String _parserName;
    
    private List _dataFormatsMapping = OntoramaConfig.getDataFormatsMapping();
	private String _sourcePackageName = "ontorama.ontotools.source.FileSource";
	private String _filename;
	
	private QuerySettings _querySettings;
	
	private QueryEngine _lastQueryEngine;
	
	private Hashtable _queryEngineToQueryConfigMapping;

    private class GraphLoadedEventHandler implements EventBrokerListener {
        EventBroker eventBroker;
        public GraphLoadedEventHandler(EventBroker eventBroker)  {
            this.eventBroker = eventBroker;
            eventBroker.subscribe(this, GraphLoadedEvent.class, Graph.class);
        }

        public void processEvent(Event event) {
            _graph = (Graph) event.getSubject();
            System.out.println("\n\nloaded graph = " + _graph);
        }
    }
    
    public FileBackend(){
    	_queryEngineToQueryConfigMapping = new Hashtable();
    }

    public void setEventBroker(EventBroker eventBroker) {
        _eventBroker = eventBroker;
        new GraphLoadedEventHandler(this._eventBroker);
    }

    public JPanel getPanel(){
        return null;
    }

    public JMenu getMenu() {
        JMenu menu = new FileJMenu(this);
        return menu;
    }

	/**
	 * @see ontorama.backends.Backend#createNode(java.lang.String, java.lang.String)
	 */
	public Node createNode(String name, String fullName) {
		return new NodeImpl(name, fullName);
	}

	/**
	 * @see ontorama.backends.Backend#createEdge(Node, Node, EdgeType)
	 */
	public Edge createEdge(Node fromNode, Node toNode, EdgeType edgeType)
							throws NoSuchRelationLinkException {
		return new EdgeImpl(fromNode, toNode, edgeType);
	}
	/**
	 * @see ontorama.backends.Backend#getDataFormats()
	 */
	public Collection getDataFormats() {
		return _dataFormatsMapping;
	}

    public void loadFile(File file){
        DataFormatMapping mapping = Util.getMappingForFile(_dataFormatsMapping, file);
        System.out.println("FileBackend::loadFile, mapping = " + mapping);
        
        _filename = file.getAbsolutePath();

        if ((mapping == null) || (mapping.getParserName() == null)) {
        	/// @todo need to throw a 'parser not specified exception' here?
        	ErrorDialog.showError(OntoRamaApp.getMainFrame(), "Error", "There is no parser specified for this file type ");
        	return;
        }

        _parserName = mapping.getParserName();
        
        _querySettings = new QuerySettings(_parserName, _filename);
    
    	System.out.println("FileBackend::parserName = " + _parserName);
       
       GeneralQueryEvent queryEvent = new GeneralQueryEvent(new Query());
       _eventBroker.processEvent(queryEvent);
    }

    public void saveFile(String filename){
        try {
            System.out.println("Saving file = " + filename);
            File file = new File(filename);
            FileWriter writer = new FileWriter(file);

			DataFormatMapping mapping = Util.getMappingForFile(_dataFormatsMapping, file);
			
			if ((mapping == null) || (mapping.getWriterName() == null)) {
				/// @todo need exception here?
				ErrorDialog.showError(OntoRamaApp.getMainFrame(), "Error", "There is no writer specified for this file type ");
				return;
			}
			
			
        	ModelWriter modelWriter = new RdfModelWriter();

        	System.out.println("FileBackend:: writerName = " + mapping.getWriterName());

            modelWriter.write(_graph, writer);

            writer.close();

        } catch (IOException e) {
             System.err.println("Error when writing file");
             e.printStackTrace();
        } catch (ModelWriterException e) {
            e.printStackTrace();
            System.err.println("ModelWriterException:  " + e.getMessage());
        }
    }
    
    protected void processQueryFromHistoryElement(Query query, QuerySettings querySettings) {
    	_querySettings = querySettings;
    	GeneralQueryEvent queryEvent = new GeneralQueryEvent(query);
    	_eventBroker.processEvent(queryEvent);    	
    }

	

	/**
	 * @see ontorama.backends.Backend#createGraph(ontorama.ontotools.query.QueryResult, org.tockit.events.EventBroker)
	 */
	public Graph createGraph(QueryResult qr, EventBroker eb) throws InvalidArgumentException  {
		Graph res = new GraphImpl(qr, eb);
		return res;
	}



	/**
	 * @see ontorama.backends.Backend#executeQuery(ontorama.ontotools.query.Query)
	 */
	public QueryResult executeQuery(Query query) throws QueryFailedException, CancelledQueryException, NoSuchTypeInQueryResult {
		_lastQueryEngine = new QueryEngine( _sourcePackageName, _querySettings.getParserPackageName(), _querySettings.getSourceUri());
		QueryResult queryResult = _lastQueryEngine.getQueryResult(query);
		_queryEngineToQueryConfigMapping.put(_lastQueryEngine, new QuerySettings(_parserName, _filename));
		return queryResult;
	}
	
//	public QueryEngine getQueryEngine() {
//		return _lastQueryEngine;
//	}
//
//	/**
//	 * @see ontorama.backends.Backend#setQueryEngine(ontorama.ontotools.query.QueryEngine)
//	 */
//	public void setQueryEngine(QueryEngine queryEngine) {
//		_lastQueryEngine = queryEngine;
//		/// @todo this a hack - trying to revert to old config settings. Need to think 
//		/// how to fix this.
//		QuerySettings querySettings = (QuerySettings) _queryEngineToQueryConfigMapping.get(queryEngine);
//		if (querySettings != null) {
//			_parserName = querySettings.getParserPackageName();
//			_filename = querySettings.getSourceUri();
//		}
//	}
	

	/**
	 * @see ontorama.backends.Backend#createHistoryElement(ontorama.ontotools.query.Query, org.tockit.events.EventBroker)
	 */
	public HistoryElement createHistoryElement(Query query,EventBroker eventBroker) {
		FileHistoryElement res = new FileHistoryElement(query, eventBroker, this, _querySettings);
		return res;
	}

}
