package ontorama.ontotools;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class SourceException extends Exception {
    private String errorMsg = "";

    public SourceException(String message) {
        //errorMsg = "\nSource failed:\n";
        errorMsg = errorMsg + message;
    }

    public String getMessage() {
        return errorMsg;
    }
}