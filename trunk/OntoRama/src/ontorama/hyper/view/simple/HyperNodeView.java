package ontorama.hyper.view.simple;

/**
 *
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Iterator;

import ontorama.OntoramaConfig;
import ontorama.hyper.model.HyperNode;
import ontorama.hyper.model.PositionChangedObserver;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeType;
import ontorama.ontologyConfig.NodeTypeDisplayInfo;
import org.tockit.canvas.CanvasItem;

//import java.awt.geom.Ellipse2D.Double;

public class HyperNodeView extends CanvasItem implements PositionChangedObserver {

    /**
     * Hold the model for this ui.
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
    public static double relativeFocus = .8;

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
     * Holds the current node ui radius.
     */
    private double viewRadius;

    /**
     * Store that edge connecting this node should
     * be highlighted.
     */
    private boolean highlightEdge = false;


    /**
     * Stores if this ui is visible.
     */
    private boolean isVisible = true;

    /**
     * Create 2D object for node representation.
     */
    private Shape nodeShape = new Ellipse2D.Double(0, 0, 0, 0);

    /**
     * Hold the percentage increase for cloned node rind
     * used for showing cloned node when a cloned node gets focus.
     */
    private static final int RINGPERCENTAGE = 10;

    /**
     *  Store if node is a leaf node
     */
    private boolean isLeaf = false;

    private ontorama.model.graph.NodeType nodeType;
    private Shape pathShape;

    /**
     * Returns the radius of the projection sphere.
     */
    public static double getSphereRadius() {
        return HyperNodeView.sphereRadius;
    }

    public static double getFocalDepth() {
        return HyperNodeView.focalDepth;
    }

    /**
     * Stores the depth of the node in the graph (distance to the root element).
     */
    private double depth = 0;

    public HyperNodeView(HyperNode model, ontorama.model.graph.NodeType nodeType) {
        this.model = model;
        this.nodeType = nodeType;
        model.addPositionChangedObserver(this);
        updateProjection();
        NodeTypeDisplayInfo displayInfo = OntoramaConfig.getNodeTypeDisplayInfo(this.nodeType);
        if (model.hasClones()) {
            nodeColor = Color.red;
        } else {
            nodeColor = displayInfo.getColor();
        }
        double radius = model.getNodeRadius();
        nodeShape = new Ellipse2D.Double(-radius, -radius, radius * 2, radius * 2);
        /// @todo hardcoded node type name here
        if (nodeType.getNodeType().equals("relation")) {
            nodeShape = new Polygon();
            ((Polygon) nodeShape).addPoint( (int) projectedX, (int) projectedY);
            ((Polygon) nodeShape).addPoint( (int) projectedX , (int) (projectedY + radius));
            ((Polygon) nodeShape).addPoint( (int) (projectedX + radius), (int) (projectedY + radius));
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
     * Set if ui is folded.
     */
    public void setFolded(boolean state) {
//        System.out.println("HyperNodeView: " + this.getGraphNode().getName() + " folded state is being set to " + state);
        this.getGraphNode().setFoldState(state);
    }

    /**
     * Get if ui is folded.
     */
    public boolean getFolded() {
        return this.getGraphNode().getFoldedState();
        //return this.isFolded;
    }

    /**
     * Set if ui is visible.
     */
    public void setVisible(boolean state) {
        this.isVisible = state;
    }

    /**
     * Get if ui is visible.
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

    private float[] transform(float[] in) {
        float[] retval = new float[in.length];
        for (int i = 0; i < in.length; i+=2) {
            double x = model.getX() + in[i];
            double y = model.getY() + in[i+1];
            Point2D newCoord = transform(x,y);
            retval[i] = (float) newCoord.getX();
            retval[i+1] = (float) newCoord.getY();
        }
        return retval;
    }

    private Point2D transform(double x, double y) {
        double length = Math.sqrt(x * x + y * y + focalDepth * focalDepth);
        double scale = sphereRadius / length;
        return new Point2D.Double(scale * x, scale * y);
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
     * Method called to find nearest node ui to mouse pointer.
     */
    public boolean isNearestItem(double scrX, double scrY) {
        return false;
    }

    /**
     * Returns true if this is the node clicked on
     */
    public boolean containsPoint(Point2D point) {
        return pathShape.contains(point);
    }

    /**
     * Returns Node.
     */
    public ontorama.model.graph.Node getGraphNode() {
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
     * Return the node ui radius.
     */
    public double getViewRadius() {
        return viewRadius;
    }

    public HyperNode getModel() {
        return model;
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

        pathShape = transform(nodeShape);

        if (!isLeaf && this.getFolded()) {
            g2d.fill(pathShape.getBounds2D());
        } else {
            g2d.fill(pathShape);
        }
    }

    /**
     * @todo get movement out into nodeShape, then turn this into a function object.
     */
    private Shape transform(Shape inShape) {
        GeneralPath outShape = new GeneralPath();
        PathIterator path = inShape.getPathIterator(null);
        outShape.setWindingRule(path.getWindingRule());
        float[] points = new float[6];
        while(!path.isDone()) {
            int segType = path.currentSegment(points);
            points = transform(points);
            switch(segType) {
                case PathIterator.SEG_LINETO:
                    outShape.lineTo(points[0], points[1]);
                    break;
                case PathIterator.SEG_MOVETO:
                    outShape.moveTo(points[0], points[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    outShape.quadTo(points[0], points[1], points[2], points[3]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    outShape.curveTo(points[0], points[1], points[2], points[3], points[4], points[5]);
                    break;
                case PathIterator.SEG_CLOSE:
                    outShape.closePath();
                    break;
            }
            path.next();
        }
        return outShape;
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
        // draw lines to, and show clones
        Iterator it = this.getGraphNode().getClones().iterator();
        while (it.hasNext()) {
            ontorama.model.graph.Node cur = (ontorama.model.graph.Node) it.next();
            HyperNodeView hyperNodeView = (HyperNodeView) hypernodeviews.get(cur);
            if (hyperNodeView == null) {
                //System.out.println("HyperNodeView not found for " + cur.getName());
                continue;
            }
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
            Stroke oldStroke = g2d.getStroke();
            float[] dashstyle = {4, 4};
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL, 1, dashstyle, 0));
            g2d.draw(new Line2D.Double(x1, y1, x2, y2));
            g2d.setStroke(oldStroke);
        }
    }

    public Rectangle2D getCanvasBounds(Graphics2D g) {
        return pathShape.getBounds2D();
    }

    public boolean hasAutoRaise() {
            return false;
        }

    public Point2D getPosition() {
        Rectangle2D bounds = pathShape.getBounds2D();
        return new Point2D.Double(bounds.getX() + bounds.getWidth()/2, bounds.getY() + bounds.getHeight()/2);
    }

    public String toString() {
        return "Node ui for " + this.model.getName();
    }

	public static double getRelativeFocus() {
		return relativeFocus;
	}

	public static void setRelativeFocus(double d) {
		relativeFocus = d;
		focalDepth = sphereRadius * relativeFocus;
	}
}
