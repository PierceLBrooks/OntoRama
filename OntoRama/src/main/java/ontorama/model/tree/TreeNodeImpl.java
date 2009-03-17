package ontorama.model.tree;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import ontorama.OntoramaConfig;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeType;

public class TreeNodeImpl implements TreeNode {

    /**
     * corresponding graph node
     */
    private Node _graphNode;

    private List<TreeNode> _clones;

    private List<ChildNodeReference> _children;

    private TreeNode _parent;

    public TreeNodeImpl (Node graphNode) {
        _graphNode = graphNode;
        init();
    }

    public TreeNodeImpl (String nodeName, NodeType nodeType) {
    	_graphNode = OntoramaConfig.getBackend().createNode(nodeName, nodeName);
    	_graphNode.setNodeType(nodeType);
    	init();
    }

    private void init () {
    	_clones = new ArrayList<TreeNode>();
    	_children = new ArrayList<ChildNodeReference>();
    }


	public Node getGraphNode() {
		return _graphNode;
	}

	public String getName() {
		return _graphNode.getName();
	}

	public NodeType getNodeType() {
		return _graphNode.getNodeType();
	}

    public void addClone(TreeNode cloneNode) {
        if (! _clones.contains(cloneNode)) {
            _clones.add(cloneNode);
        }
    }

	public boolean isClone (TreeNode node) {
		TreeNodeImpl treeNode = (TreeNodeImpl) node;
		if (treeNode.getGraphNode().equals(_graphNode)) {
			return true;
		}
		return false;
	}


    public List<TreeNode> getClones() {
        return _clones;
    }

    public List<TreeNode> getChildren() {
    	Iterator<ChildNodeReference> it = _children.iterator();
    	List<TreeNode> childNodesList = new ArrayList<TreeNode>();
    	while (it.hasNext()) {
    		ChildNodeReference cur = it.next();
    		childNodesList.add(cur.node);
    	}
        return childNodesList;
    }

    public TreeEdge getEdge (TreeNode childNode) {
    	TreeEdge result = null;
    	Iterator<ChildNodeReference> it = _children.iterator();
    	while (it.hasNext()) {
    		ChildNodeReference cur = it.next();
    		if (cur.node.equals(childNode)) {
    			result = cur.edge;
    		}
    	}
    	return result;
    }

    public void addChild(TreeNode childNode, TreeEdge childEdge) {
        ChildNodeReference ref = new ChildNodeReference(childNode, childEdge);
        if (!_children.contains(ref)) {
        	_children.add(ref);
        }
    }

    public void removeChild(TreeNode childNode, TreeEdge childEdge) {
    	ChildNodeReference refToRemove = null;
        Iterator<ChildNodeReference> it = _children.iterator();
        while (it.hasNext()) {
        	ChildNodeReference cur = it.next();
        	if ( (cur.node.equals(childNode)) && (cur.edge.equals(childEdge)) ) {
        		refToRemove = cur;
        	}
        }

        _children.remove(refToRemove);
    }

	public void setParent(TreeNode parent) {
		_parent = parent;
	}


    public TreeNode getParent() {
    	return _parent;
    }

    public String toString () {
        String res = "TreeNode: " + this.getName();
        return res;
    }

    class ChildNodeReference {
    	public TreeNode node;
    	public TreeEdge edge;

    	public ChildNodeReference (TreeNode node, TreeEdge edge) {
    		this.node = node;
    		this.edge = edge;
    	}
    }
}
