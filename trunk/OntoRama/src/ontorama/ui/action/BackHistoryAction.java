package ontorama.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;

import ontorama.ui.HistoryMenu;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class BackHistoryAction extends AbstractAction {

    private static final String ACTION_COMMAND_KEY_COPY = "back-history-command";
    private static final String NAME_COPY = "Back";
    private static final String SHORT_DESCRIPTION_COPY = "Back to previous ontology";
    private static final String LONG_DESCRIPTION_COPY = "Back to previous ontology";
    //private static final String ACCELERATOR_KEY=KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.ALT_MASK);
//  private static final String ACCELERATOR_KEY="ALT+Left";


    /**
     *
     */
    public BackHistoryAction() {
        putValue(Action.NAME, NAME_COPY);
        putValue(Action.SHORT_DESCRIPTION, SHORT_DESCRIPTION_COPY);
        putValue(Action.LONG_DESCRIPTION, LONG_DESCRIPTION_COPY);
//    putValue(Action.ACCELERATOR_KEY, ACCELERATOR_KEY);
        putValue(Action.ACTION_COMMAND_KEY, ACTION_COMMAND_KEY_COPY);
    }

    /**
     *
     */
    public void actionPerformed(ActionEvent parm1) {
        int indexOfCur = HistoryMenu.getIndexOfSelectedHistoryMenuItem();
        int backInd = indexOfCur - 1;
        JCheckBoxMenuItem backItem = HistoryMenu.getMenuItem(backInd);
        System.out.println("___action: back");
        HistoryMenu.displayHistoryItem(backItem);
    }


}