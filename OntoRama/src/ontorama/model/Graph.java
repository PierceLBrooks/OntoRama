package ontorama.model;

/**
 * Graph takes an acyclic graph and converts it into a tree.
 * Graph removes all duplicate parents by cloning the children (Removes all
 * duplicate inbound edges by cloning outbound edges)
 *
 * Graph holds the root node of the graph.
 *
 */

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Hashtable;
import java.util.Enumeration;

import java.util.ConcurrentModificationException;

import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.*;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.util.Debug;


public class Graph implements GraphInterface {

    /**
     * root node
     */
    private GraphNode root;

    /**
     * debug vars
     */
    Debug debug = new Debug(false);

    /**
     * Create a Graph with given root
     *
     * @param root
     */
    public Graph( GraphNode root) {
        this.root = root;

        debug.message("Graph","constructor","before convertIntoTree testIfTree(): " + testIfTree(root));
        debug.message("Graph","constructor","before convertIntoTree number of edges: " + Edge.getIteratorSize( Edge.edges.iterator()));
        try {
            //System.out.println( printXml());
            convertIntoTree(root);
            root.calculateDepths();
        }
        catch (NoSuchRelationLinkException e ) {
            System.out.println("NoSuchRelationLinkException: " + e.getMessage());
            System.exit(-1);
        }
        debug.message("Graph","constructor","after convertIntoTree testIfTree(): " + testIfTree(root));
        debug.message("Graph","constructor","after convertIntoTree number of edges: " + Edge.getIteratorSize( Edge.edges.iterator()));
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
     * Test if current Graph is a Tree
     *
     * @param root  - root GraphNode
     */
    private boolean testIfTree (GraphNode root) {
        LinkedList queue = new LinkedList();
        boolean isTree = true;

        queue.add(root);

        while ( !queue.isEmpty()) {
            GraphNode nextQueueNode = (GraphNode) queue.remove(0);
            Iterator allOutboundNodes = Edge.getOutboundEdgeNodes(nextQueueNode);

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
     * @throws  NoSuchRelationLinkException
     */
    private void convertIntoTree (GraphNode root) throws NoSuchRelationLinkException {
        LinkedList queue = new LinkedList();
        boolean isTree = true;

        queue.add(root);

        while ( !queue.isEmpty()) {
            GraphNode nextQueueNode = (GraphNode) queue.remove(0);

            debug.message("Graph", "convertIntoTree", "--- processing node " + nextQueueNode.getName() + " -----");

            Iterator allOutboundNodes = Edge.getOutboundEdgeNodes(nextQueueNode);

            while (allOutboundNodes.hasNext()) {
                GraphNode curNode = (GraphNode) allOutboundNodes.next();
                queue.add(curNode);
                int[] typeCount = getTypeCount(Edge.getInboundEdges(curNode));
                for (int i = 0; i < typeCount.length; i++) {
                    while (typeCount[i] > 1) {
                        debug.message("Graph", "convertIntoTree", "current node " + curNode.getName() + " has multiple inbound edges, num = " + typeCount[i] + " for type = " + i);

                        // clone the node
                        GraphNode cloneNode = curNode.makeClone();

                        // add edge from cloneNode to a NodeParent with this rel type and
                        // remove edge from curNode to a NodeParent with this rel type
                        Iterator inboundEdges = Edge.getInboundEdges(curNode, i);
                        if (inboundEdges.hasNext()) {
                            Edge firstEdge = (Edge) inboundEdges.next();
                            Edge newEdge = new Edge (firstEdge.getFromNode(), cloneNode, i);
                            Edge.removeEdge(firstEdge);
                        }

                        // copy/clone all structure below
                        deepCopy(curNode, cloneNode);

                        // indicate that we processed one edge
                        typeCount[i]--;
                    }
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
     * @throws   NoSuchRelationLinkException
     */
    private void deepCopy (GraphNode node, GraphNode cloneNode ) throws NoSuchRelationLinkException {

        Iterator outboundEdgesIterator = Edge.getOutboundEdges(node);

        while (outboundEdgesIterator.hasNext()) {
            Edge curEdge = (Edge) outboundEdgesIterator.next();
            GraphNode toNode = curEdge.getToNode();
            GraphNode cloneToNode = toNode.makeClone();
            Edge newEdge = new Edge(cloneNode,cloneToNode,curEdge.getType());
            deepCopy(toNode, cloneToNode);
        }
    }

    /**
     *
     */
    private int[] getTypeCount (Iterator it) {
        int[] typeCount = new int[ontorama.OntoramaConfig.getRelationLinksSet().size()];
        for (int i = 0; i < typeCount.length; i++ ) {
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
    public String printXml () {
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
     *
     */
    private String printXmlConceptTypesEl () {
        String tab = "\t";
        String resultStr = tab + "<conceptTypes>";
        resultStr = resultStr + "\n";
        LinkedList queue = new LinkedList();
        queue.add(root);

        while ( !queue.isEmpty()) {
            GraphNode nextQueueNode = (GraphNode) queue.remove(0);
            Iterator allOutboundNodes = Edge.getOutboundEdgeNodes(nextQueueNode);

            while (allOutboundNodes.hasNext()) {
                GraphNode curNode = (GraphNode) allOutboundNodes.next();
                queue.add(curNode);

                resultStr =  resultStr + tab + tab +"<conceptType name='" + curNode.getName() + "'>";
                resultStr = resultStr + "\n";

                Hashtable conceptPropertiesConfig = OntoramaConfig.getConceptPropertiesTable();
                Enumeration e = conceptPropertiesConfig.keys();
                while (e.hasMoreElements()) {
                    String propName = (String) e.nextElement();
                    String propValue = (String) curNode.getProperty(propName);
                    if (propValue != null) {
                        resultStr = resultStr + tab + tab + tab + "<" + propName + ">";
                        resultStr = resultStr + propValue;
                        resultStr = resultStr + "</" + propName + ">";
                        resultStr = resultStr + "\n";
                    }
                }
                resultStr = resultStr + tab + tab + "</conceptType>";
                resultStr = resultStr + "\n";
            }
        }
        resultStr = resultStr + tab + "</conceptTypes>";
        resultStr = resultStr + "\n";
        return resultStr;
    }

    /**
     *
     */
    private String printXmlRelationLinksEl () {
        String tab = "\t";
        String resultStr = tab + "<relationLinks>";
        resultStr = resultStr + tab + "\n";

        Iterator edgesIterator = Edge.edges.iterator();
        while (edgesIterator.hasNext()) {
            Edge curEdge = (Edge) edgesIterator.next();
            GraphNode fromNode = curEdge.getFromNode();
            GraphNode toNode = curEdge.getToNode();
            int type = curEdge.getType();
            RelationLinkDetails relLinkDetails = OntoramaConfig.getRelationLinkDetails(type);
            resultStr = resultStr + tab + tab + "<relationLink";
            resultStr = resultStr + " name='" + relLinkDetails.getLinkName() + "'";
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