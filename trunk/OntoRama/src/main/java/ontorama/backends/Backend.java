package ontorama.backends;

import java.util.Collection;

import javax.swing.JMenu;
import javax.swing.JPanel;

import ontorama.conf.DataFormatMapping;
import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Graph;
import ontorama.model.graph.InvalidArgumentException;
import ontorama.model.graph.Node;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.QueryFailedException;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.query.QueryEngine;
import ontorama.ontotools.query.QueryResult;
import ontorama.ui.HistoryElement;

import org.tockit.events.EventBroker;

public interface Backend {
	
	public JPanel getPanel();
	public JMenu getMenu();
	
	public void setEventBroker(EventBroker eventBroker);
	
	public Node createNode (String name, String fullName);
	
	public Edge createEdge (Node fromNode, Node toNode, EdgeType edgeType) throws NoSuchRelationLinkException;
	
	public Graph createGraph (QueryResult qr) throws InvalidArgumentException;
	
	public Collection<DataFormatMapping> getDataFormats();
	
	public HistoryElement createHistoryElement (Query query, EventBroker eventBroker);
	
	public QueryEngine getQueryEngine() throws QueryFailedException;	
}
