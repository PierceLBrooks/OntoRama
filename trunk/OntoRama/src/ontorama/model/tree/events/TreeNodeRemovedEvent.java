/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: NodeRemovedEvent.java,v 1.2 2002/11/26 00:06:08 pbecker Exp $
 */
package ontorama.model.tree.events;

import ontorama.model.tree.Tree;
import ontorama.model.tree.TreeNode;


public class TreeNodeRemovedEvent extends TreeReducedEvent {
    private TreeNode node;

    public TreeNodeRemovedEvent(Tree subject, TreeNode node) {
        super(subject);
        this.node = node;
    }

    public TreeNode getNode() {
        return node;
    }
}
