package ontorama.webkbtools.writer.rdf.test;

import junit.framework.TestCase;
import ontorama.OntoramaConfig;
import ontorama.model.*;
import ontorama.model.util.GraphModificationException;
import ontorama.webkbtools.TestWebkbtoolsPackage;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.writer.ModelWriter;
import ontorama.webkbtools.writer.ModelWriterException;
import ontorama.webkbtools.writer.rdf.RdfWriter;

import java.io.PrintWriter;
import java.io.Writer;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 4/10/2002
 * Time: 10:48:08
 * To change this template use Options | File Templates.
 */
public class TestRdfWriter extends TestCase {
    public TestRdfWriter(String s) {
        super(s);
    }

    public void setUp() throws NoSuchRelationLinkException, ModelWriterException, GraphModificationException {
        String ontoramaNamespace = "http://ontorama.org/ont#";
        Graph graph = new GraphImpl();
        Node node1 = new NodeImpl("node1", ontoramaNamespace + "node1");
        Node node2 = new NodeImpl("node2", ontoramaNamespace + "node2");
        EdgeType edgeType_subtype = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype);
        edgeType_subtype.setNamespace(ontoramaNamespace);
        Edge edge1 = new EdgeImpl(node1, node2, edgeType_subtype );
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addEdge(edge1);

        ModelWriter modelWriter = new RdfWriter();
        Writer writer = new PrintWriter(System.out);
        modelWriter.write(graph, writer);
    }

    public void testSomething() {

    }
}
