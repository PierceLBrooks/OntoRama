package ontorama.backends.p2p.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import ontorama.backends.p2p.p2pprotocol.ItemReference;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 16, 2002
 * Time: 9:40:59 AM
 * To change this template use Options | File Templates.
 */
public class GroupDescriptionsComboBoxRenderer extends JLabel  implements ListCellRenderer {

    public GroupDescriptionsComboBoxRenderer() {
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
        ItemReference element = (ItemReference) value;
        setText(element.getDescription());
        setToolTipText(element.getName());
        return this;
    }
}

