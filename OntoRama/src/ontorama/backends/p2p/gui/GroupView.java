/*
 * Created on 13/02/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package ontorama.backends.p2p.gui;

import ontorama.backends.p2p.p2pprotocol.GroupItemReference;

/**
 * @author nataliya
 */
public interface GroupView {
	public void addGroup(GroupItemReference groupReferenceElement);
	public void removeGroup(GroupItemReference groupReferenceElement);
}
