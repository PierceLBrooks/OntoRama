package ontorama.backends.p2p.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import net.jxta.peergroup.PeerGroup;
import ontorama.backends.p2p.p2pprotocol.SearchGroupResultElement;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 16, 2002
 * Time: 9:39:36 AM
 * To change this template use Options | File Templates.
 */
public class GroupNamesComboBoxRenderer extends JLabel  implements ListCellRenderer {

    public GroupNamesComboBoxRenderer() {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
    }

    public Component getListCellRendererComponent(
                                        JList list,
                                        Object value,
                                        int index,
                                        boolean isSelected,
                                        boolean cellHasFocus) {
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
        /// @todo this if seems as a hack!
        if (value instanceof  SearchGroupResultElement) {
            SearchGroupResultElement element = (SearchGroupResultElement) value;
            setText(element.getName());
            setToolTipText(element.getDescription());
        }
        else {
            PeerGroup peerGroup = (PeerGroup) value;
            setText(peerGroup.getPeerGroupName());
        }
        return this;
    }
}
