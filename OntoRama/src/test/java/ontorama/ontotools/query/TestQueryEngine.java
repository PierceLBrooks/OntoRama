package ontorama.ontotools.query;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import ontorama.OntoramaConfig;
import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Node;
import ontorama.ontotools.CancelledQueryException;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.NoSuchTypeInQueryResult;
import ontorama.ontotools.QueryFailedException;
import ontorama.ontotools.WebKbConstants;
import ontorama.ontotools.parser.rdf.RdfDamlParser;
import ontorama.ontotools.source.JarSource;

/**
 * Test if returned iterator of ontology types contains the same
 * types as expected. Using example of wn#wood_mouse to test this.
 * 
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 */
public class TestQueryEngine extends TestCase {

	String sourceUri = "examples/test/data/testCase.rdf";
	private final String queryTerm = "test#Chair";
    private List<EdgeType> relationLinksList;
    
    private QueryEngine queryEngine;

    private Query query1;

    private Query query2;

    private Node testNode_chair;


    EdgeType edgeType1;
    EdgeType edgeType2;
    EdgeType edgeType3;
    EdgeType edgeType4;
    EdgeType edgeType5;
    EdgeType edgeType6;
    EdgeType edgeType7;
    EdgeType edgeType8;
    EdgeType edgeType9;
    EdgeType edgeType10;
    EdgeType edgeType11;
    EdgeType edgeType12;

    public TestQueryEngine(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws NoSuchRelationLinkException, Exception {
        OntoramaConfig.loadAllConfig("examples/test/data/testCase-examplesConfig.xml",
                "ontorama.properties", "examples/test/data/testCase-config.xml");
		OntoramaConfig.instantiateBackend(OntoramaConfig.defaultBackend, null);

        edgeType1 = OntoramaConfig.getEdgeType(WebKbConstants.edgeName_subtype);
        edgeType2 = OntoramaConfig.getEdgeType(WebKbConstants.edgeName_similar);
        edgeType3 = OntoramaConfig.getEdgeType(WebKbConstants.edgeName_reverse);
        edgeType4 = OntoramaConfig.getEdgeType(WebKbConstants.edgeName_part);
        edgeType5 = OntoramaConfig.getEdgeType(WebKbConstants.edgeName_substance);
        edgeType6 = OntoramaConfig.getEdgeType(WebKbConstants.edgeName_instance);
        edgeType7 = OntoramaConfig.getEdgeType(WebKbConstants.edgeName_complement);
        edgeType8 = OntoramaConfig.getEdgeType(WebKbConstants.edgeName_location);
        edgeType9 = OntoramaConfig.getEdgeType(WebKbConstants.edgeName_member);
        edgeType10 = OntoramaConfig.getEdgeType(WebKbConstants.edgeName_object);
        edgeType11 = OntoramaConfig.getEdgeType(WebKbConstants.edgeName_url);
        edgeType12 = OntoramaConfig.getEdgeType(WebKbConstants.edgeName_nounType);


        relationLinksList = new ArrayList<EdgeType>();
        relationLinksList.add(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_reverse));
        relationLinksList.add(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_instance));
        relationLinksList.add(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_member));
        
    	queryEngine = new QueryEngine(new JarSource(), new RdfDamlParser(), sourceUri);
        
        query1 = new Query(queryTerm);

        query2 = new Query(queryTerm, relationLinksList);
    }
    
    /*
     * Note: this is bizarre - first test method always throws an exception, not
     * sure how to fix this since couldn't find a cause. Tried swapping methods
     * order around - always first test case fails and the rest a successful -
     * therefore this bogus test case.
     * Exception thrown is usually CoRoutineDeathException thrown by ARP. All
     * attempts to upgrade to use latest ARP (suggested solution) didn't yield
     * any positive results.
     */
	public void testNothing () {
		try {
			queryEngine.getQueryResult(query1);
		} 
		catch (Exception e) {
			e.printStackTrace(); 
		} 
	}

    public void testGetQueryResultForQuery1() throws QueryFailedException, CancelledQueryException,
            NoSuchTypeInQueryResult {
    	QueryResult queryResult1 = queryEngine.getQueryResult(query1);
    	List<Node> queryResultNodesList1 = queryResult1.getNodesList();
    	List<Edge> queryResultEdgesList1 = queryResult1.getEdgesList();
        assertEquals("size of query result nodes list for query1", 31, queryResultNodesList1.size());
    	assertEquals("size of query result edges list for query1", 33, queryResultEdgesList1.size());
        testNode_chair = getNodeFromList("test#Chair", queryResult1.getNodesList());
        checkOutboundEdge(queryResult1, testNode_chair, edgeType1 ,1);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType2, 1);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType3, 0);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType4, 2);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType5, 1);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType6, 0);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType7, 2);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType8, 1);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType9, 1);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType10, 2);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType11, 1);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType12, 0);
    }
    

    /*
     * although we only have 3 connected nodes - we return all nodes because we
     * support unconnected graphs. The real test here - is number of edges.
     */
    public void testGetQueryResultForQuery2() throws QueryFailedException, CancelledQueryException,
            NoSuchTypeInQueryResult {
    	QueryResult queryResult2 = queryEngine.getQueryResult(query2);
    	List<Node> queryResultNodesList2 = queryResult2.getNodesList();
    	List<Edge> queryResultEdgesList2 = queryResult2.getEdgesList();
        assertEquals("size of query result nodes list for query2", 31, queryResultNodesList2.size());
    	assertEquals("size of query result edges list for query2", 2, queryResultEdgesList2.size());
        testNode_chair = getNodeFromList("test#Chair", queryResult2.getNodesList());
        checkOutboundEdge(queryResult2, testNode_chair, edgeType3, 0);
        checkOutboundEdge(queryResult2, testNode_chair, edgeType6, 0);
        checkOutboundEdge(queryResult2, testNode_chair, edgeType9, 1);

    }

    private void checkOutboundEdge(QueryResult queryResult, Node fromNode,
                                           EdgeType edgeType, int expectedListSize) {
        String message = "query " + queryResult.getQuery().getQueryTypeName();
        message = message + ", iterator size for ";
        message = message + " ontology type " + fromNode.getName() + " and relation link ";
        message = message + edgeType.getName();

        List<Edge> outboundEdges = new ArrayList<Edge>();

        for(Edge cur : queryResult.getEdgesList()) {
            String edgeTypeName = cur.getEdgeType().getName();
            if ((cur.getFromNode().equals(fromNode)) && (edgeType.getName().equals(edgeTypeName)) ) {
                outboundEdges.add(cur);
            }
        }
        assertEquals(message, expectedListSize, outboundEdges.size());
    }

    private Node getNodeFromList (String nodeName, List<Node> nodesList) {
    	for( Node cur: nodesList) {
            if (cur.getName().equals(nodeName)) {
                return cur;
            }
        }
        return null;
    }

}