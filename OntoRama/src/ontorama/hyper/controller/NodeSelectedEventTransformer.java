/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Aug 12, 2002
 * Time: 10:27:39 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.hyper.controller;

import org.tockit.events.*;
import org.tockit.canvas.events.CanvasItemSelectedEvent;
import ontorama.hyper.view.simple.HyperNodeView;
import ontorama.controller.NodeSelectedEvent;

public class NodeSelectedEventTransformer implements EventListener {
    private EventBroker eventBroker;

    public NodeSelectedEventTransformer(EventBroker eventBroker) {
        this.eventBroker = eventBroker;
        eventBroker.subscribe(this, CanvasItemSelectedEvent.class, HyperNodeView.class);
    }

    public void processEvent(Event e) {
        HyperNodeView nodeView = (HyperNodeView) e.getSubject();
        eventBroker.processEvent(new NodeSelectedEvent(nodeView.getGraphNode()));
        //System.out.println("processEvent: NodeSelected: " + nodeView);
    }
}
