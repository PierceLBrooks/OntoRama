package ontorama.ui.events;

import org.tockit.events.Event;
import ontorama.ontotools.query.Query;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 4, 2002
 * Time: 1:45:39 PM
 * To change this template use Options | File Templates.
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
