/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: NodeRemovedEvent.java,v 1.2 2002-11-26 00:06:08 pbecker Exp $
 */
package ontorama.model.graph.events;


public class NodeRemovedEvent extends GraphReducedEvent {
    private ontorama.model.graph.Node node;

    public NodeRemovedEvent(ontorama.model.graph.Graph subject, ontorama.model.graph.Node node) {
        super(subject);
        this.node = node;
    }

    public ontorama.model.graph.Node getNode() {
        return node;
    }
}
