package ontorama.hyper.model;

/**
 * NodeObserver is part of the MVC parten for updating
 * nodes in the current ui.
 */


public interface PositionChangedObserver {

    /**
     * Method called to update observer position.
     */
    public void positionChanged();

}