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
import ontorama.model.Graph;
import ontorama.model.util.GraphModificationException;
import ontorama.backends.p2p.model.P2PGraph;
import ontorama.backends.p2p.model.P2PNode;
import ontorama.backends.p2p.model.P2PNodeImpl;
import ontorama.backends.p2p.P2PBackend;

public class NodeAddedEventHandler  implements EventListener{
    private EventBroker _eventBroker;
    private P2PBackend _p2pBackend;

    public NodeAddedEventHandler(EventBroker eventBroker, P2PBackend backend) {
        _eventBroker = eventBroker;
        _p2pBackend = backend;
        System.out.println("\n\nNodeAddedEventHandler, event broker = " + _eventBroker);
        _eventBroker.subscribe(this, NodeAddedEvent.class, Graph.class);
    }

    public void processEvent(Event event) {
        System.out.println("\n\nprocessEvent in NodeAddedEventHandler");
        NodeAddedEvent nodeAddedEvent = (NodeAddedEvent) event;
        Node node = nodeAddedEvent.getNode();
        P2PNode p2pNode = new P2PNodeImpl(node.getName(), node.getIdentifier());
        try {
            System.out.println("\n\nsending assertNode event to p2p backend for node " + p2pNode.getName());
            _p2pBackend.assertNode(p2pNode, node.getCreatorUri());
        }
        catch (GraphModificationException modExc) {
            /// @todo don't know what to do with exception here...
            modExc.printStackTrace();
            System.err.println("GraphModificationException: " + modExc.getMessage());
        }
    }
}
