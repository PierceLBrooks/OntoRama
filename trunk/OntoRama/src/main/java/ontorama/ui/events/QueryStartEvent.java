package ontorama.ui.events;

import org.tockit.events.Event;
import ontorama.ontotools.query.Query;

/**
 * Event created every time we want to start a new query.
 * 
 * @author nataliya
 */
public class QueryStartEvent implements Event {
    protected Query subject;

    public QueryStartEvent(Query subject) {
        this.subject = subject;
    }

    public Object getSubject() {
        return subject;
    }
}
