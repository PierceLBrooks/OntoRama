/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: StandardEvent.java,v 1.1 2002-08-01 04:53:47 johang Exp $
 */
package org.tockit.events;

public class StandardEvent implements Event {
    private Object subject;

    public StandardEvent(Object subject) {
        this.subject = subject;
    }

    public Object getSubject() {
        return subject;
    }
}
