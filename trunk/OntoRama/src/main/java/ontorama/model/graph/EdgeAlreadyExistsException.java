/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Sep 25, 2002
 * Time: 1:17:26 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model.graph;


public class EdgeAlreadyExistsException extends GraphModificationException {
    public EdgeAlreadyExistsException(ontorama.model.graph.Edge edge) {
        super("Edge " + edge + "already exists in the graph");
    }
}
