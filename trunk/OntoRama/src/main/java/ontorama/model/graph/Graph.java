package ontorama.model.graph;

import java.util.List;

import org.tockit.events.EventBroker;

import ontorama.ontotools.NoSuchRelationLinkException;

/**
 * 
 * Graph models a collection of nodes and collection of edges.
 * A graph may form collection of trees or collection of nodes.
 * 
 * @todo it might be a pretty good idea to put factory methods for nodes and edges on this interface.
 */

public interface Graph {

    /**
     * Returns root node of the graph (node that corresponds to a query term or
     * if there was no query - node with most descendants)..
     */
    public Node getRootNode();

    /**
     * @return   list of tree roots in the forest
     */
    public List<Node> getBranchRootsList();

	public boolean nodeIsInGivenBranch(Node rootNode, Node node);

    /**
     * return list of graph nodes
     */
    public List<Node> getNodesList();

    public List<Edge> getEdgesList ();

    public List<Edge> getOutboundEdges(Node node);
    public List<Edge> getInboundEdges (Node node);

    public List<Object> getInboundEdgeNodesByType (Node node, EdgeType relLink);
    public List<Object> getOutboundEdgeNodesByType (Node node, EdgeType relLink);

    public List<Edge> getOutboundEdgesDisplayedInGraph (Node node);

    public List<Edge> getInboundEdgesDisplayedInGraph (Node node);

    public Edge getEdge(Node fromNode, Node toNode, EdgeType edgeType);

    public void removeEdge(Edge remEdge);
    public void addEdge(Edge edge) throws GraphModificationException, NoSuchRelationLinkException ;
    public void addEdge(Node fromNode, Node toNode, EdgeType edgeType) throws NoSuchRelationLinkException, GraphModificationException;

    public void addNode (Node node)  throws GraphModificationException;

    public void removeNode (Node node);

	public int getNumOfDescendants (Node node);

	public EventBroker getEventBroker();
}