package ontorama.ontotools.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ontorama.model.graph.EdgeType;
import ontorama.model.graph.EdgeTypeImpl;
import ontorama.ontotools.query.Query;

import junit.framework.TestCase;


/**
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */
public class TestQuery extends TestCase {

    private String termName;
    private List<EdgeType> testRelationLinksList;

    private Query query1;
    private Query query2;

    private List<EdgeType> expectedRelLinksList2 = new ArrayList<EdgeType>();


    public TestQuery(String name) {
        super(name);
    }

    protected void setUp() {
        termName = "catInTheHat";

        testRelationLinksList = new ArrayList<EdgeType>();
        testRelationLinksList.add(new EdgeTypeImpl("0"));
        testRelationLinksList.add(new EdgeTypeImpl("2"));
        testRelationLinksList.add(new EdgeTypeImpl("5"));

        query1 = new Query(termName);
        query2 = new Query(termName, testRelationLinksList);
        query2.setDepth(4);

        expectedRelLinksList2 = new ArrayList<EdgeType>();
        expectedRelLinksList2.add(new EdgeTypeImpl("0"));
        expectedRelLinksList2.add(new EdgeTypeImpl("2"));
        expectedRelLinksList2.add(new EdgeTypeImpl("5"));
    }

    public void testGetQueryName() {
        assertEquals("query termName for query1", termName, query1.getQueryTypeName());
        assertEquals("query termName for query2", termName, query2.getQueryTypeName());
    }

    public void testGetRelationLinksCollection() {
        assertEquals("relaton links collection for query1 should be empty", 0,
                query1.getRelationLinksCollection().size());

        Collection<EdgeType> expectedCollection2 = expectedRelLinksList2;
        assertEquals("relation links collection for query2", expectedCollection2,
                query2.getRelationLinksCollection());
    }

    public void testGetRelationLinksIterator() {
        assertEquals("relation links iterator for query1 should be empty", 0, query1.getRelationLinksList().size());
        assertEquals("size of relation links iterator for query2", expectedRelLinksList2.size(), query2.getRelationLinksList().size());
        assertEquals("relation links iterator for query2 should contain integer 2",true, query2.getRelationLinksList().contains(new EdgeTypeImpl("2")));
        assertEquals("relation links iterator for query2 should not contain integer 3", false, query2.getRelationLinksList().contains(new EdgeTypeImpl("3")));
    }

    public void testGetRelationLinksList() {
        assertEquals("relation links list for query1 should be empty", 0,
                query1.getRelationLinksList().size());
        assertEquals("size of relation links list for query2", expectedRelLinksList2.size(),
                query2.getRelationLinksList().size());
        for (int i = 0; i < expectedRelLinksList2.size(); i++) {
            EdgeType et1 = expectedRelLinksList2.get(i);
            EdgeType et2 = query2.getRelationLinksList().get(i);
            assertEquals("elements of lists in the same position should be the same",
                    et1, et2);
        }
    }

    public void testSetRelationLinks() {
        List<EdgeType> testRelationLinksList = new ArrayList<EdgeType>();
        testRelationLinksList.add(new EdgeTypeImpl("22"));
        testRelationLinksList.add(new EdgeTypeImpl("4"));

        query2.setRelationLinks(testRelationLinksList);

        assertEquals(testRelationLinksList.size(), query2.getRelationLinksList().size());

        assertEquals(new EdgeTypeImpl("22"), query2.getRelationLinksList().get(0));
        assertEquals(new EdgeTypeImpl("4"), query2.getRelationLinksList().get(1));
    }

    public void testGetDepth() {
        assertEquals("depth for query1", -1, query1.getDepth());
        assertEquals("depth for query1", 4, query2.getDepth());
    }

    public void testSetDepth() {
        query1.setDepth(8);
        assertEquals("set depth for query1", 8, query1.getDepth());
    }

}