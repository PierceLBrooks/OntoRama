package ontorama.ui.events;

import ontorama.ontotools.query.Query;
import org.tockit.events.Event;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 9, 2002
 * Time: 1:38:14 PM
 * To change this template use Options | File Templates.
 */
public class QueryCancelledEvent implements Event {
    private Query query;

    /// @todo don't need to remember subject here! not sure how to implement this without a subject

    public QueryCancelledEvent (Query subject) {
        this.query = subject;
        System.out.println("new QueryCancelledEvent() for query " + query);
    }

    public Object getSubject() {
        return query;
    }
}
