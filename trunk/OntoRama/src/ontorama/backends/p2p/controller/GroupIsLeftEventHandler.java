/*
 * Created on 13/02/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package ontorama.backends.p2p.controller;

import ontorama.backends.p2p.gui.GroupView;
import ontorama.backends.p2p.p2pprotocol.GroupReferenceElement;

import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

/**
 * @author nataliya
 */
public class GroupIsLeftEventHandler implements EventBrokerListener {

	GroupView _groupView;
	
	/**
	 * 
	 */
	public GroupIsLeftEventHandler(GroupView groupView) {
		super();
		_groupView = groupView;
	}

	/* (non-Javadoc)
	 * @see org.tockit.events.EventBrokerListener#processEvent(org.tockit.events.Event)
	 */
	public void processEvent(Event event) {
		GroupReferenceElement groupElement = (GroupReferenceElement) event.getSubject(); 
		System.out.println("GroupIsLeftEventHandler::processEvent for group " + groupElement);
		_groupView.removeGroup(groupElement);
	}

}
