package ontorama.backends;

import java.util.Iterator;

/** 
 * This class handles how the merging should be done.
 * The information that can be used (among others) are:
 * Information in the P2PGraphNodes (creator, creationDate, URI, descr, etc)
 * Information in the P2PEdges (creator, creationDate, type, fromNode,toNode)
 */ 
public class Rules {

	private ExtendedGraph extGraph = null;

	public Rules (ExtendedGraph extGraph) {
		this.extGraph = extGraph;		
	}


	/**
	 * Rules for adding an edge
	 * true IFF
	 * newEdge != <all Edges in RejectedEdge>
	 * 
	 * @param newEdge the edge we want to add
	 */
	public boolean checkAddEdge(EdgeObject newEdgeObject) {
		boolean retVal;
		if (extGraph.isThisRejected(newEdgeObject)) {
			retVal = false;
		} else {
			retVal = true;	
		}
		return retVal;	
	}
	
	public boolean checkAddNode(GraphNode newNode) {
		//Should later containfs a rule that says that you are not allowed 
		//to add a node if it's rejected
		return true;		
	}
	
	/**
	 * See if there is something that conflicts if we set the give Edge as 
	 * rejected (depending of the rules) 
	 * Set Edge rejected IFF
	 * the edge you want to reject already exist and are "valid"
	 */	
	public boolean checkRejectEdge(EdgeObject rejectEdge) {
		Iterator edgesIt = extGraph.getEdge().getEdgeIterator();
		while (edgesIt.hasNext()) {
			EdgeObject curr = (EdgeObject) edgesIt.next();
			if ((curr.getFromNode().equals(rejectEdge.getFromNode())) &&
			   (curr.getToNode().equals(rejectEdge.getToNode())) && 
			   (curr.getType() == rejectEdge.getType())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkUpdateNode(GraphNode newNode) {
		if (extGraph.getAllNodes().containsKey(newNode.getFullName())) {
			return true;		
		} else {
			return false;	
		}
	}	

}
