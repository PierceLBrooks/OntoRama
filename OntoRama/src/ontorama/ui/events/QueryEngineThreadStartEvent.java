package ontorama.ui.events;

import org.tockit.events.Event;

import ontorama.ontotools.query.Query;
import ontorama.ontotools.query.QueryEngine;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 4, 2002
 * Time: 1:45:39 PM
 * To change this template use Options | File Templates.
 */
public class QueryEngineThreadStartEvent implements Event {
    protected QueryEngine subject;
    protected Query query;

    public QueryEngineThreadStartEvent(QueryEngine subject, Query query) {
        this.subject = subject;
        this.query = query;
    }

    public Object getSubject() {
        return subject;
    }
    
    public Query getQuery () {
    	return this.query;
    }
}
