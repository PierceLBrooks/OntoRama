package ontorama.backends.p2p.events;

import ontorama.backends.p2p.GroupItemReference;

import org.tockit.events.Event;

/**
 * @author nataliya
 */
public class LeaveGroupEvent implements Event {
	
	private GroupItemReference _groupRefElement;
	
	public LeaveGroupEvent (GroupItemReference subject) {
		_groupRefElement = subject;
	}

	/**
	 * @see org.tockit.events.Event#getSubject()
	 */
	public Object getSubject() {
		return _groupRefElement;
	}

}
