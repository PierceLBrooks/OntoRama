package ontorama.hyper.canvas;

/**
 * CanvasManager for now handles mouse events, and is responsible
 * for drawing canvas items.
 */

import ontorama.hyper.model.HyperNode;
import ontorama.hyper.view.simple.HyperNodeView;
import ontorama.hyper.view.simple.LabelView;

import javax.swing.JComponent;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;



public class CanvasManager extends JComponent
        implements MouseListener, MouseMotionListener, FocusChangedObserver {

    /**
     * Store the last point.
     */
    protected Point2D lastPoint = new Point2D.Double( 0, 0 );

    /**
     * Holds the current canvas scale.
     */
    protected double canvasScale;

    /**
     * Hold the current root node for the tree.
     *
     * ///TODO Mouse events should not be handled here. root is required here for mouse moves.
     */
    //protected GraphNode root = null;

    /**
     * Hold the mapping of HyperNode to GraphNodes
     */
    protected Hashtable hypernodes;

    /**
     * Holds the mapping of HyperNodeView to HyperNode
     */
     protected Hashtable hypernodeviews;

    /**
     * Store the hyper view canvas items.
     */
    protected List canvasItems = new LinkedList();

    /**
     * Flag to stop labels being drawn in drag mode.
     */
    private boolean noLabels = false;

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
    private HyperNode focusNode = null;

    /**
     * Holds the remaining length of the animation.
     *
     * If negative animation we don't animate at the moment.
     */
    private long lengthOfAnimation = -1;

    /**
     * The time when we did the last animation step.
     */
    private long animationTime = 0;

    /**
     * draw nodes and lines.
     */
    protected void drawNodes( Graphics2D g2d ) {
        if(lengthOfAnimation > 0) {
            animate();
            //noLabels = true;
        }
        Iterator it = canvasItems.iterator();
        while( it.hasNext() ) {
            CanvasItem cur = (CanvasItem)it.next();
//            if( noLabels && cur instanceof LabelView ) {
//                continue;
//            }
            cur.draw(g2d);
        }
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

    public void mouseClicked( MouseEvent e ) {
        Iterator it = canvasItems.iterator();
        while( it.hasNext() ) {
            CanvasItem cur = (CanvasItem)it.next();
            if( cur instanceof HyperNodeView ) {
                double curX = e.getX() - getSize().width/2;
                double curY = e.getY() - getSize().height/2;
                curX = curX * ( 1 / canvasScale);
                curY = curY  * (1 / canvasScale);
                boolean found = cur.isClicked( curX, curY);
                if(  found == true ) {
                    ((HyperNodeView)cur).hasFocus();
                }
            }
        }
    }

    public void mousePressed( MouseEvent e ) {
        lastPoint.setLocation( e.getPoint() );
    }

    public void mouseReleased(MouseEvent e) {
        noLabels = false;
        dragmode = false;
        repaint();
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
        if(dragmode == false) {
            double dragedAmount = Math.sqrt( (lpx-x)*(lpx-x)+(lpy-y)*(lpy-y) );
            if( dragedAmount > DRAG ) {
                dragmode = true;
                //noLabels = true;
            }
            else {
                return;
            }
        }
        double xDif = (lpx - x);
        double yDif = (lpy - y);
        lastPoint.setLocation( x, y );
        moveCanvasItems( xDif, yDif );
        //paint(this.getGraphics());
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

    /**
     * FocusChanged called by FocusListen to
     * emit a change in which node has focus.
     * The node that has focus is centered.
     */
    public void focusChanged( Object obj ){
        focusNode = (HyperNode)obj;
        // calculate the length of the animation as a function of the distance
        // in the euclidian space (before hyperbolic projection)
        double distance = Math.sqrt( focusNode.getX() * focusNode.getX() +
                                     focusNode.getY() * focusNode.getY() );
        lengthOfAnimation = (long)(distance*1.5);
        animationTime = System.currentTimeMillis();
        repaint();
    }

    public void mouseMoved(MouseEvent e) {
        /*Iterator it = canvasItems.iterator();
        statusBar = "";
        while( it.hasNext() ) {
            CanvasItem cur = (CanvasItem)it.next();
            double x = getSize().width - e.getX();
            double y = getSize().height - e.getY();
            double cartX = (e.getX() + ( -1 * x ))/2;
            double cartY = (e.getY() + ( -1 * y ))/2;
            cartX = cartX * ( 1 / canvasScale);
            cartY = cartY  * (1 / canvasScale);
            boolean found = cur.isNearestItem( cartX, cartY);
            if(  found == true && cur instanceof NodeView) {
                statusBar = ((NodeView)cur).getName();
            }
            repaint();*/
    }
 }