package ontorama.backends.p2p.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ontorama.backends.p2p.P2PBackend;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 12:12:09
 * To change this template use Options | File Templates.
 */
public class ActionUpdateP2PPanel extends AbstractAction {
    private P2PBackend _p2pBackend;

    public ActionUpdateP2PPanel(String name, P2PBackend p2pBackend) {
        super(name);
        _p2pBackend = p2pBackend;
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("...action updatePanel");

        _p2pBackend.getSender().peerDiscovery();
    }

}
