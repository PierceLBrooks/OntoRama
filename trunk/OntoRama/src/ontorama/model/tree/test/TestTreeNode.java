package ontorama.model.tree.test;

import junit.framework.TestCase;
import ontorama.backends.Backend;
import ontorama.model.graph.*;
import ontorama.model.tree.TreeNode;
import ontorama.model.tree.TreeNodeImpl;
import ontorama.model.tree.TreeEdge;
import ontorama.model.tree.TreeEdgeImpl;
import ontorama.OntoramaConfig;
import ontorama.ontotools.NoSuchRelationLinkException;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Nov 28, 2002
 * Time: 10:35:47 AM
 * To change this template use Options | File Templates.
 */
public class TestTreeNode  extends TestCase{
	
	private Backend _backend;

    Node _graphNode;
    TreeNode _treeNode;
    TreeNode _cloneNode;
    TreeNode _childNode1;
    TreeNode _childNode2;
    TreeEdge _childEdge1;
    TreeEdge _childEdge2;

    public TestTreeNode(String name) {
        super(name);
    }

    public void setUp () throws NoSuchRelationLinkException {
    	_backend = OntoramaConfig.instantiateBackend(OntoramaConfig.defaultBackend, null);
    	OntoramaConfig.activateBackend(_backend);
    	
        _graphNode = _backend.createNode("node", "node");
        _treeNode = new TreeNodeImpl(_graphNode);

        //Node graphNode2 = _backend.createNode("node2");
        _cloneNode = new TreeNodeImpl(_graphNode);
        _treeNode.addClone(_cloneNode);

        Node childGraphNode1 = _backend.createNode("child1", "child1");
        Node childGraphNode2 = _backend.createNode("child2", "child2");
        _childNode1 = new TreeNodeImpl(childGraphNode1);
        _childNode2 = new TreeNodeImpl(childGraphNode2);
        List edgeTypes = OntoramaConfig.getEdgeTypesList();
        if (edgeTypes.size() < 2) {
            System.err.println("expecting at least 2 edge types to be configured");
            System.exit(-1);
        }
        EdgeType et1 = (EdgeType) edgeTypes.get(0);
        EdgeType et2 = (EdgeType) edgeTypes.get(1);
        Edge edge1 = _backend.createEdge(_graphNode, childGraphNode1, et1);
        _childEdge1 = new TreeEdgeImpl(edge1, _childNode1);
        _treeNode.addChild(_childNode1, _childEdge1);
        _childNode1.setParent(_treeNode);
        Edge edge2 = _backend.createEdge(_graphNode, childGraphNode2, et2);
        _childEdge2 = new TreeEdgeImpl(edge2, _childNode2);
        _treeNode.addChild(_childNode2, _childEdge2);
        _childNode2.setParent(_treeNode);
    }

    public void testClones () {
        List clones = _treeNode.getClones();
        assertEquals("number of clones for node ", 1, clones.size());
        TreeNode cloneNode = (TreeNode) clones.get(0);
        assertEquals("clone for node " , _cloneNode, cloneNode);
        assertEquals("clones should contain the same graph node ", true, _treeNode.isClone(_cloneNode));
    }

    public void testAddClone () {
        TreeNode newClone = new TreeNodeImpl(_graphNode);
        _treeNode.addClone(newClone);
        List clones = _treeNode.getClones();
        assertEquals("number of clones for node ", 2, clones.size());
    }

    public void testChildren () {
        List children = _treeNode.getChildren();
        assertEquals("number of children for _treeNode ", 2, children.size());
        
        assertEquals("expecting to find node in children list ", true, children.contains(_childNode1));
        assertEquals("expecting ro find node in children list ", true, children.contains(_childNode2));
    }
    
    public void testGetParent() {
    	assertEquals("parent for node _childNode1 ", _treeNode, _childNode1.getParent());
    }
    
    
}
