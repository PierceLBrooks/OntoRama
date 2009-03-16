package ontorama.ontotools.parser.rdf;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import ontorama.OntoramaConfig;
import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeType;
import ontorama.ontotools.CancelledQueryException;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.ParserException;
import ontorama.ontotools.SourceException;
import ontorama.ontotools.WebKbConstants;
import ontorama.ontotools.parser.Parser;
import ontorama.ontotools.parser.ParserResult;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.source.Source;

/**
 * Test if parser can produce valid collection of ontology types.
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
 */
public class TestRdfDamlParser extends TestCase {

    protected ParserResult parserResult;

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
    
    protected String sourcePackageName = "ontorama.ontotools.source.JarSource";

    private Source source;
    private Parser parser;
    
    public TestRdfDamlParser(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        OntoramaConfig.loadAllConfig("examples/test/data/testCase-examplesConfig.xml",
                "ontorama.properties", "examples/test/data/testCase-config.xml");
                
        OntoramaConfig.instantiateBackend(OntoramaConfig.defaultBackend, null);
        
    	source = (Source) (Class.forName(sourcePackageName).newInstance());
    	Reader r = source.getSourceResult("examples/test/data/testCase.rdf", new Query("test#Chair")).getReader();

        parser = new RdfDamlParser();
        buildResult(parser, r);

        List<Node> nodesList = parserResult.getNodesList();
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

    protected void buildResult (Parser parser, Reader r) throws ParserException {
        parserResult = parser.getResult(r);
    }
    
	public void testResultSize() {
		assertEquals("number of nodes", 31, parserResult.getNodesList().size());
	}
	
	public void testEdgesForNullNodes () {
		for(Edge edge : parserResult.getEdgesList()) {
			Node fromNode = edge.getFromNode();
			Node toNode = edge.getToNode();
			assertEquals("edge node should never be null", false, (fromNode == null));
			assertEquals("edge node should never be null", false, (toNode == null));
		}

	}	

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

	/*
	 * test rel link 'subtype' for type chair
	 * id = 1
	 */
	public void testEdge_chair_subtype() throws NoSuchRelationLinkException {
		testingEdge(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_subtype), testNode_chair, "test#Armchair", 1);
		testingEdge(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_subtype), testNode_furniture, "test#Chair", 1);
	}

	/*
	 * test rel link 'similar' for type chair
	 * id = 2
	 */
	public void testEdge_chair_similar() throws NoSuchRelationLinkException {
		testingEdge(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_similar),testNode_chair,"test#OtherChairs", 1);
	}

	/*
	 * test rel link 'reverse' for type chair
	 * id = 3
	 */
	public void testEdge_chair_reverse() throws NoSuchRelationLinkException {
		//testingRelationLink("reverse", 5, testType_chair, "", 0);
	}

	/*
	 * test rel link 'part' for type chair
	 * id = 4
	 */
	public void testEdge_chair_part() throws NoSuchRelationLinkException {
		// these links are in reversed order, so we are testing
		// types chair, backrest and leg.
		testingEdge(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_part), testNode_chair, "test#Backrest", 1);
		testingEdge(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_part), testNode_chair, "test#Leg", 1);
	}

	/*
	 * test rel link 'substance' for type chair
	 * id = 5
	 */
	public void testEdge_chair_substance() throws NoSuchRelationLinkException {
		testingEdge(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_substance), testNode_chair, "test#SomeSubstanceNode", 1);
		testingEdge(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_substance), testNode_chair, "test#SomeSubstanceNode", 1);
	}

	/*
	 * test rel link 'instance' for type chair
	 * id = 6
	 */
	public void testEdge_chair_instance() throws NoSuchRelationLinkException {
		testingEdge(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_instance), testNode_chair, "test#MyChair", 0);
		// 2 here to account for a fact that rdf resource is an instance of rdf-schema#Class
		// so we have 1 type that instance of type 'chair' + 1 rdf-schema#Class
		testingEdge(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_instance), testNode_myChair, "test#Chair", 1);
	}

	/*
	 * test rel link 'exclusion/complement' for type chair
	 * id = 7
	 */
	public void testEdge_chair_complement() throws NoSuchRelationLinkException {
		testingEdge(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_complement), testNode_chair, "test#Table", 1);
		testingEdge(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_complement), testNode_table, "test#Chair", 0);
	}

	/*
	 * test rel link 'location' for type chair
	 * id = 8
	 */
	public void testEdge_chair_location() throws NoSuchRelationLinkException {
		testingEdge(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_location), testNode_chair, "test#SomeLocation", 1);
		testingEdge(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_location), testNode_someLocation, "test#Chair", 0);
	}

	/*
	 * test rel link 'member' for type chair
	 * id = 9
	 */
	public void testEdge_chair_member() throws NoSuchRelationLinkException {
		testingEdge(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_member), testNode_chair, "test#AllChairs", 1);
		testingEdge(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_member), testNode_allChairs, "test#Chair", 0);
	}

	/*
	 * test rel link 'object' for type chair
	 * id = 10
	 */
	public void testEdge_chair_object() throws NoSuchRelationLinkException {
		testingEdge(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_object), testNode_chair, "test#SomeObject",1);
		testingEdge(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_object), testNode_chair, "test#ACHRONYM", 1);
	}

	/*
	 * test rel link 'url' for type chair
	 * id = 11
	 */
	public void testEdge_chair_url() throws NoSuchRelationLinkException {
		testingEdge(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_url), testNode_chair, "OntoRama", 1);
		testingEdge(OntoramaConfig.getEdgeType(WebKbConstants.edgeName_url), testNode_url, "test#Chair", 0);
	}

	public void testNodeTypes () {
		List<Node> nodesList = parserResult.getNodesList();
		List<Node> conceptsList = new ArrayList<Node>();
		List<Node> relationsList = new ArrayList<Node>();
		for (Node cur : nodesList) {
			NodeType curNodeType = cur.getNodeType();
			if (curNodeType == OntoramaConfig.CONCEPT_TYPE) {
				conceptsList.add(cur);
			} else if (curNodeType == OntoramaConfig.RELATION_TYPE) {
				relationsList.add(cur);
			} else {
				fail("unknown node type");
			}
		}
		assertEquals("number of concept nodes ", 26, conceptsList.size());
		assertEquals("number of relation nodes ", 5, relationsList.size());

	}

    public void testInvalidRDF_extraColumnInElementName() throws java.lang.ClassNotFoundException,
            java.lang.IllegalAccessException, java.lang.InstantiationException,
            SourceException, CancelledQueryException {

		source = (Source) Class.forName(sourcePackageName).newInstance();
    	Reader r = source.getSourceResult("examples/test/data/testCase-invalidRDF-1.rdf", new Query("test#Chair")).getReader();
        parser = new RdfDamlParser();
        try {
            parser.getResult(r);
            fail("failed to catch ParserException for invalid RDF file");
        } catch (ParserException e) {
        }
    }

    public void testInvalidRDF_DoubleSlashComments() throws java.lang.ClassNotFoundException,
            java.lang.IllegalAccessException, java.lang.InstantiationException,
            SourceException, CancelledQueryException {

    	source = (Source) Class.forName(sourcePackageName).newInstance();
    	Reader r = source.getSourceResult("examples/test/data/testCase-invalidRDF-2.rdf", new Query("test#Chair")).getReader();
        parser = new RdfDamlParser();
        try {
            parser.getResult(r);
            fail("failed to catch ParserException for invalid RDF file");
        } catch (ParserException e) {
        }

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
        List<Edge> edges = getEdgesFromList( fromNode, toNodeName, edgeType);
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
    protected Node getGraphNodeFromList (String name, List<Node> list) {
    	for(Node cur : list) {
            if (cur.getName().equals(name)) {
                return cur;
            }
        }
        return null;
    }

    protected List<Edge> getEdgesFromList (Node fromNode, String toNodeName, EdgeType edgeType) {
        List<Edge> result = new ArrayList<Edge>();
        List<Edge> allEdges = parserResult.getEdgesList();
        for (Edge edge : allEdges) {
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