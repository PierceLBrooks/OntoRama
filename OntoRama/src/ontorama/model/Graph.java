package ontorama.model;

import ontorama.webkbtools.util.NoSuchRelationLinkException;

import java.util.List;

/**
 *
 */

public interface Graph {

    /**
     * Returns root node of the tree.
     * The tree is modified from acyclic graph
     */
    public Node getRootNode();


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
    public boolean nodeIsInGivenBranch(Node rootNode, Node node);

    public List getEdgesList ();

    public List getOutboundEdges(Node node);
    public List getInboundEdges (Node node);

    public List getInboundEdgeNodes (Node node, EdgeType relLink);
    public List getOutboundEdgeNodes (Node node, EdgeType relLink);

    /**
     * @todo this method doesn't belong here - should have another abstraction layer - Tree for this
     * @param node
     * @return
     */
    public List getOutboundEdgesDisplayedInGraph (Node node);

    /**
     * @todo this method doesn't belong here - should have another abstraction layer - Tree for this
     * @param node
     * @return
     */
    public List getInboundEdgesDisplayedInGraph (Node node);

    public Edge getEdge(Node fromNode, Node toNode, EdgeType edgeType);

    public void removeEdge(Edge remEdge);
    public void addEdge(Edge edge);
    public void addEdge(Node fromNode, Node toNode, EdgeType edgeType) throws NoSuchRelationLinkException;

    public void addNode (Node node);

    public void removeNode (Node node);

}