package ontorama.ui.events;

import org.tockit.events.Event;

import ontorama.backends.examplesmanager.ExamplesHistoryElement;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 4, 2002
 * Time: 1:45:39 PM
 * To change this template use Options | File Templates.
 */
public class HistoryQueryStartEvent implements Event {
    protected ExamplesHistoryElement subject;

    public HistoryQueryStartEvent(ExamplesHistoryElement subject) {
        this.subject = subject;
    }

    public Object getSubject() {
        return subject;
    }
}
