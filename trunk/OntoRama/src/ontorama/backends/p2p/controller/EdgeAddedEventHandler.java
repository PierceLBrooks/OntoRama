package ontorama.backends.p2p.controller;

import ontorama.backends.p2p.P2PBackendImpl;
import ontorama.backends.p2p.model.P2PEdge;
import ontorama.model.graph.Graph;
import ontorama.model.graph.GraphModificationException;
import ontorama.model.graph.events.GraphEdgeAddedEvent;
import ontorama.ontotools.NoSuchRelationLinkException;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 16, 2002
 * Time: 10:48:59 AM
 * To change this template use Options | File Templates.
 */
public class EdgeAddedEventHandler implements EventBrokerListener {
    private EventBroker _eventBroker;
    private P2PBackendImpl _p2pBackend;

    public EdgeAddedEventHandler (EventBroker eventBroker, P2PBackendImpl backend) {
        _eventBroker = eventBroker;
        _p2pBackend = backend;
        _eventBroker.subscribe(this, GraphEdgeAddedEvent.class, Graph.class);
    }

    public void processEvent(Event event) {
        GraphEdgeAddedEvent edgeAddedEvent = (GraphEdgeAddedEvent) event;
        P2PEdge p2pEdge = (P2PEdge) edgeAddedEvent.getEdge();
        System.out.println("\np2p EdgeAddedEventHandler for edge " + p2pEdge);
        try {
//        	P2PNode fromNode = (P2PNode) OntoramaConfig.getBackend().createNode(edge.getFromNode().getName(), edge.getFromNode().getIdentifier());
//        	P2PNode toNode = (P2PNode) OntoramaConfig.getBackend().createNode(edge.getToNode().getName(), edge.getToNode().getIdentifier());
//            P2PEdge p2pEdge = (P2PEdge) OntoramaConfig.getBackend().createEdge(fromNode, toNode, edge.getEdgeType());
            _p2pBackend.assertEdge(p2pEdge, p2pEdge.getCreatorUri());
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
