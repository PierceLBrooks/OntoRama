package ontorama.backends;

/**
 * TODO:search::this function does n't yet include the rejected edges
 * TODO:the rejectedEdges are ONLY saved while the computer is up n running, forgets everything while turn off has not got any way of printing it and thats way, must decide how to represent them in parse them in again
 * TODO:toRDFModel::does NOT include the extended Graph (rejected edges) how should rejected edges be represented in the RDF file
 * 
 * TODO addEdge and rejectEdge don't regard the namespace for the relationType
 */

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ontorama.util.Debug;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.util.NoSuchPropertyException;

import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.Model;



/**
 * Extends Graph to be able to handle rejected Edges to. A rejected Edge 
 * means that someone has decided to reject (not to use) a specific Edge 
 * (relation between two nodes)
 * 
 * The  class offers to add and reject informaiton and for to search on 
 * the information. A search result in a acyclic graph which is converted 
 * into a tree. The returned Graph removes all duplicate parents by cloning 
 * the children (Removes all duplicate inbound edges by cloning outbound edges)
 * 
 * The ExtendedGraph is built by calls from ParserFromRDF via calls to addNode
 * and addEdge
 *
 * <p>
 * Copyright:    Copyright (c) 2002
 * <br>
 * Company:     DSTC
 */
public class ExtendedGraph extends Graph {
 
 //First add all nodes
 //Second all edges an edge can be between an existing node and 
 //an unexisting node => see how ONtoRama handles it, probably just create a 
 //node with an URI and the the link is shown if no other information is provided
 
 
 
    /**
     * Hold a list of rejected Edges
     */
    private Edge rejectedEgdes = null;
	
	/**
	 * 
	 */
	private Hashtable allNodes = null;
	
 	/**
	 * The rules that defines how the ontology shall be merged
	 */
	Rules rules = null;
	

	public ExtendedGraph() {
		this.rejectedEgdes = new Edge();	
		this.rules = new Rules(this);
		this.allNodes = new Hashtable();
	}

	public ExtendedGraph(Edge rejectedEdges,
						  Hashtable allNodes,
						  Rules rules,
						  Edge edge, 
			          	  Hashtable processedNodes, 
	    		      	  GraphNode root, 
	          			  List _unconnectedNodes, 
			          	  Debug debug) {
		super(edge,processedNodes, root,_unconnectedNodes, debug);
		this.rejectedEgdes = rejectedEdges;	
		this.rules = rules;
		this.allNodes = allNodes;
	}
	
	public void addEdge(String fromURI, String toURI, int relationType,String namespace) {
		//stem.err.println("ExtGraph::addEdge:" + toURI + ";" + fromURI + ";" + relationType);
		GraphNode fromNode = null;
		GraphNode toNode = null;
		EdgeObject newEdgeObject = null;

		try {
			fromNode = this.getGraphNode(fromURI);
		} catch (NoSuchGraphNodeException e) {
			//System.err.println("A new node created:" + fromURI);
			fromNode = new GraphNode(fromURI,fromURI);
			this.addNode(fromNode);
		}
		try {
			toNode = this.getGraphNode(toURI);					
		} catch (NoSuchGraphNodeException e) {
			//System.err.println("A new node created:" + fromURI);
			toNode = new GraphNode(toURI,toURI);
			this.addNode(toNode);
		}
			
		newEdgeObject = new EdgeObject(fromNode, toNode, relationType, namespace);

		if (rules.checkAddEdge(newEdgeObject)) {
			//if the edge does not clash with anything, add it	
			this.getEdge().addEdge(newEdgeObject);
			//System.err.println("ExtendedGraph::addEdge(" + newEdgeObject + ")"); 
		}	

	}


	public void addNode(GraphNode node) {
		//Make an exact copy with all data
		GraphNode newNode;
		try {
			newNode = node.makeCopy();

			if (rules.checkAddNode(newNode)) {
				//The ExtGraphNode should be added
				//if there already exist a copy, owerwrite it
				if (this.getNodes().containsKey(node.getFullName())) {
					GraphNode updateGraphNode = (GraphNode) this.getNodes().get(node.getFullName());
					updateGraphNode.update(newNode);
				} else {
					//otherwise add it
					this.getNodes().put(newNode.getFullName(),newNode);								
				}
			}	
		} catch (NoSuchPropertyException e) {
			System.err.println("Error ExtGraph::addNode");
			//TODO do something
		}
	}
	
	public void rejectEdge(String fromURI, String toURI, int relationType,String namespace) {
		GraphNode fromNode = null;
		GraphNode toNode = null;
		EdgeObject newEdgeObject = null;

		try {
			fromNode = this.getGraphNode(fromURI);
			toNode = this.getGraphNode(toURI);					
			
			newEdgeObject = new EdgeObject(fromNode, toNode, relationType,namespace);

			if (rules.checkRejectEdge(newEdgeObject)) {
				EdgeObject remExtEdgeObj = this.getEdgeObject(fromNode,toNode,relationType);
				if (remExtEdgeObj != null) {
					this.getEdge().removeEdge(remExtEdgeObj);
				}
				
				//add it to reject list
				this.rejectedEgdes.addEdge(newEdgeObject);
				
			}	
		} catch (NoSuchGraphNodeException e) {
			//TODO errohandling
		}		
	}

	public void updateNode(GraphNode node) {
		try {
			GraphNode updateNode;
			updateNode = this.getGraphNode(node.getFullName());
			if (rules.checkUpdateNode(updateNode)) {
				updateNode.update(node);
			}	
		} catch (NoSuchGraphNodeException e) {
			//TODO errohandling
			System.err.println("extGraph::ERROR A2");
		}			
	}	
	
/**
 * Makes a copy of Extenteded Graph, makes in into a Tree and then do the search
 * TODO this function does n't yet include the rejected edges
 * TODO this method do a total search, and does NOT regard the depth parameter
 */
	public ExtendedGraph search(Query query) {
		//keeps track of the depth for every node, the shallowest depth is used for every node
		Hashtable queueNodesDepth = new Hashtable();
		
		ExtendedGraph newExtGraph = new ExtendedGraph();
		String searchedURI = query.getQueryTypeName();
		HashSet alreadyProcessedNodes = new HashSet();
		try {
			ExtendedGraph extGraph = (ExtendedGraph) this.makeCopy();
			GraphNode searchedNode = extGraph.getGraphNode(searchedURI);
			int queryDepth = query.getDepth();

			LinkedList relationTypeToSearchFor = (LinkedList) query.getRelationLinksList();
			LinkedList queueNodes = new LinkedList();
			LinkedList nodesList;
			String fromNodeURI;
			String toNodeURI;
			Iterator edgesIt;
			Iterator nodesIt;
			GraphNode currGraphNode;
			GraphNode tempGraphNode;
			EdgeObject currEdgeObj;			
			int tempInt = 0;			
			boolean depthReached = false;

			//add searchNode
			queueNodes.add(searchedNode);
			queueNodesDepth.put(searchedNode,new Integer(0));			
			newExtGraph.addNode(searchedNode);

			while ((!queueNodes.isEmpty()) && !depthReached) {												
				currGraphNode = (GraphNode) queueNodes.remove(0);	

				tempInt = ((Integer) queueNodesDepth.get(currGraphNode)).intValue();
				if (tempInt < queryDepth) {
					nodesList = (LinkedList) extGraph.getEdge().getOutboundEdgeNodesList(currGraphNode,
															relationTypeToSearchFor);		
	
					//add outbound nodes to the queue
					queueNodes.addAll(this.getNotProcessedNodesFrom(nodesList, alreadyProcessedNodes));
	
	
					//add the inbound nodes to the graph
					nodesIt = nodesList.iterator();
					while (nodesIt.hasNext()) {
						tempGraphNode = (GraphNode) nodesIt.next();
						newExtGraph.addNode(tempGraphNode);	
						tempInt = ((Integer) queueNodesDepth.get(currGraphNode)).intValue();
						tempInt++; 					
						queueNodesDepth.put(tempGraphNode,new Integer(tempInt));
					}
	
					edgesIt = extGraph.getEdge().getInboundEdgesList(currGraphNode, 
														relationTypeToSearchFor).iterator();
														
					while (edgesIt.hasNext()) {
						currEdgeObj = (EdgeObject) edgesIt.next();
						
						fromNodeURI = currEdgeObj.getFromNode().getFullName();
						toNodeURI = currEdgeObj.getToNode().getFullName();
						//make sure that the GraphNode has been added, if not, do it
						try {
							newExtGraph.getGraphNode(fromNodeURI);
						} catch (NoSuchGraphNodeException e) {
							GraphNode newNode = new GraphNode(fromNodeURI,fromNodeURI);
							//System.err.println("ERRORA create a new node cause it didn't exist:" + fromNodeURI);
							newExtGraph.addNode(newNode);	
						}
							
						try {
							newExtGraph.getGraphNode(toNodeURI);
						} catch (NoSuchGraphNodeException e) {
							//System.err.println("ERRORB create a new node cause it didn't exist:" + fromNodeURI);
							GraphNode newNode = new GraphNode(toNodeURI);
							newExtGraph.addNode(newNode);	
						}
	
						newExtGraph.addEdge(fromNodeURI,
											toNodeURI,
											currEdgeObj.getType(),
											currEdgeObj.getNamespace());
					}				
				} else {
					depthReached = true;
				}
			}			
		} catch (NoSuchGraphNodeException e) {
			System.err.println("The node you searched for can not be found:" + searchedURI);
		}
		return newExtGraph;
	}

	private LinkedList getNotProcessedNodesFrom(LinkedList nodesList, HashSet alreadyProccesedNodes) {
		Iterator listIt = nodesList.iterator();
		LinkedList retVal = new LinkedList();
		GraphNode currGraphNode;
		
		while (listIt.hasNext()) {
			currGraphNode = (GraphNode) listIt.next();
			if (!alreadyProccesedNodes.contains(currGraphNode.getFullName())) {
				alreadyProccesedNodes.add(currGraphNode.getFullName());
				retVal.add(currGraphNode);
			}
		}
		return retVal;
	}


	public EdgeObject getRejectedEdgeObject(GraphNode fromNode, GraphNode toNode, int relLink) throws NoSuchEdgeObjectException {
        Iterator it = this.rejectedEgdes.getOutboundEdges(fromNode, relLink);
        EdgeObject retVal = null;
        while (it.hasNext()) {
            EdgeObject curEdge = (EdgeObject) it.next();

            GraphNode curNode = curEdge.getToNode();
			//This could have been a comp[arison between objects but changes when we had problem wit searchRejected()
            if (curNode.getFullName().equals(toNode.getFullName())) {
                retVal = curEdge;
            }
        }
        if (retVal == null) {
    		throw new NoSuchEdgeObjectException("");
        } else {
    		return retVal;
        }
	}
	
	public boolean isThisRejected(EdgeObject edgeObj) {
		boolean retVal = false;	
	
		try {
			EdgeObject existingEdgeObj = this.getRejectedEdgeObject(edgeObj.getFromNode(),
																	edgeObj.getToNode(),
																	edgeObj.getType());
			retVal = true;																	
		} catch (NoSuchEdgeObjectException e) {
			retVal = false;
		}
	
		return retVal;	
	}
	

	public String toRDFString() {
		String retVal = null;
		Model rdfModel = this.toRDFModel();
		
		retVal = this.toRDFStringFromModel(rdfModel);
		
		retVal = this.toRDFStringWorkaround(retVal);

		return retVal;
			
	}

	//TODO implement this for rejectedNodes too, now we only have it for Graph
	private ModelMem toRDFModel() {
		/**
		 * Contains a mapping between processedNodes and resources in the model
		 * Key: processedNodes.getFullName()
		 * Value: Model.Resource
		 */
		Hashtable relNodesToResource = new Hashtable();
		ModelMem rdfModel = this.toRDFModel(relNodesToResource);
		
		//TODO make sure we get a enumeration
		//Enumeration nodesEnum = (Enumeration) list.iterator();
		
		//rdfModel = this.writeRDFModel(rdfModel,relNodesToResource,nodesEnum);
	
		return rdfModel;

	}
   
	public Hashtable getAllNodes() {
		allNodes.putAll(this.getNodes());
		return this.allNodes;
	}

	public Iterator getAllEdgesIt() {
		List allEdges = new LinkedList();
		allEdges.addAll(this.getEdge().getEdges());
		allEdges.addAll(this.rejectedEgdes.getEdges());
		
		return allEdges.iterator();
	}
	
	public ExtendedGraph makeCopy() {
		ExtendedGraph retVal = new ExtendedGraph(
									this.rejectedEgdes,
									this.allNodes,
									this.rules,
									this.getEdge(),
									this.getNodes(),
									this.getRootNode(),
									this.getUnconnectedNodesList(),
									new Debug(false));
		return retVal;
	}

	public void addRejectedEdges(List edges) {
		Iterator edgesIt = edges.iterator();
		EdgeObject currEdgeObj = null;
		EdgeObject newEdgeObj = null;
		GraphNode fromNode = null;
		GraphNode toNode = null;

		while (edgesIt.hasNext()) {
			currEdgeObj = (EdgeObject) edgesIt.next();
			try {
				fromNode = getGraphNode(currEdgeObj.getFromNode().getFullName());
			} catch (NoSuchGraphNodeException e) {
				fromNode = new GraphNode(currEdgeObj.getFromNode().getFullName(),currEdgeObj.getFromNode().getFullName());
				this.addNode(fromNode);				
			}
			try {
				toNode = getGraphNode(currEdgeObj.getToNode().getFullName());
			} catch (NoSuchGraphNodeException e) {
				toNode = new GraphNode(currEdgeObj.getToNode().getFullName(),currEdgeObj.getToNode().getFullName());
				this.addNode(toNode);
			}

			newEdgeObj = new EdgeObject(fromNode, toNode, currEdgeObj.getType(),currEdgeObj.getNamespace());
			this.rejectedEgdes.getEdges().add(newEdgeObj);
		}
	}

	public List getRejectedEdges() {
		return this.rejectedEgdes.getEdges();
	}
	
	public String printRejected() {
		Iterator it = this.rejectedEgdes.getEdgeIterator();
		EdgeObject edgObj;
		while (it.hasNext()) {
			edgObj = (EdgeObject) it.next();
			System.out.println("Edge:" + edgObj);
		}	
		return "";
	}
}
