/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 20/08/2002
 * Time: 15:41:09
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.hyper.controller;

import org.tockit.events.*;
import org.tockit.canvas.events.CanvasItemActivatedEvent;
import ontorama.hyper.view.simple.SimpleHyperView;
import ontorama.hyper.view.simple.HyperNodeView;
import ontorama.graph.controller.GraphViewFocusEventHandler;
import ontorama.controller.NodeSelectedEvent;

/**
 *
 */
public class NodeActivatedEventHandler implements EventListener {
    private SimpleHyperView simpleHyperView;
    private EventBroker eventBroker;

    public NodeActivatedEventHandler(SimpleHyperView simpleHyperView, EventBroker eventBroker) {
        this.simpleHyperView = simpleHyperView;
        this.eventBroker = eventBroker;
        eventBroker.subscribe(this, CanvasItemActivatedEvent.class, HyperNodeView.class);
    }

    public void processEvent(Event e) {
        HyperNodeView nodeView = (HyperNodeView) e.getSubject();
        eventBroker.processEvent(new NodeSelectedEvent(nodeView.getGraphNode()));
        simpleHyperView.toggleFold(nodeView.getGraphNode());
    }
}