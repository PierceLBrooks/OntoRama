/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: GraphChangedEvent.java,v 1.3 2002/12/02 05:04:01 nataliya Exp $
 */
package ontorama.model.tree.events;

import ontorama.model.tree.Tree;
import org.tockit.events.Event;

public class TreeChangedEvent implements Event {
    private Tree tree;

    public TreeChangedEvent(Tree subject) {
        this.tree = subject;
    }

    public Object getSubject() {
        return tree;
    }

    public Tree getTree() {
        return tree;
    }
}
