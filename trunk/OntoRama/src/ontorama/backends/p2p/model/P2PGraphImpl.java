package ontorama.backends.p2p.model;

import java.net.URI;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import ontorama.backends.p2p.model.util.NoSuchP2PNodeException;
import ontorama.model.GraphImpl;
import ontorama.model.NoTypeFoundInResultSetException;
import ontorama.model.util.GraphModificationException;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.query.QueryResult;
import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

/**
 * Build a collection of GraphNodes and Edges that form a Graph.
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
public class P2PGraphImpl extends GraphImpl implements P2PGraph {

	public P2PGraphImpl() {
		super();
	}

	public P2PGraphImpl(QueryResult queryResult) 
		throws NoTypeFoundInResultSetException, NoSuchRelationLinkException {
		super(queryResult);
	}


	public void add(ParserResult parserResult) throws GraphModificationException, NoSuchRelationLinkException {
		try {

			Iterator it = parserResult.getNodesList().iterator();
			while (it.hasNext()) {
				this.addNode((P2PNode) it.next());
			}	
			it = parserResult.getEdgesList().iterator();
			while (it.hasNext()) {
					this.addEdge((P2PEdge) it.next());
			}	
		} catch (GraphModificationException e) {
			throw e;
		} catch (NoSuchRelationLinkException e) {
			throw e;
		}
	}
	
	
    /**
     * Assert a node.
     * 
     * @param p2pnode to assert
     * @param userIdUri for the asserter
     */
     public void assertNode(P2PNode node, URI userIdUri) throws GraphModificationException {
		try {
			node.addAssertion(userIdUri);
			this.addNode(node);
		} catch (GraphModificationException e) {
			throw e;
		}
    }
		

    /**
     * Reject a node.
     * 
     * @param p2pnode to reject
     * @param userIdUri for the rejecter
     */
    public void rejectNode (P2PNode node, URI userIdUri) throws GraphModificationException {
    	try {
    		node.addRejection(userIdUri);
			this.addNode(node);
		} catch (GraphModificationException e) {
			throw e;
		}
    }


    /**
     * Assert an edge.
     * 
     * @param p2pedge to assert
     * @param userIdUri for the asserter
     */
    public void assertEdge (P2PEdge edge, URI userIdUri) 
    		throws GraphModificationException, NoSuchRelationLinkException {
		try {
			edge.addAssertion(userIdUri);
			this.addEdge(edge);
		} catch (GraphModificationException e) {
			throw e;
		} catch (NoSuchRelationLinkException e) {
			throw e;
		}    
    }
    
    /**
     * Reject an edge.
     * 
     * @param p2pedge to reject
     * @param userIdUri for the rejecter
     */
    public void rejectEdge (P2PEdge edge, URI userIdUri) 
    	throws GraphModificationException, NoSuchRelationLinkException {
		try {
			edge.addRejection(userIdUri);
			this.addEdge(edge);
		} catch (GraphModificationException e) {
			throw e;
		} catch (NoSuchRelationLinkException e) {
			throw e;
		}    
    }


//TODO left to implement: 
/**
 * Return the searchresult for given Query
 * @todo does NOT regard the realtion type in the search, uses all realtionstypes
 */
	public P2PGraph search(Query query) {
		//keeps track of the depth for every node, the shallowest depth is used for every node
		Hashtable queueNodesDepth = new Hashtable();
		
		P2PGraphImpl newExtGraph = new P2PGraphImpl();
		String searchedURI = query.getQueryTypeName();
		HashSet alreadyProcessedNodes = new HashSet();
		try {
			P2PGraphImpl extGraph = (P2PGraphImpl) this.clone();
			P2PNode searchedNode = extGraph.getP2PNode(searchedURI);
			int queryDepth = query.getDepth();

			LinkedList queueNodes = new LinkedList();
			LinkedList nodesList;
			String fromNodeURI,toNodeURI;
			Iterator edgesIt,nodesIt;
			P2PNode currGraphNode,tempGraphNode;
			P2PNode fromNode = null;
			P2PNode toNode = null;
			P2PEdge currEdgeObj;			
			int tempInt = 0;			
			boolean depthReached = false;

			//add searchNode
			queueNodes.add(searchedNode);
			queueNodesDepth.put(searchedNode,new Integer(0));			
			newExtGraph.addNode(searchedNode);

			while ((!queueNodes.isEmpty()) && !depthReached) {												
				currGraphNode = (P2PNode) queueNodes.remove(0);	

				tempInt = ((Integer) queueNodesDepth.get(currGraphNode)).intValue();
				if (tempInt < queryDepth) {
					nodesList = (LinkedList) extGraph.getOutboundEdgeNodes(currGraphNode);
	
					//add outbound nodes to the queue
					queueNodes.addAll(this.getNotProcessedNodesFrom(nodesList, alreadyProcessedNodes));
	
	
					//add the inbound nodes to the graph
					nodesIt = nodesList.iterator();
					while (nodesIt.hasNext()) {
						tempGraphNode = (P2PNode) nodesIt.next();
						newExtGraph.addNode(tempGraphNode);	
						tempInt = ((Integer) queueNodesDepth.get(currGraphNode)).intValue();
						tempInt++; 					
						queueNodesDepth.put(tempGraphNode,new Integer(tempInt));
					}
	
					edgesIt = extGraph.getInboundEdgeNodes(currGraphNode).iterator();
														
					while (edgesIt.hasNext()) {
						currEdgeObj = (P2PEdge) edgesIt.next();
						
						fromNodeURI = currEdgeObj.getFromNode().getIdentifier();
						toNodeURI = currEdgeObj.getToNode().getIdentifier();
						
						//make sure that the GraphNode has been added, if not, do it
						try {
							newExtGraph.getP2PNode(fromNodeURI);
						} catch (NoSuchP2PNodeException e) {
							fromNode = new P2PNodeImpl(fromNodeURI,null,null);
							//System.err.println("ERRORA create a new node cause it didn't exist:" + fromNodeURI);
							newExtGraph.addNode(fromNode);

						}
							
						try {
							newExtGraph.getP2PNode(toNodeURI);
						} catch (NoSuchP2PNodeException e) {
							//System.err.println("ERRORB create a new node cause it didn't exist:" + fromNodeURI);
							toNode = new P2PNodeImpl(toNodeURI,null,null);
							newExtGraph.addNode(toNode);	
						}
	
						P2PEdge tempEdge = new P2PEdgeImpl(fromNode,
															toNode,
															currEdgeObj.getEdgeType(),
															null,
															null);
						newExtGraph.addEdge(tempEdge);
					}				
				} else {
					depthReached = true;
				}
			}			
		} catch (NoSuchP2PNodeException e) {
			System.err.println("The node you searched for can not be found:" + searchedURI);
		} catch (GraphModificationException e) {
			System.err.println("Error");			
		} catch (NoSuchRelationLinkException e) {			
			System.err.println("No relation of that type found");			
		} catch (CloneNotSupportedException e) {
			System.err.println("This object can not be cloned");			
		}
		return newExtGraph;
	}

	private LinkedList getNotProcessedNodesFrom(LinkedList nodesList, HashSet alreadyProccesedNodes) {
		Iterator listIt = nodesList.iterator();
		LinkedList retVal = new LinkedList();
		P2PNode currGraphNode;
		
		while (listIt.hasNext()) {
			currGraphNode = (P2PNode) listIt.next();
			if (!alreadyProccesedNodes.contains(currGraphNode.getIdentifier())) {
				alreadyProccesedNodes.add(currGraphNode.getIdentifier());
				retVal.add(currGraphNode);
			}
		}
		return retVal;
	}
	
	public P2PNode getP2PNode(String nodeUri) throws NoSuchP2PNodeException {
		Iterator it = this.getNodesList().iterator();
		P2PNode currNode = null;
		while (it.hasNext()) {
			currNode = (P2PNode) it.next();
			if (currNode.getIdentifier().equals(nodeUri)) {
				return currNode;	
			}	
		}	
		throw new NoSuchP2PNodeException("Could not find a not with the name:" + nodeUri); 
	}

//TODO left to implement for printing
	public String toRDFString() {
		return null;
	}
}
