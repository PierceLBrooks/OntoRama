/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 21/08/2002
 * Time: 10:05:50
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.view.hyper.controller;

import ontorama.view.hyper.view.HyperNodeView;
import ontorama.view.hyper.view.SimpleHyperView;
import org.tockit.canvas.events.CanvasItemPointedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventListener;

public class NodePointedEventHandler implements EventListener {
    private SimpleHyperView simpleHyperView;

    public NodePointedEventHandler(SimpleHyperView simpleHyperView, EventBroker eventBroker) {
        this.simpleHyperView = simpleHyperView;
        eventBroker.subscribe(this, CanvasItemPointedEvent.class, HyperNodeView.class);
    }

    public void processEvent(Event e) {
        HyperNodeView nodeView = (HyperNodeView) e.getSubject();
        //System.out.println("processEvent: NodePointed: " + nodeView);
        simpleHyperView.highlightNodePathToRoot(nodeView);
    }
}
