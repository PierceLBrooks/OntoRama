package ontorama.backends.p2p.gui;

import ontorama.backends.p2p.p2pprotocol.ItemReference;

/**
 * This interface will be used to implement any GUI components that will be
 * displaying a selectable list of peer groups and provide means of accessing
 * selected group.
 * @author nataliya
 */
public interface GroupChooser {
	public ItemReference getSelectedGroup ();
}
