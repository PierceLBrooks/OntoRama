package ontorama.ontotools;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class SourceException extends QueryFailedException {

	public SourceException(String message) {
		super(message);
	}

    public SourceException(String message, Throwable originalException) {
    	super(message, originalException);
    }

}