package ontorama.backends.filemanager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenu;

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
import ontorama.ui.ErrorPopupMessage;
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
    private List _panels = null;
    private EventBroker _eventBroker;
    private String _parserName;
    
    private List _dataFormatsMapping = OntoramaConfig.getDataFormatsMapping();
	private String _sourcePackageName = "ontorama.ontotools.source.FileSource";
	private String _filename;

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
        //We don't have any panels to this backend
        _panels = new LinkedList();       
    }

    public void setEventBroker(EventBroker eventBroker) {
        _eventBroker = eventBroker;
        new GraphLoadedEventHandler(this._eventBroker);
    }

    public List getPanels(){
        return _panels;
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
        _filename = file.getAbsolutePath();
    	System.out.println("FileBackend::Loading file = " + _filename);
        
        String extension = Util.getExtension(file);
        DataFormatMapping mapping = getMappingForExtension(extension);
        System.out.println("FileBackend::loadFile, mapping = " + mapping);

        if ((mapping == null) || (mapping.getParserName() == null)) {
        	/// @todo need to throw a 'parser not specified exception' here?
        	new ErrorPopupMessage("There is no parser specified for this file type ", OntoRamaApp.getMainFrame());
        	return;
        }

        _parserName = mapping.getParserName();
    
    	System.out.println("FileBackend::parserName = " + _parserName);
       
       GeneralQueryEvent queryEvent = new GeneralQueryEvent(new Query());
       _eventBroker.processEvent(queryEvent);
    }

    public void saveFile(String filename){
        try {
            System.out.println("Saving file = " + filename);
            File file = new File(filename);
            FileWriter writer = new FileWriter(file);

			String extension = Util.getExtension(filename);
			
			DataFormatMapping mapping = getMappingForExtension(extension);
			
			if ((mapping == null) || (mapping.getWriterName() == null)) {
				/// @todo need exception here?
				new ErrorPopupMessage("There is no writer specified for this file type ", OntoRamaApp.getMainFrame());
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

	
	private DataFormatMapping getMappingForExtension (String extension) {
		Iterator it = _dataFormatsMapping.iterator();
		while (it.hasNext()) {
			DataFormatMapping element = (DataFormatMapping) it.next();
			if (element.getFileExtention().equals(extension)) {
				return element;
			}
		}
		return null;
	}
	
	

	/**
	 * @see ontorama.backends.Backend#getParser()
	 */
	public String getParser() {
		return _parserName;
	}
	
	

	/**
	 * @see ontorama.backends.Backend#getSourcePackageName()
	 */
	public String getSourcePackageName() {
		return _sourcePackageName;
	}


	/**
	 * @see ontorama.backends.Backend#getSourceUri()
	 */
	public String getSourceUri() {
		return _filename;
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
		QueryEngine queryEngine = new QueryEngine( _sourcePackageName, _parserName, _filename);
		QueryResult queryResult = queryEngine.getQueryResult(query);
		return queryResult;
	}

}
