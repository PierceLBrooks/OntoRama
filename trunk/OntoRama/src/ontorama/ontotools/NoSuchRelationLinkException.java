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

    /**
     * @todo  dont' need parameter maxValue, need to remove it.
     */
    public NoSuchRelationLinkException(int relationLink, int maxValue) {
        errorMsg = "RelationLink " + relationLink + " does not exist. Enter a value between 0 and " + maxValue;
    }

    public NoSuchRelationLinkException (String linkName) {
        errorMsg = "RelationLink " + linkName + "doesn't exist. Check configuration file";
    }

}