package ontorama.webkbtools.inputsource.webkb;

import junit.framework.TestCase;

import ontorama.OntoramaConfig;

import ontorama.webkbtools.query.Query;

/**
 * <p>Description:
 * Test case for WebkbQueryStringConstructor </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestWebkbQueryStringConstructor extends TestCase {

  private String expectedQueryString;

  private Query query;
  private String queryOutputFormat;

  /**
   *
   */
  public TestWebkbQueryStringConstructor(String name) {
    super(name);
  }

  /**
   *
   */
  protected void setUp() {
    expectedQueryString = "?term=wn%23cat&recursLink=%3E&format=RDF&noHTML";

    query = new Query("wn#cat");
    queryOutputFormat = "RDF";
  }

  /**
   *
   */
  public void testGetQueryString() {
    WebkbQueryStringConstructor queryStringConstructor = new WebkbQueryStringConstructor();
    String returnedQueryString = queryStringConstructor.getQueryString(query, queryOutputFormat);

    assertEquals("query string", expectedQueryString, returnedQueryString);
  }


}