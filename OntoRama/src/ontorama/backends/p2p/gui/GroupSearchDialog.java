/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 10, 2002
 * Time: 1:21:38 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.backends.p2p.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class GroupSearchDialog extends JDialog {

    private static final String _title = "Group Search";

    public static final int OPTION_NAME = 1;
    public static final int OPTION_DESCR = 2;
    public static final int OPTION_ALL = 3;

    private JRadioButton _nameButton;
    private JRadioButton _descriptionButton;
    private JRadioButton _allGroupsButton;

    private JTextField _nameField;
    private JTextField _descriptionField;

    private String _value = null;
    private boolean _cancelled = false;
    private int _selectedOption;

    public GroupSearchDialog (Frame parent) {
        super(parent, _title, true);

        JButton cancelButton = new JButton("Cancel");
        final JButton okButton = new JButton("OK");

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _cancelled = true;
                setVisible(false);
            }
        });

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (verifyAllFieldsInputIsCorrect()) {
                   _cancelled = false;
                    setVisible(false);
                }
            }
        });
        getRootPane().setDefaultButton(okButton);


        JPanel mainContentPanel = buildMainContentPanel();
        JPanel topPanel = buildTopDescriptionPanel();
        JPanel buttonPanel = DialogUtil.buildButtonsPanel(okButton, cancelButton);

        Container contentPane = getContentPane();
        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(mainContentPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        pack();
        DialogUtil.centerDialog(parent, this);
    }

    private JPanel buildTopDescriptionPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        /// @todo put meaninfull text into the label
        JLabel label1 = new JLabel("Search for P2P groups ");
        topPanel.add(label1);
        return topPanel;
    }

    private JPanel buildMainContentPanel() {
        _allGroupsButton = new JRadioButton("All Groups");
        _allGroupsButton.setSelected(true);
        _nameButton = new JRadioButton("Name");
        _descriptionButton = new JRadioButton("Description");

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(_allGroupsButton);
        buttonGroup.add(_nameButton);
        buttonGroup.add(_descriptionButton);

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

        JPanel mainContentPanel = new JPanel(new GridLayout(3,2));
        mainContentPanel.add(_allGroupsButton);
        mainContentPanel.add(new JLabel());

        mainContentPanel.add(_nameButton);
        mainContentPanel.add(_nameField);

        mainContentPanel.add(_descriptionButton);
        mainContentPanel.add(_descriptionField);

        return mainContentPanel;

    }

    private boolean verifyAllFieldsInputIsCorrect () {
        if (_nameButton.isSelected()) {
            if (DialogUtil.textInputIsValid(this,_nameField.getText(), " name ")) {
                _value = _nameField.getText();
                _selectedOption = GroupSearchDialog.OPTION_NAME;
                return true;
            }
        }
        else if (_descriptionButton.isSelected()) {
            if (DialogUtil.textInputIsValid(this, _descriptionField.getText(), " description ")) {
                _value = _descriptionField.getText();
                _selectedOption = GroupSearchDialog.OPTION_DESCR;
                return true;
            }
        }
        else if (_allGroupsButton.isSelected()) {
            _selectedOption = GroupSearchDialog.OPTION_ALL;
            return true;
        }
        return false;
    }

    public String getValue() {
        return _value;
    }


    public boolean actionIsCancelled () {
        return _cancelled;
    }

    public int getSelectedOption () {
        return _selectedOption;
    }
}
