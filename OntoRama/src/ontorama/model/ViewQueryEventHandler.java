/*
 * Created by IntelliJ IDEA.
 * User: johang
 * Date: 1/08/2002
 * Time: 15:49:15
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model;

import ontorama.model.ViewQuery;
import ontorama.ontotools.query.Query;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class ViewQueryEventHandler implements EventBrokerListener {
    private ViewQuery queryView;
    private EventBroker eventBroker;

    public ViewQueryEventHandler(EventBroker eventBroker, ViewQuery query) {
        this.queryView = query;
        this.eventBroker = eventBroker;
        this.eventBroker.subscribe(this, QueryEvent.class, GeneralNode.class);
    }

    public void processEvent(Event e) {
        GeneralNode node = (GeneralNode) e.getSubject();
        Query query = new Query(node.getName());
        System.out.println("\n\nGraphViewQueryEventHandler for queryView = " + this.queryView);
        //this.queryView.query(node.getName());
        // start QueryStartEvent
        this.eventBroker.processEvent(new QueryStartEvent(query));
        // ontoramaApp shoudl listen for QueryStartEvent and update ui
        // once thread is finished it should send QueryEndEvent
        // ontoramaApp should listen for QueryEndEvent and update ui
        // then there is no need for queryViews
    }
}
