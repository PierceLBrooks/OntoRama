package ontorama.model.tree.events;

import ontorama.model.tree.TreeNode;
import org.tockit.events.EventBroker;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 2, 2002
 * Time: 10:10:55 AM
 * To change this template use Options | File Templates.
 */
public class TreeNodeSelectedEvent extends TreeNodeEvent {
    public TreeNodeSelectedEvent(TreeNode subject, EventBroker eventBroker) {
        super(subject, eventBroker);
        eventBroker.processEvent(new NodeClonesRequestEvent(subject));
    }

}
