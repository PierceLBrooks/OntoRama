package ontorama.model.graph.events;

import org.tockit.events.Event;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 24/10/2002
 * Time: 09:15:52
 * To change this template use Options | File Templates.
 */
public class GraphLoadedEvent implements Event {
    private ontorama.model.graph.Graph graph;

    public GraphLoadedEvent(ontorama.model.graph.Graph subject) {
        this.graph = subject;
    }

    public Object getSubject() {
        return graph;
    }
}
