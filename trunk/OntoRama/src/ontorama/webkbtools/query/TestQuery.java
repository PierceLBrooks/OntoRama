package ontorama.webkbtools.query;

import java.util.LinkedList;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.*;

import ontorama.util.IteratorUtil;

import ontorama.webkbtools.query.Query;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestQuery extends TestCase {

  private String termName;
  private List testRelationLinksList;

  private Query query1;
  private Query query2;

  private List expectedRelLinksList1 = new LinkedList();
  private List expectedRelLinksList2 = new LinkedList();


  /**
   *
   */
  public TestQuery(String name) {
    super(name);
  }

  /**
   *
   */
  protected void setUp() {
    termName = "catInTheHat";

    testRelationLinksList = new LinkedList();
    testRelationLinksList.add(new Integer(0));
    testRelationLinksList.add(new Integer(2));
    testRelationLinksList.add(new Integer(5));

    query1 = new Query(termName);
    query2 = new Query(termName, testRelationLinksList);

    expectedRelLinksList1 = new LinkedList();
    expectedRelLinksList2 = new LinkedList();
    expectedRelLinksList2.add(new Integer(0));
    expectedRelLinksList2.add(new Integer(2));
    expectedRelLinksList2.add(new Integer(5));
  }

  /**
   * test method getQueryTypeName()
   */
  public void testGetQueryName () {
    assertEquals("query termName for query1", termName, query1.getQueryTypeName());
    assertEquals("query termName for query2", termName, query2.getQueryTypeName());
  }

  /**
   * test method getRelationLinksCollection
   */
  public void testGetRelationLinksCollection () {
    assertEquals("relaton links collection for query1 should be empty", 0,
                      query1.getRelationLinksCollection().size());

    Collection expectedCollection2 = (Collection) expectedRelLinksList2;
    assertEquals("relation links collection for query2", expectedCollection2,
                      query2.getRelationLinksCollection());
  }

  /**
   * test method getRelationLinksIterator
   */
  public void testGetRelationLinksIterator () {
    assertEquals("relation links iterator for query1 should be empty", 0,
                    IteratorUtil.getIteratorSize(query1.getRelationLinksIterator()));

    assertEquals("size of relation links iterator for query2",
                  IteratorUtil.getIteratorSize(expectedRelLinksList2.iterator()),
                  IteratorUtil.getIteratorSize(query2.getRelationLinksIterator()));
    assertEquals("relation links iterator for query2 should contain integer 2",
                    true, IteratorUtil.objectIsInIterator(new Integer(2), query2.getRelationLinksIterator()));
    assertEquals("relation links iterator for query2 should not contain integer 3",
                    false, IteratorUtil.objectIsInIterator(new Integer(3), query2.getRelationLinksIterator()));
  }

  /**
   *
   */
  public void testGetRelationLinksList () {
    assertEquals("relation links list for query1 should be empty", 0,
                          query1.getRelationLinksList().size());
    assertEquals("size of relation links list for query2", expectedRelLinksList2.size(),
                          query2.getRelationLinksList().size());
    for (int i = 0; i < expectedRelLinksList2.size(); i++) {
      Integer int1 = (Integer) expectedRelLinksList2.get(i);
      Integer int2 = (Integer) query2.getRelationLinksList().get(i);
      assertEquals("elements of lists in the same position should be the same",
                          int1, int2);
    }
  }

  /**
   *
   */
  public void testSetRelationLinks() {
    LinkedList testRelationLinksList = new LinkedList();
    testRelationLinksList.add(new Integer(22));
    testRelationLinksList.add(new Integer(4));

    query2.setRelationLinks(testRelationLinksList);

    assertEquals(testRelationLinksList.size(), query2.getRelationLinksList().size());

    assertEquals(new Integer(22), query2.getRelationLinksList().get(0));
    assertEquals(new Integer(4), query2.getRelationLinksList().get(1));
  }
}