/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 10, 2002
 * Time: 10:59:41 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.backends.p2p.gui;

import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.gui.action.*;
import ontorama.view.OntoRamaApp;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class P2PJMenu extends JMenu {

    private P2PBackend _p2pBackend;

    private static boolean p2pEnabled = false;

    private static final String _menuName = "P2P";

    private Action _enableP2PAction;
    private Action _searchAction;
    private Action _joinGroupAction;
    private Action _leaveGroupAction;
    private Action _updatePanelAction;
    private Action _resetChangePanelAction;

    public P2PJMenu (P2PBackend p2pBackend) {
        super();
        _p2pBackend = p2pBackend;
        setText(_menuName);

        _enableP2PAction = new ActionEnableP2P("Start P2P");
        _searchAction = new ActionGroupSearch("Group search");

        add(_enableP2PAction);
        addSeparator();

        add(_searchAction);
        addSeparator();

        _joinGroupAction = new ActionJoinGroup("Join Group");
        add(_joinGroupAction);
        _leaveGroupAction = new ActionLeaveGroup("Leave Group");
        add(_leaveGroupAction);
        addSeparator();

        _updatePanelAction = new ActionUpdateP2PPanel("Update P2P Panel");
        add(_updatePanelAction);

        _resetChangePanelAction = new ActionResetChangePanel("Reset Change Panel");
        add(_resetChangePanelAction);
        addSeparator();
    }

    private class ActionEnableP2P extends AbstractAction {
        public ActionEnableP2P(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            _p2pBackend = new P2PBackend(null);
        }

    }

}
