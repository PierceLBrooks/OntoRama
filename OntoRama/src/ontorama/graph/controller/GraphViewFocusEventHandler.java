/*
 * Created by IntelliJ IDEA.
 * User: johang
 * Date: 1/08/2002
 * Time: 15:49:15
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.graph.controller;

import ontorama.controller.NodeSelectedEvent;
import ontorama.graph.view.GraphView;
import ontorama.model.GraphNode;
import ontorama.model.Graph;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventListener;

public class GraphViewFocusEventHandler implements EventListener {
    private GraphView graphView;
    private EventBroker eventBroker;

    public GraphViewFocusEventHandler(EventBroker eventBroker, GraphView graphView) {
        this.graphView = graphView;
        this.eventBroker = eventBroker;
        eventBroker.subscribe(this, NodeSelectedEvent.class, GraphNode.class);
    }

    public void processEvent(Event e) {
        GraphNode node = (GraphNode) e.getSubject();
//        System.out.println("graphview = " + graphView);


        Graph graph = this.graphView.getGraph();
        if (graph != null) {
            if (graph.nodeIsInCurrentGivenBranch(graph.getRootNode(), node)) {
                System.out.println("node is in current displayed branch");
            }
            else {
                System.out.println("node is NOT in current displayed branch");
                new GraphRootResetEvent(node);
            }
        }
        this.graphView.focus(node);
    }
}
