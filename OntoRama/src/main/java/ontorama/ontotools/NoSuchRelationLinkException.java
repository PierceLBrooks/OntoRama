package ontorama.ontotools;

@SuppressWarnings("serial")
public class NoSuchRelationLinkException extends Exception {

    private String errorMsg = null;

    public NoSuchRelationLinkException (String linkName) {
        errorMsg = "RelationLink " + linkName + " doesn't exist. Check configuration file";
    }
    
    public String getMessage() {
    	return errorMsg;
    }

}