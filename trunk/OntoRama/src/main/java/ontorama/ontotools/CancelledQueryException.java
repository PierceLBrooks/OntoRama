package ontorama.ontotools;

/**
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 */

@SuppressWarnings("serial")
public class CancelledQueryException extends Exception {

    public CancelledQueryException() {
    	super("Query was cancelled by the user");
    }
}