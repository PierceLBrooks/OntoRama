package ontorama.backends.p2p.gui;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ontorama.backends.p2p.GroupItemReference;
import ontorama.backends.p2p.gui.renderer.*;

public class GroupChooserPanel extends JPanel {

    private GroupChooserComboBox _nameChooser;

    public GroupChooserPanel (Vector groups, String labelString) {
        super();

        Object[] sortedGroups = sortCaseInsensitive(groups);
        _nameChooser = new GroupChooserComboBox(sortedGroups);
        _nameChooser.setRenderer(new GroupNamesComboBoxRenderer());

        setLayout(new BorderLayout());

        JPanel p = new JPanel();
        p.add(new JLabel(labelString));
        add(p, BorderLayout.NORTH);

        add(_nameChooser, BorderLayout.CENTER);
    }

   
    public GroupChooser getGroupChooser () {
    	return (GroupChooser) _nameChooser;
    }

    private Object[] sortCaseInsensitive(Collection collectionToSort) {
        Object[] result = collectionToSort.toArray();
    	Arrays.sort(result, new Comparator(){
            public int compare(Object o1, Object o2) {
                GroupItemReference ref1 = (GroupItemReference) o1;
                GroupItemReference ref2 = (GroupItemReference) o2;
                String name1 = ref1.getName().toLowerCase();
                String name2 = ref2.getName().toLowerCase();
                return name1.compareTo(name2);
            }
    	});
        return result;
    }
}

