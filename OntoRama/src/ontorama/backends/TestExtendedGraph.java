package ontorama.backends;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;

import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.webkbtools.query.parser.rdf.RdfDamlParser;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.util.ParserException;

public class TestExtendedGraph {

	private TestExtendedGraph test = new TestExtendedGraph();
	
	public TestExtendedGraph() {
		
	}
	
	public void main(String[] args) {
		
		System.err.println("Now is the ExtGraphTested");
		this.testExtGraph();
		
		System.err.println("Now is the parser for the ExtGraphTested");
		this.testParser();

	}
	

	public void testParser() {
		
		try {
			ExtendedGraph extGraph = new ExtendedGraph();
			BufferedReader reader;
			RdfDamlParser parser = new RdfDamlParser();
			String fileName = "d:/temp/wn_tail.rdf";
			
			reader = new BufferedReader(new FileReader(fileName));
			
			ParserResult parserResult = parser.getResult(reader);
			extGraph.add(parserResult.getNodesList(),parserResult.getEdgesList());
			
			try {
				extGraph.setRoot("http://www.webkb.org/kb/theKB_terms.rdf/wn#Tail");
			} catch (NoSuchGraphNodeException e) {
				System.err.println("Could not find the root node");
			}
				
			System.err.println(extGraph.toRDFString());

		} catch (FileNotFoundException e) {
			System.err.println("Error1");
		} catch (ParserException e) {
			System.err.println("Error2");
		}
	}
	
	public void testExtGraph() {
		ExtendedGraph extGraph = new ExtendedGraph();
		
		GraphNode extNode1 = new GraphNode("node1","uri node 1");
		GraphNode extNode2 = new GraphNode("node2","uri node 2");		
		GraphNode extNode3 = new GraphNode("node3","uri node 3");
		
		LinkedList temp = new LinkedList();
		
		try {
			temp.add("Test description");
			extNode1.setProperty("Description", "http://undefined.se",temp);
			extNode2.setProperty("Description", "http://undefined.se", temp);
			extNode3.setProperty("Description", "http://undefined.se", temp);
			
			temp = new LinkedList();
			temp.add("Test Synonym");
			extNode1.setProperty("Synonym", "http://undefined.se", temp);
			extNode2.setProperty("Synonym", "http://undefined.se", temp);
			extNode3.setProperty("Synonym", "http://undefined.se", temp);
			
			temp = new LinkedList();
			temp.add("Test creator");
			extNode1.setProperty("Creator", "http://undefined.se", temp);
			extNode2.setProperty("Creator", "http://undefined.se", temp);
			extNode3.setProperty("Creator", "http://undefined.se", temp);
		} catch (NoSuchRelationLinkException e) {
			//do nothing
		}
		//Adds the nodes
		extGraph.addNode(extNode1);
		extGraph.addNode(extNode2);
		extGraph.addNode(extNode3);
		
		//Adds the relations
		extGraph.addEdge(extNode1.getFullName(), extNode2.getFullName(), 1, "http://undefined.se");
		extGraph.addEdge(extNode1.getFullName(), extNode3.getFullName(), 1, "http://undefined.se");

		try {
			extGraph.setRoot(extNode1.getFullName());
		} catch (NoSuchGraphNodeException e) {
			System.err.println("Could not find the root node");		
		}

		
		//Print the result
		try {
			System.err.println(extGraph.toRDFString());
			
			//updates a node
			temp = new LinkedList();
			temp.add("Test Synonym check for update");
			GraphNode newExtNode1 = extNode1.makeCopy();
			newExtNode1.setProperty("Synonym", "http://undefined.se", temp);
			extGraph.updateNode(newExtNode1);
		} catch (NoSuchRelationLinkException e) {
			System.err.println("ERROR1");
		}
	}
	
}
