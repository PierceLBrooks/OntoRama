/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 10, 2002
 * Time: 10:59:41 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.backends.p2p.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.gui.action.ActionJoinGroup;
import ontorama.backends.p2p.gui.action.ActionLeaveGroup;
import ontorama.backends.p2p.gui.action.ActionResetChangePanel;
import ontorama.backends.p2p.gui.action.ActionUpdateP2PPanel;
import ontorama.backends.p2p.p2pmodule.P2PSender;
import ontorama.ui.OntoRamaApp;

public class P2PJMenu extends JMenu {

    private P2PBackend _p2pBackend;
    private P2PSender _p2pSender;
    private Frame _parentFrame;

    private static boolean p2pEnabled = false;

    private static final String _menuName = "P2P";

    private Action _enableP2PAction;
    private Action _searchAction;
    private Action _joinGroupAction;
    private Action _leaveGroupAction;
    private Action _updatePanelAction;
    private Action _resetChangePanelAction;

    public P2PJMenu (P2PBackend p2pBackend, P2PSender p2pSender) {
        super();
        _p2pBackend = p2pBackend;
        _p2pSender = p2pSender;
        _parentFrame = OntoRamaApp.getMainFrame();
        setText(_menuName);

        _enableP2PAction = new ActionEnableP2P("Show P2P updates window");
        add(_enableP2PAction);
        addSeparator();

//        _searchAction = new ActionGroupSearch("Group search", _p2pSender);
//        add(_searchAction);
//        addSeparator();

        _joinGroupAction = new ActionJoinGroup("Join Group", _p2pSender);
        add(_joinGroupAction);
        _leaveGroupAction = new ActionLeaveGroup("Leave Group", _p2pSender);
        add(_leaveGroupAction);
        addSeparator();

        _updatePanelAction = new ActionUpdateP2PPanel("Update Peer Panel",_p2pSender);
        add(_updatePanelAction);

        _resetChangePanelAction = new ActionResetChangePanel("Reset Change Panel", _p2pBackend);
        add(_resetChangePanelAction);
        addSeparator();
    }

    private class ActionEnableP2P extends AbstractAction {
        public ActionEnableP2P(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            //_p2pBackend = new P2PBackend();
            //P2PMainPanel p2pPanel = new P2PMainPanel(_parentFrame);
            //p2pPanel.showP2PPanel(true);
            _p2pBackend.showPanels(true);

        }

    }

}
