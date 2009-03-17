package ontorama.model.graph;

@SuppressWarnings("serial")
public class GraphCyclesDisallowedException extends Exception {
	public GraphCyclesDisallowedException(String message) {
		super(message);
	}
}
