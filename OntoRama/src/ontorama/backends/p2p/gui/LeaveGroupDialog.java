package ontorama.backends.p2p.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

import ontorama.backends.p2p.GroupItemReference;
import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.P2PBackendImpl;
import ontorama.backends.p2p.events.GroupJoinedEvent;
import ontorama.backends.p2p.events.LeaveGroupEvent;
import ontorama.ui.ErrorDialog;
import ontorama.ui.OntoRamaApp;


/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 16, 2002
 * Time: 9:45:06 AM
 * To change this template use Options | File Templates.
 */
public class LeaveGroupDialog extends JDialog {

    private static final String _title = "Leave P2P Group";

    private P2PBackend _p2pBackend;

	private class LocalGroupJoinedEventHandler implements EventBrokerListener {
		JDialog dialog;
		LocalGroupJoinedEventHandler(JDialog dialog) {
			this.dialog = dialog;
		}
		public void processEvent(Event event) {
			dialog.setVisible(false);
		}

	}

    public LeaveGroupDialog(Frame parent, P2PBackend p2pBackend)  {
        super(parent, _title, true);
        _p2pBackend = p2pBackend;
        
        _p2pBackend.getEventBroker().subscribe(new LocalGroupJoinedEventHandler(this), GroupJoinedEvent.class, GroupItemReference.class);

        Vector foundGroups = new Vector();
        try {
            foundGroups = ((P2PBackendImpl)_p2pBackend).getSender().joinedGroups();
            Iterator it = foundGroups.iterator();
        }
        catch (Exception e) {
            /// @todo deal with exceptions propertly
            e.printStackTrace();
        }
        final GroupChooserPanel groupsPanel = new GroupChooserPanel(foundGroups, "Choose group to leave");

        JButton cancelButton = new JButton("Cancel");
        final JButton okButton = new JButton("OK");

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        
    	okButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			GroupItemReference groupToLeave = groupsPanel.getGroupChooser().getSelectedGroup();
    			if (groupToLeave == null) {
    				ErrorDialog.showError(OntoRamaApp.getMainFrame(), "Error", "Please choose a group you wish to leave");
    			}
    			else {
    				_p2pBackend.getEventBroker().processEvent(new LeaveGroupEvent(groupToLeave));
    			}
    		}
    	});

        getRootPane().setDefaultButton(okButton);

        JPanel buttonPanel = DialogUtil.buildButtonsPanel(okButton, cancelButton);

        Container contentPanel = getContentPane();
        contentPanel.add(groupsPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        pack();
        DialogUtil.centerDialog(parent, this);
        setVisible(true);
    }
}
