/*
 * Created by IntelliJ IDEA.
 * User: johang
 * Date: 1/08/2002
 * Time: 16:14:47
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.controller;

import ontorama.model.Node;

public class NodeSelectedEvent extends NodeEvent {
    public NodeSelectedEvent(Node subject) {
        super(subject);
    }
}
