/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 10, 2002
 * Time: 1:21:38 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.backends.p2p.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GroupSearchDialog extends JDialog {
    private JList list;

    private static final String _title = "Group Search";

    private static int OPTION_NAME = 1;
    private static int OPTION_DESCR = 2;
    private static int OPTION_ALL = 3;

    private JRadioButton _nameButton;
    private JRadioButton _descriptionButton;
    private JRadioButton _allGroupsButton;

    private JTextField _nameField;
    private JTextField _descriptionField;

    private String _value = null;
    private boolean _cancelled = false;
    private int _selectedOption;

    public GroupSearchDialog (Frame frame) {
        super(frame, _title, true);

        JButton cancelButton = new JButton("Cancel");
        final JButton okButton = new JButton("OK");

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _cancelled = true;
                showDialog(false);
            }
        });

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (_nameButton.isSelected()) {
                    System.out.println("name button is selected");
                    if (textInputIsValid(_nameField.getText(), " name ")) {
                        _cancelled = false;
                        _value = _nameField.getText();
                        _selectedOption = GroupSearchDialog.OPTION_NAME;
                        showDialog(false);
                    }
                }
                else if (_descriptionButton.isSelected()) {
                    System.out.println("description button is selected");
                    if (textInputIsValid(_descriptionField.getText(), " description ")) {
                        _cancelled = false;
                        _value = _descriptionField.getText();
                        _selectedOption = GroupSearchDialog.OPTION_DESCR;
                        showDialog(false);
                    }
                }
                else if (_allGroupsButton.isSelected()) {
                    System.out.println("all groups button is selected");
                    _cancelled = false;
                    _selectedOption = GroupSearchDialog.OPTION_ALL;
                    showDialog(false);
                }
            }
        });
        getRootPane().setDefaultButton(okButton);

        _nameButton = new JRadioButton("Name");
        _descriptionButton = new JRadioButton("Description");
        _allGroupsButton = new JRadioButton("All Groups");

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(_nameButton);
        buttonGroup.add(_descriptionButton);
        buttonGroup.add(_allGroupsButton);

        _nameField = new JTextField();
        _nameField.addKeyListener(new KeyListener () {
            public void keyTyped(KeyEvent e) {
                _nameButton.setSelected(true);
            }
            public void keyPressed(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}
        } );
        _descriptionField = new JTextField();
        _descriptionField.addKeyListener(new KeyListener() {
           public void keyTyped (KeyEvent e) {
               _descriptionButton.setSelected(true);
           }
            public void keyPressed(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}
        });

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        /// @todo put meaninfull text into the label
        JLabel label1 = new JLabel("Search for ... ");
        JLabel label2 = new JLabel("TODO: put something meaninfull here)...");
        topPanel.add(label1);
        topPanel.add(label2);

        JPanel mainContentPanel = new JPanel(new GridLayout(3,2));
        mainContentPanel.add(_nameButton);
        mainContentPanel.add(_nameField);

        mainContentPanel.add(_descriptionButton);
        mainContentPanel.add(_descriptionField);

        mainContentPanel.add(_allGroupsButton);


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(okButton);

        Container contentPane = getContentPane();
        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(mainContentPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        pack();
    }

    private void showDialog (boolean isVisible) {
        setVisible(isVisible);
    }

    public String getValue() {
        return _value;
    }

    private void setValue(String value) {
        _value = value;
    }

    private boolean textInputIsValid (String text, String promptName) {
        if (text.length() <= 0) {
            JOptionPane.showMessageDialog(this, "Please enter " + promptName + ".");
            return false;
        }
        return true;
    }

    public boolean actionIsCancelled () {
        return _cancelled;
    }

    public int getSelectedOption () {
        return _selectedOption;
    }
}
