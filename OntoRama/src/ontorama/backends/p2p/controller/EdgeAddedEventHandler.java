package ontorama.backends.p2p.controller;

import org.tockit.events.EventBroker;
import org.tockit.events.Event;
import org.tockit.events.EventListener;
import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.model.P2PEdge;
import ontorama.backends.p2p.model.P2PEdgeImpl;
import ontorama.backends.p2p.model.P2PGraphImpl;
import ontorama.model.events.EdgeAddedEvent;
import ontorama.model.Graph;
import ontorama.model.Edge;
import ontorama.model.util.GraphModificationException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 16, 2002
 * Time: 10:48:59 AM
 * To change this template use Options | File Templates.
 */
public class EdgeAddedEventHandler implements EventListener {
    private EventBroker _eventBroker;
    private P2PBackend _p2pBackend;

    public EdgeAddedEventHandler (EventBroker eventBroker, P2PBackend backend) {
        _eventBroker = eventBroker;
        _p2pBackend = backend;
        _eventBroker.subscribe(this, EdgeAddedEvent.class, Graph.class);
    }

    public void processEvent(Event event) {
        EdgeAddedEvent edgeAddedEvent = (EdgeAddedEvent) event;
        Edge edge = edgeAddedEvent.getEdge();
        try {
            P2PEdge p2pEdge = new P2PEdgeImpl(edge.getFromNode(), edge.getToNode(), edge.getEdgeType());
            _p2pBackend.assertEdge(p2pEdge, edge.getCreatorUri());
        }
        catch (NoSuchRelationLinkException exc ) {
            /// @todo handle exception properly.
            exc.printStackTrace();
        }
        catch (GraphModificationException modExc) {
            /// @todo don't know what to do with exception here...
            modExc.printStackTrace();
            System.err.println("GraphModificationException: " + modExc.getMessage());
        }
    }
}
