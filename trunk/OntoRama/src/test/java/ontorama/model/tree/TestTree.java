package ontorama.model.tree;

import junit.framework.TestCase;
import ontorama.model.tree.Tree;
import ontorama.model.tree.TreeImpl;
import ontorama.model.tree.TreeModificationException;
import ontorama.model.tree.TreeNode;
import ontorama.model.tree.TreeNodeImpl;
import ontorama.model.graph.*;
import ontorama.OntoramaConfig;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.query.QueryResult;
import ontorama.ontotools.query.Query;

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
    
    Node _node7;

    public TestTree(String name) {
        super(name);
    }

    public void setUp () throws NoSuchRelationLinkException, InvalidArgumentException {
    	OntoramaConfig.instantiateBackend(OntoramaConfig.defaultBackend, null);
    	
        List graphNodesList = new LinkedList ();
        List graphEdgesList = new LinkedList ();
        _nodeType1 = OntoramaConfig.CONCEPT_TYPE;
        Node node1 = new NodeImpl("node1", "node1");
        node1.setNodeType(_nodeType1);
        Node node2 = new NodeImpl("node2", "node2");
        node2.setNodeType(_nodeType1);
        Node node3 = new NodeImpl("node3", "node3");
        node3.setNodeType(_nodeType1);
        Node node4 = new NodeImpl("node4", "node4");
        node4.setNodeType(_nodeType1);
        Node node5 = new NodeImpl("node5", "node5");
        node5.setNodeType(_nodeType1);
        Node node6 = new NodeImpl("node6","node6");
        node6.setNodeType(_nodeType1);
        _node7 = new NodeImpl("node7", "node7");
        _node7.setNodeType(_nodeType1);
        Node node8 = new NodeImpl("node8", "node8");
        node8.setNodeType(_nodeType1);
        Node node9 = new NodeImpl("node9", "node9");
        node9.setNodeType(_nodeType1);
        Node node10 = new NodeImpl("node10", "node10");
        node10.setNodeType(_nodeType1);
        Node node11 = new NodeImpl("node11", "node11");
        node11.setNodeType(_nodeType1);
        Node node12 = new NodeImpl("node12", "node12");
        node12.setNodeType(_nodeType1);
        graphNodesList.add(node1);
        graphNodesList.add(node2);
        graphNodesList.add(node3);
        graphNodesList.add(node4);
        graphNodesList.add(node5);
        graphNodesList.add(node6);
        graphNodesList.add(_node7);
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
        Edge e10_7 = new EdgeImpl(node10, _node7, _edgeType1);
        Edge e3_5 = new EdgeImpl(node3, node5, _edgeType1);
        Edge e4_11 = new EdgeImpl(node4, node11, _edgeType1);
        Edge e4_6 = new EdgeImpl(node4, node6, _edgeType1);
        Edge e5_6 = new EdgeImpl(node5, node6, _edgeType1);
        Edge e11_12 = new EdgeImpl(node11, node12, _edgeType1);
        Edge e6_7 = new EdgeImpl(node6, _node7, _edgeType1);
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
        _graph = new GraphImpl(queryRes);


        _tree = new TreeImpl(_graph, _graph.getRootNode());

    }

    public void testTreeNodesNum () {
        assertEquals("number of tree nodes ", 16 , countNumOfNodes());
    }

    public void testCloneWithChildren () {
        TreeNode treeNode = getNodeByName("node6");
    	assertEquals("tree should contain node6 ", true, (treeNode != null));
        List<TreeNode> clones = treeNode.getClones();
        assertEquals("number of clones for node6 ", 1, clones.size());
        TreeNode clone = clones.get(0);

        List<TreeNode> treeNodeChildren = treeNode.getChildren();
        List<TreeNode> cloneNodeChildren = clone.getChildren();
        assertEquals("number of children in clones should be the same (checking clones for node6) ",
                                        treeNodeChildren.size(), cloneNodeChildren.size());
    }

    public void testClone () {
        TreeNode treeNode = getNodeByName("node7");
        List<TreeNode> clones = treeNode.getClones();
        assertEquals("tree should contain node7 ", true, (treeNode != null));
        assertEquals("number of clones for node7 ", 2, clones.size());

        Iterator<TreeNode> it = clones.iterator();
        while (it.hasNext()) {
            TreeNode curClone = it.next();
            assertEquals("number of clones in each clones should be the same ",
                                    treeNode.getClones().size(), curClone.getClones().size());
            assertEquals("number of children in each clones should be the same ",
                                    treeNode.getChildren().size(), curClone.getChildren().size());
            assertEquals("clones should contain the same graph node object ",
                                    true, treeNode.isClone( curClone));
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
        List<TreeNode> q = new LinkedList<TreeNode>();
        q.add(rootNode);

        int treeDepth = 0;
        int branchDepth = 0;
        Iterator<TreeNode> topLevelChildren = rootNode.getChildren().iterator();
        while (topLevelChildren.hasNext()) {
            TreeNode child = topLevelChildren.next();
            branchDepth = calcBranchDepth(1, child);
            if (branchDepth > treeDepth) {
                treeDepth = branchDepth;
            }
        }
        assertEquals("branchDepth of the tree should be ", 4, treeDepth);
    }
    
    public void testAddNode () throws TreeModificationException {
        int originalNodeCount = countNumOfNodes();

        Node newNode = new NodeImpl("newNode", "newNode");
        TreeNode newTreeNode = new TreeNodeImpl(newNode);
        TreeNode node9 = getNodeByName("node9");
        _tree.addNode(newTreeNode, node9, _edgeType1);

        assertEquals("number of nodes after add ", originalNodeCount + 1, countNumOfNodes());
    }

	public void testAddNodeToClone ()  throws TreeModificationException {
		int originalNodeCount = countNumOfNodes();
		
		Node newNode = new NodeImpl("newNode", "newNode");
		TreeNode newTreeNode = new TreeNodeImpl(newNode);
		TreeNode node7 = getNodeByName("node7");
		_tree.addNode(newTreeNode, node7, _edgeType1);

		assertEquals("number of nodes after add ", originalNodeCount + 3, countNumOfNodes());

        List<TreeNode> node7clones = node7.getClones();
        Iterator<TreeNode> it = node7clones.iterator();
        while (it.hasNext()) {
            TreeNode cur = it.next();
            Iterator<TreeNode> children = cur.getChildren().iterator();
            boolean found = false;
            while (children.hasNext()) {
                TreeNode child = children.next();
                if (child.isClone(newTreeNode)) {
                    found = true;
                }
            }
            assertEquals("each clone for node7 should contain clone for new node as one of children", true, found);
        }
	}

    public void testRemoveNode () throws TreeModificationException {
        int originalNodeCount = countNumOfNodes();

        TreeNode node9 = getNodeByName("node9");
        _tree.removeNode(node9);
        assertEquals("number of nodes after remove ", originalNodeCount - 1, countNumOfNodes());

    }

    public void testRemoveClonedNode  () throws TreeModificationException {
        int originalNodeCount = countNumOfNodes();
        TreeNode node7 = getNodeByName("node7");
        _tree.removeNode(node7);
        assertEquals("number of nodes after remove ", originalNodeCount - 3, countNumOfNodes());
    }

    public void testRemoveNodeWithChildren () {
        TreeNode node10 = getNodeByName("node10");
        try {
            _tree.removeNode(node10);
            fail("should raise  a TreeModificationException");
        }
        catch (TreeModificationException e) {
        }
    }


    private int calcBranchDepth (int depth, TreeNode top) {
    	int res = depth;
        Iterator<TreeNode> children = top.getChildren().iterator();
        if (children.hasNext()) {
            res++;
        }
        while (children.hasNext()) {
			TreeNode childNode = children.next();
            res = calcBranchDepth(res, childNode);
        }
        return res;
    }
      
    private TreeNode getNodeByName (String nodeName) {
    	List<TreeNode> q = new LinkedList<TreeNode>();
    	q.add(_tree.getRootNode());
    	
    	while (!q.isEmpty()) {
    		TreeNode cur = q.remove(0);
    		Iterator<TreeNode> children = cur.getChildren().iterator();
    		while (children.hasNext()) {
    			TreeNode childNode = children.next();
    			q.add(childNode);
    		}
    		if (cur.getName().equals(nodeName)) {
    			return cur;
    		}
    	}
        return null;
    }
    
    private int countNumOfNodes () {
		int result = 0;
    	List<TreeNode> q = new LinkedList<TreeNode>();
    	q.add(_tree.getRootNode());

    	while (!q.isEmpty()) {
    		TreeNode cur = q.remove(0);
    		result++;
    		Iterator<TreeNode> children = cur.getChildren().iterator();
    		while (children.hasNext()) {
    			TreeNode childNode = children.next();
    			q.add(childNode);
    		}
    	}
    	return result;
    }
}
