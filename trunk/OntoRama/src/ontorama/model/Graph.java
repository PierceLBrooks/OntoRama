package ontorama.model;

import ontorama.webkbtools.query.QueryResult;

import java.util.List;

/**
 *
 */

public interface Graph {

    /**
     * Returns root node of the tree.
     * The tree is modified from acyclic graph
     */
    public GraphNode getRootNode();


    /**
     *
     * @return   list of unconnected nodes
     * @todo perhaps this should be called something like getBranchRootList or getTreeRootsList
     */
    public List getUnconnectedNodesList();

    /**
     * return list of graph nodes
     * @return list of graph nodes
     * @todo needed this to work around unconnected nodes problem. maybe don't need this.
     * maybe should just have a list of brach root nodes
     */
    public List getNodesList();

    /**
     *
     * @param rootNode
     * @param node
     */
    public boolean nodeIsInGivenBranch(GraphNode rootNode, GraphNode node);

    public void registerEdge(Edge edge);



}