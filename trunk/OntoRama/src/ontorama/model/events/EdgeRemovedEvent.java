/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: EdgeRemovedEvent.java,v 1.2 2002-11-18 04:28:45 nataliya Exp $
 */
package ontorama.model.events;

import ontorama.model.Edge;
import ontorama.model.Graph;

public class EdgeRemovedEvent extends GraphReducedEvent {
    private Edge edge;

    public EdgeRemovedEvent(Graph subject, Edge edge) {
        super(subject);
        this.edge = edge;
    }

    public Edge getEdge() {
        return edge;
    }
}
