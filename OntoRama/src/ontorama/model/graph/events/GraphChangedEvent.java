/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: GraphChangedEvent.java,v 1.3 2002-12-02 05:04:01 nataliya Exp $
 */
package ontorama.model.graph.events;

import ontorama.model.graph.Graph;
import org.tockit.events.Event;

public class GraphChangedEvent implements Event {
    private Graph graph;

    public GraphChangedEvent(Graph subject) {
        this.graph = subject;
    }

    public Object getSubject() {
        return graph;
    }

    public Graph getGraph() {
        return graph;
    }
}
