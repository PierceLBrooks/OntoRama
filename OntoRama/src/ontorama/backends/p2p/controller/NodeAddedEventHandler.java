/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 10, 2002
 * Time: 9:45:18 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.backends.p2p.controller;

import org.tockit.events.EventListener;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import ontorama.model.events.NodeAddedEvent;
import ontorama.model.Node;
import ontorama.model.util.GraphModificationException;
import ontorama.backends.p2p.model.P2PGraph;
import ontorama.backends.p2p.model.P2PNode;
import ontorama.backends.p2p.model.P2PNodeImpl;

public class NodeAddedEventHandler  implements EventListener{
    private EventBroker _eventBroker;
    private P2PGraph _p2pGraph;

    public NodeAddedEventHandler(EventBroker eventBroker, P2PGraph p2pGraph) {
        _eventBroker = eventBroker;
        _p2pGraph = p2pGraph;
        _eventBroker.subscribe(this, NodeAddedEvent.class, P2PGraph.class);
    }

    public void processEvent(Event event) {
        NodeAddedEvent nodeAddedEvent = (NodeAddedEvent) event;
        Node node = nodeAddedEvent.getNode();
        P2PNode p2pNode = new P2PNodeImpl(node.getName(), node.getIdentifier());
        try {
            _p2pGraph.assertNode(p2pNode, node.getCreatorUri());
        }
        catch (GraphModificationException modExc) {
            /// @todo don't know what to do with exception here...
            modExc.printStackTrace();
            System.err.println("GraphModificationException: " + modExc.getMessage());
        }
    }
}