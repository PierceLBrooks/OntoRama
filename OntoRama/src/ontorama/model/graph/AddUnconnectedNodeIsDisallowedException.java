/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Sep 25, 2002
 * Time: 11:55:13 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model.graph;

import ontorama.model.graph.Node;

public class AddUnconnectedNodeIsDisallowedException extends GraphModificationException  {
    public AddUnconnectedNodeIsDisallowedException (ontorama.model.graph.Node node) {
        super("Can't add unconnected node " + node.getName());
    }
}
