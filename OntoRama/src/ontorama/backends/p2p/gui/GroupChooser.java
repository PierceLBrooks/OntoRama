package ontorama.backends.p2p.gui;

import ontorama.backends.p2p.p2pmodule.P2PSender;
import ontorama.backends.p2p.p2pprotocol.SearchGroupResultElement;

import javax.swing.*;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 14, 2002
 * Time: 10:57:49 AM
 * To change this template use Options | File Templates.
 */
public class GroupChooser extends JPanel {

    private JRadioButton _nameButton;
    private JRadioButton _descriptionButton;

    private JComboBox _nameChooser;
    private JComboBox _descriptionChooser;

    private SearchGroupResultElement _value = null;

    private Vector _groups;

    public GroupChooser (Vector groups, String labelString) {
        super();
        _groups = groups;

        _nameButton = new JRadioButton("Name");
        _nameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                _value = (SearchGroupResultElement) _nameChooser.getSelectedItem();
            }
        });

        _descriptionButton = new JRadioButton("Description");
        _descriptionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                _value = (SearchGroupResultElement) _descriptionChooser.getSelectedItem();
            }
        });

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(_nameButton);
        buttonGroup.add(_descriptionButton);

        _nameChooser = new JComboBox(_groups);
        _nameChooser.setRenderer(new GroupNamesComboBoxRenderer());

        _nameChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                _nameButton.setSelected(true);
                _value = (SearchGroupResultElement) _nameChooser.getSelectedItem();
            }
        });


        _descriptionChooser = new JComboBox(_groups);
        _descriptionChooser.setRenderer(new GroupDescriptionsComboBoxRenderer());

        _descriptionChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                _descriptionButton.setSelected(true);
                _value = (SearchGroupResultElement) _descriptionChooser.getSelectedItem();
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


    public SearchGroupResultElement getValue () {
        return _value;
    }
}
