package ontorama.hyper.model;

/**
 * PositionChangedObservable is part of the MVC pattern for
 * notifing observers of a chainge in the model (Node).
 */


public interface PositionChangedObservable {

    /**
     * Add an observer to model.
     */
    public void addPositionChangedObserver(PositionChangedObserver observer);

}