package ontorama.hyper.canvas;

/**
 * CanvasManager for now handles mouse events, and is responsible
 * for drawing canvas items.
 */

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;

import ontorama.hyper.model.HyperNode;
import ontorama.hyper.view.simple.HyperNodeView;
import ontorama.hyper.view.simple.LabelView;
import ontorama.hyper.view.simple.SimpleHyperView;
import ontorama.model.Edge;
import ontorama.model.GraphNode;
import org.tockit.canvas.CanvasItem;
import org.tockit.canvas.events.CanvasItemActivatedEvent;
import org.tockit.canvas.events.CanvasItemSelectedEvent;
import org.tockit.events.EventBroker;


public class CanvasManager extends JComponent
        implements MouseListener, MouseMotionListener {

//    private EventBroker eventBroker;
//
//    /**
//     * Inner class to handle canvasItem single click
//     */
//    private class CanvasItemSingleClicked extends TimerTask {
//        private HyperNodeView hyperNodeView;
//
//        public CanvasItemSingleClicked(HyperNodeView hyperNodeView) {
//            this.hyperNodeView = hyperNodeView;
//        }
//
//        public void run() {
//            eventBroker.processEvent(
//                    new CanvasItemSelectedEvent(
//                            hyperNodeView,
//                            0,
//                            new Point2D.Double(0.0, 0.0),
//                            new Point2D.Double(0.0, 0.0))
//            );
//        }
//    }


    /**
     * Store the last point.
     */
    protected Point2D lastPoint = new Point2D.Double(0, 0);


    /**
     * Store the hyper view canvas items.
     */
    private List canvasItems = new ArrayList();

    /**
     * Hosds the constant value to determine if we are in drag mode.
     */
    private final int DRAG = 5;

    /**
     * Holds wether we are in drag mode
     */
    private boolean dragmode = false;



    /**
     * A timer to distinguish between single and double clicks.
     */
    private Timer singleClickTimer = new Timer();

    public CanvasManager(EventBroker eventBroker) {
//        this.eventBroker = eventBroker;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setDoubleBuffered(true);
        this.setOpaque(true);
    }


    public void addCanvasItem(CanvasItem item) {
        this.canvasItems.add(item);
    }


    public void mouseClicked(MouseEvent e) {
    }




    public void mousePressed(MouseEvent e) {
        lastPoint.setLocation(e.getPoint());
    }

    public void mouseReleased(MouseEvent e) {
//        if (dragmode == true) {
//            dragmode = false;
//            repaint();
//        } else {
//            HyperNodeView focusedHyperNodeView = getClickedItem(e);
//            if (focusedHyperNodeView == null) {
//                return;
//            }
//            if (e.getClickCount() == 1) {
//                this.singleClickTimer = new Timer();
//                this.singleClickTimer.schedule(new CanvasItemSingleClicked(focusedHyperNodeView), 300);
//            } else if (e.getClickCount() == 2) {
////                this.singleClickTimer.cancel();
//                System.out.println();
//                System.out.println("CanvasManager is sending DoubleClick");
//                System.out.println();
//                System.out.println();
//                eventBroker.processEvent(new CanvasItemActivatedEvent(focusedHyperNodeView, 0, null, null));
//            }
//            repaint();
//        }
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mouseDragged(MouseEvent e) {
//        double lpx = lastPoint.getX();
//        double lpy = lastPoint.getY();
//        double x = e.getX();
//        double y = e.getY();
//        labelView = null;
//        this.focusNode = null;
//        if (dragmode == false) {
//            double dragedAmount = Math.sqrt((lpx - x) * (lpx - x) + (lpy - y) * (lpy - y));
//            if (dragedAmount > DRAG) {
//                dragmode = true;
//            } else {
//                return;
//            }
//        }
//        currentHighlightedView = null;
//        if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == 0) {
//            double xDif = (lpx - x);
//            double yDif = (lpy - y);
//            moveCanvasItems(xDif, yDif);
//        } else {
//            // get x's and y's in cartesian coordinates
//            double curX = x - getSize().width / 2;
//            double curY = y - getSize().height / 2;
//            double lastX = lpx - getSize().width / 2;
//            double lastY = lpy - getSize().height / 2;
//            // calculate angle of rotation
//            double angle = Math.atan2(lastX, lastY) - Math.atan2(curX, curY);
//            this.rotateNodes(angle);
//        }
//        lastPoint.setLocation(x, y);
//        repaint();
    }


    public void mouseMoved(MouseEvent e) {
//        if (dragmode) {
//            return;
//        }
//        Iterator it = canvasItems.iterator();
//        double minDist = this.getWidth();
//        double dist = 0;
//        HyperNodeView closestNode = null;
//        while (it.hasNext()) {
//            CanvasItem cur = (CanvasItem) it.next();
//            if (cur instanceof HyperNodeView) {
//                double curX = e.getX() - getSize().width / 2;
//                double curY = e.getY() - getSize().height / 2;
//                curX = curX * (1 / canvasScale);
//                curY = curY * (1 / canvasScale);
//                dist = ((HyperNodeView) cur).distance(curX, curY);
//                if (dist < minDist) {
//                    minDist = dist;
//                    closestNode = (HyperNodeView) cur;
//                }
//            }
//        }
//        if (closestNode != null && !closestNode.equals(currentHighlightedView)) {
//            currentHighlightedView = closestNode;
//            closestNode.setHighlightEdge(true);
//            this.highlightEdge(closestNode.getGraphNode());
//            repaint();
//        }
    }

    
    

    /**
     * Method called when a new graph is loaded.
     *
     * Reset all global variables
     */
    protected void resetCanvas() {
        this.canvasItems.clear();
        this.dragmode = false;
    }
    
   
    
}
