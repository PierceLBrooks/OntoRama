package ontorama.ontotools;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */


public class ParserException extends QueryFailedException {

//	public ParserException(String message, Throwable originalException) {
//		super("Parser failed " + message, originalException);
//	}

    public ParserException(String message) {
    	super("Parser failed " + message);
    }
    
    

}