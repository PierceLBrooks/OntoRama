package ontorama.backends.p2p.controller;

import ontorama.OntoramaConfig;
import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.model.P2PEdge;
import ontorama.model.graph.Edge;
import ontorama.model.graph.Graph;
import ontorama.model.graph.GraphModificationException;
import ontorama.model.graph.events.GraphEdgeRemovedEvent;
import ontorama.ontotools.NoSuchRelationLinkException;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 16, 2002
 * Time: 12:14:15 PM
 * To change this template use Options | File Templates.
 */
public class EdgeRemovedEventHandler implements EventBrokerListener {
    private EventBroker _eventBroker;
    private P2PBackend _p2pBackend;

    public EdgeRemovedEventHandler (EventBroker eventBroker, P2PBackend backend) {
        _eventBroker = eventBroker;
        _p2pBackend = backend;
        _eventBroker.subscribe(this, GraphEdgeRemovedEvent.class, Graph.class);
    }

    public void processEvent(Event event) {
        GraphEdgeRemovedEvent edgeRemovedEvent = (GraphEdgeRemovedEvent) event;
        Edge edge = edgeRemovedEvent.getEdge();
    	System.out.println("p2p EdgeRemovedEventHandler for edge " + edge);        
        try {
            P2PEdge p2pEdge = (P2PEdge) OntoramaConfig.getBackend().createEdge(edge.getFromNode(), edge.getToNode(), edge.getEdgeType());
            _p2pBackend.rejectEdge(p2pEdge, edge.getCreatorUri());
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
