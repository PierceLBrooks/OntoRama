/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: EdgeRemovedEvent.java,v 1.1 2002-10-04 03:50:45 pbecker Exp $
 */
package ontorama.model.events;

import ontorama.model.Graph;
import ontorama.model.Edge;

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
