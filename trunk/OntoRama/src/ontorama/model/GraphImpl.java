package ontorama.model;



import ontorama.OntoramaConfig;
import ontorama.backends.p2p.model.P2PNode;
import ontorama.model.util.NodeAlreadyExistsException;
import ontorama.model.util.GraphModificationException;
import ontorama.model.util.EdgeAlreadyExistsException;
import ontorama.model.events.*;
import ontorama.util.Debug;
import ontorama.view.OntoRamaApp;
import ontorama.webkbtools.query.QueryResult;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

import java.util.*;

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
    private List _topLevelUnconnectedNodes;

    /**
     * list of all graph nodes
     */
    private List _graphNodes;

    /**
     * list holding all _graphEdges
     */
    private List _graphEdges = new LinkedList();

    private List _nodesToRemove = new LinkedList();
    private List _edgesToRemove = new LinkedList();

    /**
     *
     */
    Debug debug = new Debug(false);

    EventBroker _eventBroker;

    private String termName;

    /**
     * map node to num of descendants.
     * @todo this doesn't belong here
     */
    private Hashtable originalNodeDescendantsMapping = new Hashtable();

    /**
     * Constructor for GraphImpl
     */
    public GraphImpl(EventBroker eventBroker) {
        debug.message(
                "******************* GraphBuilder constructor start *******************");
        _topLevelUnconnectedNodes = new LinkedList();
        _graphNodes = new LinkedList();
        _eventBroker = eventBroker;
        debug.message(
                "******************* GraphBuilder constructor end *******************");
    }

    /**
     * Build Graph from given QueryResult.
     *
     * NOTE: query returns an iterator of ontology types. some of those types may
     * not be relevant for our view. For example: consider following rdf:
     *       <rdfs:Class rdf:about="http://www.webkb.org/kb/theKB_terms.rdf/comms#WirelessNetwork">
     *          <rdfs:subClassOf rdf:resource="http://www.webkb.org/kb/theKB_terms.rdf/wn#Network_2"/>
     *          <rdfs:subClassOf rdf:resource="http://www.webkb.org/kb/theKB_terms.rdf/wn#Network_3"/>
     *          <rdfs:subClassOf rdf:resource="http://www.webkb.org/kb/theKB_terms.rdf/comms#TransmissionObject"/>
     *       </rdfs:Class>
     *  this example will produce ontology types: comms#WirelessNetwork, wn#Network_2, wn#Network_3, comms#TransmissionObject
     *  where comms#WirelessNetwork has 3 parents. Therefor we clone this node, but wn#Network_2, wn#Network_3 don't have
     *  parents in the comms ontology, so they are not connected to any nodes and this means they are not displayed in the view.
     *  Yet, comms#WirelessNetwork is thinking it's got 3 clones, but user can't navigate to them.
     *  (We will call those unconnected nodes 'hanging' nodes).
     *  One way to get around it is: to remove _graphEdges that don't have incoming _graphEdges (parents).
     *  This is our solution for the present.
     *  However, this solution may not be beneficial for some ontologies (that are not strict
     *  hierarchy. Some of RDF examples found on the web are such ontologies). Solution could be:
     *  instead of removing 'hanging' nodes - introduce artafficial root node and attach all these nodes
     *  to it.
     *
     * @param   queryResult
     * @throws  NoSuchRelationLinkException
     * @throws  NoTypeFoundInResultSetException
     */
    public GraphImpl(QueryResult queryResult, EventBroker eventBroker)
            throws
            NoSuchRelationLinkException,
            NoTypeFoundInResultSetException {
        this(eventBroker);
        debug.message(
                "******************* GraphBuilder constructor start *******************");

        // remove all _graphEdges before building new set of _graphEdges
        /// @todo commented out sending this event for now because it creates a null pointer
        // exception sometimes when we load a new graph.
        //removeAllEdges();

        termName = queryResult.getQuery().getQueryTypeName();
        List nodesList = queryResult.getNodesList();
        List edgesList = queryResult.getEdgesList();

        try {
            buildGraph( nodesList, edgesList);
            if (termName == null) {
                root = findRootNode();
                /// @todo a hack here for rdf distillery
                OntoramaConfig.getCurrentExample().setRoot(root.getName());
            }
            else {
                root = findRootNode(termName);
                if (root == null) {
                    throw new NoTypeFoundInResultSetException(termName);
                }
            }
            System.out.println("root = " + root);
            System.out.println("calling trasformGraphIntoTree from GraphImpl");
            transformGraphIntoTree();
        } catch (NoSuchRelationLinkException e) {
            throw e;
        }
        debug.message(
                "******************* GraphBuilder constructor end *******************");
    }

    /**
     *
     */
    private void buildGraph( List nodesList, List edgesList) {
        _graphNodes = nodesList;
        _graphEdges = edgesList;

        checkForCycles(edgesList);

        Iterator it = _graphNodes.iterator();
        while (it.hasNext()) {
            Node curNode = (Node) it.next();
            int descendantsNum = calculateNodeDescendants(curNode);
            originalNodeDescendantsMapping.put(curNode, new Integer(descendantsNum));
        }
    }

    private void checkForCycles(List edgesList) {
        List edgesToRemove = new LinkedList();
        Iterator it = edgesList.iterator();
        while (it.hasNext()) {
            Edge curEdge = (Edge) it.next();
            List curEdgesToRemove = checkEdgeForCycle(curEdge);
            edgesToRemove.addAll(curEdgesToRemove);
        }
        Iterator edgesToRemoveIterator = edgesToRemove.iterator();
        while (edgesToRemoveIterator.hasNext()) {
            Edge curEdge = (Edge) edgesToRemoveIterator.next();
            removeEdge(curEdge);
        }
    }

    public void transformGraphIntoTree () throws NoSuchRelationLinkException {
        _topLevelUnconnectedNodes = listTopLevelUnconnectedNodes();
        listItemsToRemove(_topLevelUnconnectedNodes);
        // clean up
        removeUnconnectedEdges();
        removeUnconnectedNodesFromGraph();

        convertIntoTree(root);
        System.out.println("finished convertIntoTree()");
        calculateDepths(root, 0);
    }

    private List checkEdgeForCycle (Edge oneWayEdge) {
        List edgesToRemove = new LinkedList();

        Node fromNode = oneWayEdge.getFromNode();
        Node toNode = oneWayEdge.getToNode();
        EdgeType edgeType = oneWayEdge.getEdgeType();

        if (! OntoramaConfig.getEdgeDisplayInfo(edgeType).isDisplayInGraph()) {
            return new LinkedList();
        }

        Edge reversedEdge = getEdge(toNode, fromNode, edgeType);
        if ((oneWayEdge != null) && (reversedEdge != null)) {
            String message = "Relation links: \n";
            message = message + "edge: " + fromNode + " -> " + toNode + " , edgeType = " + edgeType + "\n";
            message = message + " and \n";
            message = message + "edge: " + toNode + " -> " + fromNode + " , edgeType = " + edgeType + "\n";
            message = message + " are reversable. This is not going to work in the current graph model, \n";
            message = message + " we won't display this relation link here.\n";
            message = message + " Please consider changing dispay properties for this relaton link in the config file.";

            edgesToRemove.add(oneWayEdge);
            //edgesToRemove.add(reversedEdge);

            //removeEdge(oneWayEdge);
            //removeEdge(reversedEdge);

            System.err.println("\n\n" + message);
            OntoRamaApp.showErrorDialog(message);
        }
        return edgesToRemove;
    }

    private Node findRootNode (String rootNodeName) {
        Iterator it = _graphNodes.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            if (node.getName().equals(rootNodeName)) {
                return node;
            }
        }
        return null;
    }

    /**
     *
     */
    private void removeUnconnectedEdges() {
        Iterator it = _edgesToRemove.iterator();
        while (it.hasNext()) {
            Edge curEdge = (Edge) it.next();
            removeEdge(curEdge);
        }
    }


    /**
     *
     */
    private void removeUnconnectedNodesFromGraph() {
        Iterator it = _nodesToRemove.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            _graphNodes.remove(node);
        }
    }

    private List listTopLevelUnconnectedNodes () {
        LinkedList result = new LinkedList();
        Iterator allNodes = _graphNodes.iterator();
        while (allNodes.hasNext()) {
            Node curNode = (Node) allNodes.next();
            if (curNode.equals(root)) {
                continue;
            }
            // get inbound nodes (parents) and check how many there is.
            // If there is no parents - this node is not attached
            // to anything, hence - 'hanging node'
            Iterator inboundEdges = getInboundEdges(curNode).iterator();
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
     *
     * @param topLevelUnconnectedNodes
     * @todo current approach will list all nodes created for synonyms as well
     * (in xml example). if we want to be able to click on synonyms at some point -
     * this may introduce NPE. work around - send queries for nodeNames rather
     * then graphNodes themselves.
     */
    private void listItemsToRemove(List topLevelUnconnectedNodes) {
        _nodesToRemove = new LinkedList();
        _edgesToRemove = new LinkedList();

        Iterator it = topLevelUnconnectedNodes.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            listItemsToRemove(node);
        }
    }

   private void listItemsToRemove (Node node) {
        if (!_nodesToRemove.contains(node)) {
            _nodesToRemove.add(node);
        }

        Iterator curOutEdges = getOutboundEdges(node).iterator();
        while (curOutEdges.hasNext()) {
            Edge curEdge = (Edge) curOutEdges.next();
            Node toNode = curEdge.getToNode();
            if (toNode == root) {
                // don't remove parents of root node
                continue;
            }
            _edgesToRemove.add(curEdge);
            if (getInboundEdges(toNode).size() > 1 ) {
                if ( ! nodeIsInGivenBranch(root, toNode)) {
                    listItemsToRemove(toNode);
                }
            }
            else {
                listItemsToRemove(toNode);
            }
        }
    }

    /**
     * Returns the root node of the graph.
     *
     * @return Node root
     */
    public Node getRootNode() {
        return root;
    }

    /**
     *
     */
    public boolean nodeIsInGivenBranch (Node branchRoot, Node node) {
        List q = new LinkedList();
        q.add(branchRoot);

        while (!q.isEmpty()) {
            Node curNode = (Node) q.remove(0);
            if (curNode.equals(node)) {
                return true;
            }

            Iterator it = getOutboundEdges(curNode).iterator();
            while (it.hasNext()) {
                Edge curEdge = (Edge) it.next();
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
    public List getNodesList() {
        return _graphNodes;
    }

    /**
     *
     */
    public List getUnconnectedNodesList() {
        return _topLevelUnconnectedNodes;
    }

    /**
     * Test if current Graph is a Tree
     *
     * @param root  - root Node
     */
    private boolean testIfTree(Node root) {
        LinkedList queue = new LinkedList();
        boolean isTree = true;

        queue.add(root);
        while (!queue.isEmpty()) {
            Node nextQueueNode = (Node) queue.remove(0);
            Iterator allOuboundEdges = getOutboundEdges(nextQueueNode).iterator();
            while (allOuboundEdges.hasNext()) {
                Edge curEdge = (Edge) allOuboundEdges.next();
                Node curNode = curEdge.getToNode();
                queue.add(curNode);
                Iterator inboundEdges = getInboundEdges(curNode).iterator();
                int count = 0;
                while (inboundEdges.hasNext()) {
                    count++;
                    inboundEdges.next();
                }
                if (count > 1) {
                    debug.message(
                            "Graph",
                            "testIfTree",
                            " node "
                            + curNode.getName()
                            + " has multiple inbound _graphEdges");
                    isTree = false;
                }
            }
        }
        return isTree;
    }

    /**
     * Convert Graph into Tree by cloning nodes with duplicate parents (inbound _graphEdges)
     *
     * @param   root - root node for the graph
     * @throws  NoSuchRelationLinkException
     */
    private void convertIntoTree(Node root)
            throws NoSuchRelationLinkException {
        LinkedList queue = new LinkedList();
        queue.add(root);
        System.out.println("convertIntoTree, root = " + root);
        while (!queue.isEmpty()) {
            Node nextQueueNode = (Node) queue.remove(0);
            debug.message(
                    "Graph",
                    "convertIntoTree",
                    "--- processing node " + nextQueueNode.getName() + " -----");
            Iterator allOutboundEdges = getOutboundEdges(nextQueueNode).iterator();
            while (allOutboundEdges.hasNext()) {
                Edge curEdge = (Edge) allOutboundEdges.next();
                Node curNode = curEdge.getToNode();
                queue.add(curNode);
                List inboundEdgesList = getInboundEdgesDisplayedInGraph(curNode);
                if (inboundEdgesList.size() <= 1) {
                    continue;
                }
                cloneInboundEdges(inboundEdgesList, curEdge);
            }
        }
    }

    private void cloneInboundEdges(List inboundEdgesList, Edge curEdge) throws NoSuchRelationLinkException {
        Iterator inboundEdges = inboundEdgesList.iterator();
        Node toNode = curEdge.getToNode();
        // do not need to clone all edges - only need to clone (edges - 1), otherwise will
        // end up with too many clones.
        if (inboundEdges.hasNext()) {
            inboundEdges.next();
        }
        List edgesToCloneQueue = new LinkedList();
        while (inboundEdges.hasNext()) {
            Edge edge = (Edge) inboundEdges.next();
            EdgeType edgeType = edge.getEdgeType();
            if (OntoramaConfig.getEdgeDisplayInfo(edgeType).isDisplayInGraph()) {
                edgesToCloneQueue.add(curEdge);
            }
        }

        while (! edgesToCloneQueue.isEmpty()) {
            // clone the node
            if (_topLevelUnconnectedNodes.contains(toNode)) {
                continue;
            }
            Node cloneNode = toNode.makeClone();
            _graphNodes.add(cloneNode);

            Edge edgeToClone = (Edge) edgesToCloneQueue.remove(0);
            Edge newEdge = new EdgeImpl(
                    edgeToClone.getFromNode(),
                    cloneNode,
                    edgeToClone.getEdgeType());
            try {
                addEdge(newEdge);
            }
            catch (GraphModificationException e) {
            }
            removeEdge(edgeToClone);
            // copy/clone all structure below
            deepCopy(toNode, cloneNode);
        }
    }

    /**
     * Recursively copy all node's outbound _graphEdges into cloneNode, so we
     * and up with cloneNode that has exactly the same children as given node.
     * These children are not first node's descendants themselfs - but their clones.
     *
     * @param   node    original node
     * @param   cloneNode   copy node that needs all outbound _graphEdges filled in
     * @throws  NoSuchRelationLinkException
     */
    private void deepCopy(Node node, Node cloneNode)
            throws NoSuchRelationLinkException {
        Iterator outboundEdgesIterator = getOutboundEdges(node).iterator();
        while (outboundEdgesIterator.hasNext()) {
            Edge curEdge = (Edge) outboundEdgesIterator.next();
            Node toNode = curEdge.getToNode();
            Node cloneToNode = toNode.makeClone();
            Edge newEdge = new EdgeImpl(cloneNode, cloneToNode, curEdge.getEdgeType());
            try {
                addEdge(newEdge);
            }
            catch (GraphModificationException e) {
            }
            EdgeType edgeType = curEdge.getEdgeType();
            if (! OntoramaConfig.getEdgeDisplayInfo(edgeType).isDisplayInGraph()) {
                continue;
            }
            deepCopy(toNode, cloneToNode);
        }
    }

    public void addEdge(Edge edge) throws GraphModificationException, NoSuchRelationLinkException {
        Iterator it = _graphEdges.iterator();
        while (it.hasNext()) {
            Edge cur = (Edge) it.next();
            /// @todo we might want to implement equality here
            if ((edge.getFromNode().equals(cur.getFromNode())) &&
                    (edge.getToNode().equals(cur.getToNode())) &&
                    (edge.getEdgeType() == cur.getEdgeType())) {
                // this edge is already registered
                throw new EdgeAlreadyExistsException(edge);
            }
        }
        addNodesForNewEdge(edge);
        _graphEdges.add(edge);
        _eventBroker.processEvent(new EdgeAddedEvent(this,edge));
    }

    /**
     * @param fromNode
     * @param toNode
     * @param edgeType
     */
    public void addEdge(Node fromNode, Node toNode, EdgeType edgeType) throws NoSuchRelationLinkException, GraphModificationException {
        Edge newEdge = new EdgeImpl(fromNode, toNode, edgeType);
        System.out.println("adding new edge " + newEdge);
        addEdge(newEdge);
    }

    /**
     *
     * @param edge
     * @throws GraphModificationException
     * @todo stupid method - only for one purpose - not to throw and exception where we don't want it.
     * need to rethink exceptions structure.
     */
    private void addNodesForNewEdge (Edge edge) throws GraphModificationException {
        Node fromNode = edge.getFromNode();
        Node toNode = edge.getToNode();
        Node tempNode;
        try  {
            tempNode = existSameName(fromNode);
            if (tempNode != null) {
                ((EdgeImpl) edge).setFromNode(tempNode);
            } else {
                addNode(fromNode);
            }
        }
        catch (GraphModificationException e) {
            if (e instanceof NodeAlreadyExistsException) {
                // ignore here because we may be adding an edge between two existing nodes
            }
            else {
                throw e;
            }
        }

        try  {
            tempNode = existSameName(toNode);
            if (tempNode != null) {
                ((EdgeImpl) edge).setToNode(tempNode);
            } else {
                addNode(toNode);
            }
        }
        catch (GraphModificationException e) {
            if (e instanceof NodeAlreadyExistsException) {
                // ignore here because we may be adding an edge between two existing nodes
            }
            else {
                throw e;
            }
        }


    }

    /**
     * Help method for getting nodes with same name fron the graphNodes list
     */
    protected Node existSameName(Node node) {
        Iterator it = _graphNodes.iterator();
        while (it.hasNext()){
            Node tmpNode = (Node)it.next();
            if (tmpNode.getIdentifier().equals(node.getIdentifier())){
                return tmpNode;
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
        /*
        List inboundEdges = getInboundEdges(node);
        List outboundEdges = getOutboundEdges(node);
        if ( (inboundEdges.size() == 0) && (outboundEdges.size() == 0) ) {
            throw new AddUnconnectedNodeIsDisallowedException(node);
        }
        */
        if (_graphNodes.contains(node)) {
            throw new NodeAlreadyExistsException(node);
        }

        _graphNodes.add(node);
        _eventBroker.processEvent(new NodeAddedEvent(this, node));
    }

    /**
     *
     */
    public void removeEdge(Edge remEdge) {
        _graphEdges.remove(remEdge);
        _eventBroker.processEvent(new EdgeRemovedEvent(this, remEdge));
    }

    /**
     *
     */
    public void removeAllEdges() {
        _graphEdges.clear();
        /// @todo do we want to sent one event each time? Or maybe a specific one?
        _eventBroker.processEvent(new GraphReducedEvent(this));
    }

    /**
     * @todo This does not remove subtrees, only the node and its connecting edges are removed, which might lead to more
     *       graph components. Should probably behave differently on a Tree structure, but we can't really do anthing
     *       useful on a generic Graph.
     */
    public void removeNode (Node node) {
        _graphNodes.remove(node);
        List edgesToRemove = new ArrayList();
        for (Iterator iterator = _graphEdges.iterator(); iterator.hasNext();) {
            Edge edge = (Edge) iterator.next();
            if(edge.getToNode() == node) {
                edgesToRemove.add(edge);
            }
            if(edge.getFromNode() == node) {
                edgesToRemove.add(edge);
            }
        }
        for (Iterator iterator = edgesToRemove.iterator(); iterator.hasNext();) {
            Edge edge = (Edge) iterator.next();
            removeEdge(edge);
        }
        _eventBroker.processEvent(new NodeRemovedEvent(this, node));
    }

    public List getEdgesList() {
        return _graphEdges;
    }

    /**
     *
     */
    public Edge getEdge(Node fromNode, Node toNode, EdgeType edgeType) {
        Iterator it = getOutboundEdges(fromNode, edgeType);
        while (it.hasNext()) {
            Edge curEdge = (Edge) it.next();
            Node curNode = curEdge.getToNode();
            if (curNode.equals(toNode)) {
                return curEdge;
            }
        }
        return null;
    }

    /**
     *
     */
    public List getOutboundEdges(Node node) {
        return getEdges(node, true);
    }

    /**
     *
     */
    public List getInboundEdges(Node node) {
        return getEdges(node, false);
    }

    /**
     *
     * @param  node Node
     *         flag - true if we want to get list of outbound _graphEdges,
     *         false of we want to get a list of inbound _graphEdges.
     * @return iterator of Edges
     */
    private List getEdges(Node node, boolean flag) {
        List result = new LinkedList();
        Iterator it = _graphEdges.iterator();
        while (it.hasNext()) {
            EdgeImpl cur = (EdgeImpl) it.next();
            Node graphNode = cur.getEdgeNode(flag);
            if (graphNode.equals(node)) {
                result.add(cur);
            }
        }
        return result;
    }

    /**
     *
     * @param  node Node
     *         int relationType
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.      *
     * @return iterator of Edges
     */
    private Iterator getEdges(Node node, EdgeType relationType, boolean flag) {
        List result = new LinkedList();
        Iterator nodeEdgesIt = getEdges(node, flag).iterator();
        while (nodeEdgesIt.hasNext()) {
            Edge cur = (Edge) nodeEdgesIt.next();
            if (cur.getEdgeType() == relationType) {
                result.add(cur);
            }
        }
        return result.iterator();
    }

    /**
     *
     */
    public Iterator getOutboundEdges(Node node, EdgeType edgeType) {
        return getEdges(node, edgeType, true);
    }

    /**
     *
     */
    public Iterator getInboundEdges(Node node, EdgeType edgeType) {
        return getEdges(node, edgeType, false);
    }

    /**
     *
     * @param  node Node
     *         Set relationLinks
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.
     * @return iterator of Nodes
     */
    private Iterator getEdgeNodes(Node node, Set relationLinks, boolean flag) {
        List result = new LinkedList();
        Iterator nodeEdgesIt = getEdges(node, flag).iterator();
        while (nodeEdgesIt.hasNext()) {
            EdgeImpl cur = (EdgeImpl) nodeEdgesIt.next();
            Iterator it = relationLinks.iterator();
            while (it.hasNext()) {
                EdgeType curRel = (EdgeType) it.next();
                if (cur.getEdgeType().equals(curRel)) {
                    result.add(cur.getEdgeNode(!flag));
                }
            }
        }
        return result.iterator();
    }

    /**
     *
     */
    public List getOutboundEdgeNodes(Node node, EdgeType edgeType) {
        return getEdgeNodes(node, edgeType, true);
    }

    /**
     *
     */
    public List getInboundEdgeNodes(Node node, EdgeType edgeType) {
        return getEdgeNodes(node, edgeType, false);
    }

    /**
     *
     * @param  node Node
     *         int relationType
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.      *
     * @return iterator of Nodes
     */
    private List getEdgeNodes(Node node, EdgeType edgeType, boolean flag) {
        List result = new LinkedList();
        Iterator nodeEdgesIt = getEdges(node, flag).iterator();
        while (nodeEdgesIt.hasNext()) {
            EdgeImpl cur = (EdgeImpl) nodeEdgesIt.next();
            if (cur.getEdgeType().equals(edgeType)) {
                result.add(cur.getEdgeNode(!flag));
            }
        }
        return result;
    }



    /**

     *

     */

    public List getOutboundEdgeNodes(Node node) {

        return getEdgeNodes(node, true);

    }



    /**

     *

     */

    public List getInboundEdgeNodes(Node node) {

        return getEdgeNodes(node, false);

    }



    /**

     *

     * @param  node Node

     *         int relationType

     *         boolean flag - true if we want to get list of outbound nodes,

     *         false of we want to get a list of inbound nodes.      *

     * @return iterator of Nodes

     */

    private List getEdgeNodes(Node node, boolean flag) {

        return getEdgeNodesList(node, flag);

    }



    /**

     *

     * @param  node Node

     *         int relationType

     *         boolean flag - true if we want to get list of outbound nodes,

     *         false of we want to get a list of inbound nodes.      *

     * @return list of Nodes

     */

    private List getEdgeNodesList(Node node, boolean flag) {

        List result = new LinkedList();

        Iterator nodeEdgesIt = getEdges(node, flag).iterator();

        while (nodeEdgesIt.hasNext()) {

            EdgeImpl cur = (EdgeImpl) nodeEdgesIt.next();
            result.add(cur.getEdgeNode(!flag));

        }

        return result;

    }



    /**

     * Calculate the depths of all children in respect to this node.

     */

    public void calculateDepths(Node top, int depth) {

        top.setDepth(depth);

        Iterator it = getOutboundEdges(top).iterator();

        while (it.hasNext()) {

            Edge outboundEdge = (Edge) it.next();

            Node outboundNode = outboundEdge.getToNode();

            outboundNode.setDepth(depth + 1);

            calculateDepths(outboundNode, depth + 1);

        }

    }



    public List getOutboundEdgesDisplayedInGraph (Node node) {
        List result = new LinkedList();
        Iterator it = getOutboundEdges(node).iterator();
        while (it.hasNext()) {
            Edge edge = (Edge) it.next();
            EdgeType edgeType = edge.getEdgeType();
            if (OntoramaConfig.getEdgeDisplayInfo(edgeType).isDisplayInGraph())  {
                result.add(edge);
            }
        }
        return result;
    }



    public List getInboundEdgesDisplayedInGraph (Node node) {
        List result = new LinkedList();
        Iterator it =  getInboundEdges(node).iterator();
        while (it.hasNext()) {
            Edge edge = (Edge) it.next();
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
        Iterator it = _graphNodes.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            if (getInboundEdgeNodes(node).size() == 0) {
                continue;
            }
            int numOfDescendants = calculateNodeDescendants(node);
            if (numOfDescendants > maxDescendants) {
                maxDescendants = numOfDescendants;
                rootNode = node;
            }
        }
        return rootNode;
    }

    public int calculateNodeDescendants (Node node) {
        int descendants = 0;

        List q = new LinkedList();
        q.addAll(getOutboundEdges(node));

        while (!q.isEmpty()) {
            Edge curEdge = (Edge) q.remove(0);
            EdgeType edgeType = curEdge.getEdgeType();
            /// @todo this if seems a bit hacky
            if (!OntoramaConfig.getEdgeDisplayInfo(edgeType).isDisplayInGraph()) {
                 continue;
            }
            //Node curNode = (Node) q.remove(0);
            Node curNode = curEdge.getToNode();

            List children = getOutboundEdges(curNode);
            q.addAll(children);
            descendants++;
        }
        return descendants;
    }

    public int getNumOfDescendants (Node node) {
        Integer num = (Integer) originalNodeDescendantsMapping.get(node);
        return num.intValue();
    }



}

