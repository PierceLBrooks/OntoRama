package ontorama.backends.p2p.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import ontorama.backends.p2p.p2pprotocol.PeerObject;

/**
 * Renderer for a list displaying peers.
 * 
 * @author nataliya
 * Created on 6/03/2003
 */
public class PeersListCellRenderer extends JLabel implements ListCellRenderer {
	public PeersListCellRenderer() {
		setOpaque(true);
		setHorizontalAlignment(LEFT);
		setVerticalAlignment(CENTER);
	}
	public Component getListCellRendererComponent(
						JList list,	Object value,
						int index, boolean isSelected,
						boolean hasFocus) {
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		if (value == null) {
			return this;
		}
		PeerObject peerObj = (PeerObject) value;
		setText(peerObj.getPeerName());
		setToolTipText("Peer name: " + peerObj.getPeerName() + ", peer id: " + peerObj.getPeerId());
		return this;
	}
}
