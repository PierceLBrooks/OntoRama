/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Sep 25, 2002
 * Time: 1:15:36 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model.util;

import ontorama.model.Node;

public class NodeAlreadyExists extends Exception  {
    public NodeAlreadyExists(Node node) {
        super("Node " + node.getName() + " is already exists in the graph");
    }
}
