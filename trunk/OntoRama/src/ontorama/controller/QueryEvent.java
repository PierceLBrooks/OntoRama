/*
 * Created by IntelliJ IDEA.
 * User: johang
 * Date: 1/08/2002
 * Time: 16:14:47
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.controller;

import ontorama.model.GraphNode;

public class QueryEvent extends NodeEvent {
    public QueryEvent(GraphNode subject) {
        super(subject);
    }
}
