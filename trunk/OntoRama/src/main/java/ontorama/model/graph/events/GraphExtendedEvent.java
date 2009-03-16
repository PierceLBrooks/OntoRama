package ontorama.model.graph.events;

import ontorama.model.graph.Graph;

/**
 * @author nataliya
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class GraphExtendedEvent extends GraphChangedEvent {
	public GraphExtendedEvent(Graph subject) {
		super(subject);
	}
	

}
