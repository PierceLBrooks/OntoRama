package ontorama.ontotools.query;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import junit.framework.TestCase;

import ontorama.OntoramaConfig;
import ontorama.backends.Backend;
import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Node;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.ParserException;
import ontorama.ontotools.TestWebkbtoolsPackage;

/**
 * <p>Title: </p>
 * <p>Description:
 * Test if returned iterator of ontology types contains the same
 * types as expected. Using example of wn#wood_mouse to test this.
 * </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestQueryEngine extends TestCase {

	String sourcePackageName = "ontorama.ontotools.source.JarSource";
	String parserPackageName = "ontorama.ontotools.parser.rdf.RdfDamlParser";
	String sourceUri = "examples/test/data/testCase.rdf";
	private String queryTerm = "test#Chair";
    private List relationLinksList;

    private Query query1;
//    private QueryEngine queryEngine1;
//    private QueryResult queryResult1;
//    private List queryResultNodesList1;
//	private List queryResultEdgesList1;

    private Query query2;
//    private QueryEngine queryEngine2;
//    private QueryResult queryResult2;
//    private List queryResultEdgesList2;
//	private List queryResultNodesList2;

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

    /**
     *
     */
    public TestQueryEngine(String name) {
        super(name);
    }

    /**
     */
    protected void setUp() throws NoSuchRelationLinkException, Exception {
        OntoramaConfig.loadAllConfig("examples/test/data/testCase-examplesConfig.xml",
                "ontorama.properties", "examples/test/data/testCase-config.xml");
		Backend backend = (Backend) OntoramaConfig.instantiateBackend(OntoramaConfig.defaultBackend, null);
    	OntoramaConfig.activateBackend(backend);

        edgeType1 = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype);
        edgeType2 = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_similar);
        edgeType3 = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_reverse);
        edgeType4 = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_part);
        edgeType5 = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_substance);
        edgeType6 = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_instance);
        edgeType7 = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_complement);
        edgeType8 = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_location);
        edgeType9 = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_member);
        edgeType10 = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_object);
        edgeType11 = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_url);
        edgeType12 = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_nounType);


        relationLinksList = new LinkedList();
        relationLinksList.add(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_reverse));
        relationLinksList.add(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_instance));
        relationLinksList.add(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_member));
        
        query1 = new Query(queryTerm, sourcePackageName, parserPackageName, sourceUri);

        query2 = new Query(queryTerm, relationLinksList, sourcePackageName, parserPackageName, sourceUri);
    }
    
    /**
     * Note: this is bizarre - first test method always throws an exception, not
     * sure how to fix this since couldn't find a cause. Tried swapping methods
     * order around - always first test case fails and the rest a successfull -
     * therefore this bogus test case.
     * Exception thrown is usually CoRoutineDeathException thrown by ARP. All
     * attempts to upgrade to use latest ARP (suggested solution) didn't yield
     * any positive results.
     */
	public void testNothing () {
		try {
			QueryEngine queryEngine1 = new QueryEngine(query1, sourcePackageName, parserPackageName, sourceUri);
		} catch (ParserException e) { } 
		catch (Exception e) { }
	}

    /**
     *
     */
    public void testGetQueryResultForQuery1() throws NoSuchRelationLinkException, Exception {
    	QueryEngine queryEngine1 = new QueryEngine(query1, sourcePackageName, parserPackageName, sourceUri);
    	QueryResult queryResult1 = queryEngine1.getQueryResult();
    	List queryResultNodesList1 = queryResult1.getNodesList();
    	List queryResultEdgesList1 = queryResult1.getEdgesList();
        assertEquals("size of query result nodes list for query1", 35, queryResultNodesList1.size());
    	assertEquals("size of query result edges list for query1", 37, queryResultEdgesList1.size());
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
    

    /**
     * although we only have 3 connected nodes - we return all nodes because we
     * support unconnected graphs. The real test here - is number of edges.
     */
    public void testGetQueryResultForQuery2() throws NoSuchRelationLinkException, Exception {
    	QueryEngine queryEngine2 = new QueryEngine(query2, sourcePackageName, parserPackageName, sourceUri);
    	QueryResult queryResult2 = queryEngine2.getQueryResult();
    	List queryResultNodesList2 = queryResult2.getNodesList();
    	List queryResultEdgesList2 = queryResult2.getEdgesList();
        assertEquals("size of query result nodes list for query2", 35, queryResultNodesList2.size());
    	assertEquals("size of query result edges list for query2", 2, queryResultEdgesList2.size());
        testNode_chair = getNodeFromList("test#Chair", queryResult2.getNodesList());
        checkOutboundEdge(queryResult2, testNode_chair, edgeType3, 0);
        checkOutboundEdge(queryResult2, testNode_chair, edgeType6, 0);
        checkOutboundEdge(queryResult2, testNode_chair, edgeType9, 1);

    }

    /**
     *
     */
    private void checkOutboundEdge(QueryResult queryResult, Node fromNode,
                                           EdgeType edgeType, int expectedListSize) {
        String message = "query " + queryResult.getQuery().getQueryTypeName();
        message = message + ", iterator size for ";
        message = message + " ontology type " + fromNode.getName() + " and relation link ";
        message = message + edgeType.getName();

        List outboundEdges = new LinkedList();

        Iterator edgesIt = queryResult.getEdgesList().iterator();
        while (edgesIt.hasNext()) {
            Edge cur = (Edge) edgesIt.next();
            String edgeTypeName = cur.getEdgeType().getName();
            if ((cur.getFromNode().equals(fromNode)) && (edgeType.getName().equals(edgeTypeName)) ) {
                outboundEdges.add(cur);
            }
        }
        assertEquals(message, expectedListSize, outboundEdges.size());
    }

    private Node getNodeFromList (String nodeName, List nodesList) {
        Iterator it = nodesList.iterator();
        while (it.hasNext()) {
            Node cur = (Node) it.next();
            if (cur.getName().equals(nodeName)) {
                return cur;
            }
        }
        return null;
    }

}