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
public class Graph implements GraphInterface {

    /**
     * Graph
     */
    private Graph graph = null;

    /**
     * Hold a set of available relation links
     */
    private Set relationLinksSet = OntoramaConfig.getRelationLinksSet();

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
    private List _unconnectedNodes;

    /**
     * list of all graph nodes
     */
    private List _allNodes;


    /**
     *
     */
    Debug debug = new Debug(false);

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
    public Graph(QueryResult queryResult)
            throws
            NoSuchRelationLinkException,
            NoTypeFoundInResultSetException,
            NoSuchPropertyException {
        debug.message(
                "******************* GraphBuilder constructor start *******************");

        String termName = queryResult.getQuery().getQueryTypeName();
        Iterator ontIterator = queryResult.getOntologyTypesIterator();

        processedTypes = new LinkedList();
        _unconnectedNodes = new LinkedList();
        _allNodes = new LinkedList();

        // remove all edges before building new set of edges
        Edge.removeAllEdges();

        try {
            buildGraph(termName, ontIterator);


            List unconnectedEdges = listUnconnectedEdgesAndNodes();
            System.out.println("\n\nunconnectedNodes = " + _unconnectedNodes);

//			//System.out.println("\n\nunconnected edges = " + unconnectedEdges);
//			if (unconnectedEdges.size() != 0) {
//				GraphNode node = new GraphNode("tempNode");
//				Iterator it = unconnectedEdges.iterator();
//				while (it.hasNext()) {
//					Edge curEdge = (Edge) it.next();
//					System.out.println("unconnectedEdge = " + curEdge.fromNode + " -> " + curEdge.toNode);
////					GraphNode edgeFromNode = curEdge.getFromNode();
////					if ( (edgeFromNode.getName().endsWith("Class")) ||
////						( edgeFromNode.getName().endsWith("Property")) ){
////						Edge.removeEdge(curEdge);
////						continue;
////					}
////					Edge newEdge = new Edge(node, edgeFromNode, 1);
//				}
////				Edge edgeConnectToRoot = new Edge(node, root,1);
////				root = node;
//			}

            // clean up
            //removeUnconnectedEdges();

            debug.message("graph , root = " + root);

            if (!processedNodes.containsKey(termName)) {
                throw new NoTypeFoundInResultSetException(termName);
            }
            debug.message("graph , num of nodes = " + processedNodes.size());

            debug.message(
                    "Graph",
                    "constructor",
                    "before convertIntoTree testIfTree(): " + testIfTree(root));
            debug.message(
                    "Graph",
                    "constructor",
                    "before convertIntoTree number of nodes: "
                    + getNodesList().size());
            debug.message(
                    "Graph",
                    "constructor",
                    "before convertIntoTree number of edges: "
                    + Edge.getIteratorSize(Edge.edges.iterator()));
            //System.out.println( printXml());
            convertIntoTree(root);
            root.calculateDepths();
            debug.message(
                    "Graph",
                    "constructor",
                    "after convertIntoTree testIfTree(): " + testIfTree(root));
            debug.message(
                    "Graph",
                    "constructor",
                    "after convertIntoTree number of nodes: "
                    + getNodesList().size());
            debug.message(
                    "Graph",
                    "constructor",
                    "after convertIntoTree number of edges: "
                    + Edge.getIteratorSize(Edge.edges.iterator()));
            debug.message(
                    "Graph , constructor, after convertIntoTree number of nodes: "
                    + getNodesList().size());

            //graph = new Graph( root );
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
     * @param   ontologyType, rootName
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
//        GraphNode node = (GraphNode) processedNodes.get(ot.getName());
//        //System.out.println("----processing ot = " + ot.getName());
//        if (node == null) {
//            node = new GraphNode(ot.getName());
//            node.setFullName(ot.getFullName());
//            processedNodes.put(ot.getName(), node);
//        }
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
        Iterator relLinks = OntoramaConfig.getRelationLinksSet().iterator();
        while (relLinks.hasNext()) {
            Integer relLink = (Integer) relLinks.next();
            Iterator relatedTypes = ot.getIterator(relLink.intValue());
            while (relatedTypes.hasNext()) {
                OntologyType relatedType = (OntologyType) relatedTypes.next();

                GraphNode relNode = getNodeFromNodesList(relatedType.getName(), relatedType.getFullName());
//                        (GraphNode) processedNodes.get(relatedType.getName());
//                if (relNode == null) {
//                    relNode = new GraphNode(relatedType.getName());
//                    relNode.setFullName(relatedType.getFullName());
//                    processedNodes.put(relatedType.getName(), relNode);
//                }
                Edge oneWayEdge = Edge.getEdge(node, relNode, relLink.intValue());
                Edge reversedEdge = Edge.getEdge(relNode, node, relLink.intValue());
                if ((oneWayEdge != null) || (reversedEdge != null)) {
                    String message = "Relation links: ";
                    message = message + "edge: " + node + " -> " + relNode + " , type = " + relLink.intValue();
                    message = message + " and ";
                    message = message + "edge: " + relNode + " -> " + node + " , type = " + relLink.intValue();
                    message = message + " are reversable. This is not going to work in the current graph model, ";
                    message = message + " we won't display this relation link here.";
                    message = message + " Please consider moving this relation link into concept properties in the config file.";
                    //System.err.println("\n\n\none of these edges already exists \n\n\n");
                    if (oneWayEdge != null) {
                        Edge.removeEdge(oneWayEdge);
                    }
                    if (reversedEdge != null) {
                        Edge.removeEdge(reversedEdge);
                    }
                    OntoRamaApp.showErrorDialog(message);
                } else {
                    new Edge(node, relNode, relLink.intValue());
                    debug.message(
                            "\t edge: "
                            + node
                            + " -> "
                            + relNode
                            + " , type = "
                            + relLink.intValue());
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
            _allNodes.add(node);
        }
        return node;
    }

    /**
     *
     */
    private void removeUnconnectedEdges() {
        int lastNumOfEdges = -1;
        debug.message("number of Edges = " + Edge.edges.size());
        while (Edge.edges.size() != lastNumOfEdges) {
            debug.message(
                    "number of Edges = "
                    + Edge.edges.size()
                    + ", lastNumOfEdges = "
                    + lastNumOfEdges);
            lastNumOfEdges = Edge.edges.size();
            cleanUpEdges();
        }
        debug.message("number of Edges = " + Edge.edges.size());
        debug.message("root = " + root);
    }

    /**
     * Remove all edges that are 'hanging in space' (edges that don't
     * have any parents).
     * @todo  does it need to be recusive?
     * @todo  if not making this recursive - is there a better way to do this?
     *        Recursive may not work because we are trying to get rid of
     *        'hanging' nodes, which by definition don't have parents.
     *        this means that we can't get to them via recursion anyway.
     */
    private void cleanUpEdges() {
        Iterator unconnectedEdges = listUnconnectedEdgesAndNodes().iterator();
        while (unconnectedEdges.hasNext()) {
            Edge cur = (Edge) unconnectedEdges.next();
            Edge.removeEdge(cur);
        }

//		Iterator allNodes = processedNodes.values().iterator();
//		while (allNodes.hasNext()) {
//			GraphNode curNode = (GraphNode) allNodes.next();
//
//			if (curNode == root) {
//				continue;
//			}
//
//			// get inbound nodes (parents) and check how many there is.
//			// If there is no parents - this node is not attached
//			// to anything, hence - 'hanging node'
//			Iterator inboundNodes = Edge.getInboundEdges(curNode);
//			if (Edge.getIteratorSize(inboundNodes) == 0) {
//				// get outbound edges for this node and remove them
//				Iterator curOutEdges = Edge.getOutboundEdges(curNode);
//				while (curOutEdges.hasNext()) {
//					Edge curEdge = (Edge) curOutEdges.next();
//					if (curEdge.getToNode() == root) {
//						// don't remove parents of root node
//						continue;
//					}
//					Edge.removeEdge(curEdge);
//				}
//			}
//		}
    }

    /**
     */
    private List listUnconnectedEdgesAndNodes() {
        List unconnectedEdges = new LinkedList();

        Iterator allNodes = processedNodes.values().iterator();
        while (allNodes.hasNext()) {
            GraphNode curNode = (GraphNode) allNodes.next();

//			if (curNode == root) {
//				continue;
//			}

            // get inbound nodes (parents) and check how many there is.
            // If there is no parents - this node is not attached
            // to anything, hence - 'hanging node'
            Iterator inboundNodes = Edge.getInboundEdges(curNode);

//			System.out.println("node = " + curNode);
//			Iterator it = Edge.getInboundEdges(curNode);
//			while (it.hasNext()) {
//				System.out.println("\tinbound edge = " + (Edge) it.next());
//			}
//

            if (!inboundNodes.hasNext()) {
                unconnectedEdges.add(curNode);
                if (!_unconnectedNodes.contains(curNode)) {
                    _unconnectedNodes.add(curNode);
                    System.out.println("unconnected node = " + curNode + ", num of children in the branch = " + curNode.getBranchNodesNum());
                }

                // get outbound edges for this node
                Iterator curOutEdges = Edge.getOutboundEdges(curNode);
                while (curOutEdges.hasNext()) {
                    Edge curEdge = (Edge) curOutEdges.next();
                    if (curEdge.getToNode() == root) {
                        // don't remove parents of root node
                        continue;
                    }
                    //Edge.removeEdge(curEdge);
                    //System.out.println("unconnected Edge = " + curEdge);
                }
            }
        }
        return unconnectedEdges;
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
     * Set root node
     */
    public void setRoot(GraphNode rootNode) {
        root = rootNode;
    }

    /**
     * Get nodes list. Convinience method.
     *
     * @return  list of all graph nodes
     */
    public List getNodesList() {
//        LinkedList queue = new LinkedList();
//        LinkedList result = new LinkedList();
//        boolean isTree = true;
//
//        queue.add(this.root);
//
//        while (!queue.isEmpty()) {
//            GraphNode nextQueueNode = (GraphNode) queue.remove(0);
//            result.add(nextQueueNode);
//
//            Iterator allOutboundNodes =
//                    Edge.getOutboundEdgeNodes(nextQueueNode);
//            while (allOutboundNodes.hasNext()) {
//                GraphNode curNode = (GraphNode) allOutboundNodes.next();
//                queue.add(curNode);
//            }
//        }
//        return result;
        return _allNodes;
    }

    /**
     *
     */
    public List getUnconnectedNodesList() {
        return _unconnectedNodes;
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
                    Edge.getOutboundEdgeNodes(nextQueueNode);

            while (allOutboundNodes.hasNext()) {
                GraphNode curNode = (GraphNode) allOutboundNodes.next();
                queue.add(curNode);

                Iterator inboundEdges = Edge.getInboundEdges(curNode);
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
                    Iterator it = Edge.getInboundEdges(curNode);
                    while (it.hasNext()) {
                        Edge edge = (Edge) it.next();
                    }
                    isTree = false;
                }
            }
        }
        return isTree;
    }

    /**
     * Convert Graph into Tree by cloning nodes with duplicate parents (inbound edges)
     *
     * @param   GraphNode root
     * @throws  NoSuchRelationLinkException, NoSuchPropertyException
     */
    private void convertIntoTree(GraphNode root)
            throws NoSuchRelationLinkException, NoSuchPropertyException {
        LinkedList queue = new LinkedList();
        boolean isTree = true;

        queue.add(root);

        while (!queue.isEmpty()) {
            GraphNode nextQueueNode = (GraphNode) queue.remove(0);

            debug.message(
                    "Graph",
                    "convertIntoTree",
                    "--- processing node " + nextQueueNode.getName() + " -----");

            Iterator allOutboundNodes =
                    Edge.getOutboundEdgeNodes(nextQueueNode);

            while (allOutboundNodes.hasNext()) {
                GraphNode curNode = (GraphNode) allOutboundNodes.next();
                queue.add(curNode);

                Iterator inboundEdges = Edge.getInboundEdges(curNode);
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
                    if (_unconnectedNodes.contains(curNode)) {
                        continue;
                    }

                    GraphNode cloneNode = curNode.makeClone();
                    _allNodes.add(cloneNode);

                    // add edge from cloneNode to a NodeParent with this rel type and
                    // remove edge from curNode to a NodeParent with this rel type
                    Iterator it = Edge.getInboundEdges(curNode);
                    if (it.hasNext()) {
                        Edge firstEdge = (Edge) it.next();
                        Edge newEdge =
                                new Edge(
                                        firstEdge.getFromNode(),
                                        cloneNode,
                                        firstEdge.getType());
                        Edge.removeEdge(firstEdge);
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
     * @throws   NoSuchRelationLinkException, NoSuchPropertyException
     */
    private void deepCopy(GraphNode node, GraphNode cloneNode)
            throws NoSuchRelationLinkException, NoSuchPropertyException {

        Iterator outboundEdgesIterator = Edge.getOutboundEdges(node);

        while (outboundEdgesIterator.hasNext()) {
            Edge curEdge = (Edge) outboundEdgesIterator.next();
            GraphNode toNode = curEdge.getToNode();
            GraphNode cloneToNode = toNode.makeClone();
            Edge newEdge = new Edge(cloneNode, cloneToNode, curEdge.getType());
            deepCopy(toNode, cloneToNode);
        }
    }

    /**
     *
     */
    private int[] getTypeCount(Iterator it) {
        int[] typeCount =
                new int[ontorama.OntoramaConfig.getRelationLinksSet().size()];
        for (int i = 0; i < typeCount.length; i++) {
            typeCount[i] = 0;
        }

        while (it.hasNext()) {
            Edge curEdge = (Edge) it.next();
            int type = curEdge.getType();
            typeCount[type]++;
        }
        return typeCount;
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
                    Edge.getOutboundEdgeNodes(nextQueueNode);

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

        Iterator edgesIterator = Edge.edges.iterator();
        while (edgesIterator.hasNext()) {
            Edge curEdge = (Edge) edgesIterator.next();
            GraphNode fromNode = curEdge.getFromNode();
            GraphNode toNode = curEdge.getToNode();
            int type = curEdge.getType();
            RelationLinkDetails relLinkDetails =
                    OntoramaConfig.getRelationLinkDetails(type);
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

}
