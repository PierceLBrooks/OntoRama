/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 21/08/2002
 * Time: 10:05:50
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.views.hyper.controller;

import ontorama.views.hyper.view.HyperNodeView;
import ontorama.views.hyper.view.SimpleHyperView;
import org.tockit.canvas.events.CanvasItemMouseEnterEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class NodePointedEventHandler implements EventBrokerListener {
    private SimpleHyperView simpleHyperView;

    public NodePointedEventHandler(SimpleHyperView simpleHyperView, EventBroker eventBroker) {
        this.simpleHyperView = simpleHyperView;
        eventBroker.subscribe(this, CanvasItemMouseEnterEvent.class, HyperNodeView.class);
    }

    public void processEvent(Event e) {
        HyperNodeView nodeView = (HyperNodeView) e.getSubject();
        //System.out.println("processEvent: NodePointed: " + nodeView);
        simpleHyperView.highlightNodePathToRoot(nodeView);
    }
}
