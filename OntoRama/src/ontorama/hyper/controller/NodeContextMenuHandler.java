/*
 * $id$
 */
package ontorama.hyper.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import ontorama.OntoramaConfig;
import ontorama.controller.NodeSelectedEvent;
import ontorama.hyper.view.simple.HyperNodeView;
import ontorama.hyper.view.simple.SimpleHyperView;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Graph;
import ontorama.model.graph.Node;
import ontorama.ontologyConfig.EdgeTypeDisplayInfo;
import org.tockit.canvas.events.CanvasItemContextMenuRequestEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventListener;

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
            final ontorama.model.graph.EdgeType edgeType = (ontorama.model.graph.EdgeType) iterator.next();
            EdgeTypeDisplayInfo displayInfo = OntoramaConfig.getEdgeDisplayInfo(edgeType);
            if(displayInfo.isDisplayInGraph()) {
                menuItem = new JMenuItem(edgeType.getName());
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        NewRelatedNodeCreator creator =
                                        new NewRelatedNodeCreator(simpleHyperView, nodeView.getGraphNode(), edgeType);
                        ontorama.model.graph.Node newNode = creator.createNewRelatedNode();
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
                ontorama.model.graph.Graph graph = simpleHyperView.getGraph();
                ontorama.model.graph.Node graphNode = nodeView.getGraphNode();
                graph.removeNode(graphNode);
                for (Iterator iterator = graphNode.getClones().iterator(); iterator.hasNext();) {
                    ontorama.model.graph.Node clone = (ontorama.model.graph.Node) iterator.next();
                    graph.removeNode(clone);
                }
            }
        });
        menu.add(menuItem);
        Point2D awtPos = ev.getAWTPosition();
        menu.show(simpleHyperView, (int) awtPos.getX(), (int) awtPos.getY());
    }
}
