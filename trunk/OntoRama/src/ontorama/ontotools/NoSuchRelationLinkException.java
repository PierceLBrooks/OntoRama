package ontorama.ontotools;


/**

 * Title:

 * Description:

 * Copyright:    Copyright (c) 2001

 * Company:

 * @author

 * @version 1.0

 */


public class NoSuchRelationLinkException extends Exception {

    private String errorMsg = null;

    public NoSuchRelationLinkException (String linkName) {
        errorMsg = "RelationLink " + linkName + " doesn't exist. Check configuration file";
    }
    
    public String getMessage() {
    	return errorMsg;
    }

}