package ontorama.backends;

import java.util.Collection;
import java.util.List;

import javax.swing.JMenu;

import ontorama.OntoramaConfig;
import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeImpl;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeImpl;
import ontorama.ontotools.NoSuchRelationLinkException;

import org.tockit.events.EventBroker;

/**
 * This is backend to be used by test cases.
 * @author nataliya
 */
public class ExamplesBackend implements Backend {
	
	private List _dataFormats = OntoramaConfig.getDataFormatsMapping();


	/**
	 * Constructor for TestingBackend.
	 */
	public ExamplesBackend() {
	}

	/**
	 * @see ontorama.backends.Backend#getMenu()
	 */
	public JMenu getMenu() {
		return null;
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

}
