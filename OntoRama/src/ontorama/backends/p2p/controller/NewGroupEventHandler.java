package ontorama.backends.p2p.controller;

import java.util.List;

import net.jxta.peergroup.PeerGroupID;
import ontorama.backends.p2p.GroupItemReference;
import ontorama.backends.p2p.p2pmodule.P2PSender;
import ontorama.ui.ErrorDialog;
import ontorama.ui.OntoRamaApp;

import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

/**
 * @author nataliya
 */
public class NewGroupEventHandler implements EventBrokerListener{
	
	P2PSender _sender;
	
	public NewGroupEventHandler (P2PSender sender) {
		_sender = sender;
	}
	
	public void processEvent(Event event) {
		GroupItemReference newGroupRefElement = (GroupItemReference) event.getSubject();
		try {
			_sender.sendCreateGroup(newGroupRefElement.getName(),newGroupRefElement.getDescription());
			List result = _sender.sendSearchGroup("Name",newGroupRefElement.getName());
			/// @todo probably should handle whole vector, not just one element.
			if ( result.isEmpty()) {
				return;
			}
			GroupItemReference groupToJoin = (GroupItemReference) result.get(0);
			_sender.sendJoinGroup((PeerGroupID) groupToJoin.getID());
		}
		catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError(OntoRamaApp.getMainFrame(), e, "Error joining a group", e.getMessage());
		}
	}
	

}
