package ontorama.backends.p2p.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ontorama.backends.p2p.P2PBackendImpl;
import ontorama.backends.p2p.gui.P2PMainPanel;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 12:13:55
 * To change this template use Options | File Templates.
 */
public class ActionResetChangePanel extends AbstractAction {
     private P2PBackendImpl _p2pBackend;

    public ActionResetChangePanel(String name, P2PBackendImpl p2pBackend) {
        super(name);
        _p2pBackend = p2pBackend;
    }

    public void actionPerformed(ActionEvent e) {
    	P2PMainPanel panel = (P2PMainPanel) _p2pBackend.getPanel();
        panel.getChangePanel().empty();
    }


}
