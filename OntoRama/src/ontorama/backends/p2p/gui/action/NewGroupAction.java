package ontorama.backends.p2p.gui.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JTextField;

import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.gui.DialogUtil;
import ontorama.backends.p2p.p2pprotocol.GroupReferenceElement;
import ontorama.ui.ErrorDialog;

/**
 * @author nataliya
 */
public class NewGroupAction extends AbstractAction {

	private JTextField _newGroupNameField;
	private JTextField _newGroupDescrField;
	private Component _parent;
	private P2PBackend _p2pBackend;
	private static final String _errMessageTitle = "Error joining peer group";

	public NewGroupAction (Component parent, JTextField newGroupNameField, 
							JTextField newGroupDescrField, P2PBackend p2pBackend) {
								
		super("Create group");								
		_parent = parent;
		_newGroupNameField = newGroupNameField;
		_newGroupDescrField = newGroupDescrField;
		_p2pBackend = p2pBackend;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		String input = _newGroupNameField.getText();
		if (! DialogUtil.textInputIsValid(_parent, input, "name")) {
			return;
		}
		System.out.println("input = " + input);
		try {
			System.out.println("attempting to create new group with name " + input + " and description: " + _newGroupDescrField.getText());
			_p2pBackend.getSender().sendCreateGroup(input,_newGroupDescrField.getText());
			Vector resVector = _p2pBackend.getSender().sendSearchGroup("Name",input);
			/// @todo probably should handle whole vector, not just one element.
			if ( resVector.isEmpty()) {
				ErrorDialog.showError(_parent, _errMessageTitle, "Failed to create new group");
				return;
			}
			GroupReferenceElement newGroup = (GroupReferenceElement) resVector.firstElement();
			String groupId = newGroup.getID().toString();
			System.out.println("trying to join group id " + groupId);
			_p2pBackend.getSender().sendJoinGroup(groupId);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError(_parent, e, _errMessageTitle, e.getMessage());
		}
	}

}
