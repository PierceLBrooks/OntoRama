/**
 * Title:			P2P - protocol
 * Description:
 * Copyright:		Copyright (c) 2002
 * Company:			DSTC
 * @author:		johang
 * @version:		1.0, 2:08:27 PM
 */
package ontorama.backends.p2p.test;

import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;

import ontorama.OntoramaConfig;
import ontorama.backends.p2p.model.P2PEdge;
import ontorama.backends.p2p.model.P2PEdgeImpl;
import ontorama.backends.p2p.model.P2PGraph;
import ontorama.backends.p2p.model.P2PGraphImpl;
import ontorama.backends.p2p.model.P2PNode;
import ontorama.backends.p2p.model.P2PNodeImpl;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.GraphModificationException;
import ontorama.ontotools.TestWebkbtoolsPackage;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.writer.ModelWriter;
import ontorama.ontotools.writer.ModelWriterException;
import ontorama.ontotools.writer.rdf.RdfModelWriter;
import ontorama.ontotools.parser.rdf.RdfDamlParser;
import ontorama.ontotools.parser.ParserResult;

/**
 * Description
 *
 *
 * Last revised:	2:08:26 PM
 */
public class TestP2PBackend {

	public static void main(String[] args) {
		TestP2PBackend test = new TestP2PBackend();
		test.test2();
	}

	public void test1(){
        try {
            RdfDamlParser parser = new RdfDamlParser();
            String filename = "d:/temp/wn_tail.rdf";
            System.out.println();
            System.out.println("filename = " + filename);
            Reader reader = new FileReader(filename);
			ParserResult parserResult = parser.getResult(reader);

			P2PGraph graph = new P2PGraphImpl();
			graph.add(parserResult);

	        ModelWriter modelWriter = new RdfModelWriter();
	        Writer writer = new PrintWriter(System.out);
	        modelWriter.write(graph, writer);

        } catch (Exception e) {
            System.out.println("Failed: " + e);
            System.exit(-1);
        }
	}

	public void test2() {
		try {
	        String ontoramaNamespace = "http://ontorama.org/ont#";
	        URI creator = new URI("ontorama::user@domain.com");
   	        URI creator2 = new URI("ontorama::john.doe@domain.com");
	        URI creator3 = new URI("ontorama::lisa@domain.com");
	        P2PGraph graph = new P2PGraphImpl();
	        P2PNode node1 = new P2PNodeImpl("node1", ontoramaNamespace + "node1",creator,null);
	        P2PNode node2 = new P2PNodeImpl("node2", ontoramaNamespace + "node2",creator,null);
	        P2PNode node3 = new P2PNodeImpl("node3", ontoramaNamespace + "node3",creator,null);
	        P2PNode node4 = new P2PNodeImpl("node4", ontoramaNamespace + "node4",creator,null);
			node2.addAssertion(creator2);
			node1.addAssertion(creator2);
			node1.addRejection(creator3);


	        ontorama.model.graph.EdgeType edgeType_subtype = OntoramaConfig.getEdgeType(TestWebkbtoolsPackage.edgeName_subtype);
	        edgeType_subtype.setNamespace(ontoramaNamespace);
	        P2PEdge edge1 = new P2PEdgeImpl(node1, node2, edgeType_subtype,creator,null);
			edge1.addAssertion(creator2);
			edge1.addRejection(creator3);


	        P2PEdge edge2 = new P2PEdgeImpl(node2, node3, edgeType_subtype,creator,null);
   	        P2PEdge edge3 = new P2PEdgeImpl(node3, node4, edgeType_subtype,creator,null);


			graph.assertNode(node1,creator);
	        graph.assertNode(node2,creator);
	        graph.assertNode(node3,creator);
	        graph.assertNode(node4,creator);
	        graph.assertEdge(edge1,creator);
	        graph.assertEdge(edge2,creator);
	        graph.assertEdge(edge3,creator);

	        ModelWriter modelWriter = new RdfModelWriter();
	        Writer writer = new PrintWriter(System.out);
			modelWriter.write(graph, writer);


			P2PGraph searchGraph = new P2PGraphImpl();
			Query query = new Query("http://ontorama.org/ont#node2",null);
			query.setDepth(10);

			searchGraph = graph.search(query);
			modelWriter.write(searchGraph, writer);


		} catch (ModelWriterException e) {
		} catch (GraphModificationException e) {
		} catch (NoSuchRelationLinkException e) {
		} catch (URISyntaxException e) {
		}
	}

}
