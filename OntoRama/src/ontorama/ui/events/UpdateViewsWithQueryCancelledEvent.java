package ontorama.ui.events;

import ontorama.ontotools.query.Query;
import org.tockit.events.Event;

/**
 * Event sent when query is cancelled and we need to update views to reflect this.
 */
public class UpdateViewsWithQueryCancelledEvent implements Event {
    private Query query;

    public UpdateViewsWithQueryCancelledEvent (Query subject) {
        this.query = subject;
    }

    public Object getSubject() {
        return query;
    }
}
