package ontorama.model.tree;

import ontorama.model.graph.Edge;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Nov 25, 2002
 * Time: 2:03:52 PM
 * To change this template use Options | File Templates.
 */
public interface TreeEdge {

    public Edge getGraphEdge ();

    public TreeNode getToNode ();
}
