/*
 * Created by IntelliJ IDEA.
 * User: johang
 * Date: 1/08/2002
 * Time: 15:49:15
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model.graph.controller;

import ontorama.model.graph.view.GraphView;
import ontorama.model.graph.events.NodeSelectedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class GraphViewFocusEventHandler implements EventBrokerListener {
    private GraphView graphView;
    private EventBroker eventBroker;

    public GraphViewFocusEventHandler(EventBroker eventBroker, GraphView graphView) {
        this.graphView = graphView;
        this.eventBroker = eventBroker;
        eventBroker.subscribe(this, NodeSelectedEvent.class, ontorama.model.graph.NodeImpl.class);
    }

    public void processEvent(Event e) {
        ontorama.model.graph.Node node = (ontorama.model.graph.Node) e.getSubject();
//        System.out.println("graphview = " + graphView);
        this.graphView.focus(node);
    }
}
