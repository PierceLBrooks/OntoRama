
package ontorama.model;


import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

import java.io.File;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

import ontorama.*;
import ontorama.webkbtools.query.*;
import ontorama.webkbtools.datamodel.*;
import ontorama.webkbtools.util.*;


public class GraphBuilder {

    /**
     * Hold the node in the hashtable by there name.
     */
    private Hashtable nodes = new Hashtable();
    private Graph graph = null;

    private String filename;

    private Set relationLinksSet = OntoramaConfig.getRelationLinksSet();

    private List processedTypes = new LinkedList();
    private Hashtable processedNodes = new Hashtable();

    private GraphNode edgeRoot = null;

    /**
     *
     * @todo: should never return null!!! need to introduce exception chain up to the parser??
     * @todo:  replace parseTypeToNode(ot,1) below with something more meaninfull (instead
     * of hardcoding int=1 use iterator on a set of relation types)
     */
    public GraphBuilder(QueryResult queryResult)
                throws NoSuchRelationLinkException, NoTypeFoundInResultSetException {


        String termName = queryResult.getQuery().getQueryTypeName();
        Iterator ontIterator = queryResult.getOntologyTypesIterator();
        processedTypes = new LinkedList();
        try {
          while (ontIterator.hasNext()) {
              OntologyType ot = (OntologyTypeImplementation) ontIterator.next();
                //Iterator relLinks = OntoramaConfig.getRelationLinksSet().iterator();
                //while (relLinks.hasNext()) {
                    //Integer relLink = (Integer) relLinks.next();
                      //parseTypeToNode(ot,relLink.intValue());

                      //parseTypeToNode(ot,OntoramaConfig.SUBTYPE);
                      parseTypeToNode(ot,1);


                      //parseTypeToNode(ot,OntoramaConfig.SUPERTYPE);

                      makeEdges(ot,termName);

                //}
              //parseTypeToEdge(ot);

          }

          GraphNode root = (GraphNode) nodes.get(termName);
          if (root == null) {
            throw new NoTypeFoundInResultSetException(termName);
          }
          Collection collection = nodes.values();
          Iterator it = collection.iterator();
          while (it.hasNext()) {
            GraphNode n = (GraphNode) it.next();
            System.out.println(n.getName());
          }
          graph = new Graph( collection, root, edgeRoot );
        }
        catch (NoSuchRelationLinkException e) {
            throw e;
        }
        //catch (ClassNotFoundException ce) {
        //    System.out.println("ClassNotFoundException: " + ce);
        //    System.exit(1);
        //}
        //catch (Exception e) {
        //    System.out.println("Exception: " + e);
        //}
    }

    /**
     *
     */
    private void makeEdges (OntologyType ot,String rootName) throws NoSuchRelationLinkException {
        if ( processedTypes.contains(ot)) {
            return;
        }
        processedTypes.add(ot);

        GraphNode node = (GraphNode) processedNodes.get(ot.getName());
        if (node == null) {
            node = new GraphNode (ot.getName());
            processedNodes.put(ot.getName(), node);
        }

        if (rootName.equals(node.getName())) {
        //if (processedTypes.size() == 1) {
        //if (processedTypes.size() == 1) {
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("edgeRoot = " + node.getName());
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();

            edgeRoot = node;
        }

        Iterator relLinks = OntoramaConfig.getRelationLinksSet().iterator();
        while (relLinks.hasNext()) {
            Integer relLink = (Integer) relLinks.next();
            Iterator relatedTypes = ot.getIterator(relLink.intValue());
            while (relatedTypes.hasNext()) {
                OntologyType relatedType = (OntologyType) relatedTypes.next();

                GraphNode relNode = (GraphNode) processedNodes.get(relatedType.getName());
                if (relNode == null) {
                    relNode = new GraphNode (relatedType.getName());
                    processedNodes.put(relatedType.getName(), relNode);
                }
                new Edge(node,relNode,relLink.intValue());
                makeEdges(relatedType, rootName);
            }
        }
    }


    /**
     *
     */
     private void parseTypeToEdge (OntologyType ot) throws NoSuchRelationLinkException {


        GraphNode outboundNode = new GraphNode (ot.getName());
        /*
        if (outboundNode.getName() == termName) {
            root = outboundNode;
        }
        */

        Iterator allRelationLinks = relationLinksSet.iterator();
        while (allRelationLinks.hasNext()) {
            Integer curRel = (Integer) allRelationLinks.next();

            Iterator typeRelations = ot.getIterator(curRel.intValue());
            while (typeRelations.hasNext()) {
                OntologyType inboundType = (OntologyType) typeRelations.next();
                GraphNode inboundNode = new GraphNode (inboundType.getName());
                new Edge (outboundNode, inboundNode, curRel.intValue());
            }
        }
     }

    /**
     * Read nodes into hashtable
     */
    private void parseTypeToNode(OntologyType ot, int relLink ) throws NoSuchRelationLinkException {
        System.out.println("--parseTypeToNode--" + ot + ", relLink = " + relLink);

        // find node with name
        String nodeName = ot.getName();
        GraphNode conceptNode = (GraphNode)nodes.get( nodeName );
        // if not found --> create one
        if( conceptNode == null ) {
            conceptNode = new GraphNode( nodeName );
            // add child to hashtable
            nodes.put(nodeName, conceptNode );
        }
        //get children

        //Iterator it = ot.getIterator(OntoramaConfig.SUBTYPE);
        Iterator it = ot.getIterator(relLink);
        while(it.hasNext()) {
            OntologyType child = (OntologyType) it.next();
            System.out.println("........................child = " + child);

            // find node with name of child
            nodeName = child.getName();
            GraphNode childNode = (GraphNode)nodes.get( nodeName );
            // if not found --> create one
            if( childNode == null ) {
                childNode = new GraphNode( nodeName );
                // add child to hashtable\
                nodes.put(nodeName, childNode );
            }
            // add child to outer node
            if( !conceptNode.hasChild(childNode)) {
              conceptNode.addChild(childNode);
            }
            if( !childNode.hasParent( conceptNode ) ){
              childNode.addParent(conceptNode);
            }
        }
    }

    public Graph getGraph() {
        return graph;
    }
}
