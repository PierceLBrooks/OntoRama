/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 21/08/2002
 * Time: 10:01:21
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.hyper.controller;

import org.tockit.events.*;
import org.tockit.canvas.events.CanvasItemDraggedEvent;
import ontorama.hyper.view.simple.SimpleHyperView;
import ontorama.hyper.view.simple.HyperNodeView;

public class   NodeDraggedEventHandler implements EventListener {
    private SimpleHyperView simpleHyperView;

    public NodeDraggedEventHandler(SimpleHyperView simpleHyperView, EventBroker eventBroker) {
        this.simpleHyperView = simpleHyperView;
        eventBroker.subscribe(this, CanvasItemDraggedEvent.class, HyperNodeView.class);
     }

     public void processEvent(Event e) {
         HyperNodeView nodeView = (HyperNodeView) e.getSubject();
         CanvasItemDraggedEvent draggedEvent = (CanvasItemDraggedEvent) e;
         simpleHyperView.dragNode(nodeView, draggedEvent);
         //System.out.println("processEvent: NodeDragged: " + nodeView);
     }
}
