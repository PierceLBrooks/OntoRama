package ontorama.backends.p2p.gui.action;

import ontorama.backends.p2p.gui.GroupSearchDialog;
import ontorama.view.OntoRamaApp;

import javax.swing.*;
import java.awt.event.ActionEvent;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 12:07:44
 * To change this template use Options | File Templates.
 */
public class ActionGroupSearch extends AbstractAction{
        public ActionGroupSearch(String name) {
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
                int selectedOption = searchDialog.getSelectedOption();
                if (selectedOption == GroupSearchDialog.OPTION_ALL) {
                    System.out.println("option selected: all groups");
                }
                if (selectedOption == GroupSearchDialog.OPTION_DESCR) {
                    System.out.println("option selected: search by description");
                }
                if (selectedOption == GroupSearchDialog.OPTION_NAME) {
                    System.out.println("option selected: search by name");
                }
                System.out.println("value = " + searchDialog.getValue());
            }
        }

}
