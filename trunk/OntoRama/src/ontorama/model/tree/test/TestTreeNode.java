package ontorama.model.tree.test;

import junit.framework.TestCase;
import ontorama.model.graph.NodeImpl;
import ontorama.model.graph.Node;
import ontorama.model.tree.TreeNode;
import ontorama.model.tree.TreeNodeImpl;

import java.util.List;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Nov 28, 2002
 * Time: 10:35:47 AM
 * To change this template use Options | File Templates.
 */
public class TestTreeNode  extends TestCase{

    Node _graphNode;
    TreeNode _treeNode;
    TreeNode _cloneNode;

    public TestTreeNode(String name) {
        super(name);
    }

    public void setUp () {
        _graphNode = new NodeImpl("node");
        _treeNode = new TreeNodeImpl(_graphNode);

        //Node graphNode2 = new NodeImpl("node2");
        _cloneNode = new TreeNodeImpl(_graphNode);
        _treeNode.addClone(_cloneNode);
    }

    public void testGetGraphNode () {
        assertEquals(_graphNode, _treeNode.getGraphNode());
    }

    public void testClones () {
        List clones = _treeNode.getClones();
        assertEquals("number of clones for node ", 1, clones.size());
        TreeNode cloneNode = (TreeNode) clones.get(0);
        assertEquals("clone for node " , _cloneNode, cloneNode);
        assertEquals("clones should contain the same graph node ", _treeNode.getGraphNode(), _cloneNode.getGraphNode());
    }

    public void testAddClone () {
        TreeNode newClone = new TreeNodeImpl(_graphNode);
        _treeNode.addClone(newClone);
        List clones = _treeNode.getClones();
        assertEquals("number of clones for node ", 2, clones.size());
    }

    public void testSetClones () {
        TreeNode clone1 = new TreeNodeImpl(_graphNode);
        TreeNode clone2 = new TreeNodeImpl (_graphNode);
        List clonesList  = new LinkedList();
        clonesList.add(clone1);
        clonesList.add(clone2);
        _treeNode.setClones(clonesList);
    }

}
