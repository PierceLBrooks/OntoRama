/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Aug 19, 2002
 * Time: 10:32:52 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.view.action;

import ontorama.view.OntoRamaApp;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ShowUnconnectedNodesAction extends AbstractAction {

    private static final String ACTION_COMMAND_KEY_STR = "show-unconnected-nodes-command";
    private static final String NAME_STR = "Toggle Unconnected Nodes View";
    private static final String LONG_DESCRIPTION_STR = "Show list of nodes that are not connected to root node via relation links";
    private static final String SHORT_DESCRIPTION_STR = LONG_DESCRIPTION_STR;

    private boolean _unconnectedNodesIsShowing = false;

    /**
     *
     */
    public ShowUnconnectedNodesAction( boolean unconnectedNodesIsOn) {
        putValue(Action.NAME, NAME_STR);
        putValue(Action.SHORT_DESCRIPTION, SHORT_DESCRIPTION_STR);
        putValue(Action.LONG_DESCRIPTION, LONG_DESCRIPTION_STR);
        putValue(Action.ACTION_COMMAND_KEY, ACTION_COMMAND_KEY_STR);
        _unconnectedNodesIsShowing = unconnectedNodesIsOn;
    }

    /**
     *
     */
    public void actionPerformed(ActionEvent parm1) {
        System.out.println("\n****************\nShowUnconnectedNodesAction, current status = " + _unconnectedNodesIsShowing);
        _unconnectedNodesIsShowing = !_unconnectedNodesIsShowing;
        System.out.println(", new status = " + _unconnectedNodesIsShowing);
        OntoRamaApp.showUnconnectedNodesList();
    }

    /**
     *
     * @return unconnectedNodesIsShowing - boolean value, true if if list of unconnected nodes is currently shown in UI
     */
    public boolean unconnectedNodesListIsShowing () {
        return _unconnectedNodesIsShowing;
    }

    public void setUnconnectedNodesShowingStatus (boolean showUnconnectedNodes) {
        _unconnectedNodesIsShowing = showUnconnectedNodes;
    }

}
