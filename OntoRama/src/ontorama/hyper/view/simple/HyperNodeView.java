package ontorama.hyper.view.simple;

/**
 *
 */

import ontorama.hyper.canvas.CanvasItem;

//import hyper.canvas.CanvasItem;
import ontorama.hyper.model.HyperNode;
//import hyper.model.Position3D;
import ontorama.hyper.model.PositionChaingedObserver;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.Iterator;

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
     * ///TODO this color should be stored and maybe based on
     * ontology type.
     */
    private Color nodeColor = Color.blue;

    /**
     * Store the node fade color.
     */
    private Color fadeColor = nodeColor;

    /**
     * Holds the current view radius.
     */
    private int viewRadius;

    /**
     * Sets the projection information for all nodes.
     */
    public static void setProjection(double sphereRadius, double focalDepth) {
        //NodeView.sphereRadius = sphereRadius;
        //NodeView.focalDepth = focalDepth;
    }

    /**
     * Sets the projection information for all nodes, setting the focal depth
     * to 110% of the radius.
     */
    public static void setProjection(double sphereRadius) {
        //NodeView.sphereRadius = sphereRadius;
        //NodeView.focalDepth = sphereRadius * relativeFocus;
    }

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
     * Draw the node in the hyperbolic plane.
     */
    public void draw( Graphics2D g2d ) {
        if(model.getPosition().distance(0,0) > 10000) {
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
        viewRadius = (int)(model.getNoderadius() * scale);
        double red = nodeColor.getRed() + ( ( 255 -  nodeColor.getRed() ) * colorScale );
        double green = nodeColor.getGreen() + ( ( 255 -  nodeColor.getGreen() ) * colorScale );
        double blue = nodeColor.getBlue() + ( ( 255 -  nodeColor.getBlue() ) * colorScale );
        fadeColor = new Color( (int)red, (int)green, (int)blue );
        g2d.setColor( fadeColor );
        g2d.fillOval(	(int)projectedX - viewRadius,
                        (int)projectedY - viewRadius,
                       (int)viewRadius * 2, (int)viewRadius * 2);
        g2d.setColor( Color.black );
        g2d.drawString(model.getName(), (int)projectedX, (int)projectedY );
    }

    public String toString() {
        return "Node view for " + this.model.getName();
    }
}