package ontorama.backends;

import java.util.List;

import javax.swing.JMenu;

import ontorama.model.graph.Node;
import org.tockit.events.EventBroker;

/**
 * Backend interface
 * @author nataliya
 */
/* Methods we want:
Node createNode(...) // instead of constructor
Edge createEdge(...) // instead of constructor
Collection getExportFormats() // returns (writer,format name, extension..)
Parser getParser() // ???
Graph search(Query)
List getPanels()
JMenu getMenu()
		 */
public interface Backend {
	
	public List getPanels();
	public JMenu getMenu();
	
	public void setEventBroker(EventBroker eventBroker);
	
	public Node createNode (String name, String fullName);
	


}
