package ontorama.hyper.view.simple;

/**
 * HyperEdgeView is responsible for drawing lines between
 * two node views, and displaying the image icon to
 * represent the relationship type between the two nodes.
 */

import ontorama.ontologyConfig.RelationLinkDetails;
import org.tockit.canvas.CanvasItem;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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
    private int relLink;

    public HyperEdgeView(HyperNodeView from, HyperNodeView to, int relLink) {
        this.from = from;
        this.to = to;
        this.relLink = relLink;
    }

    public void draw(Graphics2D g2d) {
        //System.out.println("HyperEdgeView draw: from = " + from + ", to = " + to + ", relLink = " + relLink);
        if (!this.to.getVisible()) {
            return;
        }
        RelationLinkDetails relLinkDetails = ontorama.OntoramaConfig.getRelationLinkDetails(relLink);
        ImageIcon iconImg = relLinkDetails.getDisplayIcon();
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
        g2d.draw(new Line2D.Double(from.getProjectedX(),
                from.getProjectedY(),
                to.getProjectedX(),
                to.getProjectedY()));

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

    public String toString() {
        return "Line from " + from + " ( " + from.getProjectedX() + ", " + from.getProjectedY() + " )" +
                " to " + to + " ( " + to.getProjectedX() + "," + to.getProjectedY() + ")";
    }
}