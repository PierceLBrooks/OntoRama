/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: TestEventType1.java,v 1.1 2002-08-01 04:53:50 johang Exp $
 */
package org.tockit.events.tests;

import org.tockit.events.Event;

public class TestEventType1 implements Event {
    private Object source;

    public TestEventType1(Object source) {
        this.source = source;
    }

    public Object getSubject() {
        return source;
    }
}