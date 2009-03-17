package ontorama.model.tree;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import org.tockit.events.EventBroker;

import ontorama.OntoramaConfig;
import ontorama.model.graph.*;
import ontorama.model.tree.events.TreeNodeAddedEvent;
import ontorama.model.tree.events.TreeNodeRemovedEvent;
import ontorama.ontotools.NoSuchRelationLinkException;

/**
 * User: nataliya
 * Date: Nov 25, 2002
 * Time: 2:29:08 PM
 */
public class TreeImpl implements Tree {

    private Graph _graph;
    private TreeNode _root;
    private EventBroker _eventBroker;

    /**
     * Create a tree from the given graph with the given root node. The second parameter
     * is usefull when we have a graph that represents a forest of trees.
     * @param graph
     * @param graphRootNode
     */
    public TreeImpl (Graph graph, Node graphRootNode) {
        _graph = graph;
        _eventBroker = new EventBroker();
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
			Edge graphEdge = OntoramaConfig.getBackend().createEdge(((TreeNodeImpl) parentTreeNode).getGraphNode(),
										((TreeNodeImpl) newNode).getGraphNode(),
										edgeType);
			_graph.addNode(graphNode);
			_graph.addEdge(graphEdge);

			addTreeEdge(graphEdge, parentTreeNode, newNode);

			Iterator<TreeNode> clones = parentTreeNode.getClones().iterator();
			while (clones.hasNext()) {
				TreeNode curClone = clones.next();
				TreeNode curNewNode = addTreeNode(graphNode);
				addTreeEdge(graphEdge, curClone, curNewNode);
			}
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

	public void removeNode (TreeNode nodeToRemove) throws TreeModificationException {
        if (nodeToRemove.getChildren().size() > 0) {
            throw new TreeModificationException("Node " + nodeToRemove.getName() + " has children, please remove them first");
        }
		removeTreeNode(nodeToRemove);

		Iterator<TreeNode> clones = nodeToRemove.getClones().iterator();
		while  (clones.hasNext()) {
			TreeNode clone = clones.next();
			removeTreeNode(clone);
		}
		_eventBroker.processEvent(new TreeNodeRemovedEvent(this, nodeToRemove));
	}
	
	public EventBroker getEventBroker() {
		return _eventBroker;
	}
	
	public Graph getGraph() {
		return _graph;
	}

	private void removeTreeNode(TreeNode nodeToRemove) {
		TreeNode parent = nodeToRemove.getParent();
		TreeEdge parentEdge = parent.getEdge(nodeToRemove);

		Node graphNode = ((TreeNodeImpl) nodeToRemove).getGraphNode();
		Edge graphEdge = ((TreeEdgeImpl) parentEdge).getGraphEdge();
		_graph.removeEdge(graphEdge);
		if (nodeToRemove.getClones().size() == 0) {
			_graph.removeNode(graphNode);
		}
		parent.removeChild(nodeToRemove, parentEdge);

        List<TreeNode> clones = nodeToRemove.getClones();
        Iterator<TreeNode> it = clones.iterator();
        while (it.hasNext()) {
            TreeNode cur = it.next();
            List<TreeNode> curClonesList = cur.getClones();
            curClonesList.remove(nodeToRemove);
        }
	}

    private void buildTree (Node topGraphNode) {
        _root = addTreeNode(topGraphNode);
        traverseBuild(topGraphNode, _root);
    }

    private void traverseBuild (Node topGraphNode, TreeNode topTreeNode ) {
        Iterator<Edge> outboundEdges = _graph.getOutboundEdgesDisplayedInGraph(topGraphNode).iterator();
        while (outboundEdges.hasNext()) {
            Edge curGraphEdge = outboundEdges.next();
            TreeNode toNode = addTreeNode (curGraphEdge.getToNode());
            addTreeEdge (curGraphEdge, topTreeNode, toNode);
            traverseBuild(curGraphEdge.getToNode(), toNode);
        }
    }

    private TreeNode addTreeNode (Node graphNode) {
        TreeNode treeNode = new TreeNodeImpl(graphNode);
        if (_root != null ) {
        	addTreeNode (treeNode);
        }
        return treeNode;
    }

	private void addTreeNode (TreeNode treeNode) {
		/// since we should be
		/// able to uniquely identify nodes by the graph nodes they contain - if we find two nodes with
		/// the same graph node, we assume that they are each other clones.
		List<TreeNode> q = new LinkedList<TreeNode>();
		q.add(_root);
		while (!q.isEmpty()) {
			TreeNode cur = q.remove(0);
			List<TreeNode> children = cur.getChildren();
			q.addAll(children);
			if (cur.isClone(treeNode)) {
					cur.addClone(treeNode);
					treeNode.addClone(cur);
			}
		}
	}

    private TreeEdge addTreeEdge (Edge graphEdge, TreeNode fromNode, TreeNode toNode) {
        TreeEdge treeEdge = new TreeEdgeImpl(graphEdge, toNode);
    	fromNode.addChild(toNode, treeEdge);
    	toNode.setParent(fromNode);
        return treeEdge;
    }

}
