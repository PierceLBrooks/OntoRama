package ontorama.backends.p2p.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

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


import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.gui.action.NewGroupAction;
import ontorama.backends.p2p.p2pprotocol.GroupReferenceElement;
import ontorama.ui.ErrorDialog;

/**
 * @author nataliya
 */
public class GroupsPanel extends JPanel {

	JPanel _newGroupPanel;
	JPanel _allGroupsPanel;
	P2PBackend _p2pBackend;
	private DefaultListModel _allGroupsListModel;
	private DefaultListModel _joinedGroupsListModel;

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

		JTextField newGroupNameField = DialogUtil.createNewGroupNameTextField();
		JTextField newGroupDescrField = DialogUtil.createNewGroupDescriptionTextField();
		
		newGroupNameField.setMaximumSize(newGroupNameField.getPreferredSize());
		newGroupDescrField.setMaximumSize(newGroupDescrField.getPreferredSize());

		JButton cancelButton = new JButton("Clear");
		JButton okButton = new JButton("Create group");
		okButton.setAction(new NewGroupAction(this, newGroupNameField, newGroupDescrField, _p2pBackend));
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
		
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(new JLabel("Joined"));
		_joinedGroupsListModel = new DefaultListModel();
		GroupChooser joinedGroupsList = new GroupChooserList(_joinedGroupsListModel);
		JScrollPane leftListScrollPane = new JScrollPane((JList) joinedGroupsList);
		leftPanel.add(leftListScrollPane); 
		
		centerPanel.setLayout(new BoxLayout(centerPanel,BoxLayout.Y_AXIS));
		JButton leaveGroupButton = new JButton(">>"); 
		JButton joinGroupButton = new JButton("<<");
		
		JButton refreshButton = new JButton("Refresh");
		refreshButton.setToolTipText("Refresh list of available groups");
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateGroups();
			}
		});
		
		leaveGroupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		joinGroupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);


		centerPanel.add(leaveGroupButton);
		centerPanel.add(joinGroupButton);
		centerPanel.add(refreshButton);
		
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.add(new JLabel("Available groups"));
		_allGroupsListModel = new DefaultListModel();
		GroupChooser allGroupsList = new GroupChooserList(_allGroupsListModel);
		JScrollPane rigthListScrollPane = new JScrollPane((JList) allGroupsList);
		rightPanel.add(rigthListScrollPane);
		
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
		System.out.println("GroupsPanel::populateWithFoundGroups");
		Vector foundGroups = new Vector();
		try {
			foundGroups = _p2pBackend.getSender().sendSearchGroup(null, null);
		} 
		catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError(this, e, "Error searching for available groups", e.getMessage());
		}
		
		Enumeration e = foundGroups.elements();
		while (e.hasMoreElements()) {
			GroupReferenceElement cur = (GroupReferenceElement) e.nextElement();
			if (groupsListContainsGroup(_allGroupsListModel, cur)) {
				continue;
			}
			if (groupsListContainsGroup(_joinedGroupsListModel, cur)) {
				continue;
			}
			_allGroupsListModel.addElement(cur);
		}
	}

	private void populateWithJoinedGroups () {
		Vector joinedGroups = _p2pBackend.getSender().getJoinedGroupsInSearchGroupResultFormat();
		Enumeration e = joinedGroups.elements();
		while (e.hasMoreElements()) {
			GroupReferenceElement element = (GroupReferenceElement) e.nextElement();
			if (groupsListContainsGroup(_joinedGroupsListModel, element)) {
				continue;
			}
			if (groupsListContainsGroup(_allGroupsListModel, element)) {
				continue;
			}
			_joinedGroupsListModel.addElement(element);
		}

	}
	
	private boolean groupsListContainsGroup (DefaultListModel list, GroupReferenceElement group) {
		Enumeration e = list.elements();
		while (e.hasMoreElements()) {
			GroupReferenceElement cur = (GroupReferenceElement) e.nextElement();
			if (cur.getID().equals(group.getID())) {
				return true;
			}
		}
		return false;
	}
	


//	public void run() {
//		System.out.println("run");
//		 while (true) {
//		 	if (_p2pBackend.getSender() != null) {
//				populateWithFoundGroups();
//		 	}
//			 // wait a bit before sending next discovery message
//			 try {
//				 Thread.sleep(10 * 1000);
//			 }
//			 catch(Exception e) {
//			 	e.printStackTrace();
//			 }
//		 } //end while
//	 }
	

}
