package ontorama.ontotools.parser.rdf.test;

import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import ontorama.OntoramaConfig;
import ontorama.backends.examplesmanager.ExamplesBackend;
import ontorama.backends.p2p.model.P2PEdge;
import ontorama.backends.p2p.model.P2PNode;
import ontorama.ontotools.parser.Parser;
import ontorama.ontotools.parser.ParserResult;
import ontorama.ontotools.parser.rdf.RdfP2pParser;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.source.Source;
import ontorama.util.TestingUtils;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 27/09/2002
 * Time: 14:45:18
 * To change this template use Options | File Templates.
 */
public class TestRdfP2pParser extends TestCase {

    private List _nodesList;
    private List _edgesList;

   public TestRdfP2pParser(String name) {
        super(name);
    }
    protected void setUp() throws Exception {
    	
//    	Backend backend = OntoramaConfig.instantiateBackend("ontorama.backends.p2p.P2PBackend", null);
//    	OntoramaConfig.activateBackend(backend);

        OntoramaConfig.loadAllConfig("examples/test/p2p/examplesConfig.xml",
                "ontorama.properties", "examples/test/p2p/config.xml");
    	ExamplesBackend backend = (ExamplesBackend) OntoramaConfig.instantiateBackend(OntoramaConfig.defaultBackend, null);
    	OntoramaConfig.activateBackend(backend);
    	
        backend.setCurrentExample(TestingUtils.getExampleByName("p2p_test1"));

        Source source = (Source) (Class.forName(backend.getSourcePackageName()).newInstance());
        
        Reader r = source.getSourceResult(backend.getSourceUri(), new Query("wn#Tail")).getReader();

        Parser parser = new RdfP2pParser();
        ParserResult parserResult = parser.getResult(r);

        _nodesList = parserResult.getNodesList();
        _edgesList = parserResult.getEdgesList();

        System.out.println("\n\n\n");
        Iterator it = parserResult.getNodesList().iterator();
        while (it.hasNext()) {
            P2PNode node = (P2PNode) it.next();
            System.out.println("node = " + node);
            System.out.println("\tassertions: " + node.getAssertions());
            System.out.println("\trejections: " + node.getRejectionsList());
        }
        it = parserResult.getEdgesList().iterator();
        while (it.hasNext()) {
            P2PEdge edge = (P2PEdge) it.next();
            System.out.println("edge = " + edge);
            System.out.println("\trejections: " + edge.getRejections());
            System.out.println("\tassertions: " + edge.getAssertions());
        }
    }

    public void testNodesNum () {
        assertEquals("number of nodes ", 11, _nodesList.size() );
    }

    public void testEdgesNum () {
        assertEquals("number of edges ", 11, _edgesList.size());
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

    public void testEdge () {
        P2PNode toNode = getNodeFromList("http://www.webkb.org/kb/theKB_terms.rdf/wn#Tail");
        P2PNode fromNode = getNodeFromList("http://www.webkb.org/kb/theKB_terms.rdf/wn#Outgrowth");
        P2PEdge edge = getEdgeFromList(fromNode, toNode);
        assertEquals("edge should exist in the edgesList ", true, (edge != null));
        assertEquals("number of asserstions for edge ", 1, edge.getAssertions().size());
        assertEquals("number of rejections for edge ", 1, edge.getRejections().size());
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
}

