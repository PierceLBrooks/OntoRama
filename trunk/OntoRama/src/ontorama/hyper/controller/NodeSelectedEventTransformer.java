/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Aug 12, 2002
 * Time: 10:27:39 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.hyper.controller;

import ontorama.controller.NodeSelectedEvent;
import ontorama.hyper.view.simple.HyperNodeView;
import org.tockit.canvas.events.CanvasItemSelectedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventListener;

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
