/*
 * Created on 13/02/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package ontorama.backends.p2p.events;

import ontorama.backends.p2p.p2pprotocol.GroupReferenceElement;

import org.tockit.events.Event;

/**
 * @author nataliya
 */
public class GroupJoinedEvent  implements Event {
	protected GroupReferenceElement subject;

	public GroupJoinedEvent (GroupReferenceElement subject) {
		this.subject = subject;
	}

	public Object getSubject() {
		return subject;
	}
}
