/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 22/08/2002
 * Time: 10:25:24
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.views.hyper.controller;

import java.awt.event.InputEvent;

import ontorama.views.hyper.view.SimpleHyperView;
import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.canvas.events.CanvasItemEventFilter;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;
import org.tockit.events.filters.EventFilter;
import org.tockit.events.filters.EventTypeFilter;

public class DraggedEventHandler implements EventBrokerListener {
    private SimpleHyperView simpleHyperView;

    public DraggedEventHandler(SimpleHyperView simpleHyperView, EventBroker eventBroker) {
        this.simpleHyperView = simpleHyperView;
        eventBroker.subscribe(this, new EventFilter[]{new EventTypeFilter(CanvasItemDraggedEvent.class),
                          new CanvasItemEventFilter(InputEvent.BUTTON1_DOWN_MASK, 
                          						    InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK |
        						 					InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK |
        						 					InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)});
    }

    public void processEvent(Event e) {
        CanvasItemDraggedEvent draggedEvent = (CanvasItemDraggedEvent) e;
        simpleHyperView.drag(draggedEvent);
    }
}
