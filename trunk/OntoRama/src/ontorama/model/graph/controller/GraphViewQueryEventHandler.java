/*
 * Created by IntelliJ IDEA.
 * User: johang
 * Date: 1/08/2002
 * Time: 15:49:15
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model.graph.controller;

import ontorama.model.graph.view.GraphQuery;
import ontorama.model.graph.events.QueryEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class GraphViewQueryEventHandler implements EventBrokerListener {
    private GraphQuery graphQuery;

    public GraphViewQueryEventHandler(EventBroker eventBroker, GraphQuery graphQuery) {
        this.graphQuery = graphQuery;
        eventBroker.subscribe(this, QueryEvent.class, ontorama.model.graph.NodeImpl.class);
    }

    public void processEvent(Event e) {
        ontorama.model.graph.Node node = (ontorama.model.graph.Node) e.getSubject();
        System.out.println("\n\nGraphViewQueryEventHandler for graphQuery = " + this.graphQuery);
        this.graphQuery.query(node);
    }
}
