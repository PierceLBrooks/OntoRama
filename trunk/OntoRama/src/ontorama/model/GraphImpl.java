package ontorama.model;

import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.RelationLinkDetails;
import ontorama.util.Debug;
import ontorama.view.OntoRamaApp;
import ontorama.webkbtools.query.QueryResult;
import ontorama.webkbtools.util.NoSuchPropertyException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

import java.util.*;

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

//    /**
//     * Hold processed nodes (nodes created for Graph)
//     * Keys - node names, values - node objects
//     */
//    private Hashtable processedNodes = new Hashtable();

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


    private List _nodesToRemove = new LinkedList();
    private List _edgesToRemove = new LinkedList();


    /**
     *
     */
    Debug debug = new Debug(false);
    /**
     * list holding all _graphEdges
     */
    public List _graphEdges = new LinkedList();

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
     * @throws  NoSuchPropertyException
     * @todo  maybe we shouldn't remove non-connected _graphEdges, but attach them to artafficial
     *        root node. (this is good solution for static ontologies, but not so for
     *        webkb ontology (remember ontology viewer for hibkb)).
     */
    public GraphImpl(QueryResult queryResult)
            throws
            NoSuchRelationLinkException,
            NoTypeFoundInResultSetException,
            NoSuchPropertyException {
        debug.message(
                "******************* GraphBuilder constructor start *******************");
        _topLevelUnconnectedNodes = new LinkedList();
        _graphNodes = new LinkedList();
        // remove all _graphEdges before building new set of _graphEdges
        removeAllEdges();

        String termName = queryResult.getQuery().getQueryTypeName();
        List nodesList = queryResult.getNodesList();
        List edgesList = queryResult.getEdgesList();

        try {
            buildGraph( nodesList, edgesList);
            root = findRootNode(termName);
            if (root == null) {
                throw new NoTypeFoundInResultSetException(termName);
            }

            _topLevelUnconnectedNodes = listTopLevelUnconnectedNodes();
            listItemsToRemove(_topLevelUnconnectedNodes);
            System.out.println("items to remove num = " + _topLevelUnconnectedNodes.size() + ", nodes num = " + _graphNodes);


            // clean up
            removeUnconnectedEdges();
            removeUnconnectedNodesFromGraph();


            //System.out.println( printXml());
            convertIntoTree(root);
            //root.calculateDepths();


        } catch (NoSuchRelationLinkException e) {
            throw e;
        } catch (NoSuchPropertyException e2) {
            throw e2;
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

//        Iterator it = edgesList.iterator();
//        while (it.hasNext()) {
//            EdgeImpl curEdge = (EdgeImpl) it.next();
//            //registerEdge(curEdge);
//            checkForCycle(curEdge);
//        }
    }


    private void checkForCycle (Edge oneWayEdge) {
        Node fromNode = oneWayEdge.getFromNode();
        Node toNode = oneWayEdge.getToNode();
        RelationLinkDetails relLinkType = oneWayEdge.getEdgeType();

        Edge reversedEdge = getEdge(toNode, fromNode, relLinkType);
        if ((oneWayEdge != null) || (reversedEdge != null)) {
            String message = "Relation links: ";
            message = message + "edge: " + fromNode + " -> " + toNode + " , edgeType = " + relLinkType;
            message = message + " and ";
            message = message + "edge: " + toNode + " -> " + fromNode + " , edgeType = " + relLinkType;
            message = message + " are reversable. This is not going to work in the current graph model, ";
            message = message + " we won't display this relation link here.";
            message = message + " Please consider moving this relation link into concept properties in the config file.";
            //System.err.println("\n\n\none of these _graphEdges already exists \n\n\n");
            if (oneWayEdge != null) {
                removeEdge(oneWayEdge);
            }
            if (reversedEdge != null) {
                removeEdge(reversedEdge);
            }
            OntoRamaApp.showErrorDialog(message);
        }
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
//        Iterator allNodes = processedNodes.values().iterator();
        Iterator allNodes = _graphNodes.iterator();
        while (allNodes.hasNext()) {
            Node curNode = (Node) allNodes.next();

            if (curNode.equals(root)) {
                continue;
            }

            // get inbound nodes (parents) and check how many there is.
            // If there is no parents - this node is not attached
            // to anything, hence - 'hanging node'
            Iterator inboundNodes = getInboundEdges(curNode);

            if (!inboundNodes.hasNext()) {
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

        Iterator curOutEdges = getOutboundEdges(node);
        while (curOutEdges.hasNext()) {
            Edge curEdge = (Edge) curOutEdges.next();
//            System.out.println("\toutbound edge: " + curEdge);
            Node toNode = curEdge.getToNode();
            if (toNode == root) {
                // don't remove parents of root node
                continue;
            }
            _edgesToRemove.add(curEdge);
//            System.out.println("\t\ttoNode number of inbound _graphEdges: " + EdgeImpl.getInboundEdgeNodesList(toNode).size());
//            System.out.println("\t\ttoNode inbound _graphEdges: " + EdgeImpl.getInboundEdgeNodesList(toNode));
            if (getInboundEdgeNodesList(toNode).size() > 1 ) {
//                Iterator inIt = EdgeImpl.getInboundEdges(toNode);
//                while (inIt.hasNext()) {
//                    EdgeImpl edge = (EdgeImpl) inIt.next();
//                    System.out.println("\t\t\t" + edge);
//                }
                if ( ! nodeIsInGivenBranch(root, toNode)) {
//                    System.out.println ("this node is not in current branch");
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

            Iterator it = getOutboundEdgeNodes(curNode);
            while (it.hasNext()) {
                Node nextNode = (Node) it.next();
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
            Iterator allOutboundNodes =
                    getOutboundEdgeNodes(nextQueueNode);

            while (allOutboundNodes.hasNext()) {
                Node curNode = (Node) allOutboundNodes.next();
                queue.add(curNode);

                Iterator inboundEdges = getInboundEdges(curNode);
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
     * @throws  NoSuchPropertyException
     */
    private void convertIntoTree(Node root)
            throws NoSuchRelationLinkException, NoSuchPropertyException {
        LinkedList queue = new LinkedList();

        queue.add(root);

        while (!queue.isEmpty()) {
            Node nextQueueNode = (Node) queue.remove(0);

            debug.message(
                    "Graph",
                    "convertIntoTree",
                    "--- processing node " + nextQueueNode.getName() + " -----");

            Iterator allOutboundNodes =
                    getOutboundEdgeNodes(nextQueueNode);

            while (allOutboundNodes.hasNext()) {
                Node curNode = (Node) allOutboundNodes.next();
                queue.add(curNode);

                Iterator inboundEdges = getInboundEdges(curNode);
                int count = 0;
                while (inboundEdges.hasNext()) {
                    count++;
                    inboundEdges.next();
                }
                while (count > 1) {
                    // indicate that we processed one edge
                    count--;

                    debug.message(
                            "Graph",
                            "convertIntoTree",
                            " node "
                            + curNode.getName()
                            + " has multiple inbound _graphEdges");

                    // clone the node
                    if (_topLevelUnconnectedNodes.contains(curNode)) {
                        continue;
                    }

                    Node cloneNode = curNode.makeClone();
                    _graphNodes.add(cloneNode);

                    // add edge from cloneNode to a NodeParent with this rel edgeType and
                    // remove edge from curNode to a NodeParent with this rel edgeType
                    Iterator it = getInboundEdges(curNode);
                    if (it.hasNext()) {
                        Edge firstEdge = (Edge) it.next();
                        Edge newEdge = new EdgeImpl(
                                firstEdge.getFromNode(),
                                cloneNode,
                                firstEdge.getEdgeType());
                        registerEdge(newEdge);
                        removeEdge(firstEdge);
                    }

                    // copy/clone all structure below
                    deepCopy(curNode, cloneNode);
                }

            }
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
     * @throws  NoSuchPropertyException
     */
    private void deepCopy(Node node, Node cloneNode)
            throws NoSuchRelationLinkException, NoSuchPropertyException {

        Iterator outboundEdgesIterator = getOutboundEdges(node);

        while (outboundEdgesIterator.hasNext()) {
            Edge curEdge = (Edge) outboundEdgesIterator.next();
            Node toNode = curEdge.getToNode();
            Node cloneToNode = toNode.makeClone();
            Edge newEdge = new EdgeImpl(cloneNode, cloneToNode, curEdge.getEdgeType());
            registerEdge(newEdge);
            deepCopy(toNode, cloneToNode);
        }
    }

    /**
     * Print Graph into XML tree
     */
    public String printXml() throws NoSuchPropertyException {
        String resultStr = "<ontology top='" + this.root.getName() + "'>";
        resultStr = resultStr + "\n";
        resultStr = resultStr + printXmlConceptTypesEl();
        resultStr = resultStr + printXmlRelationLinksEl();
        resultStr = resultStr + "\n";
        resultStr = resultStr + "</ontology>";
        resultStr = resultStr + "\n";

        return resultStr;
    }

    /**
     * Print all nodes into xml sniplet
     */
    private String printXmlConceptTypesEl() throws NoSuchPropertyException {
        String tab = "\t";
        String resultStr = tab + "<conceptTypes>";
        resultStr = resultStr + "\n";
        LinkedList queue = new LinkedList();
        queue.add(root);

        while (!queue.isEmpty()) {
            Node nextQueueNode = (Node) queue.remove(0);

            resultStr =
                    resultStr
                    + tab
                    + tab
                    + "<conceptType name='"
                    + nextQueueNode.getName()
                    + "'>";
            resultStr = resultStr + "\n";

            Hashtable conceptPropertiesConfig =
                    OntoramaConfig.getConceptPropertiesTable();
            Enumeration e = conceptPropertiesConfig.keys();
            while (e.hasMoreElements()) {
                String propName = (String) e.nextElement();
                System.out.println(
                        "nextQueueNode = "
                        + nextQueueNode.getName()
                        + ", propName = "
                        + propName);
                System.out.println(
                        "\tpropValue = " + nextQueueNode.getProperty(propName));
                Iterator propValueIterator =
                        nextQueueNode.getProperty(propName).iterator();
                while (propValueIterator.hasNext()) {
                    String curPropValue = (String) propValueIterator.next();
                    resultStr =
                            resultStr + tab + tab + tab + "<" + propName + ">";
                    resultStr = resultStr + curPropValue;
                    resultStr = resultStr + "</" + propName + ">";
                    resultStr = resultStr + "\n";
                }
            }
            resultStr = resultStr + tab + tab + "</conceptType>";
            resultStr = resultStr + "\n";

            Iterator allOutboundNodes =
                    getOutboundEdgeNodes(nextQueueNode);

            while (allOutboundNodes.hasNext()) {
                Node curNode = (Node) allOutboundNodes.next();
                queue.add(curNode);

            }
        }
        resultStr = resultStr + tab + "</conceptTypes>";
        resultStr = resultStr + "\n";
        return resultStr;
    }

    /**
     * Print all _graphEdges into XML sniplet
     */
    private String printXmlRelationLinksEl() {
        String tab = "\t";
        String resultStr = tab + "<relationLinks>";
        resultStr = resultStr + tab + "\n";

        Iterator edgesIterator = _graphEdges.iterator();
        while (edgesIterator.hasNext()) {
            Edge curEdge = (Edge) edgesIterator.next();
            Node fromNode = curEdge.getFromNode();
            Node toNode = curEdge.getToNode();
            RelationLinkDetails relLinkDetails = curEdge.getEdgeType();
            resultStr = resultStr + tab + tab + "<relationLink";
            resultStr =
                    resultStr + " name='" + relLinkDetails.getLinkName() + "'";
            resultStr = resultStr + " from='" + fromNode.getName() + "'";
            resultStr = resultStr + " to='" + toNode.getName() + "'";
            resultStr = resultStr + "/>";
            resultStr = resultStr + "\n";
        }
        resultStr = resultStr + tab + "</relationLinks>";
        resultStr = resultStr + "\n";
        return resultStr;
    }

    /**
     * @todo    think where this method should leave....
     */
    public void registerEdge(Edge edge) {
        boolean isInList = false;
        Iterator it = _graphEdges.iterator();
        while (it.hasNext()) {
            Edge cur = (Edge) it.next();
            if ((edge.getFromNode().equals(cur.getFromNode())) &&
                    (edge.getToNode().equals(cur.getToNode())) &&
                    (edge.getEdgeType() == cur.getEdgeType())) {
                // this edge is already registered
                //System.out.println("edge is already registered: " + edge);
                isInList = true;
            }
        }
        if (!isInList) {
            _graphEdges.add(edge);
        }
    }

    /**
     *
     */
    public void removeEdge(Edge remEdge) {
        _graphEdges.remove(remEdge);
    }

    /**
     *
     */
    public void removeAllEdges() {
        _graphEdges.clear();
    }

    public List getEdgesList() {
        return _graphEdges;
    }

    /**
     *
     */
    public Edge getEdge(Node fromNode, Node toNode, RelationLinkDetails edgeType) {
        Iterator it = getOutboundEdges(fromNode, edgeType);
        //Iterator it = EdgeImpl.getOutboundEdgeNodes(fromNode, relLink);
        while (it.hasNext()) {
            Edge curEdge = (Edge) it.next();
            //Node curNode = (Node) it.next();
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
    public Iterator getOutboundEdges(Node node) {
        return getEdges(node, true);
    }

    /**
     *
     */
    public Iterator getInboundEdges(Node node) {
        return getEdges(node, false);
    }

    /**
     *
     * @param  node Node
     *         flag - true if we want to get list of outbound _graphEdges,
     *         false of we want to get a list of inbound _graphEdges.
     * @return iterator of Edges
     */
    private Iterator getEdges(Node node, boolean flag) {
        List result = new LinkedList();
        Iterator it = _graphEdges.iterator();
        while (it.hasNext()) {
            EdgeImpl cur = (EdgeImpl) it.next();
            Node graphNode = cur.getEdgeNode(flag);
            if (graphNode.equals(node)) {
                result.add(cur);
            }
        }
        return result.iterator();
    }

    /**
     *
     * @param  node Node
     *         int relationType
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.      *
     * @return iterator of Edges
     */
    private Iterator getEdges(Node node, RelationLinkDetails relationType, boolean flag) {
        List result = new LinkedList();
        Iterator nodeEdgesIt = getEdges(node, flag);
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
    public Iterator getOutboundEdges(Node node, RelationLinkDetails edgeType) {
        return getEdges(node, edgeType, true);
    }

    /**
     *
     */
    public Iterator getInboundEdges(Node node, RelationLinkDetails edgeType) {
        return getEdges(node, edgeType, false);
    }

    /**
     *
     * @return iterator of Nodes
     */
    public Iterator getOutboundEdgeNodes(Node node, Set relationLinks) {
        return getEdgeNodes(node, relationLinks, true);
    }

    /**
     *
     * @return iterator of Nodes
     */
    public Iterator getInboundEdgeNodes(Node node, Set relationLinks) {
        return getEdgeNodes(node, relationLinks, false);
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
        Iterator nodeEdgesIt = getEdges(node, flag);
        while (nodeEdgesIt.hasNext()) {
            EdgeImpl cur = (EdgeImpl) nodeEdgesIt.next();
            Iterator it = relationLinks.iterator();
            while (it.hasNext()) {
                RelationLinkDetails curRel = (RelationLinkDetails) it.next();
                if (cur.getEdgeType().equals(curRel)) {
                    result.add(cur.getEdgeNode(!flag));
                }
            }
//            while (it.hasNext()) {
//                Integer curRel = (Integer) it.next();
//                if (cur.getEdgeType() == curRel.intValue()) {
//                    result.add(cur.getEdgeNode(!flag));
//                }
//            }
        }
        return result.iterator();
    }

    /**
     *
     */
    public Iterator getOutboundEdgeNodes(Node node, RelationLinkDetails edgeType) {
        return getEdgeNodes(node, edgeType, true);
    }

    /**
     *
     */
    public Iterator getInboundEdgeNodes(Node node, RelationLinkDetails edgeType) {
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
    private Iterator getEdgeNodes(Node node, RelationLinkDetails edgeType, boolean flag) {
        List result = new LinkedList();
        Iterator nodeEdgesIt = getEdges(node, flag);
        while (nodeEdgesIt.hasNext()) {
            EdgeImpl cur = (EdgeImpl) nodeEdgesIt.next();
            if (cur.getEdgeType().equals(edgeType)) {
                result.add(cur.getEdgeNode(!flag));
            }
        }
        return result.iterator();
    }

    /**
     *
     */
    public Iterator getOutboundEdgeNodes(Node node) {
        return getEdgeNodes(node, true);
    }

    /**
     *
     */
    public Iterator getInboundEdgeNodes(Node node) {
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
    private Iterator getEdgeNodes(Node node, boolean flag) {
        return getEdgeNodesList(node, flag).iterator();
    }

    /**
     *
     */
    public List getOutboundEdgeNodesList(Node node) {
        return getEdgeNodesList(node, true);
    }

    /**
     *
     */
    public List getInboundEdgeNodesList(Node node) {
        return getEdgeNodesList(node, false);
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
        Iterator nodeEdgesIt = getEdges(node, flag);
        while (nodeEdgesIt.hasNext()) {
            EdgeImpl cur = (EdgeImpl) nodeEdgesIt.next();
            result.add(cur.getEdgeNode(!flag));
        }
        return result;
    }

    /**
     * Convenience method that returns iterator size
     * @param   it iterator
     * @return  int size
     * @todo  perhaps this method should 'live' in util package
     */
    public static int getIteratorSize(Iterator it) {
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count = count + 1;
        }
        return count;
    }

    /**
     *
     */
    public void printAllEdges() {
        Iterator it = _graphEdges.iterator();
        while (it.hasNext()) {
            Edge edge = (Edge) it.next();
            System.out.println(edge);
        }
    }

}
