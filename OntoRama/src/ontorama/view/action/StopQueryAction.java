package ontorama.view.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import ontorama.view.OntoRamaApp;

/**
 * Action to stop current query
 */
public class StopQueryAction extends AbstractAction {

    private static final String ACTION_COMMAND_KEY_COPY = "stop-query-command";
    private static final String NAME_COPY = "Cancel";
    private static final String SHORT_DESCRIPTION_COPY = "Stop Query";
    private static final String LONG_DESCRIPTION_COPY = "Stop current Query";

    private OntoRamaApp _mainApp;

    /**
     *
     */
    public StopQueryAction(OntoRamaApp mainApp) {

        _mainApp = mainApp;

        putValue(Action.NAME, NAME_COPY);
        putValue(Action.SHORT_DESCRIPTION, SHORT_DESCRIPTION_COPY);
        putValue(Action.LONG_DESCRIPTION, LONG_DESCRIPTION_COPY);
        putValue(Action.ACTION_COMMAND_KEY, ACTION_COMMAND_KEY_COPY);
    }

    /**
     *
     */
    public void actionPerformed(ActionEvent parm1) {
        System.out.println("___action: stop query");
        _mainApp.stopQuery();
    }
}
