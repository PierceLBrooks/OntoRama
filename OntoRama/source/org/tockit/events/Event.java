/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: Event.java,v 1.1 2002-08-14 00:33:31 nataliya Exp $
 */
package org.tockit.events;

/**
 * A generic event interface.
 *
 * This is used in the EventBroker.
 */
public interface Event {
    /**
     * The subject of the event, it must not be null.
     */
    Object getSubject();
}
