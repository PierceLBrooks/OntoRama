package ontorama.model.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ontorama.OntoramaConfig;
import ontorama.model.graph.events.GraphEdgeAddedEvent;
import ontorama.model.graph.events.GraphEdgeRemovedEvent;
import ontorama.model.graph.events.GraphNodeAddedEvent;
import ontorama.model.graph.events.GraphNodeRemovedEvent;
import ontorama.model.graph.events.GraphReducedEvent;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.query.QueryResult;
import org.tockit.events.EventBroker;

/**
 * Build a collection of GraphNodes and Edges that form a Graph.
 * GraphNodes are built from OntologyTypes and Edges are bult from RelationLinks attached to Ontology Type.
 * GraphBuilder is given a QueryResult (that contains OntologyTypes Iterator)
 *
 * This results in acyclic graph which is converted into a tree.
 * Graph removes all duplicate parents by cloning the children (Removes all
 * duplicate inbound _graphEdges by cloning outbound _graphEdges)
 *
 * Graph holds the root node of the graph.
 *
 * <p>
 * Copyright:    Copyright (c) 2002
 * <br>
 * Company:     DSTC
 */
public class GraphImpl implements Graph {

    /**
     * root node
     */
    private Node root = null;

    /**
     * list of unconnected nodes or top level nodes
     */
    private List<Node> _topLevelUnconnectedNodes;

    /**
     * list of all graph nodes
     */
    private List<Node> _graphNodes;

    /**
     * list holding all _graphEdges
     */
    private List<Edge> _graphEdges = new LinkedList<Edge>();

    private EventBroker _eventBroker;

    private String termName;

    /**
     * Constructor for GraphImpl
     */
    public GraphImpl() {
        _topLevelUnconnectedNodes = new LinkedList<Node>();
        _graphNodes = new LinkedList<Node>();
        _eventBroker = new EventBroker();
    }

    /**
     * Build Graph from given QueryResult.
     *
     * NOTE: query returns an iterator of ontology types. some of those types may
     * not be relevant for our ui. For example: consider following rdf:
     *       <rdfs:Class rdf:about="http://www.webkb.org/kb/theKB_terms.rdf/comms#WirelessNetwork">
     *          <rdfs:subClassOf rdf:resource="http://www.webkb.org/kb/theKB_terms.rdf/wn#Network_2"/>
     *          <rdfs:subClassOf rdf:resource="http://www.webkb.org/kb/theKB_terms.rdf/wn#Network_3"/>
     *          <rdfs:subClassOf rdf:resource="http://www.webkb.org/kb/theKB_terms.rdf/comms#TransmissionObject"/>
     *       </rdfs:Class>
     *  this example will produce ontology types: comms#WirelessNetwork, wn#Network_2, wn#Network_3, comms#TransmissionObject
     *  where comms#WirelessNetwork has 3 parents. Therefor we clone this node, but wn#Network_2, wn#Network_3 don't have
     *  parents in the comms ontology, so they are not connected to any nodes and this means they are not displayed in the ui.
     *  Yet, comms#WirelessNetwork is thinking it's got 3 clones, but user can't navigate to them.
     *  (We will call those unconnected nodes 'hanging' nodes).
     *  These nodes will be presented in a separate list, at present called "Componenets List"
     *  located in the toolbar of the main application window.
     */
    public GraphImpl(QueryResult queryResult) throws InvalidArgumentException {
       	this();
       	try{
	        termName = queryResult.getQuery().getQueryTypeName();
	        List<Node> nodesList = queryResult.getNodesList();
	        List<Edge> edgesList = queryResult.getEdgesList();

	        buildGraph( nodesList, edgesList);
	        if (termName == null) {
	            root = findRootNode();
	        }
	        else {
	            root = findRootNode(termName);
	            if (root == null) {
	                throw new NoTypeFoundInResultSetException(termName);
	            }
	        }
       	} catch (GraphCyclesDisallowedException e) {
       		throw new InvalidArgumentException("Cyclic structure in query result", e);
       	}
    }
    
    public EventBroker getEventBroker () {
    	return _eventBroker;
    }

    private void buildGraph( List<Node> nodesList, List<Edge> edgesList) throws GraphCyclesDisallowedException {
        _graphNodes = nodesList;
        _graphEdges = edgesList;
        
        checkForCycles(edgesList);

    	_topLevelUnconnectedNodes = listTopLevelUnconnectedNodes();
    }

    private void checkForCycles(List<Edge> edgesList) throws GraphCyclesDisallowedException {
        List edgesToRemove = new LinkedList();
        Iterator<Edge> it = edgesList.iterator();
        while (it.hasNext()) {
            Edge curEdge = it.next();
            List curEdgesToRemove = checkEdgeForCycle(curEdge);
            edgesToRemove.addAll(curEdgesToRemove);
        }
        Iterator edgesToRemoveIterator = edgesToRemove.iterator();
        while (edgesToRemoveIterator.hasNext()) {
            Edge curEdge = (Edge) edgesToRemoveIterator.next();
            removeEdge(curEdge);
        }
    }

    private List checkEdgeForCycle (Edge oneWayEdge) throws GraphCyclesDisallowedException {
        List edgesToRemove = new LinkedList();

        Node fromNode = oneWayEdge.getFromNode();
        Node toNode = oneWayEdge.getToNode();
        EdgeType edgeType = oneWayEdge.getEdgeType();

        if (! OntoramaConfig.getEdgeDisplayInfo(edgeType).isDisplayInGraph()) {
            return new LinkedList();
        }

        Edge reversedEdge = getEdge(toNode, fromNode, edgeType);
        if ((oneWayEdge != null) && (reversedEdge != null)) {
            String message = "Detected cycle in the graph we are trying to display. Relation links: \n";
            message = message + "edge: " + fromNode.getName() + " -> " + toNode.getName() + " , edgeType = " + edgeType.getName() + "\n";
            message = message + " and \n";
            message = message + "edge: " + toNode.getName() + " -> " + fromNode.getName() + " , edgeType = " + edgeType.getName() + "\n";
            message = message + " are reversable. This is not going to work in the current graph model, \n";
            message = message + " Please consider changing dispay properties for this relaton link in the config file.";

//            edgesToRemove.add(oneWayEdge);
//            //edgesToRemove.add(reversedEdge);
//
//            //removeEdge(oneWayEdge);
//            //removeEdge(reversedEdge);
//
//            System.err.println("\n\n" + message);
//            _eventBroker.processEvent(new ErrorEvent(message));
			throw new GraphCyclesDisallowedException(message);
        }
        return edgesToRemove;
    }

    private Node findRootNode (String rootNodeName) {
        Iterator<Node> it = _graphNodes.iterator();
        while (it.hasNext()) {
            Node node = it.next();
            if (node.getName().equals(rootNodeName)) {
                return node;
            }
        }
        return null;
    }

    private List<Node> listTopLevelUnconnectedNodes () {
        LinkedList<Node> result = new LinkedList<Node>();
        Iterator<Node> allNodes = _graphNodes.iterator();
        while (allNodes.hasNext()) {
            Node curNode = allNodes.next();
            if (curNode.equals(root)) {
                continue;
            }
            // get inbound nodes (parents) and check how many there is.
            // If there is no parents - this node is not attached
            // to anything, hence - 'hanging node'
            Iterator<Edge> inboundEdges = getInboundEdges(curNode).iterator();
            if (! inboundEdges.hasNext()) {
                /// @todo introduced a bug - if a top level node is in the signature of relaton types,
                // we don't add it to the list of unconnected nodes, although we should.
                // This bug is apparent in the CoreCommunication ontology - keep getting different
                // number of unconnected nodes. Probably need a notion of RelationNode rather then
                // using node types to distinguish between relation and concept types.
                if (!result.contains(curNode)) {
                    result.add(curNode);
                }
            }
        }
        return result;
    }

    /**
     * Returns the root node of the graph.
     *
     * @return Node root
     */
    public Node getRootNode() {
    	//System.out.println("GraphImpl:: getRootNode returns: " + root);
        return root;
    }

    /**
     *
     */
    public boolean nodeIsInGivenBranch (Node branchRoot, Node node) {
        List<Node> q = new LinkedList<Node>();
        q.add(branchRoot);

        while (!q.isEmpty()) {
            Node curNode = q.remove(0);
            if (curNode.equals(node)) {
                return true;
            }

            Iterator<Edge> it = getOutboundEdges(curNode).iterator();
            while (it.hasNext()) {
                Edge curEdge = it.next();
                Node nextNode = curEdge.getToNode();
                q.add(nextNode);
            }
        }
        return false;
    }

    /**
     * Get nodes list. Convinience method.
     *
     * @return  list of all graph nodes
     */
    public List<Node> getNodesList() {
        return _graphNodes;
    }

    /**
     *
     */
    public List<Node> getBranchRootsList() {
        return _topLevelUnconnectedNodes;
    }

    public void addEdge(Edge edge) throws GraphModificationException, NoSuchRelationLinkException {
    	System.out.println("GraphImpl::addEdge(edge)");
        Iterator<Edge> it = _graphEdges.iterator();
        while (it.hasNext()) {
            Edge cur = it.next();
            if (edge.equals(cur)) {
                // this edge is already registered
                throw new EdgeAlreadyExistsException(edge);
            }
        }
        addNodesForNewEdge(edge);
        _graphEdges.add(edge);
        _eventBroker.processEvent(new GraphEdgeAddedEvent(this,edge));
    }

    /**
     * @param fromNode
     * @param toNode
     * @param edgeType
     */
    public void addEdge(Node fromNode, Node toNode, EdgeType edgeType) throws NoSuchRelationLinkException, GraphModificationException {
		System.out.println("GraphImpl::addEdge(fromNode, toNode, edgeType)");
        Edge newEdge = OntoramaConfig.getBackend().createEdge(fromNode, toNode, edgeType);
        addEdge(newEdge);
    }

    /**
     *
     * @param edge
     * @throws ontorama.model.graph.GraphModificationException
     */
    private void addNodesForNewEdge (Edge edge) throws GraphModificationException {
        Node fromNode = edge.getFromNode();
        Node toNode = edge.getToNode();
        Node tempNode;
        try  {
            tempNode = existSameName(fromNode);
            if (tempNode != null) {
                edge.setFromNode(tempNode);
            } else {
                addNode(fromNode);
            }
        }
        catch (GraphModificationException e) {
            if (! (e instanceof NodeAlreadyExistsException)) {
                throw e;
				// ignore case where e is instanceof NodeAlreadyExistsException
				// because we may be adding an edge between two existing nodes
            }
        }

        try  {
            tempNode = existSameName(toNode);
            if (tempNode != null) {
                edge.setToNode(tempNode);
            } else {
                addNode(toNode);
            }
        }
        catch (GraphModificationException e) {
            if (! (e instanceof NodeAlreadyExistsException)) {
            	throw e;
                // ignore case where e is instaceof NodeAlreadyExistsException here 
                // because we may be adding an edge between two existing nodes
            }
        }
    }

    /**
     * Help method for getting nodes with same name from the graphNodes list
     */
    protected Node existSameName(Node node) {
        Iterator<Node> it = _graphNodes.iterator();
        while (it.hasNext()){
            Node tmpNode = it.next();
            if (tmpNode.getIdentifier().equals(node.getIdentifier())){
                return tmpNode;
            }
        }
        return null;
    }

	/**
	 * Help method for getting nodes with same name from the graphNodes list
	 */
	protected Edge existSameEdgeName(Edge edge) {
		Iterator<Edge> it = _graphEdges.iterator();
		while (it.hasNext()){
			Edge tmpEdge = it.next();
			if (tmpEdge.getFromNode().getIdentifier().equals(edge.getFromNode().getIdentifier())) {
				if (tmpEdge.getToNode().getIdentifier().equals(edge.getToNode().getIdentifier())) {
					if (tmpEdge.getEdgeType().getName().equals(edge.getEdgeType().getName())) {
						return tmpEdge;
					}
				}
			}
		}
		return null;
	}

    /**
     * Add a node to the graph.
     * For the moment we don't allow to add unconnected nodes.
     * @param node
     * @todo error checking seems a bit stupid ;) maybe don't need this exception at all?...
     */
    public void addNode (Node node) throws GraphModificationException {
    	System.out.println("GraphImpl::addNode, node = " + node.getName());
		System.out.println("addNode: num of nodes: " + getNodesList().size() + ", num of edges: " + getEdgesList().size());
        if (_graphNodes.contains(node)) {
            throw new NodeAlreadyExistsException(node);
        }

        _graphNodes.add(node);
        _eventBroker.processEvent(new GraphNodeAddedEvent(this, node));
    }

    /**
     *
     */
    public void removeEdge(Edge remEdge) {
    	System.out.println("GraphImpl::removeEdge");
		System.out.println("removeEdge: num of nodes: " + getNodesList().size() + ", num of edges: " + getEdgesList().size());
    	
        _graphEdges.remove(remEdge);
        _eventBroker.processEvent(new GraphEdgeRemovedEvent(this, remEdge));
    }

    /**
     *
     */
    public void removeAllEdges() {
        _graphEdges.clear();
        _eventBroker.processEvent(new GraphReducedEvent(this));
    }

    /**
     * @todo This does not remove subtrees, only the node and its 
     * 		connecting edges are removed, which might lead to more
     *      graph components. Should probably behave differently on a 
     * 		Tree structure, but we can't really do anything
     *      useful on a generic Graph.
     */
    public void removeNode (Node node) {
    	System.out.println("GraphImpl::removeNode " + node.getName());
		System.out.println("assertNode: num of nodes: " + getNodesList().size() + ", num of edges: " + getEdgesList().size());    	
        _graphNodes.remove(node);
        List<Edge> edgesToRemove = new ArrayList<Edge>();
        for (Iterator<Edge> iterator = _graphEdges.iterator(); iterator.hasNext();) {
            Edge edge = iterator.next();
            if(edge.getToNode() == node) {
                edgesToRemove.add(edge);
            }
            if(edge.getFromNode() == node) {
                edgesToRemove.add(edge);
            }
        }
        for (Iterator<Edge> iterator = edgesToRemove.iterator(); iterator.hasNext();) {
            Edge edge = iterator.next();
            removeEdge(edge);
        }
        _eventBroker.processEvent(new GraphNodeRemovedEvent(this, node));
    }

    public List<Edge> getEdgesList() {
        return _graphEdges;
    }

    /**
     *
     */
    public Edge getEdge(Node fromNode, Node toNode, EdgeType edgeType) {
    	Iterator<Edge> it = _graphEdges.iterator();
		while (it.hasNext()) {
			Edge curEdge = it.next();
			if ( (curEdge.getFromNode().equals(fromNode)) &&
				(curEdge.getToNode().equals(toNode)) &&
				(curEdge.getEdgeType().equals(edgeType)) ) {
				return curEdge;	
			}
		}
        return null;
    }

    /**
     *
     */
    public List<Edge> getOutboundEdges(Node node) {
    	List<Edge> result = new LinkedList<Edge>();
    	Iterator<Edge> it = _graphEdges.iterator();
    	while (it.hasNext()) {
    		Edge curEdge = it.next();
    		if ( curEdge.getFromNode().equals(node)) {
    			result.add(curEdge);
    		}
    	}
        return result;
    }

    /**
     *
     */
    public List<Edge> getInboundEdges(Node node) {
    	List<Edge> result = new LinkedList<Edge>();
    	Iterator<Edge> it = _graphEdges.iterator();
    	while (it.hasNext()) {
    		Edge curEdge = it.next();
    		if ( curEdge.getToNode().equals(node)) {
    			result.add(curEdge);
    		}
    	}
    	return result;
    }

    /**
     *
     */
    public List<Object> getOutboundEdgeNodesByType(Node node, EdgeType edgeType) {
    	List<Object> result = new LinkedList<Object>();
    	Iterator<Edge> it = getOutboundEdges(node).iterator();
		while (it.hasNext()) {
			Edge curEdge = it.next();
			if (curEdge.getEdgeType().equals(edgeType)) {
				result.add(curEdge.getToNode());
			}
		}
        return result;
    }

    /**
     *
     */
    public List<Object> getInboundEdgeNodesByType(Node node, EdgeType edgeType) {
    	List<Object> result = new LinkedList<Object>();
    	Iterator<Edge> it = getInboundEdges(node).iterator();
    	while (it.hasNext()) {
    		Edge curEdge = it.next();
    		if (curEdge.getEdgeType().equals(edgeType)) {
    			result.add(curEdge.getFromNode());
    		}
    	}
    	return result;
    }

    public List<Edge> getOutboundEdgesDisplayedInGraph (Node node) {
        List<Edge> result = new LinkedList<Edge>();
        Iterator<Edge> it = getOutboundEdges(node).iterator();
        while (it.hasNext()) {
            Edge edge = it.next();
            EdgeType edgeType = edge.getEdgeType();
            if (OntoramaConfig.getEdgeDisplayInfo(edgeType).isDisplayInGraph())  {
                result.add(edge);
            }
        }
        return result;
    }

    public List<Edge> getInboundEdgesDisplayedInGraph (Node node) {
        List<Edge> result = new LinkedList<Edge>();
        Iterator<Edge> it =  getInboundEdges(node).iterator();
        while (it.hasNext()) {
            Edge edge = it.next();
            EdgeType edgeType = edge.getEdgeType();
            if (OntoramaConfig.getEdgeDisplayInfo(edgeType).isDisplayInGraph())  {
                result.add(edge);
            }
        }
        return result;
    }

    public Node findRootNode () {
        Node rootNode = null;
        int maxDescendants = 0;
        Iterator<Node> it = _graphNodes.iterator();
        while (it.hasNext()) {
            Node node = it.next();
            if (getInboundEdges(node).size() == 0) {
                continue;
            }
            int numOfDescendants = calculateNodeDescendants(node);
            if (numOfDescendants > maxDescendants) {
                maxDescendants = numOfDescendants;
                rootNode = node;
            }
            if  (!it.hasNext()) { 
            	if (rootNode == null) {
					// added here to take care of a case where all
					// nodes have the same number of descendants - we just take any node
					rootNode = node;
				}
            }
        }
        return rootNode;
    }

    public int calculateNodeDescendants (Node node) {
        int descendants = 0;

        List<Edge> q = new LinkedList<Edge>();
        q.addAll(getOutboundEdges(node));

        while (!q.isEmpty()) {
            Edge curEdge = q.remove(0);
            EdgeType edgeType = curEdge.getEdgeType();
            /// @todo this if seems a bit hacky
            if (!OntoramaConfig.getEdgeDisplayInfo(edgeType).isDisplayInGraph()) {
                 continue;
            }
            Node curNode = curEdge.getToNode();

            List<Edge> children = getOutboundEdges(curNode);
            q.addAll(children);
            descendants++;
        }
        return descendants;
    }

    public int getNumOfDescendants (Node node) {
    	return calculateNodeDescendants(node);
    }



}

