/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: GraphReducedEvent.java,v 1.1 2002-11-24 23:46:32 nataliya Exp $
 */
package ontorama.model.graph.events;

import ontorama.model.graph.Graph;

public class GraphReducedEvent extends GraphChangedEvent {
    public GraphReducedEvent(ontorama.model.graph.Graph subject) {
        super(subject);
    }
}
