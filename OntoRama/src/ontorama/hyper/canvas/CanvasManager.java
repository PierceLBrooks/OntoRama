package ontorama.hyper.canvas;

/**
 * CanvasManager for now handles mouse events, and is responsible
 * for drawing canvas items.
 */

import ontorama.hyper.model.HyperNode;
import ontorama.hyper.view.simple.HyperNodeView;
import ontorama.hyper.view.simple.LabelView;
import ontorama.model.Edge;
import ontorama.model.GraphNode;

import ontorama.util.event.ViewEventListener;
import ontorama.util.event.ViewEventObserver;

import javax.swing.JComponent;

import java.awt.Graphics2D;
import java.awt.Event;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.*;

import org.tockit.canvas.CanvasItem;
import org.tockit.canvas.events.CanvasItemSelectedEvent;
import org.tockit.events.EventBroker;



public class CanvasManager extends JComponent
        implements MouseListener, MouseMotionListener {

	protected ViewEventListener viewListener;

    private EventBroker eventBroker;

    /**
     * Inner class to handle canvasItem single click
     */
    private class CanvasItemSingleClicked  extends TimerTask{
        private HyperNodeView hyperNodeView;
        public CanvasItemSingleClicked( HyperNodeView hyperNodeView ) {
            this.hyperNodeView = hyperNodeView;
        }

        public void run() {
            eventBroker.processEvent(
                    new CanvasItemSelectedEvent(
                            hyperNodeView,
                            new Point2D.Double(0.0,0.0),
                            new Point2D.Double(0.0,0.0))
            );
        }
    }


    /**
     * Store the last point.
     */
    protected Point2D lastPoint = new Point2D.Double( 0, 0 );

    /**
     * Holds the current canvas scale.
     */
    protected double canvasScale = 1;

    /**
     * Hold the mapping of HyperNode to GraphNodes
     */
    protected Hashtable hypernodes = new Hashtable();

    /**
     * Holds the mapping of HyperNodeView to GraphNodes
     */
    protected Hashtable hypernodeviews = new Hashtable();

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
     * Store the HyperNode that has focus.
     */
    protected HyperNode focusNode = null;

    /**
     * Stores the hyperNodeView that is having its
     * Edge highlighted back to the root node.
     */
    private HyperNodeView currentHighlightedView = null;

    /**
     * Stores the LabelView that is selected.
     */
    protected static LabelView labelView = null;

    /**
     * Holds the remaining length of the animation.
     *
     * If negative animation we don't animate at the moment.
     */
    protected long lengthOfAnimation = -1;

    /**
     * The time when we did the last animation step.
     */
    protected long animationTime = 0;

    /**
     * A timer to distinguish between single and double clicks.
     */
    private Timer singleClickTimer = new Timer();

    public CanvasManager(ViewEventListener viewListener, EventBroker eventBroker) {
		this.viewListener = viewListener;
        this.eventBroker = eventBroker;
		this.viewListener.addObserver(this);
        this.addMouseListener( this );
        this.addMouseMotionListener( this );
        this.setDoubleBuffered( true );
        this.setOpaque( true );
    }

    /**
     * draw canvas items.
     *
     */
    protected void drawNodes( Graphics2D g2d ) {
        if(lengthOfAnimation > 0) {
            animate();
        }
        Iterator it = canvasItems.iterator();
        while( it.hasNext() ) {
            CanvasItem cur = (CanvasItem)it.next();
            cur.draw(g2d);
        }
        if( this.focusNode == null ) {
            return;
        }
        if( lengthOfAnimation <= 0 && this.focusNode.hasClones() ) {
            HyperNodeView focusHyperNode = (HyperNodeView)this.hypernodeviews.get( this.focusNode.getGraphNode() );
            focusHyperNode.showClones( g2d, hypernodeviews );
        }
    }

    public void addCanvasItem(CanvasItem item) {
        this.canvasItems.add(item);
    }

    protected void animate() {
        long newTime = System.currentTimeMillis();
        long elapsedTime = newTime - animationTime;
        double animDist = elapsedTime / (double) lengthOfAnimation;
        lengthOfAnimation -= elapsedTime;
        animationTime = newTime;
        if(animDist > 1) {
            animDist = 1;
        }
        moveCanvasItems( focusNode.getX() * animDist, focusNode.getY() * animDist );
        repaint();
    }

    /**
     * Rotate node about the center (0, 0) by angle passed.
     */
    protected void rotateNodes( double angle ) {
        Iterator it = this.hypernodes.values().iterator();
        while( it.hasNext() ) {
            HyperNode hn = (HyperNode)it.next();
            hn.rotate( angle );
        }
    }

    public void mouseClicked( MouseEvent e ) {
    }

    /**
     * Find HyperNodeView that has been clicked on.
     */
    private HyperNodeView getClickedItem( MouseEvent e ) {
        Iterator it = canvasItems.iterator();
        while( it.hasNext() ) {
            CanvasItem cur = (CanvasItem)it.next();
            if( cur instanceof HyperNodeView ) {
                double curX = e.getX() - getSize().width/2;
                double curY = e.getY() - getSize().height/2;
                curX = curX * ( 1 / canvasScale);
                curY = curY  * (1 / canvasScale);
                boolean found = cur.containsPoint(new Point2D.Double( curX, curY));
                if(  found == true ) {
                    return (HyperNodeView)cur;
                }
            }
        }
        return null;
    }

    /**
     * Return the selected LabelView.
     */
    public static LabelView getSelectedLabelView() {
        return CanvasManager.labelView;
    }

    /**
     * When a hyperNode has focus, its label is placed last in the
     * canvasItems list ( so as to be drawn last), and is told
     * that it has focus.
     */
    protected void setLabelSelected( HyperNodeView selectedNodeView ) {
//        if( selectedNodeView == null ) {
//            return;
//        }
        // find the LabelView for this HyperNodeView.
        ListIterator it = this.canvasItems.listIterator(this.canvasItems.size());
        while( it.hasPrevious() ) {
            CanvasItem canvasItem = (CanvasItem)it.previous();
            if( canvasItem instanceof LabelView ) {
                if( ((LabelView)canvasItem).hasHyperNodeView(selectedNodeView) == true) {
                    this.labelView = (LabelView)canvasItem;
                    break;
                }
            }
        }
        if( labelView != null ) {
            canvasItems.remove(this.labelView);
            canvasItems.add(this.labelView);
        }
    }

    public void mousePressed( MouseEvent e ) {
        lastPoint.setLocation( e.getPoint() );
    }

    public void mouseReleased(MouseEvent e) {
        if( dragmode == true ) {
            dragmode = false;
            repaint();
        }
        else {
            HyperNodeView focusedHyperNodeView = getClickedItem( e );
            if( focusedHyperNodeView == null ) {
                return;
            }
            if( e.getClickCount() == 1) {
                this.singleClickTimer = new Timer();
                this.singleClickTimer.schedule( new CanvasItemSingleClicked( focusedHyperNodeView ),  300 );
            } else if( e.getClickCount() == 2 ){
//                this.singleClickTimer.cancel();
                    System.out.println();
                    System.out.println("CanvasManager is sending DoubleClick");
                    System.out.println();
                    System.out.println();

                this.viewListener.notifyChange(focusedHyperNodeView.getGraphNode() , ViewEventListener.MOUSE_DOUBLECLICK);
            }
            repaint();
        }
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mouseDragged(MouseEvent e) {
        double lpx = lastPoint.getX();
        double lpy = lastPoint.getY();
        double x = e.getX();
        double y = e.getY();
        labelView = null;
        this.focusNode = null;
        if(dragmode == false) {
            double dragedAmount = Math.sqrt( (lpx-x)*(lpx-x)+(lpy-y)*(lpy-y) );
            if( dragedAmount > DRAG ) {
                dragmode = true;
            } else {
                return;
            }
        }
        currentHighlightedView = null;
        if( (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == 0 ) {
            double xDif = (lpx - x);
            double yDif = (lpy - y);
            moveCanvasItems( xDif, yDif );
        } else {
            // get x's and y's in cartesian coordinates
            double curX = x - getSize().width/2;
            double curY = y - getSize().height/2;
            double lastX = lpx - getSize().width/2;
            double lastY = lpy - getSize().height/2;
            // calculate angle of rotation
            double angle =  Math.atan2( lastX, lastY ) - Math.atan2( curX, curY );
            this.rotateNodes( angle );
        }
        lastPoint.setLocation( x, y );
        repaint();
    }

    /**
     * Move all the nodes by an offset x and y.
     */
    private void moveCanvasItems( double x, double y ) {
        Iterator it = this.hypernodes.values().iterator();
        while( it.hasNext() ) {
            HyperNode hn = (HyperNode)it.next();
            hn.move( x, y );
        }
    }

    public void mouseMoved(MouseEvent e) {
        if( dragmode ) {
            return;
        }
        Iterator it = canvasItems.iterator();
        double minDist = this.getWidth();
        double dist = 0;
        HyperNodeView closestNode = null;
        while( it.hasNext() ) {
            CanvasItem cur = (CanvasItem)it.next();
            if( cur instanceof HyperNodeView ) {
                double curX = e.getX() - getSize().width/2;
                double curY = e.getY() - getSize().height/2;
                curX = curX * ( 1 / canvasScale);
                curY = curY  * (1 / canvasScale);
                dist = ((HyperNodeView)cur).distance(curX, curY);
                if(  dist < minDist ) {
                    minDist = dist;
                    closestNode = (HyperNodeView)cur;
                }
            }
        }
        if(  closestNode != null && !closestNode.equals( currentHighlightedView )) {
            currentHighlightedView = closestNode;
            closestNode.setHighlightEdge( true );
            this.highlightEdge( closestNode.getGraphNode() );
            repaint();
        }
    }

    /**
     * Method called to highlight edges back to the root node.
     */
    private void highlightEdge( GraphNode node ) {
        Iterator it = Edge.getInboundEdgeNodes( node );
        while( it.hasNext() ) {
            GraphNode cur = (GraphNode)it.next();
            HyperNodeView hyperNodeView = (HyperNodeView)hypernodeviews.get( cur );
            if( hyperNodeView != null ) {
                hyperNodeView.setHighlightEdge( true );
                this.highlightEdge( cur );
            }
        }
    }

    /**
     * Method called when a new graph is loaded.
     *
     * Reset all global variables
     */
    protected void resetCanvas() {
        this.canvasScale = 1;
        this.canvasItems.clear();
        this.hypernodes.clear();
        this.hypernodeviews.clear();
        this.dragmode = false;
        this.focusNode = null;
        this.currentHighlightedView = null;
        this.labelView = null;
    }
 }
