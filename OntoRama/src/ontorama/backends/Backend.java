package ontorama.backends;

import java.util.Collection;
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
	
	public Collection getDataFormats();
	
	/**
	 * @todo return string - parser package name for now, will need to refactor
	 * later
	 */
	public String getParser();
	
	/**
	 * @todo wrapper method to use while refactoring
	 */
	public String getSourcePackageName();

	/**
	 * @todo wrapper method to use while refactoring
	 */
	public String getSourceUri();

}
