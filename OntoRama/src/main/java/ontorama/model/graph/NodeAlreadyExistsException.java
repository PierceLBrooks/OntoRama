package ontorama.model.graph;


@SuppressWarnings("serial")
public class NodeAlreadyExistsException extends GraphModificationException  {
    public NodeAlreadyExistsException(ontorama.model.graph.Node node) {
        super("Node " + node.getName() + " is already exists in the graph");
    }
}
