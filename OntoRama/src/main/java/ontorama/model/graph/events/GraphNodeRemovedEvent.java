/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: NodeRemovedEvent.java,v 1.2 2002/11/26 00:06:08 pbecker Exp $
 */
package ontorama.model.graph.events;

import ontorama.model.graph.Graph;
import ontorama.model.graph.Node;


public class GraphNodeRemovedEvent extends GraphReducedEvent {
    private Node node;

    public GraphNodeRemovedEvent(Graph subject, Node node) {
        super(subject);
        this.node = node;
    }

    public Node getNode() {
        return node;
    }
}
