package ontorama.ui.controller;

import ontorama.model.GeneralNode;
import ontorama.ontotools.query.Query;
import ontorama.ui.events.QueryNodeEvent;
import ontorama.ui.events.QueryStartEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

/**
 * Event Handler for QueryNodeEvent - take the event and
 * create another event that will start a query for the given node.
 */
public class QueryNodeEventHandler implements EventBrokerListener {
    private EventBroker eventBroker;

    public QueryNodeEventHandler(EventBroker eventBroker) {
        this.eventBroker = eventBroker;
        this.eventBroker.subscribe(this, QueryNodeEvent.class, GeneralNode.class);
    }

    public void processEvent(Event e) {
        GeneralNode node = (GeneralNode) e.getSubject();
        Query query = new Query(node.getName());
        System.out.println("\n\nQueryNodeEventHandler for node = " + node.getName());
        System.out.println("QueryNodeEventHandler  event broker = " + this.eventBroker);
        this.eventBroker.processEvent(new QueryStartEvent(query));
    }
}
