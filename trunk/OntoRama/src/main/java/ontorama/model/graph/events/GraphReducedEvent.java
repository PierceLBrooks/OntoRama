/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: GraphReducedEvent.java,v 1.3 2002-12-03 07:22:21 nataliya Exp $
 */
package ontorama.model.graph.events;

import ontorama.model.graph.Graph;


public class GraphReducedEvent extends GraphChangedEvent {
    public GraphReducedEvent(Graph subject) {
        super(subject);
    }
}
