package ontorama.hyper.model;

/**
 * NodeObservable is part of the MVC parten for
 * notifing observers of a chainge in the model (Node).
 */


public interface PositionChaingedObservable {

    /**
     * Add an observer to model.
     */
    public void addPositionChaingedObserver( Object obj );

}