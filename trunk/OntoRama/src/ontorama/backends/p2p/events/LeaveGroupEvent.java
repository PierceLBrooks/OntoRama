package ontorama.backends.p2p.events;

import ontorama.backends.p2p.p2pprotocol.GroupReferenceElement;
import org.tockit.events.Event;

/**
 * @author nataliya
 */
public class LeaveGroupEvent implements Event {
	
	private GroupReferenceElement _groupRefElement;
	
	public LeaveGroupEvent (GroupReferenceElement subject) {
		_groupRefElement = subject;
	}

	/**
	 * @see org.tockit.events.Event#getSubject()
	 */
	public Object getSubject() {
		return _groupRefElement;
	}

}
