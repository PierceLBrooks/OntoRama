package ontorama.backends.p2p.gui.action;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;

import ontorama.backends.p2p.GroupItemReference;
import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.gui.GroupSearchDialog;
import ontorama.ui.OntoRamaApp;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 12:07:44
 * To change this template use Options | File Templates.
 */
public class ActionGroupSearch extends AbstractAction{

    private P2PBackend _p2pBackend;

    public ActionGroupSearch(String name, P2PBackend p2pBackend) {
        super(name);
        _p2pBackend = p2pBackend;
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
                try {
                    List searchGroupResult = _p2pBackend.getSender().sendSearchGroup(null,null);
                    GroupItemReference searchGroupResultElement = null;
                    System.out.println("Found following matching " + "s");
                    Iterator it = searchGroupResult.iterator();
                        while (it.hasNext()) {
                        searchGroupResultElement = (GroupItemReference) it.next();
                        System.out.println(":" +
                            searchGroupResultElement.getName()
                            + " (ID:" + searchGroupResultElement.getID() + ")");
                            System.out.println("Description:" +
                            searchGroupResultElement.getDescription());
                    }
                }
                catch (Exception exc) {
                    /// @todo sort out exception
                    exc.printStackTrace();

                }


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
