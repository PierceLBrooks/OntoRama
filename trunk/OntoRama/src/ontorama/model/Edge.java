package ontorama.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.Set;

import ontorama.OntoramaConfig;
import ontorama.model.GraphNode;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

/**
 * Description: Edge between nodes. Edges correspong to relation links between concept types.
 * Copyright:    Copyright (c) 2001
 * Company: DSTC
 * @version 1.0
 */

public class Edge {

    /**
     * inboundNode
     */
    GraphNode fromNode;

    /**
     * outboundNodes
     */
    GraphNode toNode;

    /**
     * type
     */
    int type;

    /**
     * list holding all edges
     */
    public static List edges = new LinkedList();


    /**
     *
     */
    public Edge (GraphNode fromNode, GraphNode toNode, int type) throws NoSuchRelationLinkException {
        if ( ! (OntoramaConfig.getRelationLinksSet()).contains(new Integer (type)) ) {
            throw new NoSuchRelationLinkException (type, OntoramaConfig.getRelationLinksSet().size());
        }
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.type = type;
        registerEdge(this);
    }

    /**
     *
     */
    public GraphNode getFromNode () {
        return this.fromNode;
    }

    /**
     *
     */
    public GraphNode getToNode () {
        return this.toNode;
    }

    /**
     *
     */
    public int getType () {
        return this.type;
    }

    /**
     *
     */
    private GraphNode getEdgeNode (boolean isFromNode) {
        if (isFromNode == true) {
            return this.fromNode;
        }
        return this.toNode;
    }

    /**
     *
     */
     private static void registerEdge (Edge edge) {
        boolean isInList = false;
        Iterator it = edges.iterator();
        while (it.hasNext()) {
            Edge cur = (Edge) it.next();
            if ( (edge.getFromNode().equals(cur.getFromNode()) ) &&
                    ( edge.getToNode().equals(cur.getToNode()) ) &&
                    ( edge.getType() == cur.getType()) ) {
                // this edge is already registered
				//System.out.println("edge is already registered: " + edge);
                isInList = true;
            }
        }
        if ( !isInList) {
            //System.out.println("adding edge " + edge);
            Edge.edges.add(edge);
        }
     }

	 /**
	  *
	  */
	 public static void clearEdgesList () {
		 edges = new LinkedList();
	 }

     /**
      *
      */
     public static void removeEdge (Edge remEdge) {
        Edge.edges.remove(remEdge);
     }

     /**
      *
      */
    public static void removeAllEdges() {
      Edge.edges.clear();
    }

     /**
      *
      */
      public static Iterator getOutboundEdges(GraphNode node) {
        return getEdges(node,true);
      }

     /**
      *
      */
      public static Iterator getInboundEdges(GraphNode node) {
        return getEdges(node,false);
      }


     /**
      *
      * @param  GraphNode node
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
            //System.out.println("getEdges, cur edge node = \n\t" + graphNode + "\n\tnode = " + node);
            if ( graphNode.equals(node)) {
                //System.out.println("FOUND " + cur + " for nodes: " + node + " and " + graphNode);
                result.add(cur);
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
      * @return iterator of Edges
      */
      private static Iterator getEdges(GraphNode node, int relationType, boolean flag) {
        List result = new LinkedList();
        Iterator nodeEdgesIt = getEdges(node, flag);
        while (nodeEdgesIt.hasNext()) {
            Edge cur = (Edge) nodeEdgesIt.next();
            if ( cur.getType() == relationType ) {
                result.add(cur);
            }
        }
        return result.iterator();
      }

      /**
       *
       */
     public static Iterator getOutboundEdges (GraphNode node, int relationType) {
        return getEdges(node,true);
     }

      /**
       *
       */
     public static Iterator getInboundEdges (GraphNode node, int relationType) {
        return getEdges(node,false);
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
      * @param  GraphNode node
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
                Integer curRel = (Integer) it.next();
                if ( cur.getType() == curRel.intValue() ) {
                    result.add(cur.getEdgeNode(!flag));
                }
            }
        }
        return result.iterator();
      }

    /**
     *
     */
    public static Iterator getOutboundEdgeNodes (GraphNode node, int relationType) {
        return getEdgeNodes (node, relationType, true);
    }

    /**
     *
     */
    public static Iterator getInboundEdgeNodes (GraphNode node, int relationType) {
        return getEdgeNodes (node, relationType, false);
    }




     /**
      *
      * @param  GraphNode node
      *         int relationType
      *         boolean flag - true if we want to get list of outbound nodes,
      *         false of we want to get a list of inbound nodes.      *
      * @return iterator of Nodes
      */
      private static Iterator getEdgeNodes(GraphNode node, int relationType, boolean flag) {
        List result = new LinkedList();
        Iterator nodeEdgesIt = getEdges(node,flag);
        while (nodeEdgesIt.hasNext()) {
            Edge cur = (Edge) nodeEdgesIt.next();
            if ( cur.getType() == relationType ) {
                result.add(cur.getEdgeNode(!flag));
            }
        }
        return result.iterator();
      }

      /**
       *
       */
     public static Iterator getOutboundEdgeNodes (GraphNode node) {
        return getEdgeNodes(node,true);
     }

      /**
       *
       */
     public static Iterator getInboundEdgeNodes (GraphNode node) {
        return getEdgeNodes(node,false);
     }


     /**
      *
      * @param  GraphNode node
      *         int relationType
      *         boolean flag - true if we want to get list of outbound nodes,
      *         false of we want to get a list of inbound nodes.      *
      * @return iterator of Nodes
      */
      private static Iterator getEdgeNodes(GraphNode node, boolean flag) {
//        //System.out.println("\tgetEdgeNodes method node = " + node.getName());
//        //System.out.println("\t getEdgeNodes method, node = " + node + ", flag = " + flag);
//        List result = new LinkedList();
//        Iterator nodeEdgesIt = getEdges(node,flag);
//        while (nodeEdgesIt.hasNext()) {
//            Edge cur = (Edge) nodeEdgesIt.next();
//            //System.out.println("\t\tedge = " + cur);
//            //System.out.println("\t\t" + cur.getEdgeNode(!flag).getName());
//            result.add(cur.getEdgeNode(!flag));
//        }
//        return result.iterator();
          return getEdgeNodesList(node, flag).iterator();
      }

      /**
       *
       */
     public static List getOutboundEdgeNodesList (GraphNode node) {
        return getEdgeNodesList(node,true);
     }

      /**
       *
       */
     public static List getInboundEdgeNodesList (GraphNode node) {
        return getEdgeNodesList(node,false);
     }


     /**
      *
      * @param  GraphNode node
      *         int relationType
      *         boolean flag - true if we want to get list of outbound nodes,
      *         false of we want to get a list of inbound nodes.      *
      * @return list of Nodes
      */
      private static List getEdgeNodesList(GraphNode node, boolean flag) {
        List result = new LinkedList();
        Iterator nodeEdgesIt = getEdges(node,flag);
        while (nodeEdgesIt.hasNext()) {
            Edge cur = (Edge) nodeEdgesIt.next();
            result.add(cur.getEdgeNode(!flag));
        }
        return result;
      }

    /**
     * Convenience method that returns iterator size
     * @param   iterator it
     * @return  int size
     */
    public static int getIteratorSize (Iterator it) {
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
     public String toString() {
        String str = "Edge from '" + this.fromNode.getName() + "' to '" + this.toNode.getName() + "', type = " + type;
        //String str = "Edge from '" + this.fromNode.getName() + "' = " + this.fromNode +  " to '" + this.toNode.getName() + "' = " + this.toNode + ", type = " + type;
        return str;
     }

     /**
      *
      */
      public static void printAllEdges () {
        Iterator it = edges.iterator();
        while (it.hasNext()) {
            Edge edge = (Edge) it.next();
            System.out.println(edge);
        }
      }


}
