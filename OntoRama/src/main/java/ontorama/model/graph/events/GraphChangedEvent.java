package ontorama.model.graph.events;

import ontorama.model.graph.Graph;
import org.tockit.events.Event;

/**
 * @author nataliya
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class GraphChangedEvent implements Event {

	private Graph graph;

	public GraphChangedEvent(Graph subject) {
		this.graph = subject;
	}

	public Object getSubject() {
		return graph;
	}

	public Graph getGraph() {
		return graph;
	}

}
