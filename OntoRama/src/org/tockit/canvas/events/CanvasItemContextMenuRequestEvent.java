/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: CanvasItemContextMenuRequestEvent.java,v 1.1 2002-08-01 04:53:38 johang Exp $
 */
package org.tockit.canvas.events;

import org.tockit.canvas.CanvasItem;

import java.awt.geom.Point2D;

/**
 * Indicates that a context menu request was made on a canvas item.
 *
 * Typically this is mapped from events of the second mouse button on Windows and Unix
 * systems and can be used to open a context menu for an item.
 */
public class CanvasItemContextMenuRequestEvent extends CanvasItemEventWithPosition {
    public CanvasItemContextMenuRequestEvent(CanvasItem item, Point2D canvasPosition, Point2D awtPosition) {
        super(item, canvasPosition, awtPosition);
    }
}
