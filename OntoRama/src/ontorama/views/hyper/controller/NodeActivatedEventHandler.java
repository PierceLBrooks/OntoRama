/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 20/08/2002
 * Time: 15:41:09
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.views.hyper.controller;

import ontorama.model.tree.events.TreeNodeSelectedEvent;
import ontorama.views.hyper.view.HyperNodeView;
import ontorama.views.hyper.view.SimpleHyperView;
import org.tockit.canvas.events.CanvasItemActivatedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

/**
 *
 */
public class NodeActivatedEventHandler implements EventBrokerListener {
    private SimpleHyperView simpleHyperView;
    private EventBroker eventBroker;

    public NodeActivatedEventHandler(SimpleHyperView simpleHyperView, EventBroker eventBroker) {
        this.simpleHyperView = simpleHyperView;
        this.eventBroker = eventBroker;
        eventBroker.subscribe(this, CanvasItemActivatedEvent.class, HyperNodeView.class);
    }

    public void processEvent(Event e) {
        HyperNodeView nodeView = (HyperNodeView) e.getSubject();
        eventBroker.processEvent(new TreeNodeSelectedEvent(nodeView.getTreeNode(), this.eventBroker));
        simpleHyperView.toggleFold(nodeView.getTreeNode());
    }
}
