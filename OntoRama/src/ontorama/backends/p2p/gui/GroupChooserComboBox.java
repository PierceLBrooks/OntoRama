package ontorama.backends.p2p.gui;

import java.util.Vector;
import javax.swing.JComboBox;

import ontorama.backends.p2p.p2pprotocol.GroupReferenceElement;

/**
 * @author nataliya
 */
public class GroupChooserComboBox extends JComboBox implements GroupChooser {

	private Vector _groups;
	public GroupChooserComboBox (Vector groups) {
		super(groups);
		_groups = groups;
		
		setRenderer(new GroupNamesComboBoxRenderer());
	}

	/**
	 * @see ontorama.backends.p2p.gui.GroupChooser#getSelectedGroup()
	 */
	public GroupReferenceElement getSelectedGroup() {
		return (GroupReferenceElement) getSelectedItem();
	}

}
