package ontorama.webkbtools.util;

/**
 * <p>Title: </p>
 * <p>Description: Exception is thrown if query result doesn't
 * contain the term user is searched for.
 * </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class NoSuchTypeInQueryResult extends Exception {

  private String errorMsg = "";

  public NoSuchTypeInQueryResult(String queryTerm) {
    errorMsg = "Result set doesn't contain query term '" + queryTerm + "'";
  }

  public String getMessage() {
    return errorMsg;
  }
}