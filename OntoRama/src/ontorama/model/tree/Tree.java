package ontorama.model.tree;

import ontorama.model.graph.Node;

import java.util.List;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 27/09/2002
 * Time: 12:31:02
 * To change this template use Options | File Templates.
 */
public interface Tree {
    /**
     * Returns root node of the tree.
     */
    public TreeNode getRootNode();

    public List getNodesList();

    public List getEdgesList();

}
