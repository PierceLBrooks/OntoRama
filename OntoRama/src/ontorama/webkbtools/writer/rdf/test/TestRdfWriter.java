package ontorama.webkbtools.writer.rdf.test;

import junit.framework.TestCase;
import ontorama.OntoramaConfig;
import ontorama.model.*;
import ontorama.model.util.GraphModificationException;
import ontorama.webkbtools.TestWebkbtoolsPackage;
import ontorama.webkbtools.query.parser.rdf.RdfDamlParser;
import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.webkbtools.util.*;
import ontorama.webkbtools.writer.ModelWriter;
import ontorama.webkbtools.writer.ModelWriterException;
import ontorama.webkbtools.writer.rdf.RdfModelWriter;

import java.io.*;
import java.util.List;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 4/10/2002
 * Time: 10:48:08
 * To change this template use Options | File Templates.
 */
public class TestRdfWriter extends TestCase {

    private Graph _graph;
    private StringWriter _writer;

    public TestRdfWriter(String s) {
        super(s);
    }

    public void setUp() throws NoSuchRelationLinkException, ModelWriterException, GraphModificationException {
        String ontoramaNamespace = "http://www.webkb.org/ontorama/test#";
        String rdfsNamespace = "http://www.w3.org/2000/01/rdf-schema#";
        String dcNamespace = "http://purl.org/metadata/dublin_core#";
        String damlNamespace = "http://www.daml.org/2000/10/daml-ont#";
        String pmNamespace = "http://www.webkb.org/kb/theKB_terms.rdf/pm#";
        String rdfNamespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

        _graph = new GraphImpl();
        Node chair = new NodeImpl("Chair", ontoramaNamespace + "Chair");
        Node armchair = new NodeImpl("Armchair", ontoramaNamespace + "Armchair");
        Node furniture = new NodeImpl("Furniture", ontoramaNamespace + "Furniture");
        Node backrest = new NodeImpl("Backrest", ontoramaNamespace + "Backrest");
        Node leg = new NodeImpl("Leg", ontoramaNamespace + "Leg");
        Node otherChairs = new NodeImpl("OtherChairs", ontoramaNamespace + "OtherChairs");
        Node description = new NodeImpl("chair");

        EdgeType edgeType_subtype = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype);
        edgeType_subtype.setNamespace(rdfsNamespace);
        EdgeType edgeType_part = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_part);
        edgeType_part.setNamespace(pmNamespace);
        EdgeType edgeType_similar = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_similar);
        edgeType_similar.setNamespace(pmNamespace);
        EdgeType edgeType_synonym = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_synonym);
        edgeType_synonym.setNamespace(rdfsNamespace);

        Edge edge1 = new EdgeImpl(chair, armchair, edgeType_subtype );
        _graph.addEdge(edge1);
        Edge edge2 = new EdgeImpl(furniture, chair, edgeType_subtype);
        _graph.addEdge(edge2);
        Edge edge3 = new EdgeImpl(chair, backrest, edgeType_part);
        _graph.addEdge(edge3);
        Edge edge4 = new EdgeImpl(chair, leg, edgeType_part);
        _graph.addEdge(edge4);
        Edge edge5 = new EdgeImpl(chair, otherChairs, edgeType_similar);
        _graph.addEdge(edge5);

        Edge edge6 = new EdgeImpl(chair, description, edgeType_synonym);
        _graph.addEdge(edge6);


        ModelWriter modelWriter = new RdfModelWriter();
        _writer = new StringWriter();
        modelWriter.write(_graph, _writer);
    }

    public void testRdfResult() throws CancelledQueryException, ClassNotFoundException,
                                        InstantiationException, SourceException,
                                        IllegalAccessException,
                                        ParserException, NoSuchTypeInQueryResult {

        String str = _writer.toString();
        Reader r = new StringReader(str);
        System.out.println(str);

        Parser parser = new RdfDamlParser();
        ParserResult parserResult = parser.getResult(r);
        List nodesList = parserResult.getNodesList();
        System.out.println("nodesList = " + nodesList);
        List edgesList = parserResult.getEdgesList();
        System.out.println("edgesList = " + edgesList);


    }




}
