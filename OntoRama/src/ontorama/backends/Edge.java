package ontorama.backends;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Description: Edge between nodes. Edges correspong to relation links between concept types.
 * Copyright:    Copyright (c) 2001
 * Company: DSTC
 * @version 1.0
 */

public class Edge {

    /**
     * List of EdgeObjects
     */
    private List edges = null;


    /**
     *
     */
    public Edge() {
		this.edges = new LinkedList();
    }


    /**
     * @todo clearEdgesList and removeAllEdges are redunant?...check!
     */
    public void clearEdgesList() {
        this.edges = new LinkedList();
    }

    /**
     *
     */
    public void removeEdge(EdgeObject remEdge) {
        this.edges.remove(remEdge);
    }

    /**
     *
     */
    public void removeAllEdges() {
        this.edges.clear();
    }


	public Iterator getEdgeIterator() {
		return this.edges.iterator();	
	}

    /**
     *
     */
    public EdgeObject getEdge(GraphNode fromNode, GraphNode toNode, int relLink) {
        Iterator it = this.getOutboundEdges(fromNode, relLink);
        //Iterator it = Edge.getOutboundEdgeNodes(fromNode, relLink);
        while (it.hasNext()) {
            EdgeObject curEdge = (EdgeObject) it.next();
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
    public Iterator getOutboundEdges(GraphNode node) {
        return getEdges(node, true);
    }

    /**
     *
     */
    public Iterator getInboundEdges(GraphNode node) {
        return getEdges(node, false);
    }


  /**
     *
     */
    public List getOutboundEdgesList(GraphNode node,LinkedList relationLinks) {
        return getEdges(node, relationLinks,true);
    }

    /**
     *
     */
    public List getInboundEdgesList(GraphNode node,LinkedList relationLinks) {
        return getEdges(node, relationLinks,false);
    }


    /**
     *
     */
    public Iterator getOutboundEdges(GraphNode node, int relationType) {
        return getEdges(node, relationType, true);
    }


    /**
     *
     * @return iterator of Nodes
     */
    public Iterator getInboundEdgeNodes(GraphNode node, LinkedList relationLinks) {
        return getInboundEdgeNodesList(node, relationLinks).iterator();
    }

   /**
     *
     * @return iterator of Nodes
     */
    public Iterator getOutboundEdgeNodes(GraphNode node, LinkedList relationLinks) {
        return getOutboundEdgeNodesList(node, relationLinks).iterator();
    }


    /**
     *
     * @return iterator of Nodes
     */
    public List getOutboundEdgeNodesList(GraphNode node, LinkedList relationLinks) {
        return getEdgeNodes(node, relationLinks, true);
    }

 
    /**
     *
     */
    public Iterator getOutboundEdgeNodes(GraphNode node) {
        return getEdgeNodes(node, true);
    }

    /**
     *
     */
    public Iterator getInboundEdgeNodes(GraphNode node) {
        return getEdgeNodes(node, false);
    }


    /**
     *
     */
    public List getOutboundEdgeNodesList(GraphNode node) {
        return getEdgeNodesList(node, true);
    }

    /**
     *
     */
    public List getInboundEdgeNodesList(GraphNode node) {
        return getEdgeNodesList(node, false);
    }

    /**
     * Convenience method that returns iterator size
     * @param   iterator it
     * @return  int size
     * @todo  perhaps this method should 'live' in util package
     */
    public int getIteratorSize(Iterator it) {
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count = count + 1;
        }
        return count;
    }


	public int getNrOfEdges() {
		return	this.edges.size();
	}

    /**
     *
     */
    public void printAllEdges() {
        Iterator it = this.edges.iterator();
        while (it.hasNext()) {
            EdgeObject edgeObject = (EdgeObject) it.next();
            System.out.println(edgeObject);
        }
    }



    /**
     *
     */
    public void addEdge(EdgeObject edgeObject) {
        boolean isInList = false;
        Iterator it = edges.iterator();
        while (it.hasNext()) {
            EdgeObject cur = (EdgeObject) it.next();
            if ((edgeObject.getFromNode().equals(cur.getFromNode())) &&
                    (edgeObject.getToNode().equals(cur.getToNode())) &&
                    (edgeObject.getType() == cur.getType())) {
                // this edge is already registered
                //System.out.println("edge is already registered: " + edge);
                isInList = true;
            }
        }
        if (!isInList) {
            //System.out.println("adding edge " + edge);
            this.edges.add(edgeObject);
        }
    }





    /**
     *
     * @param  GraphNode node
     *         flag - true if we want to get list of outbound edges,
     *         false of we want to get a list of inbound edges.
     * @return iterator of Edges
     */
    private Iterator getEdges(GraphNode node, boolean flag) {
        List result = new LinkedList();
        Iterator it = this.edges.iterator();
        
        EdgeObject curEdgeObject;
        GraphNode graphNode;
        while (it.hasNext()) {
            curEdgeObject = (EdgeObject) it.next();
			graphNode = curEdgeObject.getNode(flag);
            //System.out.println("getEdges, cur edge node = \n\t" + graphNode + "\n\tnode = " + node);
            if (graphNode.equals(node)) {
                //System.out.println("FOUND " + curEdgeObject + " for nodes: " + node + " and " + graphNode);
                result.add(curEdgeObject);
            }
        }
        return result.iterator();
    }


    /**
     *
     * @param  GraphNode node
     *         Set relationType
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.      *
     * @return List of Edges
     */
    private List getEdges(GraphNode node, LinkedList relationLinks, boolean flag) {
        List result = new LinkedList();
		Iterator relIt;
        Iterator nodeEdgesObjectIt = getEdges(node, flag);
        //System.out.println("\nrelationType = " + relationType + ", node = " + node.getName());
        
        EdgeObject curEdgeObject;
        while (nodeEdgesObjectIt.hasNext()) {
            curEdgeObject = (EdgeObject) nodeEdgesObjectIt.next();
			//System.out.println("EDGE::curNode = " + curEdgeObject);
            relIt = relationLinks.iterator();
            while (relIt.hasNext()) {
                Integer curRel = (Integer) relIt.next();
				if (curEdgeObject.getType() == curRel.intValue()) {
                	//System.out.println("---adding cur to result");
                	result.add(curEdgeObject);
				}
            }
        }
        return result;
    }
    

    /**
     *
     * @param  GraphNode node
     *         int relationType
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.      *
     * @return iterator of Edges
     */
    private Iterator getEdges(GraphNode node, int relationType, boolean flag) {
        List result = new LinkedList();
        Iterator nodeEdgesObjectIt = getEdges(node, flag);
        //System.out.println("\nrelationType = " + relationType + ", node = " + node.getName());
        
        EdgeObject curEdgeObject;
        while (nodeEdgesObjectIt.hasNext()) {
            curEdgeObject = (EdgeObject) nodeEdgesObjectIt.next();
            //System.out.println("cur = " + cur);
            if (curEdgeObject.getType() == relationType) {
                //System.out.println("---adding cur to result");
                result.add(curEdgeObject);
            }
        }
        return result.iterator();
    }

    /**
     *
     * @param  GraphNode node
     *         Set relationLinks
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.
     * @return iterator of Nodes
     */
    private List getEdgeNodes(GraphNode node, LinkedList relationLinks, boolean flag) {
        List result = new LinkedList();
        Iterator nodeEdgesObjectIt = getEdges(node, flag);
	
		//System.err.println("getEdgeNodes for:" + node.getFullName());
	
		EdgeObject curEdgeObject;
		Iterator it;
        while (nodeEdgesObjectIt.hasNext()) {
            curEdgeObject = (EdgeObject) nodeEdgesObjectIt.next();
            it = relationLinks.iterator();
			//System.err.println("in getEdgesNodes with Edge:" + curEdgeObject);
            while (it.hasNext()) {
                Integer curRel = (Integer) it.next();
				//System.err.println("in getEdgesNodes with rel:" + curRel);
                if (curEdgeObject.getType() == curRel.intValue()) {
                	GraphNode tempNode = curEdgeObject.getNode(!flag);
					//System.err.println("Found a node to add: " + tempNode.getFullName());
                    result.add(tempNode);
                }
            }
        }
        return result;
    }


    /**
     *
     * @param  GraphNode node
     *         int relationType
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.      *
     * @return iterator of Nodes
     */
    private Iterator getEdgeNodes(GraphNode node, int relationType, boolean flag) {
        List result = new LinkedList();
        Iterator nodeEdgesObjectIt = getEdges(node, flag);
        
        EdgeObject curEdgeObject;
        while (nodeEdgesObjectIt.hasNext()) {
            curEdgeObject = (EdgeObject) nodeEdgesObjectIt.next();
            if (curEdgeObject.getType() == relationType) {
                result.add(curEdgeObject.getNode(!flag));
            }
        }
        return result.iterator();
    }


    /**
     *
     * @param  GraphNode node
     *         int relationType
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.      *
     * @return iterator of Nodes
     */
    private Iterator getEdgeNodes(GraphNode node, boolean flag) {
        return getEdgeNodesList(node, flag).iterator();
    }


    /**
     *
     */
    public Iterator getInboundEdges(GraphNode node, int relationType) {
        return getEdges(node, relationType, false);
    }


    /**
     *
     * @param  GraphNode node
     *         int relationType
     *         boolean flag - true if we want to get list of outbound nodes,
     *         false of we want to get a list of inbound nodes.      *
     * @return list of Nodes
     */
    private List getEdgeNodesList(GraphNode node, boolean flag) {
        List result = new LinkedList();
        Iterator nodeEdgesIt = getEdges(node, flag);

		EdgeObject curEdgeObject;
        while (nodeEdgesIt.hasNext()) {
            curEdgeObject = (EdgeObject) nodeEdgesIt.next();
            result.add(curEdgeObject.getNode(!flag));
        }
        return result;
    }

	public List getEdges() {
		return this.edges;
	}	

    /**
     *
     */
    public Iterator getOutboundEdgeNodes(GraphNode node, int relationType) {
        return getEdgeNodes(node, relationType, true);
    }
	
    /**
     *
     */
    public Iterator getInboundEdgeNodes(GraphNode node, int relationType) {
        return getEdgeNodes(node, relationType, false);
    }

/**
 * TODO
 * Methods not in use and that can be removed
 */
   /**
     *
     */
    public List AgetOutboundEdgesList(GraphNode node,LinkedList relationLinks) {
        return getEdges(node, relationLinks,true);
    }

    /**
     *
     */
    public List AgetInboundEdgesList(GraphNode node,LinkedList relationLinks) {
        return getEdges(node, relationLinks,false);
    }

 
    /**
     *
     * @return iterator of Nodes
     */
    public Iterator AgetInboundEdgeNodes(GraphNode node, LinkedList relationLinks) {
        return getOutboundEdgeNodesList(node, relationLinks).iterator();
    }

  /**
     *
     * @return iterator of Nodes
     */
    public List getInboundEdgeNodesList(GraphNode node, LinkedList relationLinks) {
        return getEdgeNodes(node, relationLinks, false);
    }

    /**
     *
     * @return iterator of Nodes
     */
    public List AgetOutboundEdgeNodesList(GraphNode node, LinkedList relationLinks) {
        return getEdgeNodes(node, relationLinks, true);
    }




}
