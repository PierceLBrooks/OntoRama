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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;



public class CanvasManager extends JComponent
        implements MouseListener, MouseMotionListener,
		ViewEventObserver {

	protected ViewEventListener viewListener;

    /**
     * Inner class to handle canvasItem single click
     */
    private class CanvasItemSingleClicked  extends TimerTask{
        private HyperNodeView hyperNodeView;
        public CanvasItemSingleClicked( HyperNodeView hyperNodeView ) {
            this.hyperNodeView = hyperNodeView;
        }

        public void run() {
            //hyperNodeView.hasFocus();
			viewListener.notifyChange(hyperNodeView.getGraphNode() , ViewEventListener.MOUSE_SINGLECLICK);
            //viewListener.notifyChange(hyperNodeView.getGraphNode() , ViewEventListener.MOUSE_DOUBLECLICK);
        }
    }


    /**
     * Store the last point.
     */
    protected Point2D lastPoint = new Point2D.Double( 0, 0 );

    /**
     * Holds the current canvas scale.
     */
    protected double canvasScale;

    /**
     * Hold the mapping of HyperNode to GraphNodes
     */
    protected Hashtable hypernodes;

    /**
     * Holds the mapping of HyperNodeView to GraphNodes
     */
     protected Hashtable hypernodeviews;

    /**
     * Store the hyper view canvas items.
     */
    protected List canvasItems = null;

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
     * Stores the hyperNodeView that is having its
     * Edge highlighted back to the root node.
     */
    private HyperNodeView currentHighlightedView = null;

    /**
     * Stores the LabelView that is selected.
     */
    private static LabelView labelView = null;

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
     * Set hyper view in scrole mode.
     */
    private static final int TRANSLATION = 2;

    /**
     * set hyper view in rotation mode.
     */
    private static final int ROTATION = 1;

    /**
     * Store hype view mode (TRANSLATION or ROTATION)
     */
    private int hyperViewMode = TRANSLATION;

    /**
     * A timer to distinguish between single and double clicks.
     */
    private Timer singleClickTimer = new Timer();;

    /**
     * draw canvas items.
     *
     * @todo ConcurrentModificationException need to be addressed.
     */
    protected void drawNodes( Graphics2D g2d ) {
        if(lengthOfAnimation > 0) {
            animate();
        }
        try {
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
        catch(java.util.ConcurrentModificationException e){drawNodes( g2d );}
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
                boolean found = cur.isClicked( curX, curY);
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
    private void setLabelSelected( HyperNodeView selectedNodeView ) {
        if( selectedNodeView == null ) {
            return;
        }
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
        if((e.getModifiers() & Event.META_MASK) != 0) {
			hyperViewMode = ROTATION;
		}
		else {
			hyperViewMode = TRANSLATION;
		}
        lastPoint.setLocation( e.getPoint() );
    }

//    private HyperNodeView focusedHyperNodeView = null;

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
//                int numOfLeaves = Edge.getIteratorSize( Edge.getOutboundEdges( hyperNodeView.getGraphNode()));
//                // don't fold if leaf node
//                if( numOfLeaves == 0 ) {
//                    return;
//                }
//                boolean foldedState = hyperNodeView.getFolded();
//                hyperNodeView.setFolded( !foldedState);
//                setFolded( foldedState,  hyperNodeView.getGraphNode() );
            }
            repaint();
        }
    }

    /**
     * Method to fold and unfold HyperNodeViews.
     */
    private void setFolded( boolean foldedState, GraphNode node ) {
        Iterator it = Edge.getOutboundEdgeNodes( node );
        while( it.hasNext() ) {
            GraphNode cur = (GraphNode)it.next();
            HyperNodeView hyperNodeView = (HyperNodeView)hypernodeviews.get( cur );
            if( hyperNodeView != null ) {
                hyperNodeView.setVisible( foldedState );
                if( !hyperNodeView.getFolded() ) {
                    this.setFolded( foldedState, cur );
                }
            }
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
        if( hyperViewMode == this.TRANSLATION ) {
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

    /**
     * FocusChanged called by FocusListen to
     * emit a change in which node has focus.
     * The node that has focus is centered.
     */
    public void focusChanged( GraphNode graphNode ){

        focusNode = (HyperNode) this.hypernodes.get (graphNode);
        // set focused node label to selected
        testIfVisibleOrFolded( (HyperNodeView)this.hypernodeviews.get( graphNode) );
        setLabelSelected( (HyperNodeView)(hypernodeviews.get(graphNode) ) );
        //place the label last in the list so that it gets drawn last.
        // calculate the length of the animation as a function of the distance
        // in the euclidian space (before hyperbolic projection)
        double distance = Math.sqrt( focusNode.getX() * focusNode.getX() +
                                     focusNode.getY() * focusNode.getY() );
        lengthOfAnimation = (long)(distance*1.5);
        animationTime = System.currentTimeMillis();
        repaint();
    }

    /**
     * When node gets focus.
     * Test if node is visible, if not find folded node and unfold.
     * If node is folded, unfold.
     */
    private void testIfVisibleOrFolded( HyperNodeView hyperNodeView ) {
        // test if visible, if not find folded node.
        System.out.println("testIfVisibleOrFolded: hyperNodeView = " + hyperNodeView);
        if( !hyperNodeView.getVisible() ) {
            System.out.println(hyperNodeView.getName() + " is not visible");
            unfoldNodes( hyperNodeView );
        }
    }

    /**
     * Unfold nodes back to root node.
     */
    private void unfoldNodes( HyperNodeView hyperNodeView ) {
//        if( hyperNodeView.getFolded() ) {
//            setFolded( true, hyperNodeView.getGraphNode() );
//            hyperNodeView.setFolded( false );
//        }
        Iterator it = Edge.getInboundEdgeNodes( hyperNodeView.getGraphNode());
        while(it.hasNext()) {
            GraphNode cur = (GraphNode)it.next();
            HyperNodeView curHyperNode = (HyperNodeView)hypernodeviews.get(cur);
            if( !curHyperNode.getVisible() ) {
                unfoldNodes( curHyperNode );
            }
            if( curHyperNode.getFolded() ) {
                setFolded( true, cur );
                curHyperNode.setFolded( false );
            }
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

    //////////////////////////ViewEventObserver interface implementation////////////////

    /**
     *
     */
    public void focus ( GraphNode node) {
      System.out.println();
      System.out.println("******* hyperView got focus for node " + node.getName());
      focusChanged(node);
      //hyperNodeView.hasFocus();
      System.out.println();
    }

    /**
     *
     */
    public void toggleFold ( GraphNode node) {
        System.out.println("******* CanvasManager got toggleFold for node comms#TransmissionObject");

        HyperNodeView focusedHyperNodeView = (HyperNodeView)this.hypernodeviews.get( node );
        if( focusedHyperNodeView == null ) {
            return;
        }
        int numOfLeaves = Edge.getIteratorSize( Edge.getOutboundEdges( node));
        // don't fold if leaf node
        if( numOfLeaves == 0 ) {
            return;
        }
        boolean foldedState = focusedHyperNodeView.getFolded();
        System.out.println("hyper view: foldedState = " + foldedState);
        focusedHyperNodeView.setFolded( !foldedState);
        System.out.println("hyper view: foldedState = " + focusedHyperNodeView.getFolded());
        setFolded( foldedState,  node );
        repaint();
        System.out.println("hyper view: finished");
    }

    /**
     *
     */
    public void query ( GraphNode node) {
    }

 }
