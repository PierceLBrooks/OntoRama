package ontorama.model.tree.test;

import junit.framework.TestCase;
import ontorama.model.tree.Tree;
import ontorama.model.tree.TreeImpl;
import ontorama.model.tree.TreeNode;
import ontorama.model.tree.TreeEdge;
import ontorama.model.graph.*;
import ontorama.OntoramaConfig;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.query.QueryResult;
import ontorama.ontotools.query.Query;
import org.tockit.events.EventBroker;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Nov 28, 2002
 * Time: 9:30:48 AM
 * To change this template use Options | File Templates.
 */
public class TestTree extends TestCase{

    EdgeType _edgeType1;
    EdgeType _edgeType2;
    EdgeType _edgeType3;
    NodeType _nodeType1;

    Graph _graph;
    Tree _tree;

    public TestTree(String name) {
        super(name);
    }

    public void setUp () throws GraphModificationException, NoSuchRelationLinkException, NoTypeFoundInResultSetException {
        List graphNodesList = new LinkedList ();
        List graphEdgesList = new LinkedList ();
        List nodeTypesList = OntoramaConfig.getNodeTypesList();
        if (nodeTypesList.size() < 1) {
            System.err.println("expecting at least 1 node type defined in the config.xml");
            System.exit(-1);
        }
        _nodeType1 = (NodeType) nodeTypesList.get(0);
        Node node1 = new NodeImpl("node1");
        node1.setNodeType(_nodeType1);
        Node node2 = new NodeImpl("node2");
        node2.setNodeType(_nodeType1);
        Node node3 = new NodeImpl("node3");
        node3.setNodeType(_nodeType1);
        Node node4 = new NodeImpl("node4");
        node4.setNodeType(_nodeType1);
        Node node5 = new NodeImpl("node5");
        node5.setNodeType(_nodeType1);
        Node node6 = new NodeImpl("node6");
        node6.setNodeType(_nodeType1);
        Node node7 = new NodeImpl("node7");
        node7.setNodeType(_nodeType1);
        Node node8 = new NodeImpl("node8");
        node8.setNodeType(_nodeType1);
        Node node9 = new NodeImpl("node9");
        node9.setNodeType(_nodeType1);
        Node node10 = new NodeImpl("node10");
        node10.setNodeType(_nodeType1);
        Node node11 = new NodeImpl("node11");
        node11.setNodeType(_nodeType1);
        Node node12 = new NodeImpl("node12");
        node12.setNodeType(_nodeType1);
        graphNodesList.add(node1);
        graphNodesList.add(node2);
        graphNodesList.add(node3);
        graphNodesList.add(node4);
        graphNodesList.add(node5);
        graphNodesList.add(node6);
        graphNodesList.add(node7);
        graphNodesList.add(node8);
        graphNodesList.add(node9);
        graphNodesList.add(node10);
        graphNodesList.add(node11);
        graphNodesList.add(node12);

        List edgesTypesList = OntoramaConfig.getEdgeTypesList();
        if (edgesTypesList.size() < 3) {
            System.err.println("expecting at least 3 edge types defined in the config.xml");
            System.exit(-1);
        }
        _edgeType1 = (EdgeType) edgesTypesList.get(0);
        _edgeType2 = (EdgeType) edgesTypesList.get(1);
        _edgeType3 = (EdgeType) edgesTypesList.get(2);
        Edge e1_2 = new EdgeImpl(node1, node2, _edgeType1);
        Edge e1_3 = new EdgeImpl(node1, node3, _edgeType1);
        Edge e1_4 = new EdgeImpl(node1, node4, _edgeType2);
        Edge e2_9 = new EdgeImpl(node2, node9, _edgeType1);
        Edge e2_10 = new EdgeImpl(node2, node10, _edgeType2);
        Edge e10_7 = new EdgeImpl(node10, node7, _edgeType1);
        Edge e3_5 = new EdgeImpl(node3, node5, _edgeType1);
        Edge e4_11 = new EdgeImpl(node4, node11, _edgeType1);
        Edge e4_6 = new EdgeImpl(node4, node6, _edgeType1);
        Edge e5_6 = new EdgeImpl(node5, node6, _edgeType1);
        Edge e11_12 = new EdgeImpl(node11, node12, _edgeType1);
        Edge e6_7 = new EdgeImpl(node6, node7, _edgeType1);
        Edge e6_8 = new EdgeImpl(node6, node8, _edgeType2);
        graphEdgesList.add(e1_2);
        graphEdgesList.add(e1_3);
        graphEdgesList.add(e1_4);
        graphEdgesList.add(e2_9);
        graphEdgesList.add(e2_10);
        graphEdgesList.add(e10_7);
        graphEdgesList.add(e3_5);
        graphEdgesList.add(e4_11);
        graphEdgesList.add(e4_6);
        graphEdgesList.add(e5_6);
        graphEdgesList.add(e11_12);
        graphEdgesList.add(e6_7);
        graphEdgesList.add(e6_8);


        QueryResult queryRes = new QueryResult(new Query("node1"), graphNodesList, graphEdgesList);
        _graph = new GraphImpl(queryRes, new EventBroker());


        _tree = new TreeImpl(_graph, _graph.getRootNode());

    }

    public void testTreeNodesNum () {
        assertEquals("number of tree nodes ", 16 , countNumOfNodes());
    }

    public void testCloneWithChildren () {
        TreeNode treeNode = getNodeByName("node6");
    	assertEquals("tree should contain node6 ", true, (treeNode != null));
        List clones = treeNode.getClones();
        assertEquals("number of clones for node6 ", 1, clones.size());
        TreeNode clone = (TreeNode) clones.get(0);

        List treeNodeChildren = treeNode.getChildren();
        List cloneNodeChildren = clone.getChildren();
        assertEquals("number of children in clones should be the same (checking clones for node6) ",
                                        treeNodeChildren.size(), cloneNodeChildren.size());
    }

    public void testClone () {
        TreeNode treeNode = getNodeByName("node7");
        List clones = treeNode.getClones();
        assertEquals("tree should contain node7 ", true, (treeNode != null));
        assertEquals("number of clones for node7 ", 2, clones.size());

        Iterator it = clones.iterator();
        while (it.hasNext()) {
            TreeNode curClone = (TreeNode) it.next();
            assertEquals("number of clones in each clones should be the same ",
                                    treeNode.getClones().size(), curClone.getClones().size());
            assertEquals("number of children in each clones should be the same ",
                                    treeNode.getChildren().size(), curClone.getChildren().size());
            assertEquals("clones should contain the same graph node object ",
                                    treeNode.getGraphNode(), curClone.getGraphNode());
        }
    }
    
    public void testParent () {
    	TreeNode node1 = getNodeByName("node1");
    	assertEquals("tree should contain node1 ", true, (node1 != null));
    	TreeNode node2 = getNodeByName("node2");
    	assertEquals("tree should contain node2 ", true, (node2 != null));
    	assertEquals("parent for node2 ", node1, node2.getParent());
    	
    }

    public void testTraversal () {
        TreeNode rootNode = _tree.getRootNode();
        List q = new LinkedList();
        q.add(rootNode);

        int treeDepth = 0;
        int branchDepth = 0;
        Iterator topLevelChildren = rootNode.getChildren().iterator();
        while (topLevelChildren.hasNext()) {
            TreeEdge curEdge = (TreeEdge) topLevelChildren.next();
            TreeNode child = curEdge.getToNode();
            branchDepth = calcBranchDepth(1, child);
            if (branchDepth > treeDepth) {
                treeDepth = branchDepth;
            }
        }
        assertEquals("branchDepth of the tree should be ", 4, treeDepth);
    }
    
    public void testDepth () {
    	TreeNode node1 = getNodeByName("node1");
    	assertEquals("tree should contain node1 ", true, (node1 != null));
    	assertEquals("depth for root node node1 ", 0, node1.getDepth());
    }
    
	public void testAddNode ()  throws GraphModificationException, NoSuchRelationLinkException {
		int originalNodeCount = countNumOfNodes();
		TreeNode node7 = getNodeByName("node7");
		
		Node newNode = new NodeImpl("newNode");
		Edge newEdge = new EdgeImpl(node7.getGraphNode(), newNode, _edgeType1);
		
		_tree.addNode(node7, newEdge, newNode);
		
		assertEquals("number of nodes after add ", originalNodeCount + 3, countNumOfNodes());
	}
    

    private int calcBranchDepth (int depth, TreeNode top) {
        Iterator children = top.getChildren().iterator();
        if (children.hasNext()) {
            depth++;
        }
        while (children.hasNext()) {
            TreeEdge curEdge = (TreeEdge) children.next();
            TreeNode toNode = curEdge.getToNode();
            depth = calcBranchDepth(depth, toNode);
        }
        return depth;
    }
      
    private TreeNode getNodeByName (String nodeName) {
    	List q = new LinkedList();
    	q.add(_tree.getRootNode());
    	
    	while (!q.isEmpty()) {
    		TreeNode cur = (TreeNode) q.remove(0);
    		Iterator children = cur.getChildren().iterator();
    		while (children.hasNext()) {
    			TreeEdge curEdge = (TreeEdge) children.next();
    			TreeNode childNode = curEdge.getToNode();
    			q.add(childNode);
    		}
    		if (cur.getGraphNode().getName().equals(nodeName)) {
    			return cur;
    		}
    	}
        return null;
    }
    
    private int countNumOfNodes () {
		int result = 0;
    	List q = new LinkedList();
    	q.add(_tree.getRootNode());

    	while (!q.isEmpty()) {
    		TreeNode cur = (TreeNode) q.remove(0);
    		result++;
    		Iterator children = cur.getChildren().iterator();
    		while (children.hasNext()) {
    			TreeEdge curEdge = (TreeEdge) children.next();
    			TreeNode childNode = curEdge.getToNode();
    			q.add(childNode);
    		}
    	}
    	return result;
    }
}
