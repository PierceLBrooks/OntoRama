package ontorama.hyper.model;

/**
 * NodeObserver is part of the MVC parten for updating
 * nodes in the current view.
 */


public interface PositionChaingedObserver {

    /**
     * Method called to update observer position.
     */
    public void positionUpdate( double x, double y);

}