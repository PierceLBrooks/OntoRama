package ontorama.ui.controller;

import org.tockit.events.EventBrokerListener;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import ontorama.ontotools.query.Query;
import ontorama.ui.events.QueryStartEvent;
import ontorama.ui.events.QueryNodeEvent;
import ontorama.ui.events.GeneralQueryEvent;
import ontorama.model.GeneralNode;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 9, 2002
 * Time: 11:27:07 AM
 * To change this template use Options | File Templates.
 */
public class GeneralQueryEventHandler implements EventBrokerListener {

    private EventBroker _eventBroker;

    public GeneralQueryEventHandler(EventBroker eventBroker) {
        _eventBroker = eventBroker;
        _eventBroker.subscribe(this, GeneralQueryEvent.class, Query.class);
    }

    public void processEvent(Event event) {
        Query query = (Query) event.getSubject();
        _eventBroker.processEvent(new QueryStartEvent(query));
    }

}
