/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 10, 2002
 * Time: 10:59:41 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.backends.p2p;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class P2PJMenu extends JMenu {

    private P2PBackend _p2pBackend;

    private static final String _menuName = "P2P";

    public P2PJMenu (P2PBackend p2pBackend) {
        super();
        _p2pBackend = p2pBackend;
        setText(_menuName);

        Action searchByName = new AbstractAction("Group search by name") {
            public void actionPerformed(ActionEvent e) {
                System.out.println("...action searchByName");
            }
        };

        Action searchByDescription = new AbstractAction("Group search by description") {
            public void actionPerformed(ActionEvent e) {
                System.out.println("...action searchByDescription");
            }
        };

        add(searchByName);
        add(searchByDescription);
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
}
