package ontorama.backends.p2p.events;

import ontorama.backends.p2p.p2pprotocol.ItemReference;
import org.tockit.events.Event;

/**
 * @author nataliya
 */
public class LeaveGroupEvent implements Event {
	
	private ItemReference _groupRefElement;
	
	public LeaveGroupEvent (ItemReference subject) {
		_groupRefElement = subject;
	}

	/**
	 * @see org.tockit.events.Event#getSubject()
	 */
	public Object getSubject() {
		return _groupRefElement;
	}

}
