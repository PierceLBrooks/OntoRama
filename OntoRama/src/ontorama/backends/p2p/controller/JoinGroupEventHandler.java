package ontorama.backends.p2p.controller;

import net.jxta.peergroup.PeerGroupID;
import ontorama.backends.p2p.p2pmodule.P2PSender;
import ontorama.backends.p2p.p2pprotocol.GroupException;
import ontorama.backends.p2p.p2pprotocol.GroupItemReference;
import ontorama.ui.ErrorDialog;
import ontorama.ui.OntoRamaApp;

import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

/**
 * @author nataliya
 */
public class JoinGroupEventHandler implements EventBrokerListener{
	
	P2PSender _sender;
	
	public JoinGroupEventHandler (P2PSender sender) {
		_sender = sender;
	}
	
	public void processEvent(Event event) {
		System.out.println("\n\nJoinGroupEventHandler::processEvent");
		GroupItemReference groupRefElement = (GroupItemReference) event.getSubject();
		try {
			_sender.sendJoinGroup((PeerGroupID) groupRefElement.getID());
		}
		catch (GroupException e) {
			e.printStackTrace();
			ErrorDialog.showError(OntoRamaApp.getMainFrame(), e, "Error joining a group", e.getMessage());
		}
	}
	

}
