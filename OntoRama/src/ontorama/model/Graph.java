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
    public NodeImpl getRootNode();


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
    public boolean nodeIsInGivenBranch(NodeImpl rootNode, NodeImpl node);

    public void registerEdge(Edge edge);


    public List getEdgesList ();


    public Iterator getOutboundEdges(NodeImpl node);

    public Iterator getInboundEdges (NodeImpl node);

    public List getOutboundEdgeNodesList(NodeImpl node);

    public List getInboundEdgeNodesList(NodeImpl node);

    public Iterator getOutboundEdgeNodes(NodeImpl node);

    public Iterator getInboundEdgeNodes (NodeImpl node);

    public Iterator getInboundEdgeNodes (NodeImpl node, RelationLinkDetails relLink);



}