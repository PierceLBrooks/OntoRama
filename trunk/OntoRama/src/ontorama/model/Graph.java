package ontorama.model;

import ontorama.webkbtools.query.QueryResult;
import ontorama.ontologyConfig.RelationLinkDetails;

import java.util.List;
import java.util.Iterator;

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

    public void registerEdge(Edge edge);


    public List getEdgesList ();


    public List getOutboundEdges(Node node);

    public List getInboundEdges (Node node);

    public List getOutboundEdgeNodesList(Node node);

    public List getInboundEdgeNodesList(Node node);

    public List getOutboundEdgeNodes(Node node);

    public List getInboundEdgeNodes (Node node);

    public List getInboundEdgeNodes (Node node, EdgeType relLink);



}