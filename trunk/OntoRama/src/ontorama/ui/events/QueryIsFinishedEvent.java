package ontorama.ui.events;

import org.tockit.events.Event;
import ontorama.ontotools.query.QueryResult;

/**
 * 
 * This event is used to indicate that query engine finished 
 * working and query result is created. At the moment this event
 * is used to pass query result over in order to create a graph 
 * from it. 
 * 
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 4, 2002
 * Time: 1:45:39 PM
 */
public class QueryIsFinishedEvent implements Event {
    protected QueryResult subject;

    public QueryIsFinishedEvent(QueryResult queryResult) {
        this.subject = queryResult;
    }

    public Object getSubject() {
        return subject;
    }
}
