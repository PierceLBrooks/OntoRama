package ontorama.util.event;

/**
 * ViewEventObservable is responsible for notifing observers of an event
 * For example: events are: single mouse click, double mouse click, etc.
 */


public interface ViewEventObservable {

    /**
     * Add an observer to model.
     */
    public void addObserver(Object obj);

}
