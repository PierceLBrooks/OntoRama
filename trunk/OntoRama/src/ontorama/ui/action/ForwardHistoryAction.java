package ontorama.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;

import ontorama.ui.HistoryMenu;
import ontorama.ui.events.DisplayHistoryItemEvent;
import org.tockit.events.EventBroker;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class ForwardHistoryAction extends AbstractAction {

    private static final String ACTION_COMMAND_KEY_COPY = "forward-history-command";
    private static final String NAME_COPY = "Forward";
    private static final String SHORT_DESCRIPTION_COPY = "Forward to the next ontology";
    private static final String LONG_DESCRIPTION_COPY = "Forward to the next ontology in the History";
    //private static final String ACCELERATOR_KEY=KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.ALT_MASK);
    //private static final String ACCELERATOR_KEY="ALT+Right";

    private EventBroker _eventBroker;

    /**
     *
     */
    public ForwardHistoryAction(EventBroker eventBroker) {
        _eventBroker = eventBroker;
        putValue(Action.NAME, NAME_COPY);
        putValue(Action.SHORT_DESCRIPTION, SHORT_DESCRIPTION_COPY);
        putValue(Action.LONG_DESCRIPTION, LONG_DESCRIPTION_COPY);
        putValue(Action.ACTION_COMMAND_KEY, ACTION_COMMAND_KEY_COPY);
    }

    /**
     *
     */
    public void actionPerformed(ActionEvent parm1) {
        int indexOfCur = HistoryMenu.getIndexOfSelectedHistoryMenuItem();
        int forwardInd = indexOfCur + 1;
    	JMenuItem forwardItem = HistoryMenu.getMenuItem(forwardInd);
        System.out.println("___action: forward");
        _eventBroker.processEvent(new DisplayHistoryItemEvent(forwardItem));
    }
}