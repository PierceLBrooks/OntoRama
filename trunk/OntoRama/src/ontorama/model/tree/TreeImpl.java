package ontorama.model.tree;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import ontorama.model.graph.*;

/**
 * User: nataliya
 * Date: Nov 25, 2002
 * Time: 2:29:08 PM
 */
public class TreeImpl implements Tree {

    private Graph _graph;
    private Node _graphRootNode;
    private TreeNode _root;

    private List _nodes = new LinkedList();
    private List _edges = new LinkedList();

    /**
     * Create a tree from the given graph with the given root node. The second parameter
     * is usefull when we have a graph that represents a forest of trees.
     * @param graph
     * @param graphRootNode
     * @throws NoSuchRelationLinkException
     */
    public TreeImpl (Graph graph, Node graphRootNode) {
        _graph = graph;
        _graphRootNode = graphRootNode;
        buildTree(graphRootNode);
    }

    public TreeNode getRootNode() {
        return _root;
    }

	public TreeNode addNode (TreeNode parentTreeNode, Edge graphEdge, Node graphNode) {
		TreeNode newNode = addTreeNode(graphNode);
		TreeEdge newEdge = addTreeEdge(graphEdge, parentTreeNode, newNode);
		
		Iterator clones = parentTreeNode.getClones().iterator();
		while (clones.hasNext()) {
			TreeNode curClone = (TreeNode) clones.next();
			TreeNode curNewNode = addTreeNode(graphNode);
			addTreeEdge(graphEdge, parentTreeNode, curNewNode);
		}
		
		//    	//_eventBroker.processEvent(new NodeAddedEvent(this, node));
		return newNode;
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
        /// @todo not sure if this is a good way to handle cloning: since we should be
        /// able to uniquely identify nodes by the graph nodes they contain - if we find two nodes with
        /// the same graph node, we assume that they are each other clones.
        Iterator it = _nodes.iterator();
        while (it.hasNext()) {
            TreeNode cur = (TreeNode) it.next();
            if (cur.getGraphNode().equals(treeNode.getGraphNode())) {
                    cur.addClone(treeNode);
                    treeNode.addClone(cur);
            }
        }
        _nodes.add(treeNode);
    	//_eventBroker.processEvent(new NodeAddedEvent(this, node));
        return treeNode;
    }

    private TreeEdge addTreeEdge (Edge graphEdge, TreeNode fromNode, TreeNode toNode) {
        TreeEdge treeEdge = new TreeEdgeImpl(graphEdge, toNode);
        _edges.add(treeEdge);
    	fromNode.addChild(treeEdge);
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
			TreeEdge childEdge = (TreeEdge) it.next();
			TreeNode childNode = childEdge.getToNode();
			childNode.setDepth(depth + 1);
			calculateDepths(childNode, depth + 1);
		}
	}


}
