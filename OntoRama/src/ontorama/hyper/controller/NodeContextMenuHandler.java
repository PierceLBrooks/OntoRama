/*
 * $id$
 */
package ontorama.hyper.controller;

import org.tockit.events.*;
import org.tockit.canvas.events.CanvasItemContextMenuRequestEvent;
import ontorama.hyper.view.simple.SimpleHyperView;
import ontorama.hyper.view.simple.HyperNodeView;
import ontorama.OntoramaConfig;
import ontorama.controller.NodeSelectedEvent;
import ontorama.ontologyConfig.EdgeTypeDisplayInfo;
import ontorama.model.EdgeType;
import ontorama.model.Node;
import ontorama.model.Graph;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Iterator;

/**
 *
 */
public class NodeContextMenuHandler implements EventListener {
    private SimpleHyperView simpleHyperView;
    private EventBroker eventBroker;

    public NodeContextMenuHandler(SimpleHyperView simpleHyperView, EventBroker eventBroker) {
        this.simpleHyperView = simpleHyperView;
        eventBroker.subscribe(this, CanvasItemContextMenuRequestEvent.class, HyperNodeView.class);
        this.eventBroker = eventBroker;
    }

    public void processEvent(Event e) {
        final HyperNodeView nodeView = (HyperNodeView) e.getSubject();
        CanvasItemContextMenuRequestEvent ev = (CanvasItemContextMenuRequestEvent) e;
        JPopupMenu menu = new JPopupMenu();
        JMenu newNodeMenu = new JMenu("Create new node");
        JMenuItem menuItem;
        List edgeTypes = OntoramaConfig.getEdgeTypesList();
        for (Iterator iterator = edgeTypes.iterator(); iterator.hasNext();) {
            final EdgeType edgeType = (EdgeType) iterator.next();
            EdgeTypeDisplayInfo displayInfo = OntoramaConfig.getEdgeDisplayInfo(edgeType);
            if(displayInfo.isDisplayInGraph()) {
                menuItem = new JMenuItem(edgeType.getName());
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        NewRelatedNodeCreator creator =
                                        new NewRelatedNodeCreator(simpleHyperView, nodeView.getGraphNode(), edgeType);
                        Node newNode = creator.createNewRelatedNode();
                        eventBroker.processEvent(new NodeSelectedEvent(newNode));
                    }
                });
                newNodeMenu.add(menuItem);
            }
        }
        menu.add(newNodeMenu);
        menuItem = new JMenuItem("Delete node");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Graph graph = simpleHyperView.getGraph();
                Node graphNode = nodeView.getGraphNode();
                graph.removeNode(graphNode);
                for (Iterator iterator = graphNode.getClones().iterator(); iterator.hasNext();) {
                    Node clone = (Node) iterator.next();
                    graph.removeNode(clone);
                }
            }
        });
        menu.add(menuItem);
        Point2D awtPos = ev.getAWTPosition();
        menu.show(simpleHyperView, (int) awtPos.getX(), (int) awtPos.getY());
    }
}