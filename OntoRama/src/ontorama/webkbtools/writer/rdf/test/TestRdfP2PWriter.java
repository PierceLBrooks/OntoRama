/* * Created by IntelliJ IDEA. * User: nataliya * Date: 8/10/2002 * Time: 11:34:38 * To change template for new class use * Code Style | Class Templates options (Tools | IDE Options). */package ontorama.webkbtools.writer.rdf.test;import junit.framework.TestCase;import ontorama.backends.p2p.model.*;import ontorama.webkbtools.writer.ModelWriter;import ontorama.webkbtools.writer.ModelWriterException;import ontorama.webkbtools.writer.rdf.RdfModelWriter;import ontorama.webkbtools.writer.rdf.RdfP2PWriter;import ontorama.webkbtools.TestWebkbtoolsPackage;import ontorama.webkbtools.query.parser.Parser;import ontorama.webkbtools.query.parser.ParserResult;import ontorama.webkbtools.query.parser.rdf.RdfDamlParser;import ontorama.webkbtools.query.parser.rdf.RdfP2pParser;import ontorama.webkbtools.util.NoSuchRelationLinkException;import ontorama.webkbtools.util.ParserException;import ontorama.model.Graph;import ontorama.model.EdgeType;import ontorama.model.Node;import ontorama.model.util.GraphModificationException;import ontorama.OntoramaConfig;import java.io.StringWriter;import java.io.Reader;import java.io.StringReader;import java.io.Writer;import java.net.URI;import java.net.URISyntaxException;import java.util.List;import java.util.Iterator;import com.hp.hpl.mesa.rdf.jena.common.prettywriter.PrettyWriter;public class TestRdfP2PWriter extends TestCase {    private P2PGraph _p2pGraph;    private String _ns_webkb = "http://www.webkb.org/kb/theKB_terms.rdf/";    private String _ns_dc = "http://www.cogsi.princeton.edu/~wn/";    private static String _ontoramaNamespace = "http://www.webkb.org/ontorama/test#";    private static String _rdfsNamespace = "http://www.w3.org/2000/01/rdf-schema#";    private static String _dcNamespace = "http://purl.org/metadata/dublin_core#";    private static String _damlNamespace = "http://www.daml.org/2000/10/daml-ont#";    private static String _pmNamespace = "http://www.webkb.org/kb/theKB_terms.rdf/pm#";    private static String _rdfNamespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";    private EdgeType _edgeType_subtype;    private EdgeType _edgeType_part;    private EdgeType _edgeType_similar;    private EdgeType _edgeType_synonym;    private EdgeType _edgeType_creator;    private EdgeType _edgeType_description;    private P2PNode _tail;    private P2PNode _wnCreator;    private P2PNode _tailComment;    private P2PNode _outgrowth;    private P2PNode _dock;    private P2PNode _tailSynonym;    private P2PEdge _e1;    private P2PEdge _e2;    private P2PEdge _e3;    private P2PEdge _e4;    private P2PEdge _e5;    public TestRdfP2PWriter(String s) {        super(s);    }    public void setUp() throws ModelWriterException, GraphModificationException,            NoSuchRelationLinkException, URISyntaxException,            ParserException {        _p2pGraph = new P2PGraphImpl();        _edgeType_subtype = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype);        _edgeType_subtype.setNamespace(_rdfsNamespace);        _edgeType_part = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_part);        _edgeType_part.setNamespace(_pmNamespace);        _edgeType_similar = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_similar);        _edgeType_similar.setNamespace(_pmNamespace);        _edgeType_synonym = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_synonym);        _edgeType_synonym.setNamespace(_rdfsNamespace);        _edgeType_creator = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_creator);        _edgeType_creator.setNamespace(_dcNamespace);        _edgeType_description = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_description);        _edgeType_description.setNamespace(_rdfsNamespace);        _tail = new P2PNodeImpl("wn#Tail", _ns_webkb + "wn#Tail");        _wnCreator = new P2PNodeImpl(_ns_dc, _ns_dc);        _tailComment = new P2PNodeImpl("some comment for tail node", "some comment for tail node");        _outgrowth = new P2PNodeImpl("wn#Outgrowth", _ns_webkb + "wn#Outgrowth");        _dock = new P2PNodeImpl("wn#Dock_4", _ns_webkb + "wn#Dock_4");        _tailSynonym = new P2PNodeImpl("tail", "tail");        _p2pGraph.addNode(_tail);        _p2pGraph.addNode(_wnCreator);        _p2pGraph.addNode(_tailComment);        _p2pGraph.addNode(_outgrowth);        _p2pGraph.addNode(_dock);        _e1 = new P2PEdgeImpl(_outgrowth, _tail, _edgeType_subtype);        _e2 = new P2PEdgeImpl(_dock, _tail, _edgeType_subtype);        _e3 = new P2PEdgeImpl(_tail, _wnCreator, _edgeType_creator);        _e4 = new P2PEdgeImpl(_tail, _tailComment, _edgeType_description);        _e5 = new P2PEdgeImpl(_tail, _tailSynonym, _edgeType_synonym);        URI userUri1 = new URI("mailto:johan@ontorama.org");        URI userUri2 = new URI("mailto:henrik@ontorama.org");        URI userUri3 = new URI("mailto:nataliya@ontorama.org");        URI userUri4 = new URI("mailto:joeDoe@ontorama.org");        _tail.addAssertion(userUri1);        _tail.addAssertion(userUri2);        _tail.addRejection(userUri4);        _e1.addRejection(userUri3);        _e2.addAssertion(userUri1);        _p2pGraph.addEdge(_e1);        _p2pGraph.addEdge(_e2);        _p2pGraph.addEdge(_e3);        _p2pGraph.addEdge(_e4);        _p2pGraph.addEdge(_e5);        ModelWriter modelWriter = new RdfP2PWriter();        Writer _writer = new StringWriter();        modelWriter.write(_p2pGraph, _writer);        String str = _writer.toString();        System.out.println(str);        Reader r = new StringReader(str);        System.out.println("\n\n trying to read output back");        Parser parser = new RdfP2pParser();        ParserResult parserResult = parser.getResult(r);        List _testNodesList = parserResult.getNodesList();        Iterator it = _testNodesList.iterator();        while (it.hasNext()) {            P2PNode node = (P2PNode) it.next();            System.out.println("p2p node: " + node);            System.out.println("\tassertions: " + node.getAssertionsList());            System.out.println("\trejections: " + node.getRejectionsList());        }        List _testEdgesList = parserResult.getEdgesList();        it = _testEdgesList.iterator();        while (it.hasNext()) {            P2PEdge edge = (P2PEdge) it.next();            System.out.println("p2p edge: " + edge);            System.out.println("\tassertions: " + edge.getAssertionsList());            System.out.println("\trejections: " + edge.getRejectionsList());        }    }    public void testSomething() {    }}