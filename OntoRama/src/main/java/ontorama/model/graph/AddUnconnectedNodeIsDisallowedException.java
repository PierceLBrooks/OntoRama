package ontorama.model.graph;


@SuppressWarnings("serial")
public class AddUnconnectedNodeIsDisallowedException extends GraphModificationException  {
    public AddUnconnectedNodeIsDisallowedException (ontorama.model.graph.Node node) {
        super("Can't add unconnected node " + node.getName());
    }
}
