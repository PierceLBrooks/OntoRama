package ontorama.webkbtools.query.parser.rdf.test;

import junit.framework.TestCase;
import ontorama.OntoramaConfig;
import ontorama.model.*;
import ontorama.ontologyConfig.examplesConfig.OntoramaExample;
import ontorama.util.TestingUtils;
import ontorama.webkbtools.inputsource.Source;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.webkbtools.query.parser.rdf.RdfDamlParser;
import ontorama.webkbtools.util.*;
import ontorama.webkbtools.TestWebkbtoolsPackage;

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

    protected Node testNode_chair;
    protected Node testNode_armchair;
    protected Node testNode_furniture;
    protected Node testNode_backrest;
    protected Node testNode_leg;
    protected Node testNode_myChair;
    protected Node testNode_someSubstanceNode;
    protected Node testNode_table;
    protected Node testNode_someLocation;
    protected Node testNode_url;
    protected Node testNode_someObject;
    protected Node testNode_allChairs;
    protected Node testNode_ACHRONYM;

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
        assertEquals(32, parserResult.getNodesList().size());
    }

    /**
     *
     */
    public void testEdgesForNullNodes () {
        Iterator edgesIt = parserResult.getEdgesList().iterator();
        while (edgesIt.hasNext()) {
            Edge edge = (Edge) edgesIt.next();
            Node fromNode = edge.getFromNode();
            Node toNode = edge.getToNode();
            assertEquals("edge node should never be null", false, (fromNode == null));
            assertEquals("edge node should never be null", false, (toNode == null));
        }

    }

    /**
     *
     */
    public void testFullName() {
        String message = "full name of the ontology type";
        assertEquals(message, "http://www.webkb.org/ontorama/test#Chair", testNode_chair.getIdentifier());
        assertEquals(message, "http://www.webkb.org/ontorama/test#Furniture", testNode_furniture.getIdentifier());
        assertEquals(message, "http://www.webkb.org/ontorama/test#Backrest", testNode_backrest.getIdentifier());
        assertEquals(message, "http://www.webkb.org/ontorama/test#Leg", testNode_leg.getIdentifier());
        assertEquals(message, "http://www.webkb.org/ontorama/test#MyChair", testNode_myChair.getIdentifier());
        assertEquals(message, "http://www.webkb.org/ontorama/test#AllChairs", testNode_allChairs.getIdentifier());
        assertEquals(message, "http://www.webkb.org/ontorama/test#SomeSubstanceNode", testNode_someSubstanceNode.getIdentifier());
        assertEquals(message, "http://www.webkb.org/ontorama/test#Table", testNode_table.getIdentifier());
        assertEquals(message, "http://www.webkb.org/OntoRama", testNode_url.getIdentifier());
        assertEquals(message, "http://www.webkb.org/ontorama/test#SomeLocation", testNode_someLocation.getIdentifier());
        assertEquals(message, "http://www.webkb.org/ontorama/test#SomeObject", testNode_someObject.getIdentifier());
        assertEquals(message, "http://www.webkb.org/ontorama/test#Armchair", testNode_armchair.getIdentifier());
    }

    /**
     *
     */
    public void testNodePropertyDescr_chair() {
        //System.out.println("\n\n\n testing property description");
        LinkedList expectedValueList = new LinkedList();
        expectedValueList.add("test term 'chair'");
    }

    /**
     *
     */
    public void testNodePropertyCreator_chair() {
        LinkedList expectedValueList = new LinkedList();
        expectedValueList.add("nataliya@dstc.edu.au");
    }

    /**
     *
     */
    public void testNodePropertySyn_chair() {
        LinkedList expectedValueList = new LinkedList();
        expectedValueList.add("chair");
        expectedValueList.add("sit");
    }


    /**
     * test rel link 'subtype' for type chair
     * id = 1
     */
    public void testEdge_chair_subtype() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype), testNode_chair, "test#Armchair", 1);
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype), testNode_furniture, "test#Chair", 1);
    }

    /**
     * test rel link 'similar' for type chair
     * id = 2
     */
    public void testEdge_chair_similar() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_similar),testNode_chair,"test#OtherChairs", 1);
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
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_part), testNode_chair, "test#Backrest", 1);
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_part), testNode_chair, "test#Leg", 1);
    }

    /**
     * test rel link 'substance' for type chair
     * id = 5
     */
    public void testEdge_chair_substance() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_substance), testNode_chair, "test#SomeSubstanceNode", 1);
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_substance), testNode_chair, "test#SomeSubstanceNode", 1);
    }

    /**
     * test rel link 'instance' for type chair
     * id = 6
     */
    public void testEdge_chair_instance() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_instance), testNode_chair, "test#MyChair", 0);
        // 2 here to account for a fact that rdf resource is an instance of rdf-schema#Class
        // so we have 1 type that instance of type 'chair' + 1 rdf-schema#Class
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_instance), testNode_myChair, "test#Chair", 1);
    }

    /**
     * test rel link 'exclusion/complement' for type chair
     * id = 7
     */
    public void testEdge_chair_complement() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_complement), testNode_chair, "test#Table", 1);
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_complement), testNode_table, "test#Chair", 0);
    }

    /**
     * test rel link 'location' for type chair
     * id = 8
     */
    public void testEdge_chair_location() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_location), testNode_chair, "test#SomeLocation", 1);
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_location), testNode_someLocation, "test#Chair", 0);
    }

    /**
     * test rel link 'member' for type chair
     * id = 9
     */
    public void testEdge_chair_member() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_member), testNode_chair, "test#AllChairs", 1);
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_member), testNode_allChairs, "test#Chair", 0);
    }

    /**
     * test rel link 'object' for type chair
     * id = 10
     */
    public void testEdge_chair_object() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_object), testNode_chair, "test#SomeObject",1);
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_object), testNode_chair, "test#ACHRONYM", 1);
    }

    /**
     * test rel link 'url' for type chair
     * id = 11
     */
    public void testEdge_chair_url() throws NoSuchRelationLinkException {
        //System.out.println("testType_chair = " + testType_chair);
        //System.out.println("testType_url = " + testType_url);
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_url), testNode_chair, "OntoRama", 1);
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_url), testNode_url, "test#Chair", 0);
    }

    public void testNodeTypes () {
        List nodesList = parserResult.getNodesList();
        List conceptsList = new LinkedList();
        List relationsList = new LinkedList();
        Iterator it = nodesList.iterator();
        while (it.hasNext()) {
            Node cur = (Node) it.next();
            NodeType curNodeType = cur.getNodeType();
            if (curNodeType.getNodeType().equals("concept")) {
                conceptsList.add(cur);
            }
            else {
                relationsList.add(cur);
            }
        }
        System.out.println("concepts list = " + conceptsList);
        System.out.println("relations list = " + relationsList);
        assertEquals("number of concept nodes ", 29, conceptsList.size());
        assertEquals("number of relation nodes ", 3, relationsList.size());

    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    protected void testingEdge (EdgeType edgeType, Node fromNode, String toNodeName,
                                       int expectedIteratorSize)
            throws NoSuchRelationLinkException {

        assertEquals("graph node should never be null here, check setUp() method" +
                " for node " + fromNode + " (checking edge type '" + edgeType.getName() + "')",
                false, (fromNode == null));

        String message1 = "number of '" + edgeType.getName() + "' links from '";
        message1 = message1 + fromNode.getName() + "'" + "to '" + toNodeName + "'";
        List edges = getEdgesFromList( fromNode, toNodeName, edgeType);
        assertEquals(message1, expectedIteratorSize, edges.size());

        if (edges.isEmpty()) {
            return;
        }
        Edge firstEdge = (Edge) edges.get(0);
        Node toNode = firstEdge.getToNode();
        String message2 = "related node (edge type: " + edgeType + "):" + toNodeName + ")";
        assertEquals(message2, toNodeName, toNode.getName());
    }


    /**
     *
     */
    protected Node getGraphNodeFromList (String name, List list) {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Node cur = (Node) it.next();
            if (cur.getName().equals(name)) {
                return cur;
            }
        }
        return null;
    }

    protected List getEdgesFromList (Node fromNode, String toNodeName, EdgeType edgeType) {
        LinkedList result = new LinkedList();
        List allEdges = parserResult.getEdgesList();
        Iterator it = allEdges.iterator();
        while (it.hasNext()) {
            Edge edge = (Edge) it.next();
            if ( (edge.getFromNode().equals(fromNode)) && (edge.getEdgeType().equals(edgeType))  ){
                Node toNode = edge.getToNode();
                if (toNode.getName().equals(toNodeName)) {
                    result.add(edge);
                }
            }
        }
        return result;
    }


}