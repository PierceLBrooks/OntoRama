package ontorama.ontotools.parser.rdf.test;

import java.io.Reader;

import ontorama.OntoramaConfig;
import ontorama.backends.Backend;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.ParserException;
import ontorama.ontotools.TestWebkbtoolsPackage;
import ontorama.ontotools.parser.rdf.RdfDamlParser;
import ontorama.ontotools.parser.rdf.RdfWebkbParser;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.source.Source;

/**
 * <p>Title: </p>
 * <p>Description:
 * Overwrite TestRdfDamlParser because the only diffefence is treatment of
 * relation link 'url', all other results should be identical to results
 * in TestRdfDamlParser.
 * </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestRdfWebkbParser extends TestRdfDamlParser {

    /**
     *
     */
    public TestRdfWebkbParser(String name) {
        super(name);
    }

    /**
     *
     */
    protected void setUp() throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, Exception, ParserException {
        OntoramaConfig.loadAllConfig("examples/test/data/testCase-examplesConfig.xml",
                "ontorama.properties", "examples/test/data/testCase-config.xml");
        
        Backend backend = (Backend) OntoramaConfig.instantiateBackend(OntoramaConfig.defaultBackend, null);
        
        Source source = (Source) Class.forName(sourcePackageName).newInstance();
        Reader r = source.getSourceResult("examples/test/data/testCase.rdf", new Query("test#Chair")).getReader();

        RdfDamlParser parser = new RdfWebkbParser();
        buildResult(parser,r);

        testNode_chair = getGraphNodeFromList("test#chair", parserResult.getNodesList());
        testNode_armchair = getGraphNodeFromList("test#armchair", parserResult.getNodesList());
        testNode_furniture = getGraphNodeFromList("test#furniture", parserResult.getNodesList());
        testNode_backrest = getGraphNodeFromList("test#backrest", parserResult.getNodesList());
        testNode_leg = getGraphNodeFromList("test#leg", parserResult.getNodesList());
        testNode_myChair = getGraphNodeFromList("test#my_chair", parserResult.getNodesList());
        testNode_someSubstanceNode = getGraphNodeFromList("test#some_substance_node", parserResult.getNodesList());
        testNode_table = getGraphNodeFromList("test#table", parserResult.getNodesList());
        testNode_someLocation = getGraphNodeFromList("test#some_location", parserResult.getNodesList());
        testNode_url = getGraphNodeFromList("http://www.webkb.org/OntoRama", parserResult.getNodesList());
        testNode_someObject = getGraphNodeFromList("test#some_object", parserResult.getNodesList());
        testNode_allChairs = getGraphNodeFromList("test#all_chairs", parserResult.getNodesList());
        testNode_ACHRONYM = getGraphNodeFromList("test#ACHRONYM", parserResult.getNodesList());

    }

    /**
     *
     */
    public void testTypeNamingForAchronym() {
        assertEquals("testing if test Ontology Type for 'test#ACHRONYM' exists, if not "
                + ", there is an error when reformatting strings from RDF capitalised"
                + " format", false, (testNode_ACHRONYM == null));
    }

    /**
     * test rel link 'subtype' for type chair
     * id = 1
     */
    public void testEdge_chair_subtype() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype), testNode_chair, "test#armchair", 1);
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype), testNode_furniture, "test#chair", 1);
    }

    /**
     * test rel link 'similar' for type chair
     * id = 2
     */
    public void testEdge_chair_similar() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_similar), testNode_chair, "test#other_chairs", 1);
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
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_part), testNode_chair, "test#backrest", 1);
        //testingRelationLink("part", 4, testType_chair, "leg", 2);
    }

    /**
     * test rel link 'substance' for type chair
     * id = 5
     */
    public void testEdge_chair_substance() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_substance), testNode_chair, "test#some_substance_node", 1);
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_substance), testNode_chair, "test#some_substance_node", 1);
    }

    /**
     * test rel link 'instance' for type chair
     * id = 6
     */
    public void testEdge_chair_instance() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_instance), testNode_chair, "test#my_chair", 0);
        // 2 here to account for a fact that rdf resource is an instance of rdf-schema#Class
        // so we have 1 type that instance of type 'chair' + 1 rdf-schema#Class
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_instance), testNode_myChair, "test#chair", 1);
    }

    /**
     * test rel link 'exclusion/complement' for type chair
     * id = 7
     */
    public void testEdge_chair_complement() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_complement), testNode_chair, "test#table", 1);
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_complement), testNode_table, "test#chair", 0);
    }

    /**
     * test rel link 'location' for type chair
     * id = 8
     */
    public void testEdge_chair_location() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_location), testNode_chair, "test#some_location", 1);
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_location), testNode_someLocation, "test#chair", 0);
    }

    /**
     * test rel link 'member' for type chair
     * id = 9
     */
    public void testEdge_chair_member() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_member), testNode_chair, "test#all_chairs", 1);
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_member), testNode_allChairs, "test#chair", 0);
    }

    /**
     * test rel link 'object' for type chair
     * id = 10
     */
    public void testEdge_chair_object() throws NoSuchRelationLinkException {
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_object), testNode_chair, "test#some_object", 1);
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_object), testNode_chair, "test#ACHRONYM", 1);
    }


    /**
     * test rel link 'url' for type chair
     * id = 11
     */
    public void testEdge_chair_url() throws NoSuchRelationLinkException {
        //System.out.println("testType_chair = " + testType_chair);
        //System.out.println("testType_url = " + testType_url);
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_url), testNode_chair, "http://www.webkb.org/OntoRama", 1);
        testingEdge(OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_url), testNode_url, "test#chair", 0);
    }
}