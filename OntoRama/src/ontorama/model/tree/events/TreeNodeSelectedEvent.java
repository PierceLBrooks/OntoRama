package ontorama.model.tree.events;

import ontorama.model.tree.TreeNode;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 2, 2002
 * Time: 10:10:55 AM
 * To change this template use Options | File Templates.
 */
public class TreeNodeSelectedEvent extends TreeNodeEvent {
    public TreeNodeSelectedEvent(TreeNode subject) {
        super(subject);
    }

}
