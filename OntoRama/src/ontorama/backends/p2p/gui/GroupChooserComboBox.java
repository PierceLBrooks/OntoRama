package ontorama.backends.p2p.gui;

import javax.swing.JComboBox;

import ontorama.backends.p2p.GroupItemReference;
import ontorama.backends.p2p.gui.renderer.*;

public class GroupChooserComboBox extends JComboBox implements GroupChooser {

	public GroupChooserComboBox (Object groups[]) {
		super(groups);
		
		setRenderer(new GroupNamesComboBoxRenderer());
	}

	public GroupItemReference getSelectedGroup() {
		return (GroupItemReference) getSelectedItem();
	}
}
