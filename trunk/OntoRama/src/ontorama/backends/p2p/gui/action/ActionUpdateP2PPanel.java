package ontorama.backends.p2p.gui.action;

import ontorama.backends.p2p.p2pmodule.P2PSender;

import javax.swing.*;
import java.awt.event.ActionEvent;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 12:12:09
 * To change this template use Options | File Templates.
 */
public class ActionUpdateP2PPanel extends AbstractAction {
    private P2PSender _p2pSender;

    public ActionUpdateP2PPanel(String name, P2PSender p2pSender) {
        super(name);
        _p2pSender = p2pSender;
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("...action updatePanel");

        _p2pSender.peerDiscovery();
    }

}
