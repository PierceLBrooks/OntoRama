package ontorama.backends;

import java.util.Collection;

import javax.swing.JMenu;
import javax.swing.JPanel;

import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Graph;
import ontorama.model.graph.InvalidArgumentException;
import ontorama.model.graph.Node;
import ontorama.ontotools.CancelledQueryException;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.NoSuchTypeInQueryResult;
import ontorama.ontotools.QueryFailedException;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.query.QueryEngine;
import ontorama.ontotools.query.QueryResult;

import org.tockit.events.EventBroker;

/**
 * Backend interface
 * @author nataliya
 */
/* Methods we want:
Collection getExportFormats() // returns (writer,format name, extension..)
Parser getParser() // ???
Graph search(Query)
		 */
public interface Backend {
	
	public JPanel getPanel();
	public JMenu getMenu();
	
	public void setEventBroker(EventBroker eventBroker);
	
	public Node createNode (String name, String fullName);
	
	public Edge createEdge (Node fromNode, Node toNode, EdgeType edgeType) throws NoSuchRelationLinkException;
	
	public Graph createGraph (QueryResult qr, EventBroker eb) throws InvalidArgumentException;
	
	public QueryResult executeQuery (Query query) throws QueryFailedException, CancelledQueryException, NoSuchTypeInQueryResult;
		
	/// @todo added this method to enable HistoryMenu to work properly. Perhaps there is a better way to do this
	/// as it feels as a hack...
	public QueryEngine getQueryEngine ();

	/// @todo added this method to enable HistoryMenu to work properly. Perhaps there is a better way to do this
	/// as it feels as a hack...
	// (need this when we displayed a history item and then we want to search for a term
	// in the displayed ontology - this requires query engine config to be reset to the current in the backend).
	public void setQueryEngine (QueryEngine queryEngine);


	public Collection getDataFormats();
	
	
}
