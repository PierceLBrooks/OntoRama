/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: CanvasItem.java,v 1.1 2002-08-14 00:33:18 nataliya Exp $
 */
package org.tockit.canvas;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Abstract class to draw 2D graph items.
 *
 * CanvasItems can be put on a Canvas where they will have a z-order and
 * can be moved.
 */
public abstract class CanvasItem {
    /**
     * Draw method called to draw canvas item
     */
    public abstract void draw(Graphics2D g);

    /**
     * Returns true when the given point is on the item.
     */
    public abstract boolean containsPoint(Point2D point);

    /**
     * Returns the rectangular bounds of the canvas item.
     */
    abstract public Rectangle2D getCanvasBounds(Graphics2D g);

    /**
     * Returns true if the item should be raised on clicks.
     *
     * This is true per default, if a subclass should not be raised this method
     * can be overwritten returning false and Canvas will not raise it automatically.
     */
    public boolean hasAutoRaise() {
        return true;
    }
}