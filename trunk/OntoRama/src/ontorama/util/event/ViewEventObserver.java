
package ontorama.util.event;

import ontorama.model.GraphNode;

/**
 * ViewEventObserver
 */


public interface ViewEventObserver {

	/**
	 *
	 */
	public void focus ( GraphNode node);
	
	/**
	 *
	 */
	public void toggleFold ( GraphNode node);
	
	/**
	 *
	 */
	public void query ( GraphNode node);
	
	

}
