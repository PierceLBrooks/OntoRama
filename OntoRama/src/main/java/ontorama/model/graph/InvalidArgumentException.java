package ontorama.model.graph;

@SuppressWarnings("serial")
public class InvalidArgumentException extends Exception {

	public InvalidArgumentException(String message) {
		super(message);
	}
	
	public InvalidArgumentException(String message, Throwable originalException) {
		super(message, originalException);
	}
}
