package ontorama.backends.p2p.controller;

import org.tockit.events.EventListener;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.model.P2PNode;
import ontorama.backends.p2p.model.P2PNodeImpl;
import ontorama.model.events.NodeRemovedEvent;
import ontorama.model.Graph;
import ontorama.model.Node;
import ontorama.model.util.GraphModificationException;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 16, 2002
 * Time: 10:35:53 AM
 * To change this template use Options | File Templates.
 */
public class NodeRemovedEventHandler implements EventListener {
    private EventBroker _eventBroker;
    private P2PBackend _p2pBackend;

    public NodeRemovedEventHandler(EventBroker eventBroker, P2PBackend backend) {
        _eventBroker = eventBroker;
        _p2pBackend = backend;
        _eventBroker.subscribe(this, NodeRemovedEvent.class, Graph.class);
    }

    public void processEvent(Event event) {
        NodeRemovedEvent nodeRemovedEvent = (NodeRemovedEvent) event;
        Node node = nodeRemovedEvent.getNode();
        P2PNode p2pNode = new P2PNodeImpl(node.getName(), node.getIdentifier());
        try {
            _p2pBackend.rejectNode(p2pNode, node.getCreatorUri());
        }
        catch (GraphModificationException modExc) {
            /// @todo don't know what to do with exception here...
            modExc.printStackTrace();
            System.err.println("GraphModificationException: " + modExc.getMessage());
        }
    }
}
