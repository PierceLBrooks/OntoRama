package ontorama.backends;

import java.util.Collection;
import java.util.List;

import javax.swing.JMenu;

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
	
	public List getPanels();
	public JMenu getMenu();
	
	public void setEventBroker(EventBroker eventBroker);
	
	public Node createNode (String name, String fullName);
	
	public Edge createEdge (Node fromNode, Node toNode, EdgeType edgeType) throws NoSuchRelationLinkException;
	
	public Graph createGraph (QueryResult qr, EventBroker eb) throws InvalidArgumentException;
	
	public QueryResult executeQuery (Query query) throws QueryFailedException, CancelledQueryException, NoSuchTypeInQueryResult;
	
	public Collection getDataFormats();
	
}
