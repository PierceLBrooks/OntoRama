package ontorama.ontotools;

/**
 * @author nataliya
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class QueryFailedException extends Exception {

	public QueryFailedException(String message) {
		super(message);
	}
	
	public QueryFailedException(String message, Throwable originalException) {
		super(message, originalException);
	}


}
