/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: CanvasItemActivatedEvent.java,v 1.1 2002-08-01 04:53:37 johang Exp $
 */
package org.tockit.canvas.events;

import org.tockit.canvas.CanvasItem;

import java.awt.geom.Point2D;

/**
 * This event is emitted whenever an canvas item was activated (e.g. by double-clicking).
 */
public class CanvasItemActivatedEvent extends CanvasItemEventWithPosition {
    public CanvasItemActivatedEvent(CanvasItem item, Point2D canvasPosition, Point2D awtPosition) {
        super(item, canvasPosition, awtPosition);
    }
}
