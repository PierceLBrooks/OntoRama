/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: GraphChangedEvent.java,v 1.2 2002-11-26 00:06:08 pbecker Exp $
 */
package ontorama.model.graph.events;

import org.tockit.events.Event;

public class GraphChangedEvent implements Event {
    private ontorama.model.graph.Graph graph;

    public GraphChangedEvent(ontorama.model.graph.Graph subject) {
        this.graph = subject;
    }

    public Object getSubject() {
        return graph;
    }

    public ontorama.model.graph.Graph getGraph() {
        return graph;
    }
}
