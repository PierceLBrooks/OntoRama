
package ontorama.model;

/**
 * NodeObservable is part of the MVC parten for
 * notifing observers of a chainge in the model (Node).
 */


public interface NodeObservable {

	/**
	 * Add an observer to model.
	 */
	public void addObserver( Object obj );

}