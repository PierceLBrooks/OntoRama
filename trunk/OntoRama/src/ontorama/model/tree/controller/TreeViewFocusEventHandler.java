package ontorama.model.tree.controller;

import ontorama.model.tree.view.TreeView;
import ontorama.model.tree.events.TreeNodeSelectedEvent;
import ontorama.model.tree.TreeNode;
import org.tockit.events.EventBroker;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 2, 2002
 * Time: 10:13:22 AM
 * To change this template use Options | File Templates.
 */
public class TreeViewFocusEventHandler implements EventBrokerListener {
    private TreeView _treeView;

    public TreeViewFocusEventHandler (EventBroker eventBroker, TreeView treeView) {
        _treeView = treeView;
        eventBroker.subscribe(this, TreeNodeSelectedEvent.class, TreeNode.class);
    }

    public void processEvent(Event e) {
        TreeNode node = (TreeNode) e.getSubject();
        //System.out.println("treeView  = " + _treeView);
        //System.out.println("focus on " + node.getGraphNode().getName());
        _treeView.focus(node);
    }
}
