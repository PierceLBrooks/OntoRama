
package ontorama.model;

/**
 * NodeObserver is part of the MVC parten for updating
 * nodes in the current view.
 */


public interface NodeObserver {

	/**
	 * Method called to update observer.
	 */
	public void update( Object observer, Object observable );

}