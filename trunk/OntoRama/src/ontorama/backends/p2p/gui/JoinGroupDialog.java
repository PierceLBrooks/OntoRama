package ontorama.backends.p2p.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

import ontorama.backends.p2p.GroupItemReference;
import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.events.GroupJoinedEvent;
import ontorama.backends.p2p.events.JoinGroupEvent;
import ontorama.backends.p2p.events.NewGroupEvent;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 10:51:01
 * To change this template use Options | File Templates.
 */
public class JoinGroupDialog extends JDialog {

    private static final String _title = "Join P2P Group";

    private P2PBackend _p2pBackend;

    private GroupChooserPanel _existingGroupPanel;
    private JPanel _newGroupPanel;
    private JTabbedPane _tabbedPanel;

    private JTextField _newGroupNameField;
    private JTextField _newGroupDescrField;

	private class LocalGroupJoinedEventHandler implements EventBrokerListener {
		
		JDialog dialog;
		
		public LocalGroupJoinedEventHandler (JDialog dialog) {
			this.dialog = dialog;
		}
		
		public void processEvent(Event event) {
			dialog.setVisible(false);
		}

	}    

    public JoinGroupDialog(Frame parent, P2PBackend p2pBackend)  {
        super(parent, _title, true);

        _p2pBackend = p2pBackend;
        _p2pBackend.getEventBroker().subscribe(new LocalGroupJoinedEventHandler(this), 
        							GroupJoinedEvent.class, GroupItemReference.class);
               
        Vector foundGroups = new Vector();
        try {
            foundGroups = _p2pBackend.getSender().sendSearchGroup(null, null);
        }
        catch (Exception e) {
            /// @todo deal with exceptions propertly
            e.printStackTrace();
        }


        _existingGroupPanel = new GroupChooserPanel(foundGroups, "Choose group to join");
        _newGroupPanel = new JPanel();
        JTabbedPane tabbedPanel = buildTabbedPanel();

        JButton cancelButton = new JButton("Cancel");
        final JButton okButton = new JButton("OK");

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        
        Action joinGroup = new AbstractAction("OK") {
        	public void actionPerformed(ActionEvent e) {
        		verifyInputCorrect();
        	}
        };
        okButton.setAction(joinGroup);

        getRootPane().setDefaultButton(okButton);

        JPanel buttonPanel = DialogUtil.buildButtonsPanel(okButton, cancelButton);

        Container contentPanel = getContentPane();
        contentPanel.add(tabbedPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        pack();
        DialogUtil.centerDialog(parent, this);
    }

    private JTabbedPane buildTabbedPanel() {
        _tabbedPanel = new JTabbedPane();

        _tabbedPanel.addTab("Existing Group", null, _existingGroupPanel, "Join existing group");
        _tabbedPanel.setSelectedComponent(_existingGroupPanel);

        _newGroupNameField = DialogUtil.createNewGroupNameTextField();
        _newGroupDescrField = DialogUtil.createNewGroupDescriptionTextField();

        _newGroupPanel.setLayout(new BoxLayout(_newGroupPanel, BoxLayout.Y_AXIS));
        _newGroupPanel.add(new JLabel(DialogUtil.newGroupNameLabel));
        _newGroupPanel.add(_newGroupNameField);
        _newGroupPanel.add(new JLabel(DialogUtil.newGroupDescriptionLabel));
        _newGroupPanel.add(_newGroupDescrField);

        _tabbedPanel.addTab("New Group", null, _newGroupPanel, "Create new group");

        return _tabbedPanel;
    }

    /**
     *
     * @return
     * @todo quite a mess, think how to refactor.
     */
    private boolean verifyInputCorrect () {
        GroupItemReference groupToJoin;
        if (_tabbedPanel.getSelectedComponent().equals(_existingGroupPanel) ) {
            groupToJoin = (GroupItemReference) _existingGroupPanel.getGroupChooser().getSelectedGroup();
            if (groupToJoin == null) {
                return false;
            }
        	_p2pBackend.getEventBroker().processEvent(new JoinGroupEvent(groupToJoin));
            return true;
        }
        if (_tabbedPanel.getSelectedComponent().equals(_newGroupPanel)) {
            String input = _newGroupNameField.getText();
            if (DialogUtil.textInputIsValid(this, input, "name")) {
				NewGroupEvent newGroupEvent = new NewGroupEvent(new GroupItemReference(null, input, _newGroupDescrField.getText()));
				_p2pBackend.getEventBroker().processEvent(newGroupEvent);
                return true;
            }
        }
        return false;
    }
}
