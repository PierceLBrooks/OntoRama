
package ontorama.tree.model;

/**
 * NodeObservable is part of the MVC parten for
 * notifing observers of a change in the model (Node).
 */


public interface OntoNodeObservable {

	/**
	 * Add an observer to model.
	 */
	public void addOntoObserver( Object obj );

}