package ontorama.backends.p2p.gui;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.jxta.peergroup.PeerGroup;
import ontorama.backends.p2p.p2pprotocol.SearchGroupResultElement;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 14, 2002
 * Time: 10:57:49 AM
 * To change this template use Options | File Templates.
 */
public class GroupChooserPanel extends JPanel {

    private JRadioButton _nameButton;
    private JRadioButton _descriptionButton;

    private JComboBox _nameChooser;
    private JComboBox _descriptionChooser;

    private Object _value = null;

    private Vector _groups;

    public GroupChooserPanel (Vector groups, String labelString) {
        super();
        _groups = groups;

        _nameButton = new JRadioButton("Name");
        _nameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                _value =  _nameChooser.getSelectedItem();
            }
        });

        _descriptionButton = new JRadioButton("Description");
        _descriptionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                _value =  _descriptionChooser.getSelectedItem();
            }
        });

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(_nameButton);
        buttonGroup.add(_descriptionButton);

        Vector sortedGroups = bubbleSort(_groups.toArray(), _groups.size());
        _nameChooser = new JComboBox(sortedGroups);
        _nameChooser.setRenderer(new GroupNamesComboBoxRenderer());

        _nameChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                _nameButton.setSelected(true);
                _value = _nameChooser.getSelectedItem();
            }
        });


        _descriptionChooser = new JComboBox(_groups);
        _descriptionChooser.setRenderer(new GroupDescriptionsComboBoxRenderer());

        _descriptionChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                _descriptionButton.setSelected(true);
                _value =  _descriptionChooser.getSelectedItem();
            }
        });

        setLayout(new BorderLayout());

        JPanel p = new JPanel();
        p.add(new JLabel(labelString));
        add(p, BorderLayout.NORTH);

        JPanel p1 = new JPanel(new FlowLayout());
        p1.add(_nameButton);
        p1.add(_nameChooser);
        add(p1, BorderLayout.CENTER);

//        JPanel p2 = new JPanel(new FlowLayout());
//        p2.add(_descriptionButton);
//        p2.add(_descriptionChooser);
//        add(p2, BorderLayout.SOUTH);
    }


    public Object getValue () {
        System.out.println("group chooser returning value = " + _value + ", class = " + _value.getClass());
        return _value;
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
              Object currentObj = array[j];
              Object previousObj = array[j-1];
              String currentName;
              String previousName;
              /// @todo this if is a hack. all vectors used in this group chooser should implement the same interface.
              if (currentObj instanceof  SearchGroupResultElement) {
                  currentName = ( (SearchGroupResultElement) currentObj ).getName().toLowerCase();
                  previousName = ( (SearchGroupResultElement) previousObj).getName().toLowerCase();
              }
              else {
                  currentName = ( (PeerGroup) currentObj).getPeerGroupName().toLowerCase();
                  previousName = ( (PeerGroup) previousObj).getPeerGroupName().toLowerCase();
              }
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
