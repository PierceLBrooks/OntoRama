package ontorama.ontotools;

@SuppressWarnings("serial")
public class NoSuchRelationLinkException extends Exception {

    public NoSuchRelationLinkException (String linkName) {
        super("RelationLink " + linkName + " doesn't exist. Check configuration file.");
    }
}