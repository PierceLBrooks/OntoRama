package ontorama.backends.p2p.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.gui.ChangePanel;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 12:13:55
 * To change this template use Options | File Templates.
 */
public class ActionResetChangePanel extends AbstractAction {
     private P2PBackend _p2pBackend;

    public ActionResetChangePanel(String name, P2PBackend p2pBackend) {
        super(name);
        _p2pBackend = p2pBackend;
    }

    public void actionPerformed(ActionEvent e) {
       ChangePanel changePanel = (ChangePanel) _p2pBackend.getPanels().get(1);
       changePanel.empty();
       
    }


}
