package ontorama.backends.p2p.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.gui.LeaveGroupDialog;
import ontorama.ui.OntoRamaApp;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 12:11:12
 * To change this template use Options | File Templates.
 */
public class ActionLeaveGroup extends AbstractAction {

    private P2PBackend _p2pBackend;

     public ActionLeaveGroup(String name, P2PBackend p2pBackend) {
         super(name);
        _p2pBackend = p2pBackend;
     }
     public void actionPerformed(ActionEvent e) {
         System.out.println("...action leave group");
         new LeaveGroupDialog(OntoRamaApp.getMainFrame(), _p2pBackend);
     }

}
