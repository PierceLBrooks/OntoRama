package ontorama.hyper.canvas;

/**
 * FocusListener is part of the MVC parten for
 * notifing observers of a chainge in focus.
 */


public interface FocusChangedObservable {

    /**
     * Add an listener to model.
     */
    public void addFocusChangedObserver( Object obj );

}