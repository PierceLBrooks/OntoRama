package ontorama.util.event;

import ontorama.graph.view.GraphView;
import ontorama.model.GraphNode;

/**
 * ViewEventObserver
 */


public interface ViewEventObserver extends GraphView {
    /**
     *
     */
    public void query(GraphNode node);
}
