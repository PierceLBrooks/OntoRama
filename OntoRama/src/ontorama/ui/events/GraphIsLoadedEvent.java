package ontorama.ui.events;

import org.tockit.events.Event;

import ontorama.model.graph.Graph;

/**
 * Event sent each time new graph is created. 
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
