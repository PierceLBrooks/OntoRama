package ontorama.ui.events;

import ontorama.ontotools.query.Query;
import org.tockit.events.Event;

/**
 * Event sent when query is cancelled.
 */
public class QueryCancelledEvent implements Event {
    private Query query;

    /// @todo don't need to remember subject here! not sure how to implement this without a subject
    public QueryCancelledEvent (Query subject) {
        this.query = subject;
    }

    public Object getSubject() {
        return query;
    }
}
