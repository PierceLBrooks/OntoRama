/*
 * Created by IntelliJ IDEA.
 * User: johang
 * Date: 1/08/2002
 * Time: 17:19:01
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.controller;

import ontorama.model.graph.Node;
import org.tockit.events.Event;

public class NodeEvent implements Event {
    protected ontorama.model.graph.Node subject;

    public NodeEvent(ontorama.model.graph.Node subject) {
        this.subject = subject;
    }

    public Object getSubject() {
        return subject;
    }
}
