package ontorama.backends;

import java.util.List;

import javax.swing.JMenu;

import ontorama.model.graph.Node;
import ontorama.model.graph.NodeImpl;

import org.tockit.events.EventBroker;

/**
 * This is backend to be used by test cases.
 * @author nataliya
 */
public class TestingBackend implements Backend {


	/**
	 * @see ontorama.backends.Backend#createNode(java.lang.String, java.lang.String)
	 */
	public Node createNode(String name, String fullName) {
		return new NodeImpl(name, fullName);
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

}
