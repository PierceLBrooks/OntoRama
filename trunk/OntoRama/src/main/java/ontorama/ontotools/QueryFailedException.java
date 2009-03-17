package ontorama.ontotools;

@SuppressWarnings("serial")
public class QueryFailedException extends Exception {

	public QueryFailedException(String message) {
		super(message);
	}
	
	public QueryFailedException(String message, Throwable originalException) {
		super(message, originalException);
	}


}
