package ontorama.hyper.model;

/**
 * NodeObservable is part of the MVC parten for
 * notifing observers of a chainge in the model (Node).
 */


public interface HyperNodeObservable {

    /**
     * Add an observer to model.
     */
    public void addHyperObserver( Object obj );

}