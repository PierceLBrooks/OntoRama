package ontorama.backends.p2p.gui.action;

import ontorama.backends.p2p.gui.LeaveGroupDialog;
import ontorama.backends.p2p.p2pmodule.P2PSender;
import ontorama.view.OntoRamaApp;

import javax.swing.*;
import java.awt.event.ActionEvent;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 12:11:12
 * To change this template use Options | File Templates.
 */
public class ActionLeaveGroup extends AbstractAction {

    private P2PSender _p2pSender;

     public ActionLeaveGroup(String name, P2PSender p2pSender) {
         super(name);
        _p2pSender = p2pSender;
     }
     public void actionPerformed(ActionEvent e) {
         System.out.println("...action leave group");
         new LeaveGroupDialog(OntoRamaApp.getMainFrame(), _p2pSender);
     }

}
