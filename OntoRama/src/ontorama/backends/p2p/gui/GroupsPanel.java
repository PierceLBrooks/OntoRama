package ontorama.backends.p2p.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;


import ontorama.backends.p2p.GroupItemReference;
import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.events.JoinGroupEvent;
import ontorama.backends.p2p.events.LeaveGroupEvent;
import ontorama.backends.p2p.events.NewGroupEvent;
import ontorama.ui.ErrorDialog;
import ontorama.ui.OntoRamaApp;

/**
 * @author nataliya
 */
public class GroupsPanel extends JPanel implements GroupView {

	JPanel _newGroupPanel;
	JPanel _allGroupsPanel;
	P2PBackend _p2pBackend;
	private GroupListModel _allGroupsListModel;
	private GroupListModel _joinedGroupsListModel;

	public GroupsPanel(P2PBackend p2pBackend) {
		super();
		_p2pBackend = p2pBackend;
		
		buildNewGroupPanel();
		buildAllGroupsPanel();
		
		BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(boxLayout);
		
		_allGroupsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		_newGroupPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		add(_allGroupsPanel);
		add(Box.createRigidArea(new Dimension(0,5)));
		add(_newGroupPanel);
		add(new JPanel(), new Integer(200));

	}
	
	private void buildNewGroupPanel() {
		_newGroupPanel = new JPanel();
		_newGroupPanel.setLayout(new BoxLayout(_newGroupPanel, BoxLayout.Y_AXIS));
		
		JPanel headingPanel = new JPanel();
		headingPanel.add(new JLabel("Create new group"));
		
		JLabel newGroupNameLabel = new JLabel(DialogUtil.newGroupNameLabel);
		JLabel newGroupDescrLabel = new JLabel(DialogUtil.newGroupDescriptionLabel);

		final JTextField newGroupNameField = DialogUtil.createNewGroupNameTextField();
		final JTextField newGroupDescrField = DialogUtil.createNewGroupDescriptionTextField();
		
		newGroupNameField.setMaximumSize(newGroupNameField.getPreferredSize());
		newGroupDescrField.setMaximumSize(newGroupDescrField.getPreferredSize());

		JButton cancelButton = new JButton("Clear");
		JButton okButton = new JButton("Create group");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String input = newGroupNameField.getText();
				if (! DialogUtil.textInputIsValid(OntoRamaApp.getMainFrame(), input, "name")) {
					return;
				}
				GroupItemReference newGroupRefElement = new GroupItemReference(null, input, newGroupDescrField.getText());
				_p2pBackend.getEventBroker().processEvent(new NewGroupEvent(newGroupRefElement));
			}
		});
		
		JPanel buttonPanel = DialogUtil.buildButtonsPanel(okButton, cancelButton);
		
		headingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		newGroupNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		newGroupNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
		newGroupDescrLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		newGroupDescrField.setAlignmentX(Component.LEFT_ALIGNMENT);
		buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		
		_newGroupPanel.add(headingPanel);
		_newGroupPanel.add(newGroupNameLabel);
		_newGroupPanel.add(newGroupNameField);
		_newGroupPanel.add(newGroupDescrLabel);
		_newGroupPanel.add(newGroupDescrField);
		_newGroupPanel.add(buttonPanel);
		
		_newGroupPanel.setBorder(BorderFactory.createEtchedBorder());		
	}
	
	private void buildAllGroupsPanel() {
		_allGroupsPanel = new JPanel();
		_allGroupsPanel.setLayout(new BoxLayout(_allGroupsPanel, BoxLayout.X_AXIS));
		
		JPanel leftPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel rightPanel = new JPanel();
		
		Dimension d = new Dimension( 150, 200);
		
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(new JLabel("Joined"));
		_joinedGroupsListModel = new GroupListModel();
		final GroupChooser joinedGroupsList = new GroupChooserList(_joinedGroupsListModel);
		JScrollPane leftListScrollPane = new JScrollPane((JList) joinedGroupsList);
		leftListScrollPane.setPreferredSize(d);
		leftPanel.add(leftListScrollPane);

		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.add(new JLabel("Available groups"));
		_allGroupsListModel = new GroupListModel();
		final GroupChooser allGroupsList = new GroupChooserList(_allGroupsListModel);
		JScrollPane rigthListScrollPane = new JScrollPane((JList) allGroupsList);
		rigthListScrollPane.setPreferredSize(d);
		rightPanel.add(rigthListScrollPane);
		
		centerPanel.setLayout(new BoxLayout(centerPanel,BoxLayout.Y_AXIS));
		JButton leaveGroupButton = new JButton(">>"); 
		leaveGroupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				GroupItemReference groupToLeave = joinedGroupsList.getSelectedGroup();
				if (groupToLeave == null) {
					ErrorDialog.showError(OntoRamaApp.getMainFrame(), "Error",
										"Please choose a group you would like to leave");
				}
				else {
					_p2pBackend.getEventBroker().processEvent(new LeaveGroupEvent(groupToLeave));
				}
			}
		});
		
		JButton joinGroupButton = new JButton("<<");
		joinGroupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				GroupItemReference groupToJoin = allGroupsList.getSelectedGroup();
				if (groupToJoin == null) {
					ErrorDialog.showError(OntoRamaApp.getMainFrame(),"Error",
							"Please choose a group you would like to join");
				}
				else {
					_p2pBackend.getEventBroker().processEvent(new JoinGroupEvent(groupToJoin));
				}
			}
		});
			
		JButton refreshButton = new JButton("Refresh");
		refreshButton.setToolTipText("Refresh list of available groups");
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");		
				System.out.println("REFRESH on groups, time: " + sdf.format(new Date()));				
				updateGroups();
			}
		});
		
		leaveGroupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		joinGroupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);


		centerPanel.add(leaveGroupButton);
		centerPanel.add(joinGroupButton);
		centerPanel.add(refreshButton);
		
		
		_allGroupsPanel.add(leftPanel);
		_allGroupsPanel.add(centerPanel);
		_allGroupsPanel.add(rightPanel);
		
		_allGroupsPanel.setBorder(BorderFactory.createEtchedBorder());

	}
	
	public void updateGroups() {
		populateWithJoinedGroups();
		populateWithFoundGroups();
	}

	private void populateWithFoundGroups() {
		List foundGroups = new LinkedList();
		try {
			foundGroups =  _p2pBackend.getSender().sendSearchGroup(null, null);
		} 
		catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError(this, e, "Error searching for available groups", e.getMessage());
		}
		
		Iterator it = foundGroups.iterator();
		while (it.hasNext()) {
			GroupItemReference cur = (GroupItemReference) it.next();
			if (_joinedGroupsListModel.contains(cur)) {
				continue;
			}
			_allGroupsListModel.addElement(cur);
		}
	}

	private void populateWithJoinedGroups () {
		List joinedGroups =  _p2pBackend.getSender().getJoinedGroupsInSearchGroupResultFormat();
		Iterator it = joinedGroups.iterator();
		while (it.hasNext()) {
			GroupItemReference element = (GroupItemReference) it.next();
			if (_allGroupsListModel.contains(element)) {
				continue;
			}
			_joinedGroupsListModel.addElement(element);
		}

	}
	
	public void addGroup(GroupItemReference groupReferenceElement) {
		_joinedGroupsListModel.addElement(groupReferenceElement);
		_allGroupsListModel.removeElement(groupReferenceElement);
	}
	
	

	/**
	 * @see ontorama.backends.p2p.gui.GroupView#removeGroup(ontorama.backends.p2p.p2pprotocol.GroupReferenceElement)
	 */
	public void removeGroup(GroupItemReference groupReferenceElement) {
		System.out.println("GroupsPanel::removeGroup, group = " + groupReferenceElement.getName() + ", ref = " + groupReferenceElement);
		_allGroupsListModel.addElement(groupReferenceElement);
		_joinedGroupsListModel.removeElement(groupReferenceElement);
		System.out.println("all_groups size = " + _allGroupsListModel.size() + ", joined group size = " + _joinedGroupsListModel.size());
		_allGroupsPanel.repaint();
	}
	
	private class GroupListModel extends DefaultListModel {
		public GroupListModel() {
			super();
		}

		public boolean contains (Object group) {
			GroupItemReference groupItem = (GroupItemReference) group;
			if (getFromGroupsList(groupItem) != null) {
				return true;
			}
			return false;
		}	
		
		public void addElement(Object group) {
			if (this.contains(group)) {
				return;
			}
			super.addElement(group);
		}
		
		public boolean removeElement(Object group) {
			GroupItemReference groupElement = this.getFromGroupsList((GroupItemReference) group);
			if (groupElement != null) {
				return super.removeElement(groupElement);
			}
			return false;
		}

		private GroupItemReference getFromGroupsList (GroupItemReference group) {
			Enumeration e = super.elements();
			while (e.hasMoreElements()) {
				GroupItemReference cur = (GroupItemReference) e.nextElement();
				if (cur.getID().equals(group.getID())) {
					return cur;
				}
			}
			return null;
		}

	}

}
