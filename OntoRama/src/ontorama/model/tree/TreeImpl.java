package ontorama.model.tree;

import java.util.Iterator;
import java.util.List;

import ontorama.model.graph.Edge;
import ontorama.model.graph.Graph;
import ontorama.model.graph.Node;
import ontorama.ontotools.NoSuchRelationLinkException;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Nov 25, 2002
 * Time: 2:29:08 PM
 * To change this template use Options | File Templates.
 */
public class TreeImpl implements Tree {

    private Graph _graph;
    private TreeNode _root;    
    
    private List _nodesToRemove;
    private List _edgesToRemove;
    

    public TreeImpl (Graph graph, Node graphRootNode) throws NoSuchRelationLinkException {
    	_root = new TreeNodeImpl(graph.getRootNode());
    	
   		//transformGraphIntoTree();
    	

    }

    public Node getRootNode() {
        return null;
    }
    
//	public void transformGraphIntoTree () throws NoSuchRelationLinkException {
//		List topLevelUnconnectedNodes = _graph.getUnconnectedNodesList();
//		listItemsToRemove(topLevelUnconnectedNodes);
//		// clean up
//		removeUnconnectedEdges();
//		removeUnconnectedNodesFromGraph();
//
//		convertIntoTree(root);
//		System.out.println("finished convertIntoTree()");
//		calculateDepths(root, 0);
//	}    
//	 /**
//	  *
//	  */
//	 private void removeUnconnectedEdges() {
//		 Iterator it = _edgesToRemove.iterator();
//		 while (it.hasNext()) {
//			 Edge curEdge = (Edge) it.next();
//			 removeEdge(curEdge);
//		 }
//	 }
//
//
//	 /**
//	  *
//	  */
//	 private void removeUnconnectedNodesFromGraph() {
//		 Iterator it = _nodesToRemove.iterator();
//		 while (it.hasNext()) {
//			 Node node = (Node) it.next();
//			 _graphNodes.remove(node);
//		 }
//	 }
//
//	 private List listTopLevelUnconnectedNodes () {
//		 LinkedList result = new LinkedList();
//		 Iterator allNodes = _graphNodes.iterator();
//		 while (allNodes.hasNext()) {
//			 Node curNode = (Node) allNodes.next();
//			 if (curNode.equals(root)) {
//				 continue;
//			 }
//			 // get inbound nodes (parents) and check how many there is.
//			 // If there is no parents - this node is not attached
//			 // to anything, hence - 'hanging node'
//			 Iterator inboundEdges = getInboundEdges(curNode).iterator();
//			 if (! inboundEdges.hasNext()) {
//				 /// @todo introduced a bug - if a top level node is in the signature of relaton types,
//				 // we don't add it to the list of unconnected nodes, although we should.
//				 // This bug is apparent in the CoreCommunication ontology - keep getting different
//				 // number of unconnected nodes. Probably need a notion of RelationNode rather then
//				 // using node types to distinguish between relation and concept types.
//				 if (!result.contains(curNode)) {
//					 result.add(curNode);
//				 }
//			 }
//		 }
//		 return result;
//	 }
//
//	 /**
//	  *
//	  * @param topLevelUnconnectedNodes
//	  * @todo current approach will list all nodes created for synonyms as well
//	  * (in xml example). if we want to be able to click on synonyms at some point -
//	  * this may introduce NPE. work around - send queries for nodeNames rather
//	  * then graphNodes themselves.
//	  */
//	 private void listItemsToRemove(List topLevelUnconnectedNodes) {
//		 _nodesToRemove = new LinkedList();
//		 _edgesToRemove = new LinkedList();
//
//		 Iterator it = topLevelUnconnectedNodes.iterator();
//		 while (it.hasNext()) {
//			 Node node = (Node) it.next();
//			 listItemsToRemove(node);
//		 }
//	 }
//
//	private void listItemsToRemove (Node node) {
//		 if (!_nodesToRemove.contains(node)) {
//			 _nodesToRemove.add(node);
//		 }
//
//		 Iterator curOutEdges = getOutboundEdges(node).iterator();
//		 while (curOutEdges.hasNext()) {
//			 Edge curEdge = (Edge) curOutEdges.next();
//			 Node toNode = curEdge.getToNode();
//			 if (toNode == root) {
//				 // don't remove parents of root node
//				 continue;
//			 }
//			 _edgesToRemove.add(curEdge);
//			 if (getInboundEdges(toNode).size() > 1 ) {
//				 if ( ! nodeIsInGivenBranch(root, toNode)) {
//					 listItemsToRemove(toNode);
//				 }
//			 }
//			 else {
//				 listItemsToRemove(toNode);
//			 }
//		 }
//	 }
//	
//	/**
//	 * Convert Graph into Tree by cloning nodes with duplicate parents (inbound _graphEdges)
//	 *
//	 * @param   root - root node for the graph
//	 * @throws  ontorama.ontotools.NoSuchRelationLinkException
//	 */
//	private void convertIntoTree(Node root)
//			throws NoSuchRelationLinkException {
//		LinkedList queue = new LinkedList();
//		queue.add(root);
//		while (!queue.isEmpty()) {
//			Node nextQueueNode = (Node) queue.remove(0);
//			debug.message(
//					"Graph",
//					"convertIntoTree",
//					"--- processing node " + nextQueueNode.getName() + " -----");
//			Iterator allOutboundEdges = getOutboundEdges(nextQueueNode).iterator();
//			while (allOutboundEdges.hasNext()) {
//				Edge curEdge = (Edge) allOutboundEdges.next();
//				Node curNode = curEdge.getToNode();
//				queue.add(curNode);
//				List inboundEdgesList = getInboundEdgesDisplayedInGraph(curNode);
//				if (inboundEdgesList.size() <= 1) {
//					continue;
//				}
//				cloneInboundEdges(inboundEdgesList, curEdge);
//			}
//		}
//	}
//
//	private void cloneInboundEdges(List inboundEdgesList, Edge curEdge) throws NoSuchRelationLinkException {
//		Iterator inboundEdges = inboundEdgesList.iterator();
//		Node toNode = curEdge.getToNode();
//		// do not need to clone all edges - only need to clone (edges - 1), otherwise will
//		// end up with too many clones.
//		if (inboundEdges.hasNext()) {
//			inboundEdges.next();
//		}
//		List edgesToCloneQueue = new LinkedList();
//		while (inboundEdges.hasNext()) {
//			Edge edge = (Edge) inboundEdges.next();
//			EdgeType edgeType = edge.getEdgeType();
//			if (OntoramaConfig.getEdgeDisplayInfo(edgeType).isDisplayInGraph()) {
//				edgesToCloneQueue.add(edge);
//			}
//		}
//
//		while (! edgesToCloneQueue.isEmpty()) {
//			// clone the node
//			if (_topLevelUnconnectedNodes.contains(toNode)) {
//				continue;
//			}
//			Node cloneNode = toNode.makeClone();
//			_graphNodes.add(cloneNode);
//
//			Edge edgeToClone = (Edge) edgesToCloneQueue.remove(0);
//			Edge newEdge = new EdgeImpl(edgeToClone.getFromNode(),  cloneNode, edgeToClone.getEdgeType());
//			try {
//				addEdge(newEdge);
//			}
//			catch (GraphModificationException e) {
//			}
//			removeEdge(edgeToClone);
//			// copy/clone all structure below
//			deepCopy(toNode, cloneNode);
//		}
//	}
//
//	/**
//	 * Recursively copy all node's outbound _graphEdges into cloneNode, so we
//	 * and up with cloneNode that has exactly the same children as given node.
//	 * These children are not first node's descendants themselfs - but their clones.
//	 *
//	 * @param   node    original node
//	 * @param   cloneNode   copy node that needs all outbound _graphEdges filled in
//	 * @throws  ontorama.ontotools.NoSuchRelationLinkException
//	 */
//	private void deepCopy(Node node, Node cloneNode)
//			throws NoSuchRelationLinkException {
//		Iterator outboundEdgesIterator = getOutboundEdges(node).iterator();
//		while (outboundEdgesIterator.hasNext()) {
//			Edge curEdge = (Edge) outboundEdgesIterator.next();
//			Node toNode = curEdge.getToNode();
//			Node cloneToNode = toNode.makeClone();
//			Edge newEdge = new EdgeImpl(cloneNode, cloneToNode, curEdge.getEdgeType());
//			try {
//				addEdge(newEdge);
//			}
//			catch (GraphModificationException e) {
//			}
//			EdgeType edgeType = curEdge.getEdgeType();
//			if (! OntoramaConfig.getEdgeDisplayInfo(edgeType).isDisplayInGraph()) {
//				continue;
//			}
//			deepCopy(toNode, cloneToNode);
//		}
//	}
	
}
