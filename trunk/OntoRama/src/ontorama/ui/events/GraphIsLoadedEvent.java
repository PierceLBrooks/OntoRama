package ontorama.ui.events;

import org.tockit.events.Event;

import ontorama.model.graph.Graph;

/**
 * Event sent each time new graph is created. 
 * This event is used to tell views that they need to update  and
 * is also used by backends to figure out when they got a new graph -
 * we wouldn't need this if we didn't use threads.
 */
public class GraphIsLoadedEvent implements Event {
    protected Graph subject;

    public GraphIsLoadedEvent(Graph subject) {
        this.subject = subject;
    }

    public Object getSubject() {
        return subject;
    }
}
