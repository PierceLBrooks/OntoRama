package ontorama.model.graph;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class NoTypeFoundInResultSetException extends InvalidArgumentException {

    public NoTypeFoundInResultSetException(String termName) {
        super("term '" + termName + "' not found in Query Result Set");
    }
}