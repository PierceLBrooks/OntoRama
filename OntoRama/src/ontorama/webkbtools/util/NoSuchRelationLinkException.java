package ontorama.webkbtools.util;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */
import java.lang.Exception;

public class NoSuchRelationLinkException extends Exception{
  private String errorMsg = null;

  public NoSuchRelationLinkException(int relationLink, int maxValue) {
    errorMsg = "RelationLink " + relationLink + " does not exist. Enter a value between 0 and " + maxValue;
  }

  public String getMessage() {
    return errorMsg;
  }
}