package ontorama.hyper.view.simple;

/**
 *
 */

import ontorama.hyper.model.HyperNode;
import ontorama.hyper.model.PositionChangedObserver;
import ontorama.model.GraphNode;
import ontorama.model.EdgeImpl;
import org.tockit.canvas.CanvasItem;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Iterator;

//import java.awt.geom.Ellipse2D.Double;

public class HyperNodeView extends CanvasItem implements PositionChangedObserver {

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
    private static double relativeFocus = .8;

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
     * Stores if this view is visible.
     */
    private boolean isVisible = true;

    /**
     * Create 2D object for node representation.
     */
    private Ellipse2D nodeShape = new Ellipse2D.Double(0, 0, 0, 0);

    /**
     * Hold the percentage increase for cloned node rind
     * used for showing cloned node when a cloned node gets focus.
     */
    private static final int RINGPERCENTAGE = 10;

    /**
     *  Store if node is a leaf node
     */
    private boolean isLeaf = false;

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

    public HyperNodeView(HyperNode model) {
        this.model = model;
        model.addPositionChangedObserver(this);
        updateProjection();
        if (model.hasClones()) {
            nodeColor = Color.red;
        } else {
            nodeColor = Color.blue;
        }
    }

    /**
     * Returns ture if leaf node
     */
    public boolean isLeaf() {
        return this.isLeaf;
    }

    /**
     * Set if leaf node
     */
    public void setNodeAsLeafNode() {
        this.isLeaf = true;
    }

    /**
     * Set if view is folded.
     */
    public void setFolded(boolean state) {
//        System.out.println("HyperNodeView: " + this.getGraphNode().getName() + " folded state is being set to " + state);
        this.getGraphNode().setFoldState(state);
    }

    /**
     * Get if view is folded.
     */
    public boolean getFolded() {
        return this.getGraphNode().getFoldedState();
        //return this.isFolded;
    }

    /**
     * Set if view is visible.
     */
    public void setVisible(boolean state) {
        this.isVisible = state;
    }

    /**
     * Get if view is visible.
     */
    public boolean getVisible() {
        return this.isVisible;
    }

    /**
     * Update observer of change in model.
     */
    public void positionChanged() {
        updateProjection();
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
     * Project the model coordinates into the hyperbolic plane.
     *
     * x and y are the new coordinates int the euclidean plane.
     */
    public void updateProjection() {
        double x = model.getX();
        double y = model.getY();
        double length = Math.sqrt(x * x + y * y + focalDepth * focalDepth);
        double scale = sphereRadius / length;
        projectedX = scale * x;
        projectedY = scale * y;
        depth = (1 - scale) * focalDepth;
        calculateFadedColor();
        viewRadius = model.getNodeRadius() * getScale();

//     **************************** new code *****************************
        // start hyperbolic projection using Poincaré disc model
        // complex3
//        double conjugateX = x;
//        double conjugateY = -y;
//        double mu1 = (x * conjugateX) - (y * conjugateY);
//        double mu2 = (x * conjugateY) + (y * conjugateX);
//        double mi1 = 1d - mu1;//(1-mu1)
//        double mi2 = 0d - mu2;//(0-mu2)
//        //end
//        double abs = mi1*mi1 + mi2*mi2;
//        double div1 = (x * mi1 + y * mi2) / (abs);
//        double div2 = (y * mi1 - x * mi2) / (abs);
//        // now enlarge to screen coordinates

//        double abs2 = Math.sqrt(div1*div1 + div2*div2);

//        projectedX = div1;
//        projectedY = div2;
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
        double sizePos = (focalDepth - depth) / sphereRadius;
        double scale = (sizePos * sizePos * sizePos);
        return scale;
    }

    /**
     * Notify Hypernode that it has focus.
     */
    public void hasFocus() {
        System.out.println("HyperNodeView hasFocus");
        //model.hasFocus();
    }

    /**
     * Method called to find nearest node view to mouse pointer.
     */
    public boolean isNearestItem(double scrX, double scrY) {
        return false;
    }

    /**
     * Returns true if this is the node clicked on
     */
    public boolean containsPoint(Point2D point) {
        return nodeShape.contains(point);
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
    public void setHighlightEdge(boolean state) {
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
    public void draw(Graphics2D g2d) {
        if (model.getPosition().distance(0, 0) > 10000) {
            return;
        }
        if (!this.isVisible) {
            return;
        }
        updateProjection();
        g2d.setColor(fadeColor);
        nodeShape.setFrame(projectedX - viewRadius,
                projectedY - viewRadius,
                viewRadius * 2, viewRadius * 2);
        if (!isLeaf && this.getFolded()) {
            g2d.fill(nodeShape.getBounds2D());
        } else {
            g2d.fill(nodeShape);
        }
    }

    private void calculateFadedColor() {
        double x = model.getX();
        double y = model.getY();
        double dist = Math.sqrt(x * x + y * y) + 1;
        dist = dist * Math.pow(1 / dist, .1);
        if (dist > sphereRadius) {
            dist = sphereRadius;
        }
        double colorScale = (dist / sphereRadius);
        double red = nodeColor.getRed() + ((255 - nodeColor.getRed()) * colorScale);
        double green = nodeColor.getGreen() + ((255 - nodeColor.getGreen()) * colorScale);
        double blue = nodeColor.getBlue() + ((255 - nodeColor.getBlue()) * colorScale);
        fadeColor = new Color((int) red, (int) green, (int) blue);
    }

    /**
     * Method called if node is cloned, and has focus.
     *
     * Draws ring around cloned node and connects the together with a line.
     */
    public void showClones(Graphics2D g2d, Hashtable hypernodeviews) {
        double ringRadius = viewRadius + (viewRadius / RINGPERCENTAGE);
        this.showClone(g2d);
        // draw lines to, and show clones
        Iterator it = this.getGraphNode().getClones();
        while (it.hasNext()) {
            GraphNode cur = (GraphNode) it.next();
            HyperNodeView hyperNodeView = (HyperNodeView) hypernodeviews.get(cur);
            if (hyperNodeView == null) {
                //System.out.println("HyperNodeView not found for " + cur.getName());
                continue;
            }
            hyperNodeView.showClone(g2d);
            double x1 = this.projectedX;
            double y1 = this.projectedY;
            double x2 = hyperNodeView.getProjectedX();
            double y2 = hyperNodeView.getProjectedY();
            double x = x2 - x1;
            double y = y2 - y1;
            double angle = Math.atan2(x, y);
            double dist = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
            double toViewRadius = hyperNodeView.getViewRadius();
            dist = dist - (toViewRadius + (toViewRadius / RINGPERCENTAGE));
            x1 = Math.sin(angle) * ringRadius;
            y1 = Math.cos(angle) * ringRadius;
            x2 = Math.sin(angle) * dist;
            y2 = Math.cos(angle) * dist;
            g2d.draw(new Line2D.Double(x1, y1, x2, y2));
        }
    }

    /**
     * Highlight clones for focused clone node.
     */
    public void showClone(Graphics2D g2d) {
        double ringRadius = viewRadius + (viewRadius / RINGPERCENTAGE);
        nodeShape.setFrame(projectedX - ringRadius,
                projectedY - ringRadius,
                ringRadius * 2, ringRadius * 2);
        g2d.draw(nodeShape);
    }

    public Rectangle2D getCanvasBounds(Graphics2D g) {
        return nodeShape.getBounds2D();
    }

    public boolean hasAutoRaise() {
            return false;
        }

    public String toString() {
        return "Node view for " + this.model.getName();
    }
}
