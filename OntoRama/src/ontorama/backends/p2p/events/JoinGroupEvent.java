package ontorama.backends.p2p.events;

import ontorama.backends.p2p.GroupItemReference;

import org.tockit.events.Event;

/**
 * @author nataliya
 */
public class JoinGroupEvent implements Event {
	
	private GroupItemReference _groupRefElement;
	
	public JoinGroupEvent (GroupItemReference subject) {
		_groupRefElement = subject;
	}

	/**
	 * @see org.tockit.events.Event#getSubject()
	 */
	public Object getSubject() {
		return _groupRefElement;
	}

}
