package ontorama.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import ontorama.ui.HistoryMenu;

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

    private HistoryMenu _historyMenu;

    /**
     *
     */
    public ForwardHistoryAction(HistoryMenu historyMenu) {
        _historyMenu = historyMenu;
        putValue(Action.NAME, NAME_COPY);
        putValue(Action.SHORT_DESCRIPTION, SHORT_DESCRIPTION_COPY);
        putValue(Action.LONG_DESCRIPTION, LONG_DESCRIPTION_COPY);
        putValue(Action.ACTION_COMMAND_KEY, ACTION_COMMAND_KEY_COPY);
    }

    /**
     *
     */
    public void actionPerformed(ActionEvent parm1) {
		_historyMenu.displayNextHistoryItem();
    }
}