/*
 * $id$
 */
package ontorama.views.hyper.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import ontorama.OntoramaConfig;
import ontorama.conf.EdgeTypeDisplayInfo;
import ontorama.model.graph.EdgeType;
import ontorama.model.tree.Tree;
import ontorama.model.tree.TreeModificationException;
import ontorama.model.tree.TreeNode;
import ontorama.model.tree.events.TreeNodeSelectedEvent;
import ontorama.ui.ErrorDialog;
import ontorama.ui.OntoRamaApp;
import ontorama.views.hyper.view.HyperNodeView;
import ontorama.views.hyper.view.SimpleHyperView;

import org.tockit.canvas.events.CanvasItemContextMenuRequestEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

/**
 *
 */
public class NodeContextMenuHandler implements EventBrokerListener {
    private final SimpleHyperView _simpleHyperView;
    private final EventBroker _eventBroker;

    public NodeContextMenuHandler(SimpleHyperView simpleHyperView, EventBroker eventBroker) {
        this._simpleHyperView = simpleHyperView;
        eventBroker.subscribe(this, CanvasItemContextMenuRequestEvent.class, HyperNodeView.class);
        this._eventBroker = eventBroker;
    }

    public void processEvent(Event e) {
        final HyperNodeView nodeView = (HyperNodeView) e.getSubject();
        CanvasItemContextMenuRequestEvent ev = (CanvasItemContextMenuRequestEvent) e;
        JPopupMenu menu = new JPopupMenu();
        JMenu newNodeMenu = new JMenu("Create new node");
        JMenuItem menuItem;
        List<EdgeType> edgeTypes = OntoramaConfig.getEdgeTypesList();
        for (Iterator<EdgeType> iterator = edgeTypes.iterator(); iterator.hasNext();) {
            final EdgeType edgeType = iterator.next();
            EdgeTypeDisplayInfo displayInfo = OntoramaConfig.getEdgeDisplayInfo(edgeType);
            if(displayInfo.isDisplayInGraph()) {
                menuItem = new JMenuItem(edgeType.getName());
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        NewRelatedNodeCreator creator =
                                        new NewRelatedNodeCreator(_simpleHyperView, nodeView.getTreeNode(), edgeType);
						try {
							TreeNode newNode = creator.createNewRelatedNode();
							_eventBroker.processEvent(new TreeNodeSelectedEvent(newNode, _eventBroker));
						}
						catch (TreeModificationException e1) {
							e1.printStackTrace();
							ErrorDialog.showError(OntoRamaApp.getMainFrame(), "Error adding new node", e1.getMessage());
						}
                    }
                });
                newNodeMenu.add(menuItem);
            }
        }
        menu.add(newNodeMenu);
        menuItem = new JMenuItem("Delete node");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Tree tree = _simpleHyperView.getTree();
                TreeNode treeNode = nodeView.getTreeNode();
                try {
                    tree.removeNode(treeNode);
                }
                catch (TreeModificationException exc) {
                	ErrorDialog.showError(OntoRamaApp.getMainFrame(), exc, "Error", exc.getMessage());
                }
            }
        });
        menu.add(menuItem);
        Point2D awtPos = ev.getAWTPosition();
        menu.show(_simpleHyperView, (int) awtPos.getX(), (int) awtPos.getY());
    }
}
