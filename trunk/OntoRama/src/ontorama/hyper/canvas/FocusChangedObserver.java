package ontorama.hyper.canvas;

/**
 * FocusChanged is part of the MVC parten for updating
 * which node has focus.
 *
 * This call centers the node into the middle.
 */


public interface FocusChangedObserver {

    /**
     * Method called to update observer of change of focus.
     */
    public void focusChanged( Object obj );

}