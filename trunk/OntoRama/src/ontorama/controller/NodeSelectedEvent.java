/*
 * Created by IntelliJ IDEA.
 * User: johang
 * Date: 1/08/2002
 * Time: 16:14:47
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.controller;

import org.tockit.events.Event;
import ontorama.model.GraphNode;

public class NodeSelectedEvent implements Event {
    private GraphNode subject;

    public NodeSelectedEvent(GraphNode subject) {
        this.subject = subject;
    }

    public Object getSubject() {
        return subject;
    }
}
