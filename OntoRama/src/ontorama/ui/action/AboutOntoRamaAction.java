package ontorama.ui.action;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import ontorama.ui.AboutOntoRamaDialog;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class AboutOntoRamaAction extends AbstractAction {

    private static final String ACTION_COMMAND_KEY_COPY = "about-ontorama-command";
    private static final String NAME_COPY = "About OntoRama";
    private static final String SHORT_DESCRIPTION_COPY = "About OntoRama Application";
    private static final String LONG_DESCRIPTION_COPY = "Find out more about OntoRama program";

    /**
     *
     */
    public AboutOntoRamaAction() {
        putValue(Action.NAME, NAME_COPY);
        putValue(Action.SHORT_DESCRIPTION, SHORT_DESCRIPTION_COPY);
        putValue(Action.LONG_DESCRIPTION, LONG_DESCRIPTION_COPY);
        putValue(Action.ACTION_COMMAND_KEY, ACTION_COMMAND_KEY_COPY);
    }

    public void actionPerformed(ActionEvent parm1) {
        System.out.println("___action: about ontorama");
        Frame[] frames = ontorama.ui.OntoRamaApp.getFrames();
        if (frames.length > 0) {
            AboutOntoRamaDialog dialog = new AboutOntoRamaDialog(frames[0]);
        } else {
            AboutOntoRamaDialog dialog = new AboutOntoRamaDialog(null);
        }
    }
}