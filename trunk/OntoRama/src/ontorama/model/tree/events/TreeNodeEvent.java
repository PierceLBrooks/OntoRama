package ontorama.model.tree.events;

import ontorama.model.tree.TreeNode;
import org.tockit.events.Event;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 2, 2002
 * Time: 10:08:06 AM
 * To change this template use Options | File Templates.
 */
public class TreeNodeEvent implements Event {

    protected TreeNode subject;

    public TreeNodeEvent(TreeNode subject) {
        this.subject = subject;
    }

    public Object getSubject() {
        return subject;
    }

}
