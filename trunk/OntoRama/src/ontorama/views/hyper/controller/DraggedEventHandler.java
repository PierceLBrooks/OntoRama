/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 22/08/2002
 * Time: 10:25:24
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.views.hyper.controller;

import ontorama.views.hyper.view.SimpleHyperView;
import org.tockit.canvas.CanvasItem;
import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class DraggedEventHandler implements EventBrokerListener {
    private SimpleHyperView simpleHyperView;

    public DraggedEventHandler(SimpleHyperView simpleHyperView, EventBroker eventBroker) {
        this.simpleHyperView = simpleHyperView;
        eventBroker.subscribe(this, CanvasItemDraggedEvent.class, CanvasItem.class);
    }

    public void processEvent(Event e) {
        CanvasItemDraggedEvent draggedEvent = (CanvasItemDraggedEvent) e;
        simpleHyperView.drag(draggedEvent);
    }
}
