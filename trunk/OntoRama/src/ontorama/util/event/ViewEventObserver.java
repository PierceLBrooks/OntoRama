
package ontorama.util.event;

import ontorama.model.GraphNode;
import ontorama.graph.view.GraphView;

/**
 * ViewEventObserver
 */


public interface ViewEventObserver extends GraphView {
	/**
	 *
	 */
	public void query ( GraphNode node);
}
