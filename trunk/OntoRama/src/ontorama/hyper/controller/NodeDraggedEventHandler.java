/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Aug 12, 2002
 * Time: 10:28:56 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.hyper.controller;

import org.tockit.events.*;
import org.tockit.canvas.events.CanvasItemDraggedEvent;
import ontorama.hyper.view.simple.HyperNodeView;
import ontorama.hyper.view.simple.SimpleHyperView;
import ontorama.hyper.model.HyperNode;

import java.awt.event.MouseEvent;
import java.util.Iterator;

public class NodeDraggedEventHandler implements EventListener {
    public NodeDraggedEventHandler(EventBroker eventBroker) {
        eventBroker.subscribe(this, CanvasItemDraggedEvent.class, HyperNodeView.class);
    }

    public void processEvent(Event e) {
        HyperNodeView nodeView = (HyperNodeView) e.getSubject();
        CanvasItemDraggedEvent draggedEvent = (CanvasItemDraggedEvent) e;
        dragNode(nodeView, draggedEvent);
        System.out.println("processEvent: NodeDragged: " + nodeView);
    }

    /**
     *
     */
    protected void dragNode (HyperNodeView nodeView, CanvasItemDraggedEvent draggedEvent) {
//        labelView = null;
//        this.focusNode = null;
//        currentHighlightedView = null;

//        double x = draggedEvent.getCanvasToPosition().getX();
//        double y = draggedEvent.getCanvasToPosition().getY();
//        double lpx = draggedEvent.getCanvasFromPosition().getX();
//        double lpy = draggedEvent.getCanvasFromPosition().getY();
//
//        if ((draggedEvent.getModifiers() & MouseEvent.CTRL_DOWN_MASK) == 0) {
//            System.out.println("\n\nDRAG...");
//            double xDif = (lpx - x);
//            double yDif = (lpy - y);
//            moveCanvasItems(xDif, yDif);
//        } else {
//            System.out.println("\n\nROTATE...");
             //get x's and y's in cartesian coordinates
//            double curX = x - getSize().width / 2;
//            double curY = y - getSize().height / 2;
//            double lastX = lpx - getSize().width / 2;
//            double lastY = lpy - getSize().height / 2;
//            // calculate angle of rotation
//            double angle = Math.atan2(lastX, lastY) - Math.atan2(curX, curY);
//            this.rotateNodes(angle);
//        }
        //lastPoint.setLocation(x, y);
//        repaint();

    }

    /**
     * Rotate node about the center (0, 0) by angle passed.
     */
//    protected void rotateNodes(double angle) {
//        Iterator it = SimpleHyperView.hypernodes.values().iterator();
//        while (it.hasNext()) {
//            HyperNode hn = (HyperNode) it.next();
//            hn.rotate(angle);
//        }
//    }


}
