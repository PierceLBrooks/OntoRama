package ontorama.backends.p2p.gui;


import javax.swing.DefaultListModel;
import javax.swing.JList;

import ontorama.backends.p2p.GroupItemReference;
import ontorama.backends.p2p.gui.renderer.*;

/**
 * @author nataliya
 */
public class GroupChooserList extends JList implements GroupChooser {
	
	DefaultListModel _groups;
	
	public GroupChooserList (DefaultListModel groups) {
		super (groups);
		_groups = groups;
		
		setCellRenderer(new GroupNamesComboBoxRenderer());
	}

	/**
	 * @see ontorama.backends.p2p.gui.GroupChooser#getSelectedGroup()
	 */
	public GroupItemReference getSelectedGroup() {
		return (GroupItemReference) this.getSelectedValue();
	}

}
