package ontorama.model.tree;

import ontorama.model.graph.Edge;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Nov 25, 2002
 * Time: 2:25:30 PM
 * To change this template use Options | File Templates.
 */
public class TreeEdgeImpl implements TreeEdge {

    private Edge _graphEdge;
    private TreeNode _toNode;

    public TreeEdgeImpl (Edge graphEdge, TreeNode toNode) {
        _graphEdge = graphEdge;
        _toNode = toNode;
    }

    public Edge getGraphEdge() {
        return _graphEdge;
    }

    public TreeNode getToNode() {
        return _toNode;
    }


}
