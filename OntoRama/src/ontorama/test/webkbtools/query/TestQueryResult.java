package ontorama.test.webkbtools.query;

import junit.framework.TestCase;

import java.util.*;

import ontorama.test.IteratorUtil;

import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.query.QueryResult;

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
  private List expectedList;

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

    expectedList = new LinkedList();
    expectedList.add("obj1");
    expectedList.add("obj2");
    expectedList.add("obj3");
    expectedList.add("obj4");
    expectedList.add("obj5");

    queryResult = new QueryResult (query, expectedList.iterator());

  }

  /**
   *
   */
  public void testGetQuery () {
    Query testQuery = queryResult.getQuery();

    assertEquals("testing query",query,testQuery);
  }

  /**
   *
   */
  public void testGetOntologyTypesIterator () {

    List testOntTypesList = IteratorUtil.copyIteratorToList(queryResult.getOntologyTypesIterator());

    assertEquals("iterator size", expectedList.size(),testOntTypesList.size());

    Iterator it = testOntTypesList.iterator();
    while (it.hasNext()) {
      String cur = (String) it.next();
      //System.out.println("cur = " + cur);
      boolean found = expectedList.contains(cur);
      assertEquals("expected values list should contain object " + cur +
              " from the result iterator" , true, found);
    }
  }
}