/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Sep 25, 2002
 * Time: 1:15:36 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model.graph;


public class NodeAlreadyExistsException extends GraphModificationException  {
    public NodeAlreadyExistsException(ontorama.model.graph.Node node) {
        super("Node " + node.getName() + " is already exists in the graph");
    }
}
