package ontorama.webkbtools.query.parser.rdf;

import junit.framework.TestCase;
import ontorama.OntoramaConfig;
import ontorama.model.GraphNode;
import ontorama.model.EdgeIterface;
import ontorama.ontologyConfig.examplesConfig.OntoramaExample;
import ontorama.ontologyConfig.RelationLinkDetails;
import ontorama.util.TestingUtils;
import ontorama.webkbtools.inputsource.Source;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.webkbtools.util.*;

import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Title: </p>
 * <p>Description:
 * test if parser can produce valid collection of ontology types.
 * <br/>
 * Note: we only test method getOntologyTypeCollection because
 * this the main method doing all the work, method getOntologyTypeIterator
 * is only returning getOntologyTypeCollection.iterator(). So, we think
 * it should be sufficient to test getOntologyTypeCollection.
 * </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 *
 */

public class TestRdfDamlParser extends TestCase {

    protected ParserResult parserResult;

    protected Collection resultCollection;


    protected String propName_descr = "Description";
    protected String propName_creator = "Creator";
    protected String propName_synonym = "Synonym";

    protected GraphNode testNode_chair;
    protected GraphNode testNode_armchair;
    protected GraphNode testNode_furniture;
    protected GraphNode testNode_backrest;
    protected GraphNode testNode_leg;
    protected GraphNode testNode_myChair;
    protected GraphNode testNode_someSubstanceNode;
    protected GraphNode testNode_table;
    protected GraphNode testNode_someLocation;
    protected GraphNode testNode_url;
    protected GraphNode testNode_someObject;
    protected GraphNode testNode_allChairs;
    protected GraphNode testNode_ACHRONYM;

    private Source source;
    private Parser parser;


    /**
     *
     */
    public TestRdfDamlParser(String name) {
        super(name);
    }

    /**
     *
     */
    protected void setUp() throws Exception {
        System.out.println("\nsetUp method");

        OntoramaConfig.loadAllConfig("examples/test/data/testCase-examplesConfig.xml",
                "ontorama.properties", "examples/test/data/testCase-config.xml");
        OntoramaConfig.setCurrentExample(TestingUtils.getExampleByName("testCase"));

        source = (Source) (Class.forName(OntoramaConfig.sourcePackageName).newInstance());
        //Reader r = source.getReader(OntoramaConfig.sourceUri, new Query("test#Chair"));
        Reader r = source.getSourceResult(OntoramaConfig.sourceUri, new Query("test#Chair")).getReader();

        parser = new RdfDamlParser();
        buildResult(parser, r);

        List nodesList = parserResult.getNodesList();
        testNode_chair = getGraphNodeFromList("test#Chair", nodesList);
        testNode_armchair = getGraphNodeFromList("test#Armchair", nodesList);
        testNode_furniture = getGraphNodeFromList("test#Furniture", nodesList);
        testNode_backrest = getGraphNodeFromList("test#Backrest", nodesList);
        testNode_leg  = getGraphNodeFromList("test#Leg", nodesList);
        testNode_myChair = getGraphNodeFromList("test#MyChair", nodesList);
        testNode_someSubstanceNode = getGraphNodeFromList("test#SomeSubstanceNode", nodesList);
        testNode_table = getGraphNodeFromList("test#Table", nodesList);
        testNode_someLocation  = getGraphNodeFromList("test#SomeLocation", nodesList);
        testNode_url = getGraphNodeFromList("OntoRama", nodesList);
        testNode_someObject = getGraphNodeFromList("test#SomeObject", nodesList);
        testNode_allChairs = getGraphNodeFromList("test#AllChairs", nodesList);

    }

    /**
     *
     */
    protected void buildResult (Parser parser, Reader r) throws ParserException {
        parserResult = parser.getResult(r);
    }

    /**
     *
     */
    public void testInvalidRDF_extraColumnInElementName() throws java.lang.ClassNotFoundException,
            java.lang.IllegalAccessException, java.lang.InstantiationException,
            SourceException, CancelledQueryException {

        OntoramaExample testCaseToLoad = TestingUtils.getExampleByName("testCase: invalid RDF 1");
        OntoramaConfig.setCurrentExample(testCaseToLoad);

        source = (Source) (Class.forName(OntoramaConfig.sourcePackageName).newInstance());
        Reader r = source.getSourceResult(OntoramaConfig.sourceUri, new Query("test#Chair")).getReader();
        parser = new RdfDamlParser();
        try {
            parser.getResult(r);
            fail("failed to catch ParserException for invalid RDF file");
        } catch (ParserException e) {
            //System.out.println("caught parser exception as expected, message: \n" + e.getMessage());
        }
    }

    /**
     *
     */
    public void testInvalidRDF_DoubleSlashComments() throws java.lang.ClassNotFoundException,
            java.lang.IllegalAccessException, java.lang.InstantiationException,
            SourceException, CancelledQueryException {

        OntoramaExample testCaseToLoad = TestingUtils.getExampleByName("testCase: invalid RDF 2");
        OntoramaConfig.setCurrentExample(testCaseToLoad);

        source = (Source) (Class.forName(OntoramaConfig.sourcePackageName).newInstance());
        Reader r = source.getSourceResult(OntoramaConfig.sourceUri, new Query("test#Chair")).getReader();
        parser = new RdfDamlParser();
        try {
            parser.getResult(r);
            fail("failed to catch ParserException for invalid RDF file");
        } catch (ParserException e) {
            //System.out.println("caught parser exception as expected, message: \n" + e.getMessage());
        }

    }

    /**
     * @todo HACK: this test is here to reset setup to default values.
     * I think the problem is when we use other examples, and then
     * try to use default one - we need to get a new instance of source or get a new
     * reader or get a new instance of parser. not sure how to fix this
     */
    public void testNothing() {
    }

    /**
     *
     */
    public void testResultSize() {
        // expecting 14 types in the result
        assertEquals(14, parserResult.getNodesList().size());
    }

    /**
     *
     */
    public void testEdgesForNullNodes () {
        Iterator edgesIt = parserResult.getEdgesList().iterator();
        while (edgesIt.hasNext()) {
            EdgeIterface edge = (EdgeIterface) edgesIt.next();
            GraphNode fromNode = edge.getFromNode();
            GraphNode toNode = edge.getToNode();
            assertEquals("edge node should never be null", false, (fromNode == null));
            assertEquals("edge node should never be null", false, (toNode == null));
        }

    }

    /**
     *
     */
    public void testFullName() {
        String message = "full name of the ontology type";
        assertEquals(message, "http://www.webkb.org/ontorama/test#Chair", testNode_chair.getFullName());
        assertEquals(message, "http://www.webkb.org/ontorama/test#Furniture", testNode_furniture.getFullName());
        assertEquals(message, "http://www.webkb.org/ontorama/test#Backrest", testNode_backrest.getFullName());
        assertEquals(message, "http://www.webkb.org/ontorama/test#Leg", testNode_leg.getFullName());
        assertEquals(message, "http://www.webkb.org/ontorama/test#MyChair", testNode_myChair.getFullName());
        assertEquals(message, "http://www.webkb.org/ontorama/test#AllChairs", testNode_allChairs.getFullName());
        assertEquals(message, "http://www.webkb.org/ontorama/test#SomeSubstanceNode", testNode_someSubstanceNode.getFullName());
        assertEquals(message, "http://www.webkb.org/ontorama/test#Table", testNode_table.getFullName());
        assertEquals(message, "http://www.webkb.org/OntoRama", testNode_url.getFullName());
        assertEquals(message, "http://www.webkb.org/ontorama/test#SomeLocation", testNode_someLocation.getFullName());
        assertEquals(message, "http://www.webkb.org/ontorama/test#SomeObject", testNode_someObject.getFullName());
        assertEquals(message, "http://www.webkb.org/ontorama/test#Armchair", testNode_armchair.getFullName());
    }

    /**
     *
     */
    public void testNodePropertyDescr_chair() throws NoSuchPropertyException {
        //System.out.println("\n\n\n testing property description");
        LinkedList expectedValueList = new LinkedList();
        expectedValueList.add("test term 'chair'");
        testingNodeProperty(propName_descr, expectedValueList, testNode_chair);
    }

    /**
     *
     */
    public void testNodePropertyCreator_chair() throws NoSuchPropertyException {
        LinkedList expectedValueList = new LinkedList();
        expectedValueList.add("nataliya@dstc.edu.au");
        testingNodeProperty(propName_creator, expectedValueList, testNode_chair);
    }

    /**
     *
     */
    public void testNodePropertySyn_chair() throws NoSuchPropertyException {
        LinkedList expectedValueList = new LinkedList();
        expectedValueList.add("chair");
        expectedValueList.add("sit");
        testingNodeProperty(propName_synonym, expectedValueList, testNode_chair);
    }

    /**
     *
     */
    protected void testingNodeProperty(String propName, List expectedPropValueList,
                                       GraphNode node) throws NoSuchPropertyException {
        //
        assertEquals("graph node should never be null here, check setUp() method" +
                " for node " + node, false, (node == null));
        String message = "checking concept type property '" + propName;
        message = message + "' for type '" + node.getName() + "'";

        List propValue = testNode_chair.getProperty(propName);
        assertEquals(message + ", number of prop. values", expectedPropValueList.size(), propValue.size());

        for (int i = 0; i < expectedPropValueList.size(); i++) {
            assertEquals(message, expectedPropValueList.get(i), propValue.get(i));
        }
    }

    /**
     * test rel link 'subtype' for type chair
     * id = 1
     */
    public void testEdge_chair_subtype() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getRelationLinkDetails()[1], testNode_chair, "test#Armchair", 1);
        testingEdge(OntoramaConfig.getRelationLinkDetails()[1], testNode_furniture, "test#Chair", 1);
    }

    /**
     * test rel link 'similar' for type chair
     * id = 2
     */
    public void testEdge_chair_similar() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getRelationLinkDetails()[2],testNode_chair,"test#OtherChairs", 1);
    }

    /**
     * test rel link 'reverse' for type chair
     * id = 3
     */
    public void testEdge_chair_reverse() throws NoSuchRelationLinkException {
        //testingRelationLink("reverse", 5, testType_chair, "", 0);
    }

    /**
     * test rel link 'part' for type chair
     * id = 4
     */
    public void testEdge_chair_part() throws NoSuchRelationLinkException {
        // these links are in reversed order, so we are testing
        // types chair, backrest and leg.
        testingEdge(OntoramaConfig.getRelationLinkDetails()[4], testNode_chair, "test#Backrest", 1);
        testingEdge(OntoramaConfig.getRelationLinkDetails()[4], testNode_chair, "test#Leg", 1);
    }

    /**
     * test rel link 'substance' for type chair
     * id = 5
     */
    public void testEdge_chair_substance() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getRelationLinkDetails()[5], testNode_chair, "test#SomeSubstanceNode", 1);
        testingEdge(OntoramaConfig.getRelationLinkDetails()[5], testNode_chair, "test#SomeSubstanceNode", 1);
    }

    /**
     * test rel link 'instance' for type chair
     * id = 6
     */
    public void testEdge_chair_instance() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getRelationLinkDetails()[6], testNode_chair, "test#MyChair", 0);
        // 2 here to account for a fact that rdf resource is an instance of rdf-schema#Class
        // so we have 1 type that instance of type 'chair' + 1 rdf-schema#Class
        testingEdge(OntoramaConfig.getRelationLinkDetails()[6], testNode_myChair, "test#Chair", 1);
    }

    /**
     * test rel link 'exclusion/complement' for type chair
     * id = 7
     */
    public void testEdge_chair_complement() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getRelationLinkDetails()[7], testNode_chair, "test#Table", 1);
        testingEdge(OntoramaConfig.getRelationLinkDetails()[7], testNode_table, "test#Chair", 0);
    }

    /**
     * test rel link 'location' for type chair
     * id = 8
     */
    public void testEdge_chair_location() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getRelationLinkDetails()[8], testNode_chair, "test#SomeLocation", 1);
        testingEdge(OntoramaConfig.getRelationLinkDetails()[8], testNode_someLocation, "test#Chair", 0);
    }

    /**
     * test rel link 'member' for type chair
     * id = 9
     */
    public void testEdge_chair_member() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getRelationLinkDetails()[9], testNode_chair, "test#AllChairs", 1);
        testingEdge(OntoramaConfig.getRelationLinkDetails()[9], testNode_allChairs, "test#Chair", 0);
    }

    /**
     * test rel link 'object' for type chair
     * id = 10
     */
    public void testEdge_chair_object() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getRelationLinkDetails()[10], testNode_chair, "test#SomeObject",1);
        testingEdge(OntoramaConfig.getRelationLinkDetails()[10], testNode_chair, "test#ACHRONYM", 1);
    }

    /**
     * test rel link 'url' for type chair
     * id = 11
     */
    public void testEdge_chair_url() throws NoSuchRelationLinkException {
        //System.out.println("testType_chair = " + testType_chair);
        //System.out.println("testType_url = " + testType_url);
        testingEdge(OntoramaConfig.getRelationLinkDetails()[11], testNode_chair, "OntoRama", 1);
        testingEdge(OntoramaConfig.getRelationLinkDetails()[11], testNode_url, "test#Chair", 0);
    }



    protected void testingEdge (RelationLinkDetails edgeType, GraphNode fromNode, String toNodeName,
                                       int expectedIteratorSize)
            throws NoSuchRelationLinkException {

        assertEquals("graph node should never be null here, check setUp() method" +
                " for node " + fromNode + " (checking edge type '" + edgeType.getLinkName() + "')",
                false, (fromNode == null));

        String message1 = "number of '" + edgeType.getLinkName() + "' links from '";
        message1 = message1 + fromNode.getName() + "'" + "to '" + toNodeName + "'";
        List edges = getEdgesFromList( fromNode, toNodeName, edgeType);
        assertEquals(message1, expectedIteratorSize, edges.size());

        if (edges.isEmpty()) {
            return;
        }
        EdgeIterface firstEdge = (EdgeIterface) edges.get(0);
        GraphNode toNode = firstEdge.getToNode();
        String message2 = "related node (edge type: " + edgeType + "):" + toNodeName + ")";
        assertEquals(message2, toNodeName, toNode.getName());
    }


    /**
     *
     */
    protected GraphNode getGraphNodeFromList (String name, List list) {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            GraphNode cur = (GraphNode) it.next();
            if (cur.getName().equals(name)) {
                return cur;
            }
        }
        return null;
    }

    protected List getEdgesFromList (GraphNode fromNode, String toNodeName, RelationLinkDetails edgeType) {
        LinkedList result = new LinkedList();
        List allEdges = parserResult.getEdgesList();
        Iterator it = allEdges.iterator();
        while (it.hasNext()) {
            EdgeIterface edge = (EdgeIterface) it.next();
            if ( (edge.getFromNode().equals(fromNode)) && (edge.getEdgeType().equals(edgeType))  ){
                GraphNode toNode = edge.getToNode();
                if (toNode.getName().equals(toNodeName)) {
                    result.add(edge);
                }
            }
        }
        return result;
    }


}