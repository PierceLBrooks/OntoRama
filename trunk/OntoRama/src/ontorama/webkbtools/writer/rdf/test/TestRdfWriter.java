package ontorama.webkbtools.writer.rdf.test;import java.io.Reader;import java.io.StringReader;import java.io.StringWriter;import java.util.Iterator;import java.util.LinkedList;import java.util.List;import junit.framework.TestCase;import ontorama.OntoramaConfig;import ontorama.model.graph.Edge;import ontorama.model.graph.EdgeImpl;import ontorama.model.graph.EdgeType;import ontorama.model.graph.Graph;import ontorama.model.graph.GraphImpl;import ontorama.model.graph.Node;import ontorama.model.graph.NodeImpl;import ontorama.model.util.GraphModificationException;import ontorama.webkbtools.TestWebkbtoolsPackage;import ontorama.webkbtools.query.parser.Parser;import ontorama.webkbtools.query.parser.ParserResult;import ontorama.webkbtools.query.parser.rdf.RdfDamlParser;import ontorama.webkbtools.NoSuchRelationLinkException;import ontorama.webkbtools.ParserException;import ontorama.webkbtools.writer.ModelWriter;import ontorama.webkbtools.writer.ModelWriterException;import ontorama.webkbtools.writer.rdf.RdfModelWriter;import org.tockit.events.EventBroker;/* * Created by IntelliJ IDEA. * User: nataliya * Date: 4/10/2002 * Time: 10:48:08 * To change this template use Options | File Templates. */public class TestRdfWriter extends TestCase {    private ontorama.model.graph.Graph _graph;    private StringWriter _writer;    private List _testNodesList;    private List _testEdgesList;    private String _chairName = "Chair";    private String _armchairName = "Armchair";    private String _furnitureName = "Furniture";    private String _backrestName= "Backrest";    private String _legName = "Leg";    private String _otherChairsName = "OtherChairs";    private String _synonymChairName = "_chair";    private static String _ontoramaNamespace = "http://www.webkb.org/ontorama/test#";    private static String _rdfsNamespace = "http://www.w3.org/2000/01/rdf-schema#";    private static String _dcNamespace = "http://purl.org/metadata/dublin_core#";    private static String _damlNamespace = "http://www.daml.org/2000/10/daml-ont#";    private static String _pmNamespace = "http://www.webkb.org/kb/theKB_terms.rdf/pm#";    private static String _rdfNamespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";    private ontorama.model.graph.EdgeType _edgeType_subtype;    private ontorama.model.graph.EdgeType _edgeType_part;    private ontorama.model.graph.EdgeType _edgeType_similar;    private ontorama.model.graph.EdgeType _edgeType_synonym;    private ontorama.model.graph.Node _chair;    private ontorama.model.graph.Node _armchair;    private ontorama.model.graph.Node _furniture;    private ontorama.model.graph.Node _backrest;    private ontorama.model.graph.Node _leg;    private ontorama.model.graph.Node _otherChairs;    private ontorama.model.graph.Node _synonym;    private ontorama.model.graph.Edge _edge1;    private ontorama.model.graph.Edge _edge6;    /**     * Test Rdf Writer.     * The way we are going to test this is: Write a stream from our model, then read and parse     * it and then check if it is consistent with the model.     */    public TestRdfWriter(String s) {        super(s);    }    public void setUp() throws NoSuchRelationLinkException, ModelWriterException, GraphModificationException, ParserException {        _testEdgesList = new LinkedList();        _testNodesList = new LinkedList();        _graph = new ontorama.model.graph.GraphImpl(new EventBroker());        _chair = new ontorama.model.graph.NodeImpl(_chairName, _ontoramaNamespace + _chairName);        _armchair = new ontorama.model.graph.NodeImpl(_armchairName, _ontoramaNamespace + _armchairName);        _furniture = new ontorama.model.graph.NodeImpl(_furnitureName, _ontoramaNamespace + _furnitureName);        _backrest = new ontorama.model.graph.NodeImpl(_backrestName, _ontoramaNamespace + _backrestName);        _leg = new ontorama.model.graph.NodeImpl(_legName, _ontoramaNamespace + _legName);        _otherChairs = new ontorama.model.graph.NodeImpl(_otherChairsName, _ontoramaNamespace + _otherChairsName);        _synonym = new ontorama.model.graph.NodeImpl(_synonymChairName);        _edgeType_subtype = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype);        _edgeType_subtype.setNamespace(_rdfsNamespace);        _edgeType_part = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_part);        _edgeType_part.setNamespace(_pmNamespace);        _edgeType_similar = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_similar);        _edgeType_similar.setNamespace(_pmNamespace);        _edgeType_synonym = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_synonym);        _edgeType_synonym.setNamespace(_rdfsNamespace);        _edge1 = new ontorama.model.graph.EdgeImpl(_chair, _armchair, _edgeType_subtype );        _graph.addEdge(_edge1);        ontorama.model.graph.Edge edge2 = new ontorama.model.graph.EdgeImpl(_furniture, _chair, _edgeType_subtype);        _graph.addEdge(edge2);        ontorama.model.graph.Edge edge3 = new ontorama.model.graph.EdgeImpl(_chair, _backrest, _edgeType_part);        _graph.addEdge(edge3);        ontorama.model.graph.Edge edge4 = new ontorama.model.graph.EdgeImpl(_chair, _leg, _edgeType_part);        _graph.addEdge(edge4);        ontorama.model.graph.Edge edge5 = new ontorama.model.graph.EdgeImpl(_chair, _otherChairs, _edgeType_similar);        _graph.addEdge(edge5);        _edge6 = new ontorama.model.graph.EdgeImpl(_chair, _synonym, _edgeType_synonym);        _graph.addEdge(_edge6);        ModelWriter modelWriter = new RdfModelWriter();        _writer = new StringWriter();        modelWriter.write(_graph, _writer);        String str = _writer.toString();        Reader r = new StringReader(str);        Parser parser = new RdfDamlParser();        ParserResult parserResult = parser.getResult(r);        _testNodesList = parserResult.getNodesList();        _testEdgesList = parserResult.getEdgesList();    }    public void testResultingNodesList () {        assertEquals("number of nodes read from RDF should be the same as in the original model", 7, _testNodesList.size());    }    public void testResultingEdgesList () {        assertEquals("number of edges read from RDF should be the same as in the original model", 6, _testEdgesList.size());    }    public void testNodeInResultingModel_chair () {        ontorama.model.graph.Node chairNode = findNodeInListByName("test#" + _chairName);        assertEquals("node exist in the model ", true, (chairNode != null));        assertEquals("identifiers should match ", _chair.getIdentifier(), chairNode.getIdentifier() );    }    public void testNodeInResultingModel_armchair () {        ontorama.model.graph.Node armchairNode = findNodeInListByName("test#" + _armchairName);        assertEquals("node exist in the model ", true, (armchairNode != null));        assertEquals("identifiers should match ", armchairNode.getIdentifier(), armchairNode.getIdentifier() );    }    public void testEdgeInResultModel_edge1 () {        ontorama.model.graph.Edge edge = findEdgeInList("test#" + _chairName, "test#" + _armchairName, _edgeType_subtype);        assertEquals("edge exist in the model", true, (edge != null));    }    public void testEdgeInResultModel_edge6 () {        ontorama.model.graph.Edge edge = findEdgeInList("test#" + _chairName, _synonymChairName, _edgeType_synonym);        assertEquals("edge exist in the model", true, (edge != null));    }    private ontorama.model.graph.Node findNodeInListByName (String nodeName) {        Iterator it = _testNodesList.iterator();        while (it.hasNext()) {            ontorama.model.graph.Node curNode = (ontorama.model.graph.Node) it.next();            if (curNode.getName().equals(nodeName)) {                return curNode;            }        }        return null;    }    private ontorama.model.graph.Edge findEdgeInList (String fromNodeName, String toNodeName, ontorama.model.graph.EdgeType edgeType) {        Iterator it = _testEdgesList.iterator();        while (it.hasNext()) {            ontorama.model.graph.Edge curEdge = (ontorama.model.graph.Edge) it.next();            ontorama.model.graph.Node fromNode = curEdge.getFromNode();            ontorama.model.graph.Node toNode = curEdge.getToNode();            if (fromNode.getName().equals(fromNodeName)) {                if (toNode.getName().equals(toNodeName)) {                    if (curEdge.getEdgeType().equals(edgeType)) {                        return curEdge;                    }                }            }        }        return null;    }}