package ontorama.model.tree;

import ontorama.model.graph.EdgeType;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Nov 25, 2002
 * Time: 2:03:52 PM
 * To change this template use Options | File Templates.
 */
public interface TreeEdge {

    /// @todo need to think through this: maybe should have a getter for
    // a fromNode, or maybe shouldn't have ability to get nodes at all....
    public TreeNode getToNode ();

    public EdgeType getEdgeType ();
}
