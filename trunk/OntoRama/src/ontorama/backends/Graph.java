package ontorama.backends;

import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.ConceptPropertiesMapping;
import ontorama.ontologyConfig.RdfMapping;
import ontorama.ontologyConfig.RelationLinkDetails;
import ontorama.util.Debug;
import ontorama.webkbtools.util.NoSuchPropertyException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

import java.io.StringWriter;
import java.util.*;

import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;
import com.hp.hpl.mesa.rdf.jena.common.ResourceImpl;
import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.Model;
import com.hp.hpl.mesa.rdf.jena.model.Property;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.model.RDFWriter;
import com.hp.hpl.mesa.rdf.jena.model.Resource;

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
public class Graph {

	/**
	 * Hold the Edge object
	 */	
	private Edge edge = null;

     /**
     * Hold processed nodes (nodes created for Graph)
     * Keys - node names, values - node objects
     */
    private Hashtable processedNodes = null;

    /**
     * root node
     */
    private GraphNode root = null;

    /**
     * list of unconnected nodes or top level nodes
     */
    private List _unconnectedNodes = null;

    /**
     *
     */
    Debug debug = new Debug(false);


	public Graph() {
		edge = new Edge();
		processedNodes = new Hashtable();
		root = null;
        _unconnectedNodes = new LinkedList();
		debug = new Debug(false);
	}

	public Graph(Edge edge, 
	          	  Hashtable processedNodes, 
	          	  GraphNode root, 
	          	  List _unconnectedNodes, 
	          	  Debug debug) {
		this.edge = edge;
		this.processedNodes = processedNodes;
		this.root = root;
        this._unconnectedNodes = _unconnectedNodes;
		this.debug = debug;
	}


    /**
     *
     */
    private void removeUnconnectedEdges() {
        int lastNumOfEdges = -1;
        debug.message("number of Edges = " + this.edge.getNrOfEdges());
        while (this.edge.getNrOfEdges() != lastNumOfEdges) {
            debug.message(
                    "number of Edges = "
                    + this.edge.getNrOfEdges()
                    + ", lastNumOfEdges = "
                    + lastNumOfEdges);
            lastNumOfEdges = this.edge.getNrOfEdges();
            cleanUpEdges();
        }
        debug.message("number of Edges = " + this.edge.getNrOfEdges());
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
            EdgeObject curEdgeObject = (EdgeObject) unconnectedEdges.next();
            this.edge.removeEdge(curEdgeObject);
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
            Iterator inboundNodes = this.edge.getInboundEdges(curNode);

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
                    //System.out.println("unconnected node = " + curNode + ", num of children in the branch = " + curNode.getBranchNodesNum());
                }

                // get outbound edges for this node
                Iterator curOutEdges = this.edge.getOutboundEdges(curNode);
                while (curOutEdges.hasNext()) {
                    EdgeObject curEdgeObject = (EdgeObject) curOutEdges.next();
                    if (curEdgeObject.getToNode() == root) {
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
        return this.root;
    }

    /**
     * Set root node
     */
    public void setRoot(String rootNode) throws NoSuchGraphNodeException {
		GraphNode retVal = null;
		retVal = (GraphNode) this.processedNodes.get(rootNode);
		if (retVal != null) {
			this.root = retVal;
		} else {
			throw new NoSuchGraphNodeException("Could not find the graph node:" + rootNode);	
		}
    }


    /**
     * Get nodes list. Convinience method.
     *
     * @return  list of all graph nodes
     */
    public List getNodesList() {
		LinkedList retVal = new LinkedList();
		Enumeration enumNodes = this.processedNodes.elements();
		
		while (enumNodes.hasMoreElements()) {
			GraphNode curr = (GraphNode) enumNodes.nextElement();
			retVal.add(curr);	
		}
		return retVal;
    }

    /**
     *
     */
    public List getUnconnectedNodesList() {
		this.listUnconnectedEdgesAndNodes();
        return _unconnectedNodes;
    }

    /**
     * Test if current Graph is a Tree
     *
     * @param root  - root GraphNode
     */
    public boolean testIfTree(GraphNode root) {
        LinkedList queue = new LinkedList();
        boolean isTree = true;

        queue.add(root);

        while (!queue.isEmpty()) {
            GraphNode nextQueueNode = (GraphNode) queue.remove(0);
            Iterator allOutboundNodes =
                    this.edge.getOutboundEdgeNodes(nextQueueNode);

            while (allOutboundNodes.hasNext()) {
                GraphNode curNode = (GraphNode) allOutboundNodes.next();
                queue.add(curNode);

                Iterator inboundEdges = this.edge.getInboundEdges(curNode);
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
                    Iterator it = this.edge.getInboundEdges(curNode);
                    while (it.hasNext()) {
                        EdgeObject edgeObject = (EdgeObject) it.next();
                        edgeObject = edgeObject;
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
    protected void convertIntoTree(GraphNode root)
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
                    this.edge.getOutboundEdgeNodes(nextQueueNode);

            while (allOutboundNodes.hasNext()) {
                GraphNode curNode = (GraphNode) allOutboundNodes.next();
                queue.add(curNode);

                Iterator inboundEdges = this.edge.getInboundEdges(curNode);
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

                    // add edge from cloneNode to a NodeParent with this rel type and
                    // remove edge from curNode to a NodeParent with this rel type
                    Iterator it = this.edge.getInboundEdges(curNode);
                    if (it.hasNext()) {
                        EdgeObject firstEdge = (EdgeObject) it.next();
                        EdgeObject newEdgeObject =
                                new EdgeObject(
                                        firstEdge.getFromNode(),
                                        cloneNode,
                                        firstEdge.getType(),
                                        firstEdge.getNamespace());
						this.edge.addEdge(newEdgeObject);
                        this.edge.removeEdge(firstEdge);
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

        Iterator outboundEdgesIterator = this.edge.getOutboundEdges(node);

        while (outboundEdgesIterator.hasNext()) {
            EdgeObject curEdgeObject = (EdgeObject) outboundEdgesIterator.next();
            GraphNode toNode = curEdgeObject.getToNode();
            GraphNode cloneToNode = toNode.makeClone();

            EdgeObject newEdgeObject = new EdgeObject(cloneNode, 
            										cloneToNode, 
            										curEdgeObject.getType(),
            										curEdgeObject.getNamespace());

			//adds directly to the list, otherwise will this not be added
            this.getEdge().getEdges().add(newEdgeObject);
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

		EdgeObject curEdgeObject;
        while (it.hasNext()) {
            curEdgeObject = (EdgeObject) it.next();
            int type = curEdgeObject.getType();
            typeCount[type]++;
        }
        return typeCount;
    }


	protected String toRDFStringFromModel(Model rdfModel) {
		StringWriter strWr = new StringWriter();
		RDFWriter rdfWriter;

		try {

			rdfWriter = rdfModel.getWriter();
			rdfWriter.write(rdfModel, strWr, null);
		} catch (RDFException e) {
			//TODO some errorhandling
		}

		return strWr.toString();
	}

	protected String toRDFStringWorkaround(String retVal) {
		//TODO workaround
		//get the name for the namespace
		if (retVal.indexOf("http://www.w3.org/TR/1999/PR-rdf-schema-19990303#") > 0) {
			int startPos = retVal.indexOf("http://www.w3.org/TR/1999/PR-rdf-schema-19990303#") - 10;
			startPos = retVal.indexOf(":", startPos) + 1;
			int endPos = retVal.indexOf("'http://www.w3.org/TR/1999/PR-rdf-schema-19990303#");
			endPos = retVal.indexOf("=", endPos-2);
			
			String namespaceID = retVal.substring(startPos,endPos);
			//replace all rdf:Description with <namespace>:Class
			retVal = retVal.replaceAll("rdf:Description",namespaceID + ":Class");
			retVal = retVal.replaceAll(namespaceID + ":","rdfs:");
			retVal = retVal.replaceAll(":" + namespaceID + "=",":rdfs=");						
		} else {
			String firstPart = retVal.substring(0,8);
			String secondPart = "  xmlns:rdfs='http://www.w3.org/TR/1999/PR-rdf-schema-19990303#' ";
			String thirdPart = retVal.substring(8);
			
			retVal = firstPart + secondPart + thirdPart;
			
			retVal = retVal.replaceAll("rdf:Description","rdfs:Class");
		}
		return retVal;
	}


/**
 * This methods are used by the "new" model
 * the methods above are still used by OntoRama
 * 
 */
	public String toRDFString() {
		String retVal = null;
		Model rdfModel = this.toRDFModel(new Hashtable());
		
		retVal = this.toRDFStringFromModel(rdfModel);
		
		retVal = this.toRDFStringWorkaround(retVal);
			
		return retVal;
	}

	protected Resource getResource(Hashtable relTable,String URI) {
		if (relTable.containsKey(URI)) {
			return (Resource) relTable.get(URI);	
		} else {
			Resource resource = new ResourceImpl(URI);
			relTable.put(URI,resource);
			return resource;
		}	
		
	}
	
	protected ModelMem toRDFModel(Hashtable relTable) {
		/**
		 * Contains a mapping between processedNodes and resources in the model
		 * Key: processedNodes.getFullName()
		 * Value: Model.Resource
		 */
		Hashtable relNodesToResource = relTable;
		ModelMem rdfModel = new ModelMem();
		
		Enumeration nodesEnum = this.processedNodes.elements();
		
		rdfModel = this.createRDFModel(rdfModel,relNodesToResource,nodesEnum);
		
		return rdfModel;
	}
	
	
	protected ModelMem createRDFModel(ModelMem rdfModel, 
									   Hashtable relNodesToResource, 
									   Enumeration nodesEnum) {

		GraphNode currGraphNode;
		Resource resource;
		Property property;
		String propValue;
		String propName;
		Hashtable conceptProperties = OntoramaConfig.getConceptPropertiesRdfMapping();
		Hashtable rdfNameFromRelationID = createMappingFromRelationIDtoRDF();

		try {
			//save all nodes
			while (nodesEnum.hasMoreElements()) {

				currGraphNode = (GraphNode) nodesEnum.nextElement();
				resource = getResource(relNodesToResource,currGraphNode.getFullName());
	
				//save all the properties foreach node (excluding them in the Edges)
				Hashtable conceptPropertiesConfig = 
									OntoramaConfig.getConceptPropertiesTable();
	            Enumeration e = conceptPropertiesConfig.keys();
				Iterator propValueIterator;
				String propKey;
				ConceptPropertiesMapping curValue;
	            while (e.hasMoreElements()) {
					propKey = (String) e.nextElement();
					curValue = (ConceptPropertiesMapping) conceptProperties.get(propKey);
					propName = curValue.getRdfTag();
					
					property = new PropertyImpl(currGraphNode.getPropertyNamespace(propKey), propName);
	                propValueIterator = currGraphNode.getProperty(propKey).iterator();
	                while (propValueIterator.hasNext()) {
	                    propValue = (String) propValueIterator.next();
						rdfModel.add(resource, property, propValue);					
	                }
	            }
	
				//save all the ingoing edges for each node
				Iterator inEdgesIt = this.getEdge().getInboundEdges(currGraphNode);
				while (inEdgesIt.hasNext()) {
					EdgeObject edgObj = (EdgeObject) inEdgesIt.next();
					propName  = new Integer(edgObj.getType()).toString();

					propValue = (String) rdfNameFromRelationID.get(propName);
					propName = propValue;
					propValue = edgObj.getFromNode().getFullName();
					//System.err.println("FOUND EDGE " + propName + ":" + propValue);
					
					property = new PropertyImpl(edgObj.getNamespace(), propName);
					rdfModel.add(resource, property, this.getResource(relNodesToResource,propValue));
				}
			}		
		} catch (RDFException e) {
			//TODO errorhandling
			System.err.println("ERROR 1");
		} catch (NoSuchPropertyException e) {
			//TODO errorhandling
			System.err.println("ERROR 2");			
		}
		return rdfModel;
	}
	

	private Hashtable createMappingFromRelationIDtoRDF() {
		Hashtable retVal = new Hashtable();
		
		List ontologyRelationRdfMapping = OntoramaConfig.getRelationRdfMapping();
        Iterator ontologyRelationRdfMappingIterator = ontologyRelationRdfMapping.iterator();
        while (ontologyRelationRdfMappingIterator.hasNext()) {
            RdfMapping rdfMapping = (RdfMapping) ontologyRelationRdfMappingIterator.next();
            Iterator mappingTagsIterator = rdfMapping.getRdfTags().iterator();
            while (mappingTagsIterator.hasNext()) {
                String mappingTag = (String) mappingTagsIterator.next();
                //System.out.println("mappingTag = " + mappingTag + ", id = " + rdfMapping.getId());
				retVal.put(new Integer(rdfMapping.getId()).toString(), mappingTag);
            }
        }
        return retVal;
	}
	

	/**
     * Print Graph into XML tree
     */
    public String printXml() throws NoSuchPropertyException {

		System.err.println("\n\n PrintXml starts");
		
        String resultStr = "<ontology top='" + this.getRootNode().getName() + "'>";
        resultStr = resultStr + "\n";
        resultStr = resultStr + printXmlConceptTypesEl();
        resultStr = resultStr + printXmlRelationLinksEl();
        resultStr = resultStr + "\n";
        resultStr = resultStr + "</ontology>";
        resultStr = resultStr + "\n";

		System.err.println("\n\n PrintXml starts, RESULTAT:");
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
        queue.add(this.getRootNode());

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
                    this.getEdge().getOutboundEdgeNodes(nextQueueNode);

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

        Iterator edgesIterator = this.getEdge().getEdgeIterator();
        while (edgesIterator.hasNext()) {
            EdgeObject curEdgeObject = (EdgeObject) edgesIterator.next();
            GraphNode fromNode = curEdgeObject.getFromNode();
            GraphNode toNode = curEdgeObject.getToNode();
            int type = curEdgeObject.getType();
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
 	
	public Edge getEdge() {
		return this.edge;	
	}

	public Hashtable getNodes() {
		return this.processedNodes;	
	}
		
	public void addEdge(String fromURI, String toURI, int relationType,String namespace) {
		GraphNode fromNode = null;
		GraphNode toNode = null;
		EdgeObject newEdgeObject = null;

		try {
			//TODO make sure that getGraphNode works and if URI not exist create a new node should be done here
			fromNode = this.getGraphNode(fromURI);
			toNode = this.getGraphNode(toURI);					
			
			newEdgeObject = new EdgeObject(fromNode, toNode, relationType,namespace);

			this.edge.addEdge(newEdgeObject);
		} catch (NoSuchGraphNodeException e) {
			//TODO errohandling form getGraphNode
			//System.err.println("ExtendedGraph::ERROR");
		}
	}


	public void addNode(GraphNode node) {
		//Make an exact copy with all data
		GraphNode newNode;
		try {
			newNode = node.makeCopy();

			//The ExtGraphNode should be added
			//if there already exist a copy, owerwrite it
			if (this.processedNodes.containsKey(node.getFullName())) {
				GraphNode updateGraphNode = (GraphNode) this.processedNodes.get(node.getFullName());
				updateGraphNode.update(newNode);
			} else {
				//otherwise add it
				this.processedNodes.put(newNode.getFullName(),newNode);								
			}
		} catch (NoSuchPropertyException e) {
			System.err.println("Error Graph::addNOde");
			//TODO do something
		}
	}
	


	public void updateNode(GraphNode node) {
		try {
			GraphNode updateNode = this.getGraphNode(node.getFullName());
			
			updateNode.update(node);
		} catch (NoSuchGraphNodeException e) {
			System.err.println("Couldn't find the GraphNode");
			//TODO do something
		}			
	}	
	

	public GraphNode getGraphNode(String uri) throws NoSuchGraphNodeException {
		GraphNode retVal = null;
		retVal = (GraphNode) this.processedNodes.get(uri);
		
		if (retVal != null) {
			return retVal;
		} else {
			throw new NoSuchGraphNodeException("Could not find a node with the URI:" + uri);	
		}
	}
	
	public EdgeObject getEdgeObject(GraphNode fromNode, GraphNode toNode, int relLink) {
        Iterator it = this.edge.getOutboundEdges(fromNode, relLink);
        while (it.hasNext()) {
            EdgeObject curEdge = (EdgeObject) it.next();
            GraphNode curNode = curEdge.getToNode();
            if (curNode.equals(toNode)) {
                return curEdge;
            }
        }
        return null;
    }


    /**
     * Sets the depth of the node in the tree.
     *
     * @param depth
     */
    protected void setDepth() {
		this.getRootNode().setDepth(0,this);
    }


}
