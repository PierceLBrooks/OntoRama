package ontorama.ui.events;

import org.tockit.events.Event;
import ontorama.ui.HistoryElement;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 4, 2002
 * Time: 1:45:39 PM
 * To change this template use Options | File Templates.
 */
public class HistoryQueryStartEvent implements Event {
    protected HistoryElement subject;

    public HistoryQueryStartEvent(HistoryElement subject) {
        this.subject = subject;
    }

    public Object getSubject() {
        return subject;
    }
}
