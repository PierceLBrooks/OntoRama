package ontorama.ontotools;

/**
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 */

@SuppressWarnings("serial")
public class SourceException extends QueryFailedException {

	public SourceException(String message) {
		super(message);
	}

    public SourceException(String message, Throwable originalException) {
    	super(message, originalException);
    }

}