
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
import java.util.Enumeration;
import java.util.Hashtable;

import ontorama.*;
import ontorama.webkbtools.query.*;
import ontorama.webkbtools.datamodel.*;
import ontorama.webkbtools.util.*;
import ontorama.util.Debug;


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
     */
     Debug debug = new Debug(false);

    /**
     *
     * @todo: should never return null!!! need to introduce exception chain up to the parser??
     * @todo:  replace parseTypeToNode(ot,1) below with something more meaninfull (instead
     * of hardcoding int=1 use iterator on a set of relation types)
     */
    public GraphBuilder(QueryResult queryResult)
                throws NoSuchRelationLinkException, NoTypeFoundInResultSetException,
                NoSuchPropertyException {

        String termName = queryResult.getQuery().getQueryTypeName();
        Iterator ontIterator = queryResult.getOntologyTypesIterator();
        processedTypes = new LinkedList();
        try {
          while (ontIterator.hasNext()) {
              OntologyType ot = (OntologyTypeImplementation) ontIterator.next();

              Set relationLinksSet = OntoramaConfig.getRelationLinksSet();
              Iterator relationLinksIterator = relationLinksSet.iterator();
              while (relationLinksIterator.hasNext()) {
                Integer nextRelLink = (Integer) relationLinksIterator.next();
                parseTypeToNode(ot,nextRelLink.intValue());
              }
              makeEdges(ot,termName);
          }

          GraphNode root = (GraphNode) nodes.get(termName);
          if (root == null) {
            throw new NoTypeFoundInResultSetException(termName);
          }
          Collection collection = nodes.values();
          Iterator it = collection.iterator();
          while (it.hasNext()) {
            GraphNode n = (GraphNode) it.next();
            debug.message("GraphBuilder","constructor", n.getName());
          }
          graph = new Graph( collection, root, edgeRoot );
        }
        catch (NoSuchRelationLinkException e) {
            throw e;
        }
        catch (NoSuchPropertyException e2) {
            throw e2;
        }
    }

    /**
     *
     */
    private void makeEdges (OntologyType ot,String rootName) throws NoSuchRelationLinkException, NoSuchPropertyException {
        if ( processedTypes.contains(ot)) {
            return;
        }
        processedTypes.add(ot);

        GraphNode node = (GraphNode) processedNodes.get(ot.getName());
        if (node == null) {
            node = new GraphNode (ot.getName());
            processedNodes.put(ot.getName(), node);
        }
        //node.setDescription(ot.getDescription());
        //node.setCreator(ot.getCreator());
        Hashtable conceptPropertiesConfig = OntoramaConfig.getConceptPropertiesTable();
        Enumeration e = conceptPropertiesConfig.keys();
        while (e.hasMoreElements()) {
            String propertyName = (String) e.nextElement();
            if (ot.getTypeProperty(propertyName) != null) {
                node.setProperty(propertyName, ot.getTypeProperty(propertyName));
            }
        }
        //node.setDescription(ot.getTypeProperty("description"));
        //node.setCreator(ot.getTypeProperty("creator"));

        if (rootName.equals(node.getName())) {
            edgeRoot = node;
            System.out.println();
            debug.message("GraphBuilder","makeEdges","edgeRoot = " + edgeRoot.getName());
            System.out.println();
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
     * @todo    remove this method
     */
    private void parseTypeToNode(OntologyType ot, int relLink ) throws NoSuchRelationLinkException, NoSuchPropertyException {

        // find node with name
        String nodeName = ot.getName();
        GraphNode conceptNode = (GraphNode)nodes.get( nodeName );
        // if not found --> create one
        if( conceptNode == null ) {
            conceptNode = new GraphNode( nodeName );
            // add child to hashtable
            nodes.put(nodeName, conceptNode );
        }
        //conceptNode.setDescription(ot.getDescription());
        //conceptNode.setCreator(ot.getCreator());
        Hashtable conceptPropertiesConfig = OntoramaConfig.getConceptPropertiesTable();
        Enumeration e = conceptPropertiesConfig.keys();
        while (e.hasMoreElements()) {
            String propertyName = (String) e.nextElement();
            if (ot.getTypeProperty(propertyName) != null) {
                conceptNode.setProperty(propertyName, ot.getTypeProperty(propertyName));
            }
        }

        //conceptNode.setDescription(ot.getTypeProperty("description"));
        //conceptNode.setCreator(ot.getTypeProperty("creator"));

        //get children

        Iterator it = ot.getIterator(relLink);
        while(it.hasNext()) {
            OntologyType child = (OntologyType) it.next();

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
