package ontorama.backends.p2p.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.gui.JoinGroupDialog;
import ontorama.ui.OntoRamaApp;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 12:10:05
 * To change this template use Options | File Templates.
 */
public class ActionShowJoinGroupDialog extends AbstractAction{

    private P2PBackend _p2pBackend;

        public ActionShowJoinGroupDialog(String name, P2PBackend p2pBackend) {
            super(name);
            _p2pBackend = p2pBackend;
        }

        public void actionPerformed(ActionEvent e) {
            JoinGroupDialog dialog = new JoinGroupDialog(OntoRamaApp.getMainFrame(), _p2pBackend);
            dialog.show();
        }

}
