package ontorama.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 */

@SuppressWarnings("serial")
public class ExitAction extends AbstractAction {


    private static final String ACTION_COMMAND_KEY_COPY = "exit-command";
    private static final String NAME_COPY = "Exit";
    private static final String SHORT_DESCRIPTION_COPY = "Exit OntoRama Application";
    private static final String LONG_DESCRIPTION_COPY = "Exit OntoRama Application";
    private static final int MNEMONIC_KEY_COPY = 'x';


    public ExitAction() {
        super("Exit");
        putValue(Action.NAME, NAME_COPY);
        putValue(Action.SHORT_DESCRIPTION, SHORT_DESCRIPTION_COPY);
        putValue(Action.LONG_DESCRIPTION, LONG_DESCRIPTION_COPY);
        putValue(Action.MNEMONIC_KEY, new Integer(MNEMONIC_KEY_COPY));
        putValue(Action.ACTION_COMMAND_KEY, ACTION_COMMAND_KEY_COPY);
    }

    public void actionPerformed(ActionEvent ae) {
        System.exit(0);
    }
}