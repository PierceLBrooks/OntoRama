package ontorama.ontotools.query.test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ontorama.ontotools.query.Query;

import junit.framework.TestCase;


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
        query2.setDepth(4);

        expectedRelLinksList2 = new LinkedList();
        expectedRelLinksList2.add(new Integer(0));
        expectedRelLinksList2.add(new Integer(2));
        expectedRelLinksList2.add(new Integer(5));
    }

    /**
     * test method getQueryTypeName()
     */
    public void testGetQueryName() {
        assertEquals("query termName for query1", termName, query1.getQueryTypeName());
        assertEquals("query termName for query2", termName, query2.getQueryTypeName());
    }

    /**
     * test method getRelationLinksCollection
     */
    public void testGetRelationLinksCollection() {
        assertEquals("relaton links collection for query1 should be empty", 0,
                query1.getRelationLinksCollection().size());

        Collection expectedCollection2 = expectedRelLinksList2;
        assertEquals("relation links collection for query2", expectedCollection2,
                query2.getRelationLinksCollection());
    }

    /**
     * test method getRelationLinksIterator
     */
    public void testGetRelationLinksIterator() {
        assertEquals("relation links iterator for query1 should be empty", 0, query1.getRelationLinksList().size());
        assertEquals("size of relation links iterator for query2", expectedRelLinksList2.size(), query2.getRelationLinksList().size());
        assertEquals("relation links iterator for query2 should contain integer 2",true, query2.getRelationLinksList().contains(new Integer(2)));
        assertEquals("relation links iterator for query2 should not contain integer 3", false, query2.getRelationLinksList().contains(new Integer(3)));
    }

    /**
     *
     */
    public void testGetRelationLinksList() {
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

    /**
     *
     */
    public void testGetDepth() {
        assertEquals("depth for query1", -1, query1.getDepth());
        assertEquals("depth for query1", 4, query2.getDepth());
    }

    /**
     *
     */
    public void testSetDepth() {
        query1.setDepth(8);
        assertEquals("set depth for query1", 8, query1.getDepth());
    }

}