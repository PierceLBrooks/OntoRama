package ontorama.model.graph.controller;

import ontorama.model.graph.GraphView;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeImpl;
import ontorama.model.graph.events.NodeSelectedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class GraphViewFocusEventHandler implements EventBrokerListener {
    private GraphView graphView;
    
    public GraphViewFocusEventHandler(EventBroker eventBroker, GraphView graphView) {
        this.graphView = graphView;
        eventBroker.subscribe(this, NodeSelectedEvent.class, NodeImpl.class);
    }

    public void processEvent(Event e) {
        Node node = (Node) e.getSubject();
        this.graphView.focus(node);
    }
}
