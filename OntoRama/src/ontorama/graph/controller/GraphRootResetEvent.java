/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Aug 12, 2002
 * Time: 2:07:26 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.graph.controller;

import ontorama.model.GraphNode;
import ontorama.controller.NodeEvent;

public class GraphRootResetEvent extends NodeEvent {
    public GraphRootResetEvent(GraphNode subject) {
        super(subject);
    }
}
