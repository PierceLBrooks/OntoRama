package ontorama.model;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class NoTypeFoundInResultSetException extends Exception {

    public NoTypeFoundInResultSetException(String termName) {
        super ("term '" termName + "' not found in Query Result Set");
    }
}