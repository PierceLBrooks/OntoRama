/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: EdgeAddedEvent.java,v 1.2 2002-11-26 00:06:08 pbecker Exp $
 */
package ontorama.model.graph.events;


public class EdgeAddedEvent extends GraphExtendedEvent {
    private ontorama.model.graph.Edge edge;

    public EdgeAddedEvent(ontorama.model.graph.Graph subject, ontorama.model.graph.Edge edge) {
        super(subject);
        this.edge = edge;
    }

    public ontorama.model.graph.Edge getEdge() {
        return edge;
    }
}
