package ontorama.backends.p2p.gui;

import javax.swing.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;
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
public class PeerPanel extends JPanel {

    /**
     * holds mapping from groupId to the corresponding groupPanel
     */
    private Hashtable _groupToPanelMapping;

    private Vector _groupsVector;

    private JComboBox _comboBox;
    private JPanel _cardPanel;
    private CardLayout _cardLayout;

    public PeerPanel() {
        super();
        _groupToPanelMapping = new Hashtable();
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
                GroupPanel groupPanel = (GroupPanel) _groupToPanelMapping.get(selectedGroupName);
                _cardLayout.show(_cardPanel, groupPanel.getName());
                groupPanel.setVisible(true);
            }
        });

        _cardPanel = new JPanel();
        _cardLayout = new CardLayout(10, 10);
        _cardPanel.setLayout(_cardLayout);

        _cardPanel.setBorder(BorderFactory.createEtchedBorder());

        add(new JLabel("Groups "), BorderLayout.NORTH);
        add(_comboBox, BorderLayout.CENTER);
        add(_cardPanel, BorderLayout.SOUTH);

        addGroup("testId", "testGroup");
        addGroup("id2", "group2");
        addGroup("id3", "group3");

        addPeer("peerId1", "peerName1", "id2");
        addPeer("peerId2", "peerName2", "id2");
        addPeer("peerId3", "peerName3", "id2");
        addPeer("peerId4", "peerName4", "id2");
        addPeer("peerId5", "peerName5", "id2");
    }

    public void addGroup(String groupId, String groupName) {
        _groupsVector.add(groupId);

        GroupPanel groupPanel = new GroupPanel(groupId);

        _cardLayout.addLayoutComponent(groupPanel, groupId);
        _cardPanel.add(groupId, groupPanel);

        _groupToPanelMapping.put(groupId, groupPanel);
        repaint();
    }

    public void addPeer (String peerId, String peerName, String groupId) {
        GroupPanel groupPanel = (GroupPanel) _groupToPanelMapping.get(groupId);
        groupPanel.addPeer(peerName);
    }

    private class GroupPanel extends JPanel {
        Vector peersList;
        JList jlist;

        public GroupPanel(String groupName) {
            peersList = new Vector();
            setName(groupName);
            jlist = new JList(peersList);
            JScrollPane scrollPanel = new JScrollPane(jlist);
            add(scrollPanel);
        }

        public void addPeer (String name) {
            System.out.println("addPeer for name " + name);
            peersList.add(name);
            repaint();
        }

        public void removePeer (String name) {
            peersList.remove(name);
            repaint();
        }
    }
}
