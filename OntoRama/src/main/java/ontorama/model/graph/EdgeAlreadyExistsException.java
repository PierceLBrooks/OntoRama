package ontorama.model.graph;


@SuppressWarnings("serial")
public class EdgeAlreadyExistsException extends GraphModificationException {
    public EdgeAlreadyExistsException(Edge edge) {
        super("Edge " + edge + "already exists in the graph");
    }
}
