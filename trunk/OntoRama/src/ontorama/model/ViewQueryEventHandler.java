/*
 * Created by IntelliJ IDEA.
 * User: johang
 * Date: 1/08/2002
 * Time: 15:49:15
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model;

import ontorama.model.ViewQuery;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeImpl;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class ViewQueryEventHandler implements EventBrokerListener {
    private ViewQuery query;

    public ViewQueryEventHandler(EventBroker eventBroker, ViewQuery query) {
        this.query = query;
        eventBroker.subscribe(this, QueryEvent.class, GeneralNode.class);
    }

    public void processEvent(Event e) {
        GeneralNode node = (GeneralNode) e.getSubject();
        System.out.println("\n\nGraphViewQueryEventHandler for query = " + this.query);
        this.query.query(node.getName());
    }
}
