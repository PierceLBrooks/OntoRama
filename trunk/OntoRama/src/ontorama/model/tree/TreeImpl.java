package ontorama.model.tree;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import ontorama.model.graph.*;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.OntoramaConfig;

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
    public TreeImpl (Graph graph, Node graphRootNode) throws NoSuchRelationLinkException {
        _graph = graph;
        _graphRootNode = graphRootNode;
        buildTree(graphRootNode);
    }

    public TreeNode getRootNode() {
        return _root;
    }

    public List getNodesList() {
        return _nodes;
    }

    public List getEdgesList() {
        return _edges;
    }

//    private void buildTree (Node graphRootNode) {
//        _root = addTreeNode(graphRootNode);
//        // get outbound edges, create tree node/edge for each graph node/edge component
//        List queue = new LinkedList();
//        queue.add(graphRootNode);
//        while (!queue.isEmpty()) {
//            Node curGraphNode = (Node) queue.remove(0);
//            Iterator outboundEdges = _graph.getOutboundEdgesDisplayedInGraph(curGraphNode).iterator();
//            while (outboundEdges.hasNext()) {
//                Edge curGraphEdge = (Edge) outboundEdges.next();
//                TreeEdge curEdge = addTreeEdge (curGraphEdge);
//
//                queue.add(curGraphEdge.getToNode());
//            }
//        }
//    }

    private void buildTree (Node topGraphNode) {
//        System.out.println("buildTree for graph node " + topGraphNode);
        _root = addTreeNode(topGraphNode);
        traverseBuild(topGraphNode, _root);
    }

    private void traverseBuild (Node topGraphNode, TreeNode topTreeNode ) {
        Iterator outboundEdges = _graph.getOutboundEdgesDisplayedInGraph(topGraphNode).iterator();
        while (outboundEdges.hasNext()) {
            Edge curGraphEdge = (Edge) outboundEdges.next();
            TreeNode toNode = addTreeNode (curGraphEdge.getToNode());
            TreeEdge curEdge = addTreeEdge (curGraphEdge, toNode);
            topTreeNode.addChild(curEdge);
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
        return treeNode;
    }

    private TreeEdge addTreeEdge (Edge graphEdge, TreeNode toNode) {
        //TreeNode toNode = addTreeNode (graphEdge.getToNode());
        if (toNode.getParent() != null) {
            /// @todo handle error with an exception
            System.err.println("tree node " + toNode + " already has a parent!");
            System.exit(-1);
        }
        TreeEdge treeEdge = new TreeEdgeImpl(graphEdge, toNode);
        _edges.add(treeEdge);
        return treeEdge;
    }


}
