package ontorama.ontotools.inputsource.webkb;

import junit.framework.TestCase;

import ontorama.ontotools.query.Query;

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
    private String expectedQueryStringWithDepth;

    private Query query;
    private String queryOutputFormat;

    private Query queryWithDepth;

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
        expectedQueryStringWithDepth = "?term=wn%23cat&recursLink=%3E&format=RDF&depth=3&noHTML";

        query = new Query("wn#cat");
        queryOutputFormat = "RDF";

        queryWithDepth = new Query("wn#cat");
        queryWithDepth.setDepth(3);
    }

    /**
     *
     */
    public void testGetQueryString() {
        WebkbQueryStringConstructor queryStringConstructor = new WebkbQueryStringConstructor();
        String returnedQueryString = queryStringConstructor.getQueryString(query, queryOutputFormat);

        assertEquals("query string", expectedQueryString, returnedQueryString);
    }

    /**
     *
     */
    public void testGetQueryStringWithDepth() {
        WebkbQueryStringConstructor queryStringConstructor = new WebkbQueryStringConstructor();
        String returnedQueryString = queryStringConstructor.getQueryString(queryWithDepth, queryOutputFormat);

        assertEquals("query string", expectedQueryStringWithDepth, returnedQueryString);
    }


}