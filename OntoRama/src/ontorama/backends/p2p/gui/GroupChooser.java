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

    private P2PSender _p2pSender;

    private JRadioButton _nameButton;
    private JRadioButton _descriptionButton;

    private JComboBox _nameChooser;
    private JComboBox _descriptionChooser;

    private SearchGroupResultElement _value = null;

    public GroupChooser (P2PSender p2pSender) {
        super();
        _p2pSender = p2pSender;

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

        Vector foundGroups = new Vector();
        try {
            foundGroups = _p2pSender.sendSearchGroup(null, null);
        }
        catch (Exception e) {
            /// @todo deal with exceptions propertly
            e.printStackTrace();
        }

        _nameChooser = new JComboBox(foundGroups);
        _nameChooser.setRenderer(new NameComboBoxRenderer());

        _nameChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                _nameButton.setSelected(true);
                _value = (SearchGroupResultElement) _nameChooser.getSelectedItem();
            }
        });


        _descriptionChooser = new JComboBox(new Vector());
        _descriptionChooser.setRenderer(new DescriptionComboBoxRenderer());

        _descriptionChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                _descriptionButton.setSelected(true);
                _value = (SearchGroupResultElement) _descriptionChooser.getSelectedItem();
            }
        });

        setLayout(new BorderLayout());

        JPanel p = new JPanel();
        p.add(new JLabel("Choose group to join"));
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

    class NameComboBoxRenderer extends JLabel  implements ListCellRenderer {
        public NameComboBoxRenderer() {
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
            SearchGroupResultElement element = (SearchGroupResultElement) value;
            setText(element.getName());
            setToolTipText(element.getDescription());
            return this;
        }
    }

    class DescriptionComboBoxRenderer extends JLabel  implements ListCellRenderer {
        public DescriptionComboBoxRenderer() {
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
            SearchGroupResultElement element = (SearchGroupResultElement) value;
            setText(element.getDescription());
            setToolTipText(element.getName());
            return this;
        }
    }

}
