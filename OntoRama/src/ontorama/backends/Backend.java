package ontorama.backends;

import java.util.List;

import javax.swing.JMenu;

import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Node;
import ontorama.ontotools.NoSuchRelationLinkException;

import org.tockit.events.EventBroker;

/**
 * Backend interface
 * @author nataliya
 */
/* Methods we want:
Edge createEdge(...) // instead of constructor
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
	


}
