/*
 * $id$
 */
package ontorama.hyper.controller;

import org.tockit.events.*;
import org.tockit.canvas.events.CanvasItemContextMenuRequestEvent;
import ontorama.hyper.view.simple.SimpleHyperView;
import ontorama.hyper.view.simple.HyperNodeView;
import ontorama.OntoramaConfig;
import ontorama.model.EdgeType;

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

    public NodeContextMenuHandler(SimpleHyperView simpleHyperView, EventBroker eventBroker) {
        this.simpleHyperView = simpleHyperView;
        eventBroker.subscribe(this, CanvasItemContextMenuRequestEvent.class, HyperNodeView.class);
    }

    public void processEvent(Event e) {
        final HyperNodeView nodeView = (HyperNodeView) e.getSubject();
        CanvasItemContextMenuRequestEvent ev = (CanvasItemContextMenuRequestEvent) e;
        JPopupMenu menu = new JPopupMenu("Add relationship");
        JMenuItem menuItem;
        List edgeTypes = OntoramaConfig.getEdgeTypesList();
        for (Iterator iterator = edgeTypes.iterator(); iterator.hasNext();) {
            final EdgeType edgeType = (EdgeType) iterator.next();
            menuItem = new JMenuItem(edgeType.getName());
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    NewRelatedNodeCreator creator = new NewRelatedNodeCreator(simpleHyperView, nodeView.getGraphNode(), edgeType);
                }
            });
            menu.add(menuItem);
        }
        Point2D awtPos = ev.getAWTPosition();
        menu.show(simpleHyperView, (int) awtPos.getX(), (int) awtPos.getY());
    }
}
