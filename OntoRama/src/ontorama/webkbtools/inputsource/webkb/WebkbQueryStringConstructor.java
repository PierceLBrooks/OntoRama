package ontorama.webkbtools.inputsource.webkb;

import java.util.Iterator;

import java.net.URLEncoder;

import ontorama.webkbtools.query.Query;

/**
 * Description:   Implementation of QueryStringConstructorInterface that can
 * be used for WebKB-2 Ontology Server.
 *
 * Copyright:    Copyright (c) 2002
 * Company: DSTC
 * @version 1.0
 */
public class WebkbQueryStringConstructor  {

  /**
   * Get query string for given query
   * @param query
   * @return  WebKB-2 query string ready to append to the cgi script uri.
   */
  public String getQueryString (Query query, String queryOutputFormat) {
    String termName = query.getQueryTypeName();
    Iterator iterator = null;

    //queryParamTerm = termName;
    //queryParamRecursLink = ">";

    String queryString = "?";
    queryString = queryString + "term=" + URLEncoder.encode(termName);
    //queryString = queryString + "term=" + URLEncoder.encode("wn#cat");
    queryString = queryString + "&recursLink=" + URLEncoder.encode(">");;
    queryString = queryString + "&format=" + URLEncoder.encode(queryOutputFormat);
    queryString = queryString + "&depth=" + URLEncoder.encode(String.valueOf(query.getDepth()));
    queryString = queryString + "&noHTML";
    System.out.println("\nqueryString = " + queryString + "\n");
    return queryString;
  }
}