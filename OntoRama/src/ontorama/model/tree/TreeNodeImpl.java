package ontorama.model.tree;

import ontorama.model.graph.Node;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Nov 25, 2002
 * Time: 2:13:55 PM
 * To change this template use Options | File Templates.
 */
public class TreeNodeImpl implements TreeNode {

    /**
     * corresponding graph node
     */
    private Node _graphNode;

    /**
     * list of clones (TreeNodes)
     */
    private List _clones;

    /**
     * list of children - outbound TreeEdges
     */
    private List _childEdges;

    /**
     * parent TreeEdge for this node (inbound TreeEdge)
     */
    private TreeEdge _parentEdge;

    /**
     *
     * @param graphNode
     */
    public TreeNodeImpl (Node graphNode) {
        _graphNode = graphNode;
        _clones = new LinkedList();
        _childEdges = new LinkedList();
    }

    public Node getGraphNode() {
        return _graphNode;
    }

    public void addClone(TreeNode cloneNode) {
        if (! _clones.contains(cloneNode)) {
            _clones.add(cloneNode);
        }
    }

    public void setClones(List clones) {
        _clones = clones;
    }

    public List getClones() {
        return _clones;
    }

    public void setChildren(List childEdges) {
        _childEdges = childEdges;
    }

    public List getChildren() {
        return _childEdges;
    }

    public void addChild(TreeEdge childEdge) {
        if (! _childEdges.contains(childEdge)) {
              _childEdges.add(childEdge);
        }
    }

    public boolean removeChild(TreeEdge childEdge) {
        return _childEdges.remove(childEdge);
    }

    public void setParent(TreeEdge parentEdge) {
        _parentEdge = parentEdge;
    }

    public TreeEdge getParent() {
        return _parentEdge;
    }
}
