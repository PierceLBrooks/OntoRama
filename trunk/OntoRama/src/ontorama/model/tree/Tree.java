package ontorama.model.tree;

import ontorama.model.graph.Node;

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
     * The tree is modified from acyclic graph
     */
    public Node getRootNode();

}
