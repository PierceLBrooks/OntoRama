package ontorama.backends.p2p.gui;


import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ontorama.backends.p2p.gui.renderer.*;
import ontorama.backends.p2p.p2pprotocol.GroupItemReference;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 14, 2002
 * Time: 10:57:49 AM
 * To change this template use Options | File Templates.
 */
public class GroupChooserPanel extends JPanel {

    private GroupChooserComboBox _nameChooser;

    private Vector _groups;
    
    public GroupChooserPanel (Vector groups, String labelString) {
        super();
        _groups = groups;

        Vector sortedGroups = bubbleSort(_groups.toArray(), _groups.size());
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

    /*
    * sort given array of group names in alphabetical order
    * (using bubble sort algorithm)
    * @todo probably could pass it a vector, no need to use arrays?..
    */
    private Vector bubbleSort (Object array[], int bound ) {
        Object tmp;
        Vector result = new Vector();
        for ( int i = 0; i < bound; i++ ) {
          for ( int j = bound-1; j > i; j-- ) {
              GroupItemReference currentObj = (GroupItemReference) array[j];
          	GroupItemReference previousObj = (GroupItemReference) array[j-1];
              String currentName = currentObj.getName().toLowerCase();
              String previousName = previousObj.getName().toLowerCase();
              //if ( (array[j-1].toLowerCase()).compareTo ( array[j].toLowerCase()) > 0 ) {
              if ( previousName.compareTo( currentName) > 0 ) {
                  tmp = array[j-1];
                  array[j-1] = array[j];
                  array[j] = tmp;
              }
          }
        }

        List list = Arrays.asList(array);
        result = new Vector(list);
        return result;
    }


}
