package ontorama.ontotools;

@SuppressWarnings("serial")
public class ParserException extends QueryFailedException {

	public ParserException(String message, Throwable originalException) {
		super(message, originalException);
	}

    public ParserException(String message) {
    	super(message);
    }
}