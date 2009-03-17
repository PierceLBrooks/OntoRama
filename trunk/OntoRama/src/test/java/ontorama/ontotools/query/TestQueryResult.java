package ontorama.ontotools.query;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeImpl;
import ontorama.model.graph.EdgeTypeImpl;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeImpl;

/**
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 */

public class TestQueryResult extends TestCase {

    private Query query;
    private QueryResult queryResult;
    private List<Node> expectedNodesList;
    private List<Edge> expectedEdgesList;

    public TestQueryResult(String name) {
        super(name);
    }

    protected void setUp() {
        query = new Query("testQueryType");

        expectedNodesList = new ArrayList<Node>();
        NodeImpl node1 = new NodeImpl("obj1","obj1");
		expectedNodesList.add(node1);
        expectedNodesList.add(new NodeImpl("obj2","obj2"));
        expectedNodesList.add(new NodeImpl("obj3","obj3"));
        expectedNodesList.add(new NodeImpl("obj4","obj4"));
        expectedNodesList.add(new NodeImpl("obj5","obj5"));

        expectedEdgesList = new ArrayList<Edge>();
		expectedEdgesList.add(new EdgeImpl(node1, node1, new EdgeTypeImpl("edge1")));
		expectedEdgesList.add(new EdgeImpl(node1, node1, new EdgeTypeImpl("edge2")));

        queryResult = new QueryResult(query, expectedNodesList, expectedEdgesList);

    }

    public void testGetQuery() {
        Query testQuery = queryResult.getQuery();

        assertEquals("testing query", query, testQuery);
    }

    public void testGetOntologyTypesIterator() {
        assertEquals("iterator size", expectedNodesList.size(), queryResult.getNodesList().size());

        for (Node cur : queryResult.getNodesList()) {
            boolean found = expectedNodesList.contains(cur);
            assertEquals("expected values list should contain object " + cur.getName() +
                    " from the result iterator", true, found);
        }
    }
}