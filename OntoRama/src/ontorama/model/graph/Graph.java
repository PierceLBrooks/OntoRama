package ontorama.model.graph;

import java.util.List;

import ontorama.ontotools.NoSuchRelationLinkException;

/**
 * @todo it might be a pretty good idea to put factory methods for nodes and edges on this interface.
 */

public interface Graph {

    /**
     * Returns root node of the tree.
     * The tree is modified from acyclic graph
     *
     * @todo this seems to be a leftover from the code before we did support forests.
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

    public boolean nodeIsInGivenBranch(Node rootNode, Node node);

    public List getEdgesList ();

    public List getOutboundEdges(Node node);
    public List getInboundEdges (Node node);

    /// @todo isn't this more like getInboundEdgesByType ? Same for the outbound version?
    public List getInboundEdgeNodes (Node node, EdgeType relLink);
    public List getOutboundEdgeNodes (Node node, EdgeType relLink);

    public List getOutboundEdgesDisplayedInGraph (Node node);

    public List getInboundEdgesDisplayedInGraph (Node node);

    public Edge getEdge(Node fromNode, Node toNode, EdgeType edgeType);

    public void removeEdge(Edge remEdge);
    public void addEdge(Edge edge) throws GraphModificationException, NoSuchRelationLinkException ;
    /// @todo do we really need this method?
    public void addEdge(Node fromNode, Node toNode, EdgeType edgeType) throws NoSuchRelationLinkException, GraphModificationException;

    public void addNode (Node node)  throws GraphModificationException;

    public void removeNode (Node node);

}