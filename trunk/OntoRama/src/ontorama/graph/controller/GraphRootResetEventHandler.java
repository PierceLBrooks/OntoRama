/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Aug 12, 2002
 * Time: 1:56:43 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.graph.controller;

import org.tockit.events.*;
import ontorama.graph.view.GraphView;
import ontorama.model.GraphNode;
import ontorama.model.Graph;

public class GraphRootResetEventHandler implements EventListener {
    private GraphView graphView;

    public GraphRootResetEventHandler(EventBroker eventBroker, GraphView graphView) {
        this.graphView = graphView;
        eventBroker.subscribe(this, GraphRootResetEvent.class,  GraphNode.class);
    }

    public void processEvent(Event e) {

        GraphNode node = (GraphNode) e.getSubject();
        System.out.println("GraphRootResetEventHandler processEvent for node " + node.getName());

        Graph graph = this.graphView.getGraph();
        GraphNode branchRootNode = graph.getBranchRootForNode(node);
        System.out.println("...node is not in current displayed branch, setting root to " + branchRootNode);

        this.graphView.setGraph(graph);
        graph.setRoot(branchRootNode);
        this.graphView.repaint();
        this.graphView.focus(node);
    }
}
