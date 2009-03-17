package ontorama.ontotools;

/**
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 */

@SuppressWarnings("serial")
public class CancelledQueryException extends Exception {

    public CancelledQueryException() {
    }

    public String getMessage() {
        return "Query was cancelled by user";
    }
}