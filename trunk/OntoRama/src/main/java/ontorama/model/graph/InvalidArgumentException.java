package ontorama.model.graph;

/**
 * @author nataliya
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class InvalidArgumentException extends Exception {


	/**
	 * Constructor for InvalidArgumentException.
	 */
	public InvalidArgumentException(String message) {
		super(message);
	}
	
	/**
	 * Constructor for InvalidArgumentException.
	 */
	public InvalidArgumentException(String message, Throwable originalException) {
		super(message, originalException);
	}
	


}
