/*
 * Created on 13/02/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package ontorama.backends.p2p.controller;

import ontorama.backends.p2p.gui.GroupView;
import ontorama.backends.p2p.p2pprotocol.ItemReference;

import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

/**
 * @author nataliya
 */
public class GroupJoinedEventHandler implements EventBrokerListener {

	GroupView _groupView;
	
	/**
	 * 
	 */
	public GroupJoinedEventHandler(GroupView groupView) {
		super();
		_groupView = groupView;
	}

	/* (non-Javadoc)
	 * @see org.tockit.events.EventBrokerListener#processEvent(org.tockit.events.Event)
	 */
	public void processEvent(Event event) {
		ItemReference groupElement = (ItemReference) event.getSubject(); 
		_groupView.addGroup(groupElement);
	}

}
