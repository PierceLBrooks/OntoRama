package ontorama.ui.events;

import org.tockit.events.Event;
import ontorama.ontotools.query.Query;
import ontorama.ui.HistoryElement;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 9, 2002
 * Time: 10:45:46 AM
 * To change this template use Options | File Templates.
 */
public class DisplayHistoryItemEvent implements Event {
    protected JMenuItem subject;

    public DisplayHistoryItemEvent (JMenuItem subject) {
        this.subject = subject;
    }

    public Object getSubject() {
        return subject;
    }
}
