package ontorama.hyper.canvas;

/**
 * Base class for all paintable canvas items.
 */

import ontorama.hyper.model.Position3D;

import java.awt.Graphics2D;

public class CanvasItem {

    /**
     * method called to paint canvas item.
     */
    public void draw( Graphics2D g2d ) {
    }

    /**
     * Method called to find nearest canvas item to mouse pointer.
     *
     * If not over written this method shall let this
     * canvas item be ignored.
     */
    public boolean isNearestItem( double scrX, double scrY ) {
        return false;
    }
}