package ontorama.hyper.canvas;

/**
 * CanvasManager for now handles mouse events, and is responsible
 * for drawing canvas items.
 */

import ontorama.hyper.model.HyperNode;

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
        implements MouseListener, MouseMotionListener {

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
     * Store the hyper view canvas items.
     */
    protected List canvasItems = new LinkedList();

    /**
     * Flag to stop labels being drawn in drag mode.
     */
    private boolean noLabels = false;

        /**
     * StatusBar displays the node name that the mouse is over;
     */
    protected String statusBar = null;

    /**
     * draw nodes and lines.
     */
    protected void drawNodes( Graphics2D g2d ) {
        Iterator it = canvasItems.iterator();
        while( it.hasNext() ) {
            CanvasItem cur = (CanvasItem)it.next();
            cur.draw(g2d);
        }
    }

    public void mouseClicked( MouseEvent e ) {

    }

    public void mousePressed( MouseEvent e ) {
        lastPoint.setLocation( e.getPoint() );
    }

    public void mouseReleased(MouseEvent e) {
        noLabels = false;
        repaint();
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mouseDragged(MouseEvent e) {
        double xDif = (lastPoint.getX() - e.getX());
        double yDif = (lastPoint.getY() - e.getY());
        lastPoint.setLocation( e.getX(), e.getY() );
        Iterator it = this.hypernodes.values().iterator();
        while( it.hasNext() ) {
            HyperNode hn = (HyperNode)it.next();
            hn.move( xDif, yDif );
        }
        noLabels = true;
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