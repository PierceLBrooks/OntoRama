package ontorama.backends.p2p.gui;

import java.awt.Color;
import java.awt.Dimension;

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

/**
 * @author nataliya
 */
public class GroupsPanel extends JPanel {

	JPanel _newGroupPanel;
	JPanel _allGroupsPanel;

	public GroupsPanel() {
		super();
		
		buildNewGroupPanel();
		buildAllGroupsPanel();
		
		BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(boxLayout);
		add(_allGroupsPanel);
		add(Box.createRigidArea(new Dimension(0,5)));
		add(_newGroupPanel);

	}
	
	private void buildNewGroupPanel() {
		_newGroupPanel = new JPanel();
		_newGroupPanel.setBackground(Color.WHITE);
		_newGroupPanel.setLayout(new BoxLayout(_newGroupPanel, BoxLayout.Y_AXIS));
		_newGroupPanel.add(new JLabel("Create new group"));
		
		JLabel nameLabel = new JLabel("Name ");
		JTextField newGroupNameField = new JTextField(20);
		newGroupNameField.setToolTipText("Type name of a group you want to create");

		JTextField newGroupDescrField = new JTextField(40);
		newGroupDescrField.setToolTipText("Type description for this group");
		newGroupDescrField.setText("");
		
		JButton cancelButton = new JButton("Clear");
		JButton okButton = new JButton("Create group");
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(cancelButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(okButton);



		_newGroupPanel.add(nameLabel);
		_newGroupPanel.add(newGroupNameField);
		_newGroupPanel.add(new JLabel("Description"));
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
		DefaultListModel joinedGroupsListModel = new DefaultListModel();
		joinedGroupsListModel.addElement("el 1");
		joinedGroupsListModel.addElement("el 2");
		joinedGroupsListModel.addElement("el 3");
		JList joinedGroupsList = new JList(joinedGroupsListModel);
		JScrollPane leftListScrollPane = new JScrollPane(joinedGroupsList);
		leftPanel.add(leftListScrollPane); 
		
		centerPanel.setLayout(new BoxLayout(centerPanel,BoxLayout.Y_AXIS));
		centerPanel.add(new JButton(">>"));
		centerPanel.add(new JButton("<<"));
		
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.add(new JLabel("Available groups"));
		DefaultListModel allGroupsListModel = new DefaultListModel();
		allGroupsListModel.addElement("el 4");
		allGroupsListModel.addElement("el 5");
		allGroupsListModel.addElement("el 6");
		JList allGroupsList = new JList(allGroupsListModel);
		JScrollPane rigthListScrollPane = new JScrollPane(allGroupsList);
		rightPanel.add(rigthListScrollPane);
		
		_allGroupsPanel.add(leftPanel);
		_allGroupsPanel.add(centerPanel);
		_allGroupsPanel.add(rightPanel);
		
		_allGroupsPanel.setBorder(BorderFactory.createEtchedBorder());
		
	}

}
