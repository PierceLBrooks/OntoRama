package ontorama.hyper.view.simple;

/**
 * Line view is responsible for drawing lines between
 * node views.
 *
 * LineView stors the from NodeView and to NodeView.
 */

import ontorama.hyper.canvas.CanvasItem;
import ontorama.hyper.view.simple.HyperNodeView;

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
        g2d.setColor( Color.lightGray );
        g2d.draw( new Line2D.Double(    from.getProjectedX(),
                                        from.getProjectedY(),
                                        to.getProjectedX(),
                                        to.getProjectedY() ) );
    }

    public String toString() {
        return  "Line from " + from + " ( " + from.getProjectedX() + ", " + from.getProjectedY() + " )" +
                " to " + to + " ( " + to.getProjectedX() + "," + to.getProjectedY() + ")";
    }
}