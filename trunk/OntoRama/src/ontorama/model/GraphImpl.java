package ontorama.model;

import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.RelationLinkDetails;
import ontorama.util.Debug;
import ontorama.view.OntoRamaApp;
import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;
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
 * duplicate inbound edges by cloning outbound edges)
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
     * Hold a list of processed ontology types (ontology types that have
     * been converted into nodes already)
     */
    private List processedTypes = new LinkedList();

    /**
     * Hold processed nodes (nodes created for Graph)
     * Keys - node names, values - node objects
     */
    private Hashtable processedNodes = new Hashtable();

    /**
     * root node
     */
    private GraphNode root = null;

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
     * list holding all edges
     */
    public static List edges = new LinkedList();

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
     *  One way to get around it is: to remove edges that don't have incoming edges (parents).
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
     * @todo  maybe we shouldn't remove non-connected edges, but attach them to artafficial
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

        String termName = queryResult.getQuery().getQueryTypeName();
        Iterator ontIterator = queryResult.getOntologyTypesIterator();

        processedTypes = new LinkedList();
        _topLevelUnconnectedNodes = new LinkedList();
        _graphNodes = new LinkedList();

        // remove all edges before building new set of edges
        removeAllEdges();

        try {
            buildGraph(termName, ontIterator);

            _topLevelUnconnectedNodes = listTopLevelUnconnectedNodes();
            listItemsToRemove(_topLevelUnconnectedNodes);

            // clean up
            removeUnconnectedEdges();
            removeUnconnectedNodesFromGraph();

            if (!processedNodes.containsKey(termName)) {
                throw new NoTypeFoundInResultSetException(termName);
            }
            //System.out.println( printXml());
            convertIntoTree(root);
            root.calculateDepths();
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
    private void buildGraph(String termName, Iterator ontIterator)
            throws NoSuchRelationLinkException, NoSuchPropertyException {

        int count = 0;

        while (ontIterator.hasNext()) {
            OntologyType ot = (OntologyTypeImplementation) ontIterator.next();
            //System.out.println("ot = " + ot.toString());
            processOntologyType(ot, termName);
            count++;
        }
        System.out.println("\n\n Graph: processed " + count + " types");
    }

    /**
     * Process given OntologyType, create Node for it and all corresponding Edges.
     *
     * @param   ot  - ontoogy edgeType to process
     * @param   rootName - name of the root node
     */
    private void processOntologyType(OntologyType ot, String rootName)
            throws NoSuchRelationLinkException, NoSuchPropertyException {
        if (processedTypes.contains(ot)) {
            return;
        }
        processedTypes.add(ot);

        // make GraphNode and set available properties for it.
        debug.message("----processing ot = " + ot);
        GraphNode node = getNodeFromNodesList(ot.getName(), ot.getFullName());
        debug.message("\t corresponding node = " + node);
        Hashtable conceptPropertiesConfig =
                OntoramaConfig.getConceptPropertiesTable();
        Enumeration e = conceptPropertiesConfig.keys();
        while (e.hasMoreElements()) {
            String propertyName = (String) e.nextElement();
            node.setProperty(propertyName, ot.getTypeProperty(propertyName));
        }

        // check if this is root
        if (rootName.equals(node.getName())) {
            root = node;
            debug.message("GraphBuilder root = " + root.getName());
        }

        // Go through relation links and make Edges
        RelationLinkDetails[] relationLinksDetails = OntoramaConfig.getRelationLinkDetails();

        for (int i = 0; i < relationLinksDetails.length; i++) {
            RelationLinkDetails relLinkType = relationLinksDetails[i];

//        Iterator relLinks = OntoramaConfig.getRelationLinksSet().iterator();
//        while (relLinks.hasNext()) {
//            Integer relLink = (Integer) relLinks.next();
            Iterator relatedTypes = ot.getIterator(i);
            while (relatedTypes.hasNext()) {
                OntologyType relatedType = (OntologyType) relatedTypes.next();

                GraphNode relNode = getNodeFromNodesList(relatedType.getName(), relatedType.getFullName());
                Edge oneWayEdge = getEdge(node, relNode, relLinkType);
                Edge reversedEdge = getEdge(relNode, node, relLinkType);
                if ((oneWayEdge != null) || (reversedEdge != null)) {
                    String message = "Relation links: ";
                    message = message + "edge: " + node + " -> " + relNode + " , edgeType = " + relLinkType;
                    message = message + " and ";
                    message = message + "edge: " + relNode + " -> " + node + " , edgeType = " + relLinkType;
                    message = message + " are reversable. This is not going to work in the current graph model, ";
                    message = message + " we won't display this relation link here.";
                    message = message + " Please consider moving this relation link into concept properties in the config file.";
                    //System.err.println("\n\n\none of these edges already exists \n\n\n");
                    if (oneWayEdge != null) {
                        removeEdge(oneWayEdge);
                    }
                    if (reversedEdge != null) {
                        removeEdge(reversedEdge);
                    }
                    OntoRamaApp.showErrorDialog(message);
                } else {
                    Edge edge = new Edge(node, relNode, relLinkType);
                    this.registerEdge(edge);
                    debug.message(
                            "\t edge: "
                            + node
                            + " -> "
                            + relNode
                            + " , edgeType = "
                            + relLinkType);
                    //processOntologyType(relatedType, rootName);
                }
            }
        }
    }

    /**
     *
     */
    private GraphNode getNodeFromNodesList(String nodeName, String fullName) {
        GraphNode node = (GraphNode) processedNodes.get(nodeName);
        if (node == null) {
            node = new GraphNode(nodeName);
            node.setFullName(fullName);
            processedNodes.put(nodeName, node);
            _graphNodes.add(node);
        }
        return node;
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
            GraphNode node = (GraphNode) it.next();
            _graphNodes.remove(node);
        }
    }


    private List listTopLevelUnconnectedNodes () {
        LinkedList result = new LinkedList();
        Iterator allNodes = processedNodes.values().iterator();
        while (allNodes.hasNext()) {
            GraphNode curNode = (GraphNode) allNodes.next();

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
            GraphNode node = (GraphNode) it.next();
            listItemsToRemove(node);
        }

    }

    private void listItemsToRemove (GraphNode node) {
//        System.out.println("processing node " + node);

        if (!_nodesToRemove.contains(node)) {
            _nodesToRemove.add(node);
        }

        Iterator curOutEdges = getOutboundEdges(node);
        while (curOutEdges.hasNext()) {
            Edge curEdge = (Edge) curOutEdges.next();
//            System.out.println("\toutbound edge: " + curEdge);
            GraphNode toNode = curEdge.getToNode();
            if (toNode == root) {
                // don't remove parents of root node
                continue;
            }
            _edgesToRemove.add(curEdge);
//            System.out.println("\t\ttoNode number of inbound edges: " + Edge.getInboundEdgeNodesList(toNode).size());
//            System.out.println("\t\ttoNode inbound edges: " + Edge.getInboundEdgeNodesList(toNode));
            if (getInboundEdgeNodesList(toNode).size() > 1 ) {
//                Iterator inIt = Edge.getInboundEdges(toNode);
//                while (inIt.hasNext()) {
//                    Edge edge = (Edge) inIt.next();
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
     * @return GraphNode root
     */
    public GraphNode getRootNode() {
        return root;
    }

    /**
     *
     */
    public boolean nodeIsInGivenBranch (GraphNode branchRoot, GraphNode node) {
        List queue = new LinkedList();
        queue.add(branchRoot);

        while (!queue.isEmpty()) {
            GraphNode curNode = (GraphNode) queue.remove(0);

            if (curNode.equals(node)) {
                return true;
            }

            Iterator it = getOutboundEdgeNodes(curNode);
            while (it.hasNext()) {
                GraphNode nextNode = (GraphNode) it.next();
                queue.add(nextNode);
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
     * @param root  - root GraphNode
     */
    private boolean testIfTree(GraphNode root) {
        LinkedList queue = new LinkedList();
        boolean isTree = true;

        queue.add(root);

        while (!queue.isEmpty()) {
            GraphNode nextQueueNode = (GraphNode) queue.remove(0);
            Iterator allOutboundNodes =
                    getOutboundEdgeNodes(nextQueueNode);

            while (allOutboundNodes.hasNext()) {
                GraphNode curNode = (GraphNode) allOutboundNodes.next();
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
                            + " has multiple inbound edges");
                    isTree = false;
                }
            }
        }
        return isTree;
    }

    /**
     * Convert Graph into Tree by cloning nodes with duplicate parents (inbound edges)
     *
     * @param   root - root node for the graph
     * @throws  NoSuchRelationLinkException
     * @throws  NoSuchPropertyException
     */
    private void convertIntoTree(GraphNode root)
            throws NoSuchRelationLinkException, NoSuchPropertyException {
        LinkedList queue = new LinkedList();

        queue.add(root);

        while (!queue.isEmpty()) {
            GraphNode nextQueueNode = (GraphNode) queue.remove(0);

            debug.message(
                    "Graph",
                    "convertIntoTree",
                    "--- processing node " + nextQueueNode.getName() + " -----");

            Iterator allOutboundNodes =
                    getOutboundEdgeNodes(nextQueueNode);

            while (allOutboundNodes.hasNext()) {
                GraphNode curNode = (GraphNode) allOutboundNodes.next();
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
                            + " has multiple inbound edges");

                    // clone the node
                    if (_topLevelUnconnectedNodes.contains(curNode)) {
                        continue;
                    }

                    GraphNode cloneNode = curNode.makeClone();
                    _graphNodes.add(cloneNode);

                    // add edge from cloneNode to a NodeParent with this rel edgeType and
                    // remove edge from curNode to a NodeParent with this rel edgeType
                    Iterator it = getInboundEdges(curNode);
                    if (it.hasNext()) {
                        Edge firstEdge = (Edge) it.next();
                        Edge newEdge = new Edge(
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
     * Recursively copy all node's outbound edges into cloneNode, so we
     * and up with cloneNode that has exactly the same children as given node.
     * These children are not first node's descendants themselfs - but their clones.
     *
     * @param   node    original node
     * @param   cloneNode   copy node that needs all outbound edges filled in
     * @throws  NoSuchRelationLinkException
     * @throws  NoSuchPropertyException
     */
    private void deepCopy(GraphNode node, GraphNode cloneNode)
            throws NoSuchRelationLinkException, NoSuchPropertyException {

        Iterator outboundEdgesIterator = getOutboundEdges(node);

        while (outboundEdgesIterator.hasNext()) {
            Edge curEdge = (Edge) outboundEdgesIterator.next();
            GraphNode toNode = curEdge.getToNode();
            GraphNode cloneToNode = toNode.makeClone();
            Edge newEdge = new Edge(cloneNode, cloneToNode, curEdge.getEdgeType());
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
            GraphNode nextQueueNode = (GraphNode) queue.remove(0);

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
                GraphNode curNode = (GraphNode) allOutboundNodes.next();
                queue.add(curNode);

            }
        }
        resultStr = resultStr + tab + "</conceptTypes>";
        resultStr = resultStr + "\n";
        return resultStr;
    }

    /**
     * Print all edges into XML sniplet
     */
    private String printXmlRelationLinksEl() {
        String tab = "\t";
        String resultStr = tab + "<relationLinks>";
        resultStr = resultStr + tab + "\n";

        Iterator edgesIterator = edges.iterator();
        while (edgesIterator.hasNext()) {
            Edge curEdge = (Edge) edgesIterator.next();
            GraphNode fromNode = curEdge.getFromNode();
            GraphNode toNode = curEdge.getToNode();
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
        Iterator it = edges.iterator();
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
            edges.add(edge);
        }
    }

    /**
     *
     */
    public static void removeEdge(Edge remEdge) {
        edges.remove(remEdge);
    }

    /**
     *
     */
    public static void removeAllEdges() {
        edges.clear();
    }

    /**
     *
     */
    public static Edge getEdge(GraphNode fromNode, GraphNode toNode, RelationLinkDetails edgeType) {
        Iterator it = getOutboundEdges(fromNode, edgeType);
        //Iterator it = Edge.getOutboundEdgeNodes(fromNode, relLink);
        while (it.hasNext()) {
            Edge curEdge = (Edge) it.next();
            //GraphNode curNode = (GraphNode) it.next();
            GraphNode curNode = curEdge.getToNode();
            if (curNode.equals(toNode)) {
                return curEdge;
            }
        }
        return null;
    }

    /**
     *
     */
    public static Iterator getOutboundEdges(GraphNode node) {
        return getEdges(node, true);
    }

    /**
     *
     */
    public static Iterator getInboundEdges(GraphNode node) {
        return getEdges(node, false);
    }

    /**
     *
     * @param  node GraphNode
     *         flag - true if we want to get list of outbound edges,
     *         false of we want to get a list of inbound edges.
     * @return iterator of Edges
     */
    private static Iterator getEdges(GraphNode node, boolean flag) {
        List result = new LinkedList();
        Iterator it = edges.iterator();
        while (it.hasNext()) {
            Edge cur = (Edge) it.next();
            GraphNode graphNode = cur.getEdgeNode(flag);
            if (graphNode.equals(node)) {
                result.add(cur);
            }
        }
        return result.iterator();
    }

    /**
     *
     * @param  node GraphNode
     *         int relationType
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.      *
     * @return iterator of Edges
     */
    private static Iterator getEdges(GraphNode node, RelationLinkDetails relationType, boolean flag) {
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
    public static Iterator getOutboundEdges(GraphNode node, RelationLinkDetails edgeType) {
        return getEdges(node, edgeType, true);
    }

    /**
     *
     */
    public static Iterator getInboundEdges(GraphNode node, RelationLinkDetails edgeType) {
        return getEdges(node, edgeType, false);
    }

    /**
     *
     * @return iterator of Nodes
     */
    public static Iterator getOutboundEdgeNodes(GraphNode node, Set relationLinks) {
        return getEdgeNodes(node, relationLinks, true);
    }

    /**
     *
     * @return iterator of Nodes
     */
    public static Iterator getInboundEdgeNodes(GraphNode node, Set relationLinks) {
        return getEdgeNodes(node, relationLinks, false);
    }

    /**
     *
     * @param  node GraphNode
     *         Set relationLinks
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.
     * @return iterator of Nodes
     */
    private static Iterator getEdgeNodes(GraphNode node, Set relationLinks, boolean flag) {
        List result = new LinkedList();
        Iterator nodeEdgesIt = getEdges(node, flag);
        while (nodeEdgesIt.hasNext()) {
            Edge cur = (Edge) nodeEdgesIt.next();
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
    public static Iterator getOutboundEdgeNodes(GraphNode node, RelationLinkDetails edgeType) {
        return getEdgeNodes(node, edgeType, true);
    }

    /**
     *
     */
    public static Iterator getInboundEdgeNodes(GraphNode node, RelationLinkDetails edgeType) {
        return getEdgeNodes(node, edgeType, false);
    }

    /**
     *
     * @param  node GraphNode
     *         int relationType
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.      *
     * @return iterator of Nodes
     */
    private static Iterator getEdgeNodes(GraphNode node, RelationLinkDetails edgeType, boolean flag) {
        List result = new LinkedList();
        Iterator nodeEdgesIt = getEdges(node, flag);
        while (nodeEdgesIt.hasNext()) {
            Edge cur = (Edge) nodeEdgesIt.next();
            if (cur.getEdgeType().equals(edgeType)) {
                result.add(cur.getEdgeNode(!flag));
            }
        }
        return result.iterator();
    }

    /**
     *
     */
    public static Iterator getOutboundEdgeNodes(GraphNode node) {
        return getEdgeNodes(node, true);
    }

    /**
     *
     */
    public static Iterator getInboundEdgeNodes(GraphNode node) {
        return getEdgeNodes(node, false);
    }

    /**
     *
     * @param  node GraphNode
     *         int relationType
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.      *
     * @return iterator of Nodes
     */
    private static Iterator getEdgeNodes(GraphNode node, boolean flag) {
        return getEdgeNodesList(node, flag).iterator();
    }

    /**
     *
     */
    public static List getOutboundEdgeNodesList(GraphNode node) {
        return getEdgeNodesList(node, true);
    }

    /**
     *
     */
    public static List getInboundEdgeNodesList(GraphNode node) {
        return getEdgeNodesList(node, false);
    }

    /**
     *
     * @param  node GraphNode
     *         int relationType
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.      *
     * @return list of Nodes
     */
    private static List getEdgeNodesList(GraphNode node, boolean flag) {
        List result = new LinkedList();
        Iterator nodeEdgesIt = getEdges(node, flag);
        while (nodeEdgesIt.hasNext()) {
            Edge cur = (Edge) nodeEdgesIt.next();
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
    public static void printAllEdges() {
        Iterator it = edges.iterator();
        while (it.hasNext()) {
            Edge edge = (Edge) it.next();
            System.out.println(edge);
        }
    }

}
