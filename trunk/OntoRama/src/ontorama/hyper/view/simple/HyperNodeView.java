package ontorama.hyper.view.simple;

/**
 *
 */

import ontorama.hyper.canvas.CanvasItem;
import ontorama.hyper.model.HyperNode;
import ontorama.hyper.model.PositionChaingedObserver;
import ontorama.model.GraphNode;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.Hashtable;
import java.util.Iterator;

import java.awt.geom.Ellipse2D;
//import java.awt.geom.Ellipse2D.Double;

public class HyperNodeView extends CanvasItem implements PositionChaingedObserver {

    /**
     * Hold the model for this view.
     */
    private HyperNode model;

    /**
     * Hold the projected x coordinates.
     */
    private double projectedX;

    /**
     * Hold the projected y coordinates.
     */
    private double projectedY;

    /**
     * The radius of the sphere we project upon.
     */
    private static double sphereRadius = 300;

    /**
     * Used if setProjection(double) is called to determine the focal depth.
     */
    private static double relativeFocus = 1.1;

    /**
     * The focal depth. Set to 10% more than sphereRadius.
     */
    private static double focalDepth = sphereRadius * relativeFocus;

    /**
     * Set the color of the node.
     *
     * @todo this color should be stored and maybe based on
     * ontology type.
     */
    private Color nodeColor = Color.blue;

    /**
     * Store the node fade color.
     */
    private Color fadeColor = nodeColor;

    /**
     * Holds the current node view radius.
     */
    private double viewRadius;

    /**
     * Store that edge connecting this node should
     * be highlighted.
     */
    private boolean highlightEdge = false;

    /**
     * Stores if this view is folded.
     */
    private boolean isFolded = false;

    /**
     * Stores if this view is visible.
     */
    private boolean isVisible = true;

    /**
     * Create 2D object for node representation.
     */
    private Ellipse2D nodeShape = new Ellipse2D.Double( 0,0,0,0 );

    /**
     * Hold the percentage increase for cloned node rind
     * used for showing cloned node when a cloned node gets focus.
     */
    private static final int RINGPERCENTAGE = 10;

    /**
     * Returns the radius of the projection sphere.
     */
    public static double getSphereRadius() {
        return HyperNodeView.sphereRadius;
    }

    /**
     * Stores the depth of the node in the graph (distance to the root element).
     */
    private double depth = 0;

    public HyperNodeView( HyperNode model ) {
        this.model = model;
        model.addPositionChaingedObserver( this );
        project( model.getX(), model.getY() );
        if( model.hasClones() ) {
            nodeColor = Color.red;
        } else {
          nodeColor = Color.blue;
        }
    }

    /**
     * Set if view is folded.
     */
    public void setFolded( boolean state ) {
        this.isFolded = state;
    }

    /**
     * Get if view is folded.
     */
    public boolean getFolded( ) {
        return this.isFolded;
    }

    /**
     * Set if view is visible.
     */
    public void setVisible( boolean state ) {
        this.isVisible = state;
    }

    /**
     * Get if view is visible.
     */
    public boolean getVisible( ) {
        return this.isVisible;
    }

    /**
     * Update observer of change in model.
     */
    public void positionUpdate( double x, double y ) {
        project( x, y);
    }

    /**
     * Get the projected x.
     */
    public double getProjectedX() {
        return projectedX;
    }

    /**
     * Get the projected y.
     */
    public double getProjectedY() {
        return projectedY;
    }

    /**
     * Return the node name.
     */
    public String getName() {
        return model.getName();
    }

    /**
     * Project the model coordinates into the hyperbolic plana.
     */
    public void project( double x, double y ) {
        //double x = model.getX();
        //double y = model.getY();
        double length = Math.sqrt( x*x + y*y + focalDepth*focalDepth );
        double scale = sphereRadius / length;
        projectedX = scale * x;
        projectedY = scale * y;
        depth = (1-scale) * focalDepth;
    }

    /**
     * Returns node color.
     */
    public Color getNodeColor() {
        return nodeColor;
    }

    /**
     * Returns node fade color.
     */
    public Color getNodeFadeColor() {
        return fadeColor;
    }

    /**
     * Calculated the scale to adjust canvas items projection.
     */
    public double getScale() {
        double sizePos = (focalDepth - depth)/sphereRadius ;
        double scale = ( sizePos * sizePos * sizePos );
        return scale;
    }

    /**
     * Notify Hypernode that it has focus.
     */
    public void hasFocus() {
        System.out.println("HyperNodeView hasFocus");
        model.hasFocus();
    }

    /**
     * Method called to find nearest node view to mouse pointer.
     */
    public boolean isNearestItem( double scrX, double scrY ) {
        return false;
    }

    /**
     * Returns true if this is the node clicked on
     */
    public boolean isClicked( double scrX, double scrY ) {
        double x1 = this.projectedX;
        double y1 = this.projectedY;
        double dist = Math.sqrt( (scrX - x1)*(scrX - x1) + (scrY - y1)*(scrY - y1) );
        if( dist <= viewRadius) {
            return true;
        }
        return false;
    }

    /**
     * Returns the distance from the node to a ginen point.
     */
    public double distance( double scrX, double scrY ) {
        double x1 = this.projectedX;
        double y1 = this.projectedY;
        return Math.sqrt( (scrX - x1)*(scrX - x1) + (scrY - y1)*(scrY - y1) );
    }

    /**
     * Returns GraphNode.
     */
    public GraphNode getGraphNode() {
        return this.model.getGraphNode();
    }

    /**
     * Method called to set highlight flag.
     *
     * This method highlights the edge back to root node.
     */
    public void setHighlightEdge( boolean state) {
        this.highlightEdge = state;
    }

    /**
     * Get state of highlightEdge flag.
     */
    public boolean getHighlightEdge() {
        return this.highlightEdge;
    }

    /**
     * Return the node view radius.
     */
    public double getViewRadius() {
        return viewRadius;
    }

    /**
     * Draw the node in the hyperbolic plane.
     */
    public void draw( Graphics2D g2d ) {
        if(model.getPosition().distance(0,0) > 10000) {
            return;
        }
        if( !this.isVisible ) {
            return;
        }
        double x = model.getX();
        double y = model.getY();
        double dist = Math.sqrt( x*x + y*y ) + 1;
        dist = dist * Math.pow(1/dist, .1);
        if( dist > sphereRadius ) {
            dist = sphereRadius;
        }
        double scale = getScale();
        double colorScale = (dist/sphereRadius);
        viewRadius = model.getNodeRadius() * scale;
        double red = nodeColor.getRed() + ( ( 255 -  nodeColor.getRed() ) * colorScale );
        double green = nodeColor.getGreen() + ( ( 255 -  nodeColor.getGreen() ) * colorScale );
        double blue = nodeColor.getBlue() + ( ( 255 -  nodeColor.getBlue() ) * colorScale );
        fadeColor = new Color( (int)red, (int)green, (int)blue );
        g2d.setColor( fadeColor );
        if( this.isFolded ) {
            g2d.fillRect(   (int)(projectedX - viewRadius),
                             (int)(projectedY - viewRadius),
                             (int)(viewRadius * 2),
                             (int)(viewRadius * 2));
            return;
        }
        nodeShape.setFrame( projectedX - viewRadius,
                            projectedY - viewRadius,
                            viewRadius * 2, viewRadius * 2 );
        g2d.fill( nodeShape );
    }

    /**
     * Method called if node is cloned, and has focus.
     *
     * Draws ring around cloned node and connects the together with a line.
     */
    public void showClones( Graphics2D g2d, Hashtable hypernodeviews ) {
        double ringRadius = viewRadius + (viewRadius/RINGPERCENTAGE);
        this.showClone( g2d );
        // draw lines to, and show clones
        Iterator it = this.getGraphNode().getClones();
        while( it.hasNext() ) {
            GraphNode cur = (GraphNode)it.next();
            HyperNodeView hyperNodeView = (HyperNodeView)hypernodeviews.get( cur );
            if( hyperNodeView == null ) {
            	System.out.println("HyperNodeView not found for " + cur.getName());
                continue;
            }
            hyperNodeView.showClone( g2d );
            double x1 = this.projectedX;
            double y1 = this.projectedY;
            double x2 = hyperNodeView.getProjectedX();
            double y2 = hyperNodeView.getProjectedY();
            double x = x2 - x1;
            double y = y2 - y1;
            double angle = Math.atan2( x, y );
            double dist = Math.sqrt( ( x2 - x1) * ( x2 - x1) +  ( y2 - y1) * ( y2 - y1) );
            double toViewRadius = hyperNodeView.getViewRadius();
            dist = dist - ( toViewRadius + ( toViewRadius / RINGPERCENTAGE ) );
            x1 = Math.sin( angle ) * ringRadius;
            y1 = Math.cos( angle ) * ringRadius;
            x2 = Math.sin( angle ) * dist;
            y2 = Math.cos( angle ) * dist;
            g2d.draw( new Line2D.Double( x1, y1, x2, y2 ) );
        }
    }

    /**
     * Highlight clones for focused clone node.
     */
    public void showClone( Graphics2D g2d ) {
        double ringRadius = viewRadius + (viewRadius/RINGPERCENTAGE);
        nodeShape.setFrame( projectedX - ringRadius,
                            projectedY - ringRadius,
                            ringRadius * 2, ringRadius * 2 );
        g2d.draw( nodeShape );
    }

    public String toString() {
        return "Node view for " + this.model.getName();
    }
}
