package ontorama.backends.p2p.test;

import org.tockit.events.EventBroker;

import ontorama.OntoramaConfig;
import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.model.P2PGraph;
import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeImpl;
import ontorama.model.graph.Graph;
import ontorama.model.graph.GraphImpl;
import ontorama.model.graph.GraphModificationException;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeImpl;
import ontorama.model.graph.test.TestGraphPackage;
import ontorama.ontotools.NoSuchRelationLinkException;

import junit.framework.TestCase;

/**
 * @author nataliya
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TestP2PBackend2 extends TestCase {
	
	private Graph _graph;
	private P2PBackend _p2pBackend;

	/**
	 * Constructor for TestP2PBackend2.
	 * @param name
	 */
	public TestP2PBackend2(String name) {
		super(name);
	}
	
	protected void setUp() throws NoSuchRelationLinkException, GraphModificationException {
		_graph = new GraphImpl(new EventBroker());
		Node root = new NodeImpl("root");
		Node node1 = new NodeImpl("node1");
		Node node2 = new NodeImpl("node2");
		Node node1_1 = new NodeImpl("node1.1");
		Node node1_2 = new NodeImpl("node1.2");
		Node node1_3 = new NodeImpl("node1.3");
		Node node2_1 = new NodeImpl("node2.1");
		Node node2_2 = new NodeImpl("node2.2");
		Edge edge1 = new EdgeImpl(root, node1, OntoramaConfig.getEdgeType(TestGraphPackage.edgeName_subtype));
		Edge edge2 = new EdgeImpl(root, node2, OntoramaConfig.getEdgeType(TestGraphPackage.edgeName_subtype));
		Edge edge3 = new EdgeImpl(node1, node1_1, OntoramaConfig.getEdgeType(TestGraphPackage.edgeName_subtype));
		Edge edge4 = new EdgeImpl(node1, node1_2, OntoramaConfig.getEdgeType(TestGraphPackage.edgeName_subtype));
		Edge edge5 = new EdgeImpl(node1, node1_3, OntoramaConfig.getEdgeType(TestGraphPackage.edgeName_subtype));
		Edge edge6 = new EdgeImpl(node2, node2_1, OntoramaConfig.getEdgeType(TestGraphPackage.edgeName_subtype));
		Edge edge7 = new EdgeImpl(node2, node2_2, OntoramaConfig.getEdgeType(TestGraphPackage.edgeName_subtype));
		Edge edge8 = new EdgeImpl(node1_3, node2_2, OntoramaConfig.getEdgeType(TestGraphPackage.edgeName_similar));
		
		_graph.addNode(root);
		_graph.addNode(node1);
		_graph.addNode(node2);
		_graph.addNode(node1_1);
		_graph.addNode(node1_2);
		_graph.addNode(node1_3);
		_graph.addNode(node2_1);
		_graph.addNode(node2_2);
		
		_graph.addEdge(edge1);
		_graph.addEdge(edge2);
		_graph.addEdge(edge3);
		_graph.addEdge(edge4);
		_graph.addEdge(edge5);
		_graph.addEdge(edge6);
		_graph.addEdge(edge7);
		_graph.addEdge(edge8);
		
		
		_p2pBackend = new P2PBackend();		
	}
	
	public void testBuildP2PGraph() {
		_p2pBackend.buildP2PGraph(_graph);
        P2PGraph p2pGraph = _p2pBackend.getP2PGraph();
        assertEquals("number of nodes should be the same for base and resulting graphs ", _graph.getNodesList().size(), p2pGraph.getNodesList().size());
        assertEquals("number of edges should be the same for base and resulting graphs ", _graph.getEdgesList().size(), p2pGraph.getEdgesList().size());
	}
		

}
