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

import net.jxta.peergroup.PeerGroup;
import ontorama.backends.p2p.p2pmodule.P2PSender;


/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 16, 2002
 * Time: 9:45:06 AM
 * To change this template use Options | File Templates.
 */
public class LeaveGroupDialog extends JDialog {

    private static final String _title = "Leave P2P Group";

    private P2PSender _p2pSender;

    public LeaveGroupDialog(Frame parent, P2PSender p2pSender)  {
        super(parent, _title, true);
        _p2pSender = p2pSender;

        Vector foundGroups = new Vector();
        try {
            //foundGroups = _p2pSender.sendSearchGroup(null, null);
            foundGroups = _p2pSender.joinedGroups();
            System.out.println("joined groups: ");
            Iterator it = foundGroups.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                System.out.println("next = " + obj + ", class: " + obj.getClass());
            }
        }
        catch (Exception e) {
            /// @todo deal with exceptions propertly
            e.printStackTrace();
        }
        final GroupChooser groupsPanel = new GroupChooser(foundGroups, "Choose group to leave");

        JButton cancelButton = new JButton("Cancel");
        final JButton okButton = new JButton("OK");

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PeerGroup selectedGroup = (PeerGroup) groupsPanel.getValue();
                String groupId = selectedGroup.getPeerGroupID().toString();
                System.out.println("trying to leave group: name = " + selectedGroup.getPeerGroupName() + ", id = " + groupId);
                try {

                    _p2pSender.sendLeaveGroup(groupId);
                }
                catch (Exception exc) {
                    /// @todo handle exception properly!
                    exc.printStackTrace();
                }
                setVisible(false);
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
