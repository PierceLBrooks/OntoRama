package ontorama.backends.p2p.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.gui.renderer.*;
import ontorama.backends.p2p.p2pprotocol.ItemReference;


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
     * keys - id strings, values - corresponding panels.
     */
    private Hashtable _groupToPanelMapping;
       
    private DefaultComboBoxModel _groupsComboBoxModel;

    private JComboBox _comboBox;
    private JPanel _cardPanel;
    private CardLayout _cardLayout;
    
    private P2PBackend _p2pBackend;
    
    private GroupPanel _globalGroupPanel;
       
    public PeersPanel(P2PBackend backend) {
        super();
        _p2pBackend = backend;
        
        _groupToPanelMapping = new Hashtable();
        
        _groupsComboBoxModel = new DefaultComboBoxModel();

        _comboBox = new JComboBox(_groupsComboBoxModel);
        _comboBox.setRenderer(new GroupNamesComboBoxRenderer());

        _comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ItemReference selectedGroup = (ItemReference) _comboBox.getSelectedItem();
                if (selectedGroup == null) {
                    return;
                }
                if (_groupToPanelMapping.size() == 0 ) {
                    return;
                }

                GroupPanel groupPanel = (GroupPanel) _groupToPanelMapping.get(selectedGroup.getID().toString());
                _cardLayout.show(_cardPanel, groupPanel.getName());
                
                groupPanel.setVisible(true);
                groupPanel.repaint();
                repaint();
            }
        });

        _cardPanel = new JPanel();
        _cardLayout = new CardLayout(10, 10);
        _cardPanel.setLayout(_cardLayout);

        _cardPanel.setBorder(BorderFactory.createEtchedBorder());
        
		_globalGroupPanel = new GroupPanel("Global Net Group");
        JPanel tempGlobalGroupPanel = new JPanel();
        tempGlobalGroupPanel.add(_globalGroupPanel);
        tempGlobalGroupPanel.setBorder(BorderFactory.createEtchedBorder());
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");						
				System.out.println("REFRESH on peers, time (mils): " + sdf.format(new Date()));
				_p2pBackend.getSender().peerDiscovery();
				System.out.println("----+++++-------");
				_p2pBackend.getSender().peerDiscoveryForGlobalGroup();
			}
        	
        });
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(new JLabel("Groups "));
        add(_comboBox);
        add(_cardPanel);
        add(new JLabel("All active peers:"));
        add(tempGlobalGroupPanel);
        add(refreshButton);
    }


	public void addGroup(ItemReference groupReferenceElement) {
		String groupId = groupReferenceElement.getID().toString();
        if (!_groupToPanelMapping.containsKey(groupId)) {
        	_groupsComboBoxModel.addElement(groupReferenceElement);
            GroupPanel groupPanel = new GroupPanel(groupReferenceElement);
            _cardPanel.add(groupReferenceElement.getName(), groupPanel);
            _groupToPanelMapping.put(groupId, groupPanel);
            repaint();
        }
    }

    public void addPeer (String peerId, String peerName, String groupId) {
    	System.out.println("addPeer, peerName = " + peerName + ", peerId = " + peerId + ", groupId = " + groupId);
        GroupPanel groupPanel = (GroupPanel) _groupToPanelMapping.get(groupId);
        PeersJList peersList = groupPanel.getPeersList();
		peersList.addPeer(new PeerObject(peerId, peerName));
        groupPanel.repaint();
        repaint();
    }
    
    public void addPeerInGlobalList (String peerId, String peerName) {
		_globalGroupPanel.getPeersList().addPeer(new PeerObject(peerId, peerName));
    }

    public void removePeer(String senderPeerID, String groupID) {
        GroupPanel groupPanel = (GroupPanel) _groupToPanelMapping.get(groupID);
        if (groupPanel != null) {
			PeersJList peersList = groupPanel.getPeersList();
			peersList.removePeer(senderPeerID);
        }
    }

    public void removePeerFromAllGroups(String senderPeerID) {
        Enumeration enum = _groupToPanelMapping.elements();
        while (enum.hasMoreElements()) {
			String element = (String) enum.nextElement();
			GroupPanel groupPanel = (GroupPanel) _groupToPanelMapping.get(element);
			groupPanel.getPeersList().removePeer(senderPeerID);
		}
    }

    public void removeGroup(ItemReference groupRefElement) {
    	String groupID = groupRefElement.getID().toString();

        GroupPanel groupPanel = (GroupPanel) _groupToPanelMapping.get(groupID);
        _cardPanel.remove(groupPanel);

        _groupsComboBoxModel.removeElement(groupRefElement);
        _groupToPanelMapping.remove(groupID);
        repaint();
    }

    public void clear() {
        _groupToPanelMapping.clear();
        _groupsComboBoxModel.removeAllElements();
        repaint();
    }

    private class GroupPanel extends JPanel {
		PeersJList peersJList;
        String groupName;

        public GroupPanel(ItemReference group) {
			this(group.getName());
        }
        
		public GroupPanel(String groupName) {
			super();
			this.groupName = groupName;

			setName(groupName);
			peersJList = new PeersJList();

			Dimension d = new Dimension(200, 200);

			JScrollPane  scrollPanel = new JScrollPane(peersJList);				
			scrollPanel.setBorder(BorderFactory.createLoweredBevelBorder());
			scrollPanel.setPreferredSize(d); 
			
			setPreferredSize(d);
			add(scrollPanel);
		}
		
		public PeersJList getPeersList() {
			return peersJList;
		}
    }
    
    private class PeersJList extends JList {
		DefaultListModel listModel = new DefaultListModel();
    	public PeersJList() {
    		super();
    		setModel(listModel);
			setBackground(Color.YELLOW);
			
			//String prototypeCellValueStr = "";
			//for (int i = 0; i < 40; i++) {
			//	prototypeCellValueStr = prototypeCellValueStr + "a";
			//}
			//setPrototypeCellValue(new PeerObject("peerId", prototypeCellValueStr));
			
			setCellRenderer(new PeersListCellRenderer());
    	}

		public void addPeer (PeerObject peer) {
			if (findInList(peer.getPeerId()) == null) {
				listModel.addElement(peer);
				repaint();
			}
			else {
				System.out.println("addPeer skipping peer " + peer.getPeerName() + ", listmodel = " + listModel);
			}
		}

		public void removePeer (String peerID) {
			PeerObject peer = findInList(peerID);
			if (peer != null) {
				listModel.removeElement(peer);
				repaint();
			}
		}
		
		private PeerObject findInList (String peerId) {
			Enumeration e = listModel.elements();
			while (e.hasMoreElements()) {
				PeerObject curPeer = (PeerObject) e.nextElement();
				if (peerId.equals(curPeer.getPeerId())) {
					return curPeer;
				}
			}
			return null;
		}
    	
    }
}
