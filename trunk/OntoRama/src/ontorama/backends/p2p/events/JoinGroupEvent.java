package ontorama.backends.p2p.events;

import ontorama.backends.p2p.p2pprotocol.ItemReference;
import org.tockit.events.Event;

/**
 * @author nataliya
 */
public class JoinGroupEvent implements Event {
	
	private ItemReference _groupRefElement;
	
	public JoinGroupEvent (ItemReference subject) {
		_groupRefElement = subject;
	}

	/**
	 * @see org.tockit.events.Event#getSubject()
	 */
	public Object getSubject() {
		return _groupRefElement;
	}

}
