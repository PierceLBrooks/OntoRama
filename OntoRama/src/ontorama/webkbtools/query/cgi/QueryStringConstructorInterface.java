package ontorama.webkbtools.query.cgi;

import ontorama.webkbtools.query.Query;

/**
 * Description:   Implementations of QueryStringConstructorInterface
 * will provide the means to construct a query string for ontologies
 * accessed via cgi scripts/programs. Each ontology server most likely will
 * have different set of parameters, this interface provides an abstraction
 * layer, so we can seemlessly switch between different ontology servers.
 *
 * Copyright:    Copyright (c) 2002
 * Company: DSTC
 * @version 1.0
 */

public interface QueryStringConstructorInterface {

  /**
   * Get query string for given query
   * @param query
   * @param queryOutputFormat, for instance: RDF
   * @return  query string ready to append to the cgi script uri.
   */
  public String getQueryString (Query query, String queryOutputFormat);

}