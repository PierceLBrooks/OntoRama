/*
 * Created on 13/02/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package ontorama.backends.p2p.events;

import ontorama.backends.p2p.GroupItemReference;

import org.tockit.events.Event;

/**
 * @author nataliya
 */
public class GroupIsLeftEvent  implements Event {
	protected GroupItemReference subject;

	public GroupIsLeftEvent (GroupItemReference subject) {
		this.subject = subject;
	}

	public Object getSubject() {
		return subject;
	}
}
