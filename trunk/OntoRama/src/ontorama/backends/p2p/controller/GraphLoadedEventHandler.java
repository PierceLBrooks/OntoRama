package ontorama.backends.p2p.controller;

import org.tockit.events.EventBrokerListener;
import org.tockit.events.EventBroker;
import org.tockit.events.Event;
import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.model.P2PGraph;
import ontorama.model.graph.Graph;
import ontorama.model.graph.events.GraphLoadedEvent;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 13, 2002
 * Time: 12:24:45 PM
 * To change this template use Options | File Templates.
 */
public class GraphLoadedEventHandler implements EventBrokerListener {
    private EventBroker _eventBroker;
    private P2PBackend _p2pBackend;

    public GraphLoadedEventHandler (EventBroker eventBroker, P2PBackend backend) {
        _eventBroker = eventBroker;
        _p2pBackend = backend;
        _eventBroker.subscribe(this, GraphLoadedEvent.class, Graph.class);
    }

    public void processEvent(Event event) {
        GraphLoadedEvent graphLoadedEvent = (GraphLoadedEvent) event;
        Graph graph = (Graph) graphLoadedEvent.getSubject();
        //_p2pBackend.buildP2PGraph(graph);
        _p2pBackend.setP2PGraph((P2PGraph) graph);
    }
}
