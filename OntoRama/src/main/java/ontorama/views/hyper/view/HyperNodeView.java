package ontorama.views.hyper.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Iterator;

import ontorama.OntoramaConfig;
import ontorama.conf.NodeTypeDisplayInfo;
import ontorama.model.graph.NodeType;
import ontorama.model.tree.TreeNode;
import ontorama.views.hyper.model.HyperNode;
import ontorama.views.hyper.model.PositionChangedObserver;
import org.tockit.canvas.CanvasItem;

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
     * Color of the node.
     */
    private Color nodeColor;

    /**
     * Store the node fade color.
     */
    private Color fadeColor;

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
     * Hold the percentage increase for cloned node rind
     * used for showing cloned node when a cloned node gets focus.
     */
    private static final int RINGPERCENTAGE = 10;

    /**
     *  Store if node is a leaf node
     */
    private boolean isLeaf = false;

    private NodeType nodeType;
    private Shape projectedShape = new GeneralPath();

	private SphericalProjection projection = null;
	
	private boolean _isFolded = false;
	
    /**
     * Stores the depth of the node in the graph (distance to the root element).
     */
    private double depth = 0;

    public HyperNodeView(HyperNode model, NodeType nodeType, Projection projection) {
        this.model = model;
        this.nodeType = nodeType;
        this.projection = (SphericalProjection) projection;
        
        model.addPositionChangedObserver(this);
        NodeTypeDisplayInfo displayInfo = OntoramaConfig.getNodeTypeDisplayInfo(this.nodeType);
        if (model.hasClones()) {
            nodeColor = displayInfo.getCloneColor();
        } else {
            nodeColor = displayInfo.getColor();
        }
        fadeColor = nodeColor;
		updateProjection();
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
        _isFolded = state;
    }

    /**
     * Get if ui is folded.
     */
    public boolean getFolded() {
    	return _isFolded;
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

    /**
     * Project the model coordinates into the hyperbolic plane.
     *
     * x and y are the new coordinates int the euclidean plane.
     */
    public void updateProjection() {
        double x = model.getX();
        double y = model.getY();
        double length = Math.sqrt(x * x + y * y + projection.getFocalDepth() * projection.getFocalDepth());
        double scale = projection.getSphereRadius() / length;
        projectedX = scale * x;
        projectedY = scale * y;
        depth = (1 - scale) * projection.getFocalDepth();
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
        double sizePos = (projection.getFocalDepth() - depth) / projection.getSphereRadius();
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
        return projectedShape.getBounds2D().contains(point);
    }

    /**
     * Returns Node.
     */
    public TreeNode getTreeNode() {
        return this.model.getTreeNode();
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
        double x = model.getX();
        double y = model.getY();

		NodeTypeDisplayInfo displayInfo = OntoramaConfig.getNodeTypeDisplayInfo(nodeType);
        Shape displayShape = displayInfo.getShape();
        if(!displayInfo.forceUprightShape()) {
            Point2D projectedPos = projection.project(x,y);
            AffineTransform transform = AffineTransform.getRotateInstance(Math.atan2(projectedPos.getX(), -projectedPos.getY()));
            displayShape = transform.createTransformedShape(displayShape);
        }        		
        
        projectedShape = projection.project(displayShape, x, y);

		// adding the draw method makes lines visible that would not be drawn with the fill method
        if (!isLeaf && this.getFolded()) {
        	g2d.draw(projectedShape.getBounds2D());
            g2d.fill(projectedShape.getBounds2D());
        } else {
        	g2d.draw(projectedShape);
            g2d.fill(projectedShape);
        }
    }

    private void calculateFadedColor() {
        double x = model.getX();
        double y = model.getY();
        double dist = Math.sqrt(x * x + y * y) + 1;
        dist = dist * Math.pow(1 / dist, .1);
        if (dist > projection.getSphereRadius()) {
            dist = projection.getSphereRadius();
        }
        double colorScale = (dist / projection.getSphereRadius());
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
        Iterator it = this.getTreeNode().getClones().iterator();
        while (it.hasNext()) {
            TreeNode cur = (TreeNode) it.next();
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
        return projectedShape.getBounds2D();
    }

    public boolean hasAutoRaise() {
            return false;
        }

    public Point2D getPosition() {
        Rectangle2D bounds = projectedShape.getBounds2D();
        return new Point2D.Double(bounds.getX() + bounds.getWidth()/2, bounds.getY() + bounds.getHeight()/2);
    }

    public String toString() {
        return "Node ui for " + this.model.getName();
    }
}
