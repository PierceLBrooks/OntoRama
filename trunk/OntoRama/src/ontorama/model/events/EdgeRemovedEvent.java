/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: EdgeRemovedEvent.java,v 1.3 2002-11-24 23:42:21 nataliya Exp $
 */
package ontorama.model.events;

import ontorama.model.graph.Edge;
import ontorama.model.graph.Graph;

public class EdgeRemovedEvent extends GraphReducedEvent {
    private ontorama.model.graph.Edge edge;

    public EdgeRemovedEvent(ontorama.model.graph.Graph subject, ontorama.model.graph.Edge edge) {
        super(subject);
        this.edge = edge;
    }

    public ontorama.model.graph.Edge getEdge() {
        return edge;
    }
}
