package ontorama.hyper.view.simple;

/**
 * Line view is responsible for drawing lines between
 * node views.
 *
 * LineView stors the from NodeView and to NodeView.
 */

import ontorama.hyper.canvas.CanvasItem;
import ontorama.hyper.view.simple.HyperNodeView;

import ontorama.ontologyConfig.RelationLinkDetails;

import javax.swing.ImageIcon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class HyperEdgeView extends CanvasItem{

    /**
     * Store from NodeView.
     */
    private HyperNodeView from;

    /**
     * Store to NodeView.
     */
    private HyperNodeView to;

    /**
     *
     */
    int relLink;

    public HyperEdgeView( HyperNodeView from, HyperNodeView to, int relLink ) {
        this.from = from;
        this.to = to;
        this.relLink = relLink;
    }

    public void draw ( Graphics2D  g2d ) {
        RelationLinkDetails relLinkDetails = ontorama.OntoramaConfig.getRelationLinkDetails(relLink);
        ImageIcon iconImg = relLinkDetails.getDisplayIcon();
        double x1 = from.getProjectedX();
        double y1 = from.getProjectedY();
        double x2 = to.getProjectedX();
        double y2 = to.getProjectedY();
        double xDiff = x2 - x1;
        double yDiff = y2 - y1;
        double nodeViewRadius = to.getViewRadius();
        double distanceBetweenTwoNodes = Math.sqrt( ( x2 - x1) * ( x2 - x1) + ( y2 - y1) * ( y2 - y1) );
        double viewScale = to.getScale();
        double imgW = iconImg.getIconWidth() * viewScale;
        double imgH = iconImg.getIconHeight() * viewScale;
        double imgHyp = Math.sqrt( imgW * imgW + imgH * imgH);
        double scale = (nodeViewRadius + imgHyp) / distanceBetweenTwoNodes;
        double imgX = x2 - ( xDiff * scale) - imgW/2;
        double imgY = y2 - ( yDiff * scale) - imgH/2;

        g2d.setColor( Color.lightGray );
        g2d.draw( new Line2D.Double(    from.getProjectedX(),
                                        from.getProjectedY(),
                                        to.getProjectedX(),
                                        to.getProjectedY() ) );

        g2d.drawImage( iconImg.getImage(), (int)imgX, (int)imgY, (int)imgW, (int)imgH, iconImg.getImageObserver());
    }

    public String toString() {
        return  "Line from " + from + " ( " + from.getProjectedX() + ", " + from.getProjectedY() + " )" +
                " to " + to + " ( " + to.getProjectedX() + "," + to.getProjectedY() + ")";
    }
}