package ontorama.model.tree;

import java.util.LinkedList;
import java.util.List;

import ontorama.model.graph.Node;

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
     * distance to the root node
     */
    private int _depth = 0;

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

    public List getClones() {
        return _clones;
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

    
	public int getDepth() {
		return _depth;
	}

	public void setDepth(int depth) {
		_depth = depth;
	}
    

    public String toString () {
        String res = "TreeNode: " + this.getGraphNode().getName();
        return res;
    }
}
