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
import ontorama.view.OntoRamaApp;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class P2PJMenu extends JMenu {

    private P2PBackend _p2pBackend;

    private static final String _menuName = "P2P";

    private Action _searchAction;

    public P2PJMenu (P2PBackend p2pBackend) {
        super();
        _p2pBackend = p2pBackend;
        setText(_menuName);

        _searchAction = new ActionSearch("Group search");

        add(_searchAction);
        addSeparator();

        Action createGroup = new AbstractAction("Create group") {
            public void actionPerformed(ActionEvent e) {
                System.out.println("...action createGroup");
            }
        };

        Action joinGroup = new AbstractAction("Join group") {
            public void actionPerformed(ActionEvent e) {
                System.out.println("...action joinGroup");
            }
        };

        Action leaveGroup = new AbstractAction("Leave group") {
            public void actionPerformed(ActionEvent e) {
                System.out.println("...action leaveGroup");
            }
        };

        add(createGroup);
        add(joinGroup);
        add(leaveGroup);
        addSeparator();

        Action updatePanel = new AbstractAction("Update P2P Panel") {
            public void actionPerformed(ActionEvent e) {
                System.out.println("...action updatePanel");
            }
        };

        Action resetChangePanel = new AbstractAction("Reset Change Panel") {
            public void actionPerformed(ActionEvent e) {
                System.out.println("...action resetChangePanel");
            }
        };

        add(updatePanel);
        add(resetChangePanel);
        addSeparator();
    }

    private class ActionSearch extends AbstractAction {
        public ActionSearch(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            System.out.println("...action search");
            GroupSearchDialog searchDialog = new GroupSearchDialog(OntoRamaApp.getMainFrame());
            searchDialog.show();
            if (searchDialog.actionIsCancelled()) {
                System.out.println("action was cancelled");
            }
            else {
                System.out.println("selected option = " + searchDialog.getSelectedOption());
                System.out.println("value = " + searchDialog.getValue());
            }
        }
    }
}
