/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: CanvasItemEvent.java,v 1.1 2002-08-01 04:53:39 johang Exp $
 */
package org.tockit.canvas.events;

import org.tockit.canvas.CanvasItem;
import org.tockit.events.Event;

/**
 * A generic class for all canvas item related events.
 */
abstract public class CanvasItemEvent implements Event {
    private CanvasItem item;

    /**
     * Creates a new event for an item.
     */
    public CanvasItemEvent(CanvasItem item) {
        this.item = item;
    }

    /**
     * Returns the item attached to the event.
     */
    public CanvasItem getItem() {
        return item;
    }

    /**
     * Implements Event.getSource() by returning the item attached to this event.
     */
    public Object getSubject() {
        return item;
    }
}
