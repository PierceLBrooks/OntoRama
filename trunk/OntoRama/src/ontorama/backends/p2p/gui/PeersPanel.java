package ontorama.backends.p2p.gui;

import javax.swing.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 13:03:37
 * To change this template use Options | File Templates.
 */
public class PeersPanel extends JPanel {

    /**
     * holds mapping from groupId to the corresponding groupPanel
     */
    private Hashtable _groupToPanelMapping;
    private Hashtable _groupNameToGroupIdMapping;
    private Hashtable _groupIdToGroupNameMapping;
    private Vector _groupsVector;

    private JComboBox _comboBox;
    private JPanel _cardPanel;
    private CardLayout _cardLayout;

    public PeersPanel() {
        super();
        _groupToPanelMapping = new Hashtable();
        _groupNameToGroupIdMapping = new Hashtable();
        _groupIdToGroupNameMapping = new Hashtable();
        _groupsVector = new Vector();

        _comboBox = new JComboBox(_groupsVector);
        _comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedGroupName = (String) _comboBox.getSelectedItem();

                if (selectedGroupName == null) {
                    return;
                }
                if (_groupToPanelMapping.size() == 0 ) {
                    return;
                }
                String selectedGroupId = (String) _groupNameToGroupIdMapping.get(selectedGroupName);
System.out.println("PeersPanel, selectedGroupName:" + selectedGroupName + "(" + selectedGroupId + ")");

                GroupPanel groupPanel = (GroupPanel) _groupToPanelMapping.get(selectedGroupId);
                _cardLayout.show(_cardPanel, groupPanel.getName());
                groupPanel.setVisible(true);
                repaint();
            }
        });

        _cardPanel = new JPanel();
        _cardLayout = new CardLayout(10, 10);
        _cardPanel.setLayout(_cardLayout);

        _cardPanel.setBorder(BorderFactory.createEtchedBorder());

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(new JLabel("Groups "));
        add(_comboBox);
        add(_cardPanel);

    }

    public void addGroup(String groupId, String groupName) {
        if (!_groupNameToGroupIdMapping.containsKey(groupName)) {
System.err.println("PeersPanel::addGroup:" + groupName + "(" + groupId + ")");
            _groupsVector.add(groupName);
            GroupPanel groupPanel = new GroupPanel(groupId);
            _cardLayout.addLayoutComponent(groupPanel, groupId);
            _cardPanel.add(groupId, groupPanel);
            _groupToPanelMapping.put(groupId, groupPanel);
            _groupNameToGroupIdMapping.put(groupName,groupId);
            _groupIdToGroupNameMapping.put(groupId,groupName);
            repaint();
        }
    }

    public void addPeer (String peerId, String peerName, String groupId) {
        GroupPanel groupPanel = (GroupPanel) _groupToPanelMapping.get(groupId);
        groupPanel.addPeer(peerName);
    }

    public void removePeer(String senderPeerID) {
        /// @todo implement
    }

    public void removeGroup(String groupID) {
        String groupName = null;
        groupName = (String) _groupIdToGroupNameMapping.get(groupID);

        if (groupName != null) {
            GroupPanel groupPanel = (GroupPanel) _groupToPanelMapping.get(groupID);
            _cardLayout.removeLayoutComponent(groupPanel);
            _cardPanel.remove(groupPanel);

            _groupsVector.remove(groupName);
            _groupToPanelMapping.remove(groupID);
            _groupNameToGroupIdMapping.remove(groupName);
            _groupIdToGroupNameMapping.remove(groupID);
            repaint();
            //@todo repaint problem, the combobox is not repainted
        }
    }

    public void clear() {
        _groupToPanelMapping.clear();
        _groupNameToGroupIdMapping.clear();
        _groupsVector.clear();
        repaint();
    }

    private class GroupPanel extends JPanel {
        Vector peersList;
        JList jlist;

        public GroupPanel(String groupId) {
            peersList = new Vector();
            setName(groupId);
            jlist = new JList(peersList);

            JScrollPane scrollPanel = new JScrollPane(jlist);
            add(scrollPanel);
        }

        public void addPeer (String name) {
            peersList.add(name);
System.out.println("PeersPanel::GroupPanel::AddPeer" + name + "(peers in list after add:" + peersList + ")");
            jlist.setListData(peersList);
            repaint();
        }

        public void removePeer (String name) {
            peersList.remove(name);
            jlist.setListData(peersList);
            repaint();
        }
    }
}
