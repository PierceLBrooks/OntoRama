/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 22/08/2002
 * Time: 10:25:24
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.hyper.controller;

import ontorama.hyper.view.simple.SimpleHyperView;
import org.tockit.events.*;
import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.canvas.CanvasItem;

public class DraggedEventHandler implements EventListener {
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
