package ontorama.backends.p2p.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 10:51:01
 * To change this template use Options | File Templates.
 */
public class JoinGroupDialog extends JDialog {

    public static final int OPTION_EXISTING_GROUP = 1;
    public static final int OPTION_NEW_GROUP = 2;

    private static final String _title = "Join P2P Group";

    private JPanel _existingGroupPanel;
    private JPanel _newGroupPanel;
    private JTabbedPane _tabbedPanel;

    private JTextField _existingGroupNameField;
    private JTextField _newGroupNameField;

    private boolean _cancelled = false;
    private int _selectedOption;
    private String _value = null;

    public JoinGroupDialog(Frame parent)  {
        super(parent, _title, true);

        _existingGroupPanel = new JPanel();
        _newGroupPanel = new JPanel();
        JTabbedPane tabbedPanel = buildTabbedPanel();

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
                if (verifyInputCorrect() ) {
                    _cancelled = false;
                    setVisible(false);
                }
            }
        });
        getRootPane().setDefaultButton(okButton);

        JPanel buttonPanel = DialogUtil.buildButtonsPanel(okButton, cancelButton);

        Container contentPanel = getContentPane();
        contentPanel.add(tabbedPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        pack();
        DialogUtil.centerDialog(parent, this);
    }

    private JTabbedPane buildTabbedPanel() {
        _tabbedPanel = new JTabbedPane();
        JLabel nameLabel = new JLabel("Name ");

        _existingGroupNameField = new JTextField(20);
        _existingGroupNameField.setToolTipText("Type name of a group you want to join");

        _existingGroupPanel.add(new JLabel("Name "));
        _existingGroupPanel.add(_existingGroupNameField);

        _tabbedPanel.addTab("Existing Group", null, _existingGroupPanel, "Join existing group");
        _tabbedPanel.setSelectedComponent(_existingGroupPanel);

        _newGroupNameField = new JTextField(20);
        _newGroupNameField.setToolTipText("Type name of a group you want to create");

        _newGroupPanel.add(nameLabel);
        _newGroupPanel.add(_newGroupNameField);

        _tabbedPanel.addTab("New Group", null, _newGroupPanel, "Create new group");

        return _tabbedPanel;
    }

    private boolean verifyInputCorrect () {
        if (_tabbedPanel.getSelectedComponent().equals(_existingGroupPanel) ) {
            String input = _existingGroupNameField.getText();
            if (DialogUtil.textInputIsValid(this, input, "name")) {
                System.out.println("input = " + input);
                _selectedOption = JoinGroupDialog.OPTION_EXISTING_GROUP;
                _value = input;
                return true;
            }
        }
        if (_tabbedPanel.getSelectedComponent().equals(_newGroupPanel)) {
            String input = _newGroupNameField.getText();
            if (DialogUtil.textInputIsValid(this, input, "name")) {
                System.out.println("input = " + input);
                _selectedOption = JoinGroupDialog.OPTION_NEW_GROUP;
                _value = input;
                return true;
            }
        }
        return false;
    }

    public boolean actionWasCancelled () {
        return _cancelled;
    }

    public int getSelectedOption () {
        return _selectedOption;
    }

    public String getGroupName () {
        return _value;
    }

}
