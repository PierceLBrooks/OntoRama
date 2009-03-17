package ontorama.ui.events;

import org.tockit.events.Event;
import ontorama.model.GeneralNode;

/**
 * Event sent when user activated a query for 
 * given node.
 * (Used in tree view, description panel when browsing
 * parent nodes)
 */
public class QueryNodeEvent implements Event  {
    protected GeneralNode subject;

    public QueryNodeEvent(GeneralNode subject) {
        this.subject = subject;
    }

    public Object getSubject() {
        return subject;
    }

}
