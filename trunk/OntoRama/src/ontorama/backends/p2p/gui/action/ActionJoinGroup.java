package ontorama.backends.p2p.gui.action;

import ontorama.backends.p2p.gui.JoinGroupDialog;
import ontorama.view.OntoRamaApp;

import javax.swing.*;
import java.awt.event.ActionEvent;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 12:10:05
 * To change this template use Options | File Templates.
 */
public class ActionJoinGroup extends AbstractAction{

        public ActionJoinGroup(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            System.out.println("...action join group");
            JoinGroupDialog dialog = new JoinGroupDialog(OntoRamaApp.getMainFrame());
            dialog.show();
            if (dialog.actionWasCancelled()) {
                System.out.println("action was cancelled");
            }
            else {
                int selectedOption = dialog.getSelectedOption();
                if (selectedOption == JoinGroupDialog.OPTION_EXISTING_GROUP) {
                    System.out.println("selected option: join existing group");
                }
                if (selectedOption == JoinGroupDialog.OPTION_NEW_GROUP) {
                    System.out.println("selected option: create new group");
                }
                System.out.println("returned input = " + dialog.getGroupName());
            }
        }

}
