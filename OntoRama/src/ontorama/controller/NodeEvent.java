/*
 * Created by IntelliJ IDEA.
 * User: johang
 * Date: 1/08/2002
 * Time: 17:19:01
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.controller;

import org.tockit.events.Event;
import ontorama.model.GraphNode;

public class NodeEvent implements Event {
    protected GraphNode subject;

    public NodeEvent(GraphNode subject) {
        this.subject = subject;
    }

    public Object getSubject() {
        return subject;
    }
}
