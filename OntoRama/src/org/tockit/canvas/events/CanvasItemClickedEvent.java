/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: CanvasItemClickedEvent.java,v 1.1 2002-08-01 04:53:38 johang Exp $
 */
package org.tockit.canvas.events;

import org.tockit.canvas.CanvasItem;

import java.awt.geom.Point2D;

/**
 * This event is sent when a canvas item was selected somehow (e.g. clicked once).
 */
public class CanvasItemClickedEvent extends CanvasItemEventWithPosition {
    public CanvasItemClickedEvent(CanvasItem item, Point2D canvasPosition, Point2D awtPosition) {
        super(item, canvasPosition, awtPosition);
    }
}
