package ontorama.ui.events;

import org.tockit.events.Event;
import ontorama.ontotools.query.QueryResult;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 4, 2002
 * Time: 1:45:39 PM
 * To change this template use Options | File Templates.
 */
public class LoadGraphEvent implements Event {
    protected QueryResult subject;

    public LoadGraphEvent(QueryResult queryResult) {
    	System.out.println("\n\n LoadGraphEvent");
        this.subject = queryResult;
    }

    public Object getSubject() {
        return subject;
    }
}
