/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: NodeAddedEvent.java,v 1.1 2002-10-04 03:50:47 pbecker Exp $
 */
package ontorama.model.events;

import ontorama.model.Graph;
import ontorama.model.Node;

public class NodeAddedEvent extends GraphExtendedEvent {
    private Node node;

    public NodeAddedEvent(Graph subject, Node node) {
        super(subject);
        this.node = node;
    }

    public Node getNode() {
        return node;
    }
}
