/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: GraphExtendedEvent.java,v 1.2 2002-11-24 23:42:21 nataliya Exp $
 */
package ontorama.model.events;

import ontorama.model.graph.Graph;

public class GraphExtendedEvent extends GraphChangedEvent {
    public GraphExtendedEvent(ontorama.model.graph.Graph subject) {
        super(subject);
    }
}
