package ontorama.ontotools.parser.xml;

import java.io.Reader;

import junit.framework.TestCase;
import ontorama.OntoramaConfig;
import ontorama.model.graph.Node;
import ontorama.ontotools.parser.Parser;
import ontorama.ontotools.parser.ParserResult;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.source.Source;

public class TestXmlParserFull extends TestCase {
	
	String sourcePackageName = "ontorama.ontotools.source.JarSource";
	ParserResult parserResult;

	public TestXmlParserFull(String name) {
		super(name);
	}
	
	@Override
	protected void setUp() throws Exception {
		OntoramaConfig.instantiateBackend(OntoramaConfig.defaultBackend, null);
		Source source = (Source) (Class.forName(sourcePackageName).newInstance());
		Reader r = source.getSourceResult("examples/ontorama-test.xml", new Query("root")).getReader();

		Parser parser = new XmlParserFull();
		parserResult = parser.getResult(r);
	}	
	
	public void testNodesNum () {
		assertEquals("number of nodes ", 58, parserResult.getNodesList().size());
	}
	
	public void testEdgesNum () {
		assertEquals("number of edges", 72, parserResult.getEdgesList().size());
	}
	
	public void testConceptNode () {
		Node node = getNodeFromNodesList("node2");
		assert node != null : "Result should contain node2 ";
		assertEquals("node2 description ", "Node2", node.getDescription());
	}
	
	public void testRelationNode () {
		Node node = getNodeFromNodesList("generalRelationType");
		assertEquals("result should contain generalRelationType ", true, (node != null));	
	}

	
	private Node getNodeFromNodesList (String nodeName) {
		for(Node cur: parserResult.getNodesList()) {
			if (cur.getName().equals(nodeName)) {
				return cur;
			}
		}
		return null;
	}
	

}
