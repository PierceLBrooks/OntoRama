package ontorama.model.tree;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import org.tockit.events.EventBroker;

import ontorama.model.graph.*;
import ontorama.model.tree.events.TreeNodeAddedEvent;
import ontorama.ontotools.NoSuchRelationLinkException;

/**
 * User: nataliya
 * Date: Nov 25, 2002
 * Time: 2:29:08 PM
 */
public class TreeImpl implements Tree {

    private Graph _graph;
    private Node _graphRootNode;
    private TreeNode _root;
    private EventBroker _eventBroker;

    private List _nodes = new LinkedList();
    private List _edges = new LinkedList();

    /**
     * Create a tree from the given graph with the given root node. The second parameter
     * is usefull when we have a graph that represents a forest of trees.
     * @param graph
     * @param graphRootNode
     * @throws NoSuchRelationLinkException
     */
    public TreeImpl (Graph graph, Node graphRootNode, EventBroker eventBroker) {
        _graph = graph;
        _graphRootNode = graphRootNode;
        _eventBroker = eventBroker;
        buildTree(graphRootNode);
    }
    
    public TreeNode getRootNode() {
        return _root;
    }

	public TreeNode addNode (TreeNode newNode, TreeNode parentTreeNode, EdgeType edgeType) 
											throws TreeModificationException {
		Node graphNode = ((TreeNodeImpl) newNode).getGraphNode();
		addTreeNode(newNode);
		try {
			Edge graphEdge = new EdgeImpl(((TreeNodeImpl) parentTreeNode).getGraphNode(),
										((TreeNodeImpl) newNode).getGraphNode(),
										edgeType);
			_graph.addNode(graphNode);
			_graph.addEdge(graphEdge);
				
			TreeEdge newEdge = addTreeEdge(graphEdge, parentTreeNode, newNode);
			
			Iterator clones = parentTreeNode.getClones().iterator();
			while (clones.hasNext()) {
				TreeNode curClone = (TreeNode) clones.next();
				TreeNode curNewNode = addTreeNode(graphNode);
				addTreeEdge(graphEdge, parentTreeNode, curNewNode);
			}
			calculateDepths(_root, 0);
			_eventBroker.processEvent(new TreeNodeAddedEvent(this, newNode));
			return newNode;
		}
		catch (NoSuchRelationLinkException e) {
			throw new TreeModificationException(e.getMessage());
		}
		catch (GraphModificationException e) {
			throw new TreeModificationException(e.getMessage());
		}
	}    

    private void buildTree (Node topGraphNode) {
        _root = addTreeNode(topGraphNode);
        traverseBuild(topGraphNode, _root);
        calculateDepths(_root, 0);
    }

    private void traverseBuild (Node topGraphNode, TreeNode topTreeNode ) {
        Iterator outboundEdges = _graph.getOutboundEdgesDisplayedInGraph(topGraphNode).iterator();
        while (outboundEdges.hasNext()) {
            Edge curGraphEdge = (Edge) outboundEdges.next();
            TreeNode toNode = addTreeNode (curGraphEdge.getToNode());
            TreeEdge curEdge = addTreeEdge (curGraphEdge, topTreeNode, toNode);
            traverseBuild(curGraphEdge.getToNode(), toNode);
        }
    }

    private TreeNode addTreeNode (Node graphNode) {
        TreeNode treeNode = new TreeNodeImpl(graphNode);
        addTreeNode (treeNode);
        return treeNode;
    }

	private void addTreeNode (TreeNode treeNode) {
		/// since we should be
		/// able to uniquely identify nodes by the graph nodes they contain - if we find two nodes with
		/// the same graph node, we assume that they are each other clones.
		Iterator it = _nodes.iterator();
		while (it.hasNext()) {
			TreeNode cur = (TreeNode) it.next();
			if (cur.isClone(treeNode)) {
					cur.addClone(treeNode);
					treeNode.addClone(cur);
			}
		}
		_nodes.add(treeNode);
	}

    private TreeEdge addTreeEdge (Edge graphEdge, TreeNode fromNode, TreeNode toNode) {
        TreeEdge treeEdge = new TreeEdgeImpl(graphEdge, toNode);
        _edges.add(treeEdge);
    	fromNode.addChild(toNode, treeEdge);
    	toNode.setParent(fromNode);
        return treeEdge;
    }

	/**
	 * Calculate the depths of all children in respect to this node.
	 */
	public void calculateDepths(TreeNode top, int depth) {
		top.setDepth(depth);
		Iterator it = top.getChildren().iterator();
		while (it.hasNext()) {
			TreeNode childNode = (TreeNode) it.next();
			childNode.setDepth(depth + 1);
			calculateDepths(childNode, depth + 1);
		}
	}
	
	private Edge findGraphEdge (TreeNode node1, TreeNode node2, EdgeType edgeType) {
		TreeNodeImpl fromNode = (TreeNodeImpl) node1;
		TreeNodeImpl toNode = (TreeNodeImpl) node2;
		return _graph.getEdge(fromNode.getGraphNode(), toNode.getGraphNode(), edgeType);
	}


}
