/*
 * Created by IntelliJ IDEA.
 * User: johang
 * Date: 1/08/2002
 * Time: 15:49:15
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.graph.controller;

import ontorama.controller.QueryEvent;
import ontorama.graph.view.GraphQuery;
import ontorama.model.Node;
import ontorama.model.NodeImpl;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventListener;

public class GraphViewQueryEventHandler implements EventListener {
    private GraphQuery graphQuery;

    public GraphViewQueryEventHandler(EventBroker eventBroker, GraphQuery graphQuery) {
        this.graphQuery = graphQuery;
        eventBroker.subscribe(this, QueryEvent.class, NodeImpl.class);
    }

    public void processEvent(Event e) {
        Node node = (Node) e.getSubject();
        System.out.println("\n\nGraphViewQueryEventHandler for graphQuery = " + this.graphQuery);
        this.graphQuery.query(node);
    }
}
