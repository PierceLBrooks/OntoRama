package ontorama.model.tree.events;

import ontorama.model.tree.Tree;
import org.tockit.events.Event;

/**
 * @author nataliya
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TreeLoadedEvent implements Event {
	private Tree tree;

	public TreeLoadedEvent(Tree subject) {
		this.tree = subject;
	}

	public Object getSubject() {
		return tree;
	}	

}
