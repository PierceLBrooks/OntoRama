package ontorama.webkbtools.query;

import junit.framework.TestCase;
import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.RelationLinkDetails;
import ontorama.model.GraphNode;
import ontorama.model.EdgeIterface;
import ontorama.util.TestingUtils;
import ontorama.webkbtools.util.NoSuchPropertyException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

import java.util.*;

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

    private GraphNode testNode_chair;

    RelationLinkDetails edgeType1 = OntoramaConfig.getRelationLinkDetails()[1];
    RelationLinkDetails edgeType2 = OntoramaConfig.getRelationLinkDetails()[2];
    RelationLinkDetails edgeType3 = OntoramaConfig.getRelationLinkDetails()[3];
    RelationLinkDetails edgeType4 = OntoramaConfig.getRelationLinkDetails()[4];
    RelationLinkDetails edgeType5 = OntoramaConfig.getRelationLinkDetails()[5];
    RelationLinkDetails edgeType6 = OntoramaConfig.getRelationLinkDetails()[6];
    RelationLinkDetails edgeType7 = OntoramaConfig.getRelationLinkDetails()[7];
    RelationLinkDetails edgeType8 = OntoramaConfig.getRelationLinkDetails()[8];
    RelationLinkDetails edgeType9 = OntoramaConfig.getRelationLinkDetails()[9];
    RelationLinkDetails edgeType10 = OntoramaConfig.getRelationLinkDetails()[10];
    RelationLinkDetails edgeType11 = OntoramaConfig.getRelationLinkDetails()[11];
    RelationLinkDetails edgeType12 = OntoramaConfig.getRelationLinkDetails()[12];

    /**
     *
     */
    public TestQueryEngine(String name) {
        super(name);
    }

    /**
     */
    protected void setUp() throws NoSuchPropertyException,
            NoSuchRelationLinkException, Exception {
        OntoramaConfig.loadAllConfig("examples/test/data/testCase-examplesConfig.xml",
                "ontorama.properties", "examples/test/data/testCase-config.xml");
        OntoramaConfig.setCurrentExample(TestingUtils.getExampleByName("testCase"));

        queryTerm = OntoramaConfig.ontologyRoot;
        relationLinksList = new LinkedList();
        relationLinksList.add(OntoramaConfig.getRelationLinkDetails(3));
        relationLinksList.add(OntoramaConfig.getRelationLinkDetails(6));
        relationLinksList.add(OntoramaConfig.getRelationLinkDetails(9));

        query1 = new Query(queryTerm);
        queryEngine1 = new QueryEngine(query1);
        queryResult1 = queryEngine1.getQueryResult();
        //System.out.println("queryResult1 = " + queryResult1);
        queryResultList1 = queryResult1.getNodesList();

        query2 = new Query(queryTerm, relationLinksList);
        queryEngine2 = new QueryEngine(query2);
        queryResult2 = queryEngine2.getQueryResult();
        queryResultList2 = queryResult2.getNodesList();
    }

    /**
     *
     */
    public void testGetQueryResultForQuery1() throws NoSuchRelationLinkException {
        assertEquals("size of query result iterator for query1", 14, queryResultList1.size());
        testNode_chair = getNodeFromList("test#Chair", queryResult1.getNodesList());
        checkOutboundEdge(queryResult1, testNode_chair, edgeType1 ,1);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType2, 1);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType3, 0);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType4, 2);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType5, 1);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType6, 0);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType7, 1);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType8, 1);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType9, 1);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType10, 2);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType11, 1);
        checkOutboundEdge(queryResult1, testNode_chair, edgeType12, 0);
    }

    /**
     *
     */
    public void testGetQueryResultForQuery2() throws NoSuchRelationLinkException {
        assertEquals("size of query result iterator for query2", 3, queryResultList2.size());
        testNode_chair = getNodeFromList("test#Chair", queryResult2.getNodesList());
        checkOutboundEdge(queryResult2, testNode_chair, edgeType3, 0);
        checkOutboundEdge(queryResult2, testNode_chair, edgeType6, 0);
        checkOutboundEdge(queryResult2, testNode_chair, edgeType9, 1);

    }

    /**
     *
     */
    private void checkOutboundEdge(QueryResult queryResult, GraphNode fromNode,
                                           RelationLinkDetails edgeType, int expectedListSize) {
        String message = "query " + queryResult.getQuery().getQueryTypeName();
        message = message + ", iterator size for ";
        message = message + " ontology type " + fromNode.getName() + " and relation link ";
        message = message + edgeType.getLinkName();

        List outboundEdges = new LinkedList();

        Iterator edgesIt = queryResult.getEdgesList().iterator();
        while (edgesIt.hasNext()) {
            EdgeIterface cur = (EdgeIterface) edgesIt.next();
            String edgeTypeName = cur.getEdgeType().getLinkName();
            if ((cur.getFromNode().equals(fromNode)) && (edgeType.getLinkName().equals(edgeTypeName)) ) {
                outboundEdges.add(cur);
            }
        }
        assertEquals(message, expectedListSize, outboundEdges.size());
    }

    private GraphNode getNodeFromList (String nodeName, List nodesList) {
        Iterator it = nodesList.iterator();
        while (it.hasNext()) {
            GraphNode cur = (GraphNode) it.next();
            if (cur.getName().equals(nodeName)) {
                return cur;
            }
        }
        return null;
    }

}