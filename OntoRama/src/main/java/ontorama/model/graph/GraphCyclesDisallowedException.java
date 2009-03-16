package ontorama.model.graph;

/**
 * @author nataliya
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class GraphCyclesDisallowedException extends Exception {
	/**
	 * Constructor for GraphCyclesDisallowedException.
	 * @param message
	 */
	public GraphCyclesDisallowedException(String message) {
		super(message);
	}

}
