package ontorama.webkbtools.query;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestQueryResult extends TestCase {

    private Query query;
    private QueryResult queryResult;
    private List expectedNodesList;
    private List expectedEdgesList;

    /**
     *
     */
    public TestQueryResult(String name) {
        super(name);
    }

    /**
     *
     */
    protected void setUp() {
        query = new Query("testQueryType");

        expectedNodesList = new LinkedList();
        expectedNodesList.add("obj1");
        expectedNodesList.add("obj2");
        expectedNodesList.add("obj3");
        expectedNodesList.add("obj4");
        expectedNodesList.add("obj5");

        expectedEdgesList = new LinkedList();
        expectedEdgesList.add("edge1");
        expectedEdgesList.add("edge2");

        queryResult = new QueryResult(query, expectedNodesList, expectedEdgesList);

    }

    /**
     *
     */
    public void testGetQuery() {
        Query testQuery = queryResult.getQuery();

        assertEquals("testing query", query, testQuery);
    }

    /**
     *
     */
    public void testGetOntologyTypesIterator() {
        assertEquals("iterator size", expectedNodesList.size(), queryResult.getNodesList().size());

        Iterator it = queryResult.getNodesList().iterator();
        while (it.hasNext()) {
            String cur = (String) it.next();
            //System.out.println("cur = " + cur);
            boolean found = expectedNodesList.contains(cur);
            assertEquals("expected values list should contain object " + cur +
                    " from the result iterator", true, found);
        }
    }
}