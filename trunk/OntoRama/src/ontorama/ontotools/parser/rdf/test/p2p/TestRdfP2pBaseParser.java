package ontorama.ontotools.parser.rdf.test.p2p;

import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import ontorama.OntoramaConfig;
import ontorama.backends.Backend;
import ontorama.backends.p2p.model.P2PEdge;
import ontorama.backends.p2p.model.P2PNode;
import ontorama.ontotools.parser.Parser;
import ontorama.ontotools.parser.ParserResult;
import ontorama.ontotools.parser.rdf.RdfP2pParser;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.source.Source;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 27/09/2002
 * Time: 14:45:18
 * To change this template use Options | File Templates.
 */
public class TestRdfP2pBaseParser extends TestCase {

    private List _nodesList;
    private List _edgesList;
    
    private final String sourcePackageName = "ontorama.ontotools.source.JarSource";
    
    /**
     * Note: The idea is to reuse this test case for two different files
     * containing the same data in different formats (short reification and
     * verbose reifincation). To do this we only need to change sourceUri
     * location. Therefore, this class should be only used as a base class and
     * not run by itself - since sourceUri is undefined.
     */
    protected String sourceUri = null;

   public TestRdfP2pBaseParser(String name) {
        super(name);
    }
    
    protected void setUp() throws Exception {
    	
    	Backend backend = OntoramaConfig.instantiateBackend("ontorama.backends.p2p.P2PBackend", null);
    	OntoramaConfig.activateBackend(backend);

        OntoramaConfig.loadAllConfig("examples/test/p2p/examplesConfig.xml",
                "ontorama.properties", "examples/test/p2p/config.xml");

        Source source = (Source) (Class.forName(sourcePackageName).newInstance());       
        Reader r = source.getSourceResult(sourceUri, new Query("wn#Tail","","","")).getReader();
        Parser parser = new RdfP2pParser();
        ParserResult parserResult = parser.getResult(r);

        _nodesList = parserResult.getNodesList();
        _edgesList = parserResult.getEdgesList();
        
        //printNodesAndEdges();

    }
    
    /**
     * purpose of this test is to only insure that this test is run correctly - 
     * see Note for sourceUri
     */
    public void testSourceUriIsDefined () {
    	assertEquals("sourceUri should be defined in extending test cases, see javadoc for more details " +
    					" it shouldn't be null ", true, (sourceUri != null) );
    }

    public void testNodesNum () {
        assertEquals("number of nodes ", 6, _nodesList.size() );
    }

    public void testEdgesNum () {
        assertEquals("number of edges ", 5, _edgesList.size());
    }

    public void testNode_tail () {
        P2PNode tailNode = getNodeFromList("http://www.webkb.org/kb/theKB_terms.rdf/wn#Tail");
        assertEquals("returned list should contain node wn#Tail ", true, (tailNode != null));
        assertEquals("number of assertions for node wn#Tail ", 2, tailNode.getAssertions().size());
        assertEquals("number of rejections for node wn#Tail ", 1, tailNode.getRejectionsList().size());
    }

    public void testNode_outgrowth () {
        P2PNode node = getNodeFromList("http://www.webkb.org/kb/theKB_terms.rdf/wn#Outgrowth");
        assertEquals("returned list should contain node wn#Outgrowth ", true, (node != null));
        assertEquals("number of assertions for node wn#Outgrowth ", 0, node.getAssertions().size());
        assertEquals("number of rejections for node wn#Outgrowth ", 0, node.getRejectionsList().size());
    }

	public void testNode_dock_4 () {
		P2PNode node = getNodeFromList("http://www.webkb.org/kb/theKB_terms.rdf/wn#Dock_4");
		assertEquals("returned list should contain node wn#Dock_4 ", true, (node != null));
	}


    public void testEdgeForRejections () {
        P2PNode toNode = getNodeFromList("http://www.webkb.org/kb/theKB_terms.rdf/wn#Tail");
        P2PNode fromNode = getNodeFromList("http://www.webkb.org/kb/theKB_terms.rdf/wn#Outgrowth");
        P2PEdge edge = getEdgeFromList(fromNode, toNode);
        assertEquals("edge from wn#Outgrouwth to wn#Tail" + 
							" should exist in the edgesList ", true, (edge != null));
        assertEquals("number of asserstions for edge: " + printEdge(edge), 0, edge.getAssertions().size());
        assertEquals("number of rejections for edge: " + printEdge(edge), 1, edge.getRejections().size());
    }
    
    public void testEdgeForAssertions () {
    	P2PNode fromNode = getNodeFromList("http://www.webkb.org/kb/theKB_terms.rdf/wn#Tail");
    	P2PNode toNode = getNodeFromList("http://www.webkb.org/kb/theKB_terms.rdf/wn#Dock_4");
    	P2PEdge edge = getEdgeFromList(fromNode, toNode);
    	assertEquals("edge from wn#Tail to wn#Dock_4 " + 
    							" should exist in the edgesList ", true, (edge != null));
    	assertEquals("number of asserstions for edge: " + printEdge(edge), 1, edge.getAssertions().size());
    	assertEquals("number of rejections for edge: " + printEdge(edge), 0, edge.getRejections().size());
    }

    private P2PNode getNodeFromList (String nodeName) {
        Iterator it = _nodesList.iterator();
        while (it.hasNext()) {
            P2PNode node = (P2PNode) it.next();
            if (node.getName().equals(nodeName)) {
                return node;
            }
        }
        return null;
    }

    private P2PEdge getEdgeFromList (P2PNode fromNode, P2PNode toNode) {
        Iterator it = _edgesList.iterator();
        while (it.hasNext()) {
            P2PEdge edge = (P2PEdge) it.next();
            if (edge.getFromNode().equals(fromNode)) {
                if (edge.getToNode().equals(toNode)) {
                    return edge;
                }
            }
        }
        return null;
    }
    
    private String printEdge (P2PEdge edge) {
    	String str = edge.getFromNode().getName() + " -> " + edge.getEdgeType().getName() + 
    								" -> " + edge.getToNode().getName();
    	return str;
    }
    
    /**
     * used for debugging
     */
    private void printNodesAndEdges() {
    	System.out.println("\n\n\n");
    	Iterator it = _nodesList.iterator();
    	while (it.hasNext()) {
    		P2PNode node = (P2PNode) it.next();
    		System.out.println("node = " + node);
    		System.out.println("\tassertions: " + node.getAssertions());
    		System.out.println("\trejections: " + node.getRejectionsList());
    	}
    	it = _edgesList.iterator();
    	while (it.hasNext()) {
    		P2PEdge edge = (P2PEdge) it.next();
    		System.out.println("edge = " + edge);
    		System.out.println("\trejections: " + edge.getRejections());
    		System.out.println("\tassertions: " + edge.getAssertions());
    	}
    	
    }
}

