package ontorama.hyper.model;

/**
 * NodeObserver is part of the MVC parten for updating
 * nodes in the current view.
 */


public interface HyperNodeObserver {

    /**
     * Method called to update observer.
     */
    public void update( Object obj );

}