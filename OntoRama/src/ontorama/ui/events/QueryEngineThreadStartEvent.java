package ontorama.ui.events;

import org.tockit.events.Event;

import ontorama.ontotools.query.Query;
import ontorama.ontotools.query.QueryEngine;

/**
 * Event indicating that we are ready to start QueryEngineThread.
 * 
 * @author	nataliya
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
