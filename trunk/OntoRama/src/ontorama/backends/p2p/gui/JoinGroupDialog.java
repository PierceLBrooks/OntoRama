package ontorama.backends.p2p.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.p2pprotocol.GroupException;
import ontorama.backends.p2p.p2pprotocol.GroupExceptionNotAllowed;
import ontorama.backends.p2p.p2pprotocol.SearchGroupResultElement;
import ontorama.ui.ErrorDialog;

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

    private P2PBackend _p2pBackend;

    private GroupChooser _existingGroupPanel;
    private JPanel _newGroupPanel;
    private JTabbedPane _tabbedPanel;

    private JTextField _newGroupNameField;
    private JTextField _newGroupDescrField;

    private boolean _cancelled = false;
    private int _selectedOption;
    private SearchGroupResultElement _value = null;

    public JoinGroupDialog(Frame parent, P2PBackend p2pBackend)  {
        super(parent, _title, true);

        _p2pBackend = p2pBackend;
        Vector foundGroups = new Vector();
        try {
            foundGroups = _p2pBackend.getSender().sendSearchGroup(null, null);
        }
        catch (Exception e) {
            /// @todo deal with exceptions propertly
            e.printStackTrace();
        }


        _existingGroupPanel = new GroupChooser(foundGroups, "Choose group to join");
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
        
        Action joinGroup = new AbstractAction("OK") {
        	public void actionPerformed(ActionEvent e) {
        		System.out.println("\njoinGroupAction, action command = " + e.getActionCommand() + ", source = " + e.getSource());
        		if (verifyInputCorrect() ) {
        			_cancelled = false;
        			if (joinGroup(_value)) {
        				setVisible(false);
        			}
        			setVisible(false);
        		}
        	}
        };
        okButton.setAction(joinGroup);

//        okButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (verifyInputCorrect() ) {
//                    _cancelled = false;
//                    if (joinGroup(_value)) {
//                        setVisible(false);
//                    }
//                    setVisible(false);
//                }
//            }
//        });
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

        _tabbedPanel.addTab("Existing Group", null, _existingGroupPanel, "Join existing group");
        _tabbedPanel.setSelectedComponent(_existingGroupPanel);

        _newGroupNameField = DialogUtil.createNewGroupNameTextField();
        _newGroupDescrField = DialogUtil.createNewGroupDescriptionTextField();

        _newGroupPanel.setLayout(new BoxLayout(_newGroupPanel, BoxLayout.Y_AXIS));
        _newGroupPanel.add(new JLabel(DialogUtil.newGroupNameLabel));
        _newGroupPanel.add(_newGroupNameField);
        _newGroupPanel.add(new JLabel(DialogUtil.newGroupDescriptionLabel));
        _newGroupPanel.add(_newGroupDescrField);

        _tabbedPanel.addTab("New Group", null, _newGroupPanel, "Create new group");

        return _tabbedPanel;
    }

    /**
     *
     * @return
     * @todo quite a mess, think how to refactor.
     */
    private boolean verifyInputCorrect () {
        SearchGroupResultElement groupToJoin;
        if (_tabbedPanel.getSelectedComponent().equals(_existingGroupPanel) ) {
            groupToJoin = (SearchGroupResultElement) _existingGroupPanel.getValue();
            if (groupToJoin == null) {
                return false;
            }
            //String input = groupToJoin.getName();
            //System.out.println("input = " + input);
            _selectedOption = JoinGroupDialog.OPTION_EXISTING_GROUP;
            _value = groupToJoin;
            return true;
        }
        if (_tabbedPanel.getSelectedComponent().equals(_newGroupPanel)) {
            String input = _newGroupNameField.getText();
            if (DialogUtil.textInputIsValid(this, input, "name")) {
                System.out.println("input = " + input);
                _selectedOption = JoinGroupDialog.OPTION_NEW_GROUP;
                try {
                    System.out.println("attempting to create new group with name " + input + " and description: " + _newGroupDescrField.getText());
                    _p2pBackend.getSender().sendCreateGroup(input,_newGroupDescrField.getText());
                    Vector resVector = _p2pBackend.getSender().sendSearchGroup("Name",input);
                    /// @todo probably should handle whole vector, not just one element.
                    if ( resVector.isEmpty()) {
                        return false;
                    }
                    _value = (SearchGroupResultElement) resVector.firstElement();
                } catch (GroupException e) {
                    System.out.println("ERROR:");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("ERROR:");
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    private boolean joinGroup (SearchGroupResultElement group) {
        try {
            String groupId = group.getID().toString();
            System.out.println("trying to join group id " + groupId);
            _p2pBackend.getSender().sendJoinGroup(groupId);
        } catch (GroupExceptionNotAllowed e) {
        	ErrorDialog.showError(this, e, "Error", e.getMessage());
            System.out.println("ERROR:");
            e.printStackTrace();
            return false;
        } catch (GroupException e) {
        	ErrorDialog.showError(this, e, "Error", e.getMessage());
            System.out.println("ERROR:");
            e.printStackTrace();
            return false;
        }
        System.out.println("Joined: " + group.getName() + " (ID:" + group.getID() + ")");
        return true;
    }

    public boolean actionWasCancelled () {
        return _cancelled;
    }

    public int getSelectedOption () {
        return _selectedOption;
    }

    public SearchGroupResultElement getGroupName () {
        return _value;
    }

}
