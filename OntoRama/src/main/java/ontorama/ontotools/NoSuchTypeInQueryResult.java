package ontorama.ontotools;

/**
 * Exception is thrown if query result doesn't
 * contain the term user is searched for.

 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 */

@SuppressWarnings("serial")
public class NoSuchTypeInQueryResult extends Exception {

    public NoSuchTypeInQueryResult(String queryTerm) {
        super("Result set doesn't contain query term '" + queryTerm + "'");
    }
}