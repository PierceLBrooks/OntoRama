package ontorama.views.hyper.view;

/**
 * HyperEdgeView is responsible for drawing lines between
 * two node views, and displaying the image icon to
 * represent the relationship type between the two nodes.
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;

import ontorama.OntoramaConfig;
import ontorama.model.graph.EdgeType;

import org.tockit.canvas.CanvasItem;

public class HyperEdgeView extends CanvasItem {

    /**
     * Store from NodeView.
     */
    private final HyperNodeView from;

    /**
     * Store to NodeView.
     */
    private final HyperNodeView to;


    /**
     * Store the relation type for this edge
     */
    private final EdgeType relLink;

	private SphericalProjection projection = null;

    public HyperEdgeView(HyperNodeView from, HyperNodeView to, EdgeType relLink, Projection projection) {
        this.from = from;
        this.to = to;
        this.relLink = relLink;
    	this.projection = (SphericalProjection) projection;
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (!this.to.getVisible()) {
            return;
        }
        ImageIcon iconImg = OntoramaConfig.getEdgeDisplayInfo(relLink).getDisplayIcon();
        double x1 = from.getProjectedX();
        double y1 = from.getProjectedY();
        double x2 = to.getProjectedX();
        double y2 = to.getProjectedY();
        if (this.to.getHighlightEdge() == true) {
            g2d.setColor(Color.black);
            this.to.setHighlightEdge(false);
        } else {
            g2d.setColor(Color.lightGray);
        }
        double bendingStrength = 0.7;
        Point2D firstControlPoint = transform(
                from.getModel().getX() * bendingStrength + to.getModel().getX() * (1-bendingStrength),
                from.getModel().getY() * bendingStrength + to.getModel().getY() * (1-bendingStrength)
        );
        Point2D secondControlPoint = transform(
                to.getModel().getX() * bendingStrength + from.getModel().getX() * (1-bendingStrength),
                to.getModel().getY() * bendingStrength + from.getModel().getY() * (1-bendingStrength)
        );
        float[] points = new float[]{
            (float) firstControlPoint.getX(),
            (float) firstControlPoint.getY(),
            (float) secondControlPoint.getX(),
            (float) secondControlPoint.getY(),
            (float) to.getProjectedX(),
            (float) to.getProjectedY()
        };
        GeneralPath pathShape = new GeneralPath();
        pathShape.moveTo((float) from.getProjectedX(), (float) from.getProjectedY());
        pathShape.curveTo(points[0], points[1], points[2], points[3], points[4], points[5]);
        g2d.draw(pathShape);

        double xDiff = x2 - x1;
        double yDiff = y2 - y1;
        double nodeViewRadius = to.getViewRadius();
        double distanceBetweenTwoNodes = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        double viewScale = to.getScale();
        double imgW = iconImg.getIconWidth() * viewScale;
        double imgH = iconImg.getIconHeight() * viewScale;
        double imgHyp = Math.sqrt(imgW * imgW + imgH * imgH);
        double scale = (nodeViewRadius / 2 + imgHyp) / distanceBetweenTwoNodes;
        double imgX = x2 - (xDiff * scale) - imgW / 2;
        double imgY = y2 - (yDiff * scale) - imgH / 2;
        g2d.drawImage(iconImg.getImage(), (int) imgX, (int) imgY, (int) imgW, (int) imgH, iconImg.getImageObserver());
    }

    private Point2D transform(double x, double y) {
        double length = Math.sqrt(x * x + y * y + projection.getFocalDepth() * projection.getFocalDepth());
        double scale = projection.getSphereRadius() / length;
        return new Point2D.Double(scale * x, scale * y);
    }

    @Override
    public boolean containsPoint(Point2D point) {
        return false;
    }

    @Override
    public Rectangle2D getCanvasBounds(Graphics2D g) {
        return new Rectangle2D.Double(from.getProjectedX(),
                from.getProjectedY(),
                to.getProjectedX() - from.getProjectedX(),
                to.getProjectedY() - from.getProjectedY());
    }

    @Override
    public Point2D getPosition() {
        Point2D fromPos = from.getPosition();
        Point2D toPos = to.getPosition();
        return new Point2D.Double((fromPos.getX() + toPos.getX())/2, (fromPos.getY() + toPos.getY())/2);
    }

    @Override
    public String toString() {
        return "Line from " + from + " ( " + from.getProjectedX() + ", " + from.getProjectedY() + " )" +
                " to " + to + " ( " + to.getProjectedX() + "," + to.getProjectedY() + ")";
    }
}