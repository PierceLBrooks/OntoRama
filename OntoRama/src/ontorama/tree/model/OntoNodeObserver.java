
package ontorama.tree.model;

/**
 * NodeObserver is part of the MVC parten for updating
 * nodes in the current view.
 */


public interface OntoNodeObserver {

	/**
	 * Method called to update observer.
	 */
	public void update( Object obj );

}