package ontorama.backends.p2p.gui;

import javax.swing.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;
import java.awt.CardLayout;
import java.awt.BorderLayout;
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
                _cardLayout.show(groupPanel, groupPanel.getName());

            }
        });

        _cardLayout = new CardLayout(10, 10);
        _cardPanel = new JPanel(_cardLayout);

        add(new JLabel("Groups "), BorderLayout.NORTH);
        add(_comboBox, BorderLayout.CENTER);
        add(_cardPanel, BorderLayout.SOUTH);

        addGroup("testId", "testName", "testGroup");
    }

    public void addGroup(String peerId, String peerName, String groupId) {
        _comboBox.add(new JLabel(groupId));

        GroupPanel groupPanel = new GroupPanel(groupId);
        _cardLayout.addLayoutComponent(groupPanel, groupId);
        groupPanel.addPeer(peerId);

        _groupToPanelMapping.put(peerName, groupPanel);
        repaint();
    }

    private class GroupPanel extends JPanel {
        Vector peersList;
        JList jlist;

        public GroupPanel(String groupName) {
            peersList = new Vector();
            jlist = new JList(peersList);
            add(jlist);
        }

        public void addPeer (String name) {
            System.out.println("addPeer for name " + name);
            peersList.add(name);
            updateJList();
            repaint();
        }

        public void removePeer (String name) {
            peersList.remove(name);
            updateJList();
            repaint();
        }

        private void updateJList() {
            remove(jlist);
            jlist = new JList(peersList);
            add(jlist);
        }
    }
}
