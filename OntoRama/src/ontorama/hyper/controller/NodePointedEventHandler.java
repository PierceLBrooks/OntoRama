/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 21/08/2002
 * Time: 10:05:50
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.hyper.controller;

import org.tockit.events.*;
import org.tockit.canvas.events.CanvasItemPointedEvent;
import ontorama.hyper.view.simple.SimpleHyperView;
import ontorama.hyper.view.simple.HyperNodeView;

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
