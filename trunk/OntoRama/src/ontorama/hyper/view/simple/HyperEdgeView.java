package ontorama.hyper.view.simple;

/**
 * HyperEdgeView is responsible for drawing lines between
 * two node views, and displaying the image icon to
 * represent the relationship type between the two nodes.
 */

import ontorama.model.EdgeType;
import ontorama.OntoramaConfig;
import org.tockit.canvas.CanvasItem;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;

public class HyperEdgeView extends CanvasItem {

    /**
     * Store from NodeView.
     */
    private HyperNodeView from;

    /**
     * Store to NodeView.
     */
    private HyperNodeView to;


    /**
     * Store the relation type for this edge
     */
    private EdgeType relLink;

    public HyperEdgeView(HyperNodeView from, HyperNodeView to, EdgeType relLink) {
        this.from = from;
        this.to = to;
        this.relLink = relLink;
    }

    public void draw(Graphics2D g2d) {
        if (!this.to.getVisible()) {
            return;
        }
        ImageIcon iconImg = OntoramaConfig.getEdgeDisplayInfo(relLink).getDisplayIcon();
        double x1 = from.getProjectedX();
        double y1 = from.getProjectedY();
        double x2 = to.getProjectedX();
        double y2 = to.getProjectedY();
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
        if (this.to.getHighlightEdge() == true) {
            g2d.setColor(Color.black);
            this.to.setHighlightEdge(false);
        } else {
            g2d.setColor(Color.lightGray);
        }
        if (OntoramaConfig.FOUNTAINS) {
            double firstControlPos = 29 / 30.0;
            double secondControlPos = 1 / 30.0;
            float[] points = new float[]{
                (float) (from.getProjectedX() * firstControlPos + to.getProjectedX() * (1-firstControlPos)) * 1.2F,
                (float) (from.getProjectedY() * firstControlPos + to.getProjectedY() * (1-firstControlPos)) * 1.2F,
                (float) (from.getProjectedX() * secondControlPos + to.getProjectedX() * (1-secondControlPos)) * 1.2F,
                (float) (from.getProjectedY() * secondControlPos + to.getProjectedY() * (1-secondControlPos)) * 1.2F,
                (float) to.getProjectedX(),
                (float) to.getProjectedY()
            };
            GeneralPath pathShape = new GeneralPath();
            pathShape.moveTo((float) from.getProjectedX(), (float) from.getProjectedY());
            pathShape.curveTo(points[0], points[1], points[2], points[3], points[4], points[5]);
            g2d.draw(pathShape);
        } else {
            g2d.draw(new Line2D.Double(from.getProjectedX(),from.getProjectedY(),to.getProjectedX(),to.getProjectedY()));
        }

        g2d.drawImage(iconImg.getImage(), (int) imgX, (int) imgY, (int) imgW, (int) imgH, iconImg.getImageObserver());
    }

    public boolean containsPoint(Point2D point) {
        return false;
    }

    public Rectangle2D getCanvasBounds(Graphics2D g) {
        return new Rectangle2D.Double(from.getProjectedX(),
                from.getProjectedY(),
                to.getProjectedX() - from.getProjectedX(),
                to.getProjectedY() - from.getProjectedY());
    }

    public Point2D getPosition() {
        Point2D fromPos = from.getPosition();
        Point2D toPos = to.getPosition();
        return new Point2D.Double((fromPos.getX() + toPos.getX())/2, (fromPos.getY() + toPos.getY())/2);
    }

   public String toString() {
        return "Line from " + from + " ( " + from.getProjectedX() + ", " + from.getProjectedY() + " )" +
                " to " + to + " ( " + to.getProjectedX() + "," + to.getProjectedY() + ")";
    }
}