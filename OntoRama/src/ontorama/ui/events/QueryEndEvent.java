package ontorama.ui.events;

import org.tockit.events.Event;

import ontorama.model.graph.Graph;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 4, 2002
 * Time: 2:35:29 PM
 * To change this template use Options | File Templates.
 */
public class QueryEndEvent implements Event {
    protected Graph subject;

    public QueryEndEvent(Graph subject) {
        this.subject = subject;
    }

    public Object getSubject() {
        return subject;
    }
}
