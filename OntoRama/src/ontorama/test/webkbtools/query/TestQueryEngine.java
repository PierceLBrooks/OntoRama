package ontorama.test.webkbtools.query;

import junit.framework.TestCase;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Hashtable;

import ontorama.test.IteratorUtil;

import ontorama.OntoramaConfig;

import ontorama.webkbtools.query.*;
import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;
import ontorama.webkbtools.util.NoSuchPropertyException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestQueryEngine extends TestCase {

  private String queryTerm;
  private List relationLinksList;

  private Query query1;
  private QueryEngine queryEngine1;
  private QueryResult queryResult1;
  private List queryResultList1;

  private Query query2;
  private QueryEngine queryEngine2;
  private QueryResult queryResult2;
  private List queryResultList2;

  private OntologyType testType_chair;



  /**
   *
   */
  public TestQueryEngine(String name) {
    super (name);
  }

  /**
   *
   */
  protected void setUp() throws NoSuchPropertyException,
                            NoSuchRelationLinkException, Exception {
    OntoramaConfig.loadAllConfig("examples/test/data/testCase-examplesConfig.xml",
               "ontorama.properties","examples/test/data/testCase-config.xml");

    queryTerm = OntoramaConfig.ontologyRoot;
    relationLinksList = new LinkedList();
    relationLinksList.add(new Integer(3));
    relationLinksList.add(new Integer(6));
    relationLinksList.add(new Integer(9));

    query1 = new Query(queryTerm);
    queryEngine1 = new QueryEngine(query1);
    queryResult1 = queryEngine1.getQueryResult();
    queryResultList1 = IteratorUtil.copyIteratorToList(queryResult1.getOntologyTypesIterator());

    query2 = new Query(queryTerm, relationLinksList);
    queryEngine2 = new QueryEngine(query2);
    queryResult2 = queryEngine2.getQueryResult();
    queryResultList2 = IteratorUtil.copyIteratorToList(queryResult2.getOntologyTypesIterator());

  }

  /**
   *
   */
  public void testGetQueryResultForQuery1 () throws NoSuchRelationLinkException {
    assertEquals("size of query result iterator for query1", 14, queryResultList1.size());
    testType_chair = IteratorUtil.getOntologyTypeFromList("test#Chair", queryResultList1);
    checkRelationIteratorSize("query1", testType_chair, 1, 1);
    checkRelationIteratorSize("query1", testType_chair, 2, 1);
    checkRelationIteratorSize("query1", testType_chair, 3, 0);
    checkRelationIteratorSize("query1", testType_chair, 4, 2);
    checkRelationIteratorSize("query1", testType_chair, 5, 1);
    checkRelationIteratorSize("query1", testType_chair, 6, 0);
    checkRelationIteratorSize("query1", testType_chair, 7, 1);
    checkRelationIteratorSize("query1", testType_chair, 8, 1);
    checkRelationIteratorSize("query1", testType_chair, 9, 1);
    checkRelationIteratorSize("query1", testType_chair, 10, 1);
    checkRelationIteratorSize("query1", testType_chair, 11, 1);
    checkRelationIteratorSize("query1", testType_chair, 12, 0);
  }

  /**
   *
   */
  public void testGetQueryResultForQuery2 () throws NoSuchRelationLinkException  {
    assertEquals("size of query result iterator for query2", 14, queryResultList2.size());
    testType_chair = IteratorUtil.getOntologyTypeFromList("test#Chair", queryResultList2);
    checkRelationIteratorSize("query2", testType_chair, 3, 0);
    checkRelationIteratorSize("query2", testType_chair, 6, 0);
    checkRelationIteratorSize("query2", testType_chair, 9, 1);

  }

  /**
   *
   */
  private void checkRelationIteratorSize (String queryName, OntologyType ontType,
                              int relLinkId , int expectedIteratorSize)
                              throws NoSuchRelationLinkException {
    String message = "query " + queryName + ", iterator size for ";
    message = message + " ontology type " + ontType.getName() + " and relation link ";
    message = message + relLinkId;
    assertEquals(message, expectedIteratorSize, IteratorUtil.getIteratorSize(ontType.getIterator(relLinkId)));
  }
}