package ontorama.model.tree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ontorama.model.graph.Node;
import ontorama.model.graph.NodeImpl;
import ontorama.model.graph.NodeType;

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
     * list of children - object ChildNodeReference containing
     * a pair: node and edge
     */
    private List _children;
    
    /**
     * parent tree node
     */
    private TreeNode _parent;
   
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
        init();
    }
    
    public TreeNodeImpl (String nodeName, NodeType nodeType) {
    	_graphNode = new NodeImpl(nodeName);
    	_graphNode.setNodeType(nodeType);
    	init();
    }
    
    private void init () {
    	_clones = new LinkedList();
    	_children = new LinkedList();
    }
    

	protected Node getGraphNode() {
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
    

    public List getClones() {
        return _clones;
    }

    public List getChildren() {
    	Iterator it = _children.iterator();
    	List childNodesList = new LinkedList();
    	while (it.hasNext()) {
    		ChildNodeReference cur = (ChildNodeReference) it.next();
    		childNodesList.add(cur.node);
    	}
        return childNodesList;
    }
    
    public TreeEdge getEdge (TreeNode childNode) {
    	TreeEdge result = null;
    	Iterator it = _children.iterator();
    	while (it.hasNext()) {
    		ChildNodeReference cur = (ChildNodeReference) it.next();
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
        Iterator it = _children.iterator();
        while (it.hasNext()) {
        	ChildNodeReference cur = (ChildNodeReference) it.next();
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

    
	public int getDepth() {
		return _depth;
	}

	public void setDepth(int depth) {
		_depth = depth;
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
