/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: EdgeRemovedEvent.java,v 1.2 2002/11/26 00:06:08 pbecker Exp $
 */
package ontorama.model.tree.events;

import ontorama.model.tree.Tree;
import ontorama.model.tree.TreeEdge;


public class TreeEdgeRemovedEvent extends TreeReducedEvent {
    private TreeEdge edge;

    public TreeEdgeRemovedEvent(Tree subject, TreeEdge edge) {
        super(subject);
        this.edge = edge;
    }

    public TreeEdge getEdge() {
        return edge;
    }
}
