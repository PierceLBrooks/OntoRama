/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: EdgeRemovedEvent.java,v 1.2 2002/11/26 00:06:08 pbecker Exp $
 */
package ontorama.model.graph.events;

import ontorama.model.graph.Edge;
import ontorama.model.graph.Graph;

public class GraphEdgeRemovedEvent extends GraphReducedEvent {
    private Edge edge;

    public GraphEdgeRemovedEvent(Graph subject, Edge edge) {
        super(subject);
        this.edge = edge;
    }

    public Edge getEdge() {
        return edge;
    }
}
