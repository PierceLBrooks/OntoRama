package ontorama.backends.p2p.gui;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ontorama.backends.p2p.p2pprotocol.GroupReferenceElement;


/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 13:03:37
 * To change this template use Options | File Templates.
 */
public class PeersPanel extends JPanel  implements GroupView {

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

	public void addGroup(GroupReferenceElement groupReferenceElement) {
		System.out.println("\nPeersPanel::addGroup, group = " + groupReferenceElement.getName());
		String groupId = groupReferenceElement.getID().toString();
		String groupName = groupReferenceElement.getName();
        if (!_groupNameToGroupIdMapping.containsKey(groupName)) {
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
        groupPanel.addPeer(peerId, peerName);
    }

    public void removePeer(String senderPeerID, String groupID) {
        GroupPanel groupPanel = (GroupPanel) _groupToPanelMapping.get(groupID);
        if (groupPanel != null) {
            groupPanel.removePeer(senderPeerID);
        }
    }

    public void removePeerFromAllGroups(String senderPeerID) {
        //@todo implement should remove the id from every group, I don't think this method is called from the application (i.e. not generated when a peer logout)
    }

    public void removeGroup(GroupReferenceElement groupRefElement) {
    	String groupID = groupRefElement.getID().toString();
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
        HashSet peersList = new HashSet();
        DefaultListModel listModel = new DefaultListModel();
        JList jlist;
        Hashtable _peerIdToPeerNameMapping = new Hashtable();

        public GroupPanel(String groupId) {
            //peersList = new Vector();
            setName(groupId);


            jlist = new JList(listModel);

            JScrollPane scrollPanel = new JScrollPane(jlist);
            add(scrollPanel);
        }

        public void addPeer (String peerID, String peerName) {
            if (!peersList.contains(peerID)) {
                 peersList.add(peerID);

                _peerIdToPeerNameMapping.put(peerID, peerName);

                listModel.addElement(peerName);
                repaint();
            }
        }

        public void removePeer (String peerID) {
            String peerName = null;
            peerName = (String) _peerIdToPeerNameMapping.remove(peerID);
            if (peerName != null) {
                listModel.removeElement(peerName);
                peersList.remove(peerID);
                repaint();
            }
        }
    }
}
