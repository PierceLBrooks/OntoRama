package ontorama.model;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

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

/**
 * GraphBuilder is responsible for building a collection of GraphNodes and Edges that form a Graph.
 * GraphNodes are built from OntologyTypes and Edges are bult from RelationLinks attached to Ontology Type.
 * GraphBuilder is given a QueryResult (that contains OntologyTypes Iterator)
 *
 * <p>
 * Copyright:    Copyright (c) 2002
 * <br>
 * Company:     DSTC
 */
public class GraphBuilder {

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
     * graph root
     */
    private GraphNode edgeRoot = null;

    /**
     *
     */
     Debug debug = new Debug(false);

    /**
     * Build Graph from given QueryResult.
     *
     * @param   queryResult
     * @throws  NoSuchRelationLinkException
     * @throws  NoTypeFoundInResultSetException
     * @throws  NoSuchPropertyException
     * @todo: should never return null!!! need to introduce exception chain up to the parser??
     * @todo    query returns an iterator of ontology types. some of those types may
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
     */
    public GraphBuilder(QueryResult queryResult)
                throws NoSuchRelationLinkException, NoTypeFoundInResultSetException,
                NoSuchPropertyException {

        // termName is a query term, node for this ontology type is graph root
        String termName = queryResult.getQuery().getQueryTypeName();
        // get Iterator of OntologyTypes
        Iterator ontIterator = queryResult.getOntologyTypesIterator();
        processedTypes = new LinkedList();
        try {
          while (ontIterator.hasNext()) {
              OntologyType ot = (OntologyTypeImplementation) ontIterator.next();
              makeEdges(ot,termName);
          }
          System.out.println("edgeRoot = " + edgeRoot);

          if (! processedNodes.containsKey(termName)) {
             throw new NoTypeFoundInResultSetException(termName);
           }

          graph = new Graph( edgeRoot );


        }
        catch (NoSuchRelationLinkException e) {
            throw e;
        }
        catch (NoSuchPropertyException e2) {
            throw e2;
        }
    }

    /**
     * Process given OntologyType, create Node for it and all corresponding Edges.
     *
     * @param   ontologyType, rootName
     */
    private void makeEdges (OntologyType ot,String rootName) throws NoSuchRelationLinkException, NoSuchPropertyException {
        if ( processedTypes.contains(ot)) {
            return;
        }
        processedTypes.add(ot);

        // make GraphNode and set available properties for it.
        GraphNode node = (GraphNode) processedNodes.get(ot.getName());
        //System.out.println("----processing ot = " + ot.getName());
        if (node == null) {
            node = new GraphNode (ot.getName());
            processedNodes.put(ot.getName(), node);
        }
        Hashtable conceptPropertiesConfig = OntoramaConfig.getConceptPropertiesTable();
        Enumeration e = conceptPropertiesConfig.keys();
        while (e.hasMoreElements()) {
            String propertyName = (String) e.nextElement();
            if (node.getName().endsWith("Mongoose")) {
              System.out.println ("node = " + node.getName() + ", setting propName = " + propertyName + ", value = " + ot.getTypeProperty(propertyName));
            }
            node.setProperty(propertyName, ot.getTypeProperty(propertyName));
        }

        // check if this is root
        if (rootName.equals(node.getName())) {
            edgeRoot = node;
            debug.message("GraphBuilder","makeEdges","edgeRoot = " + edgeRoot.getName());
        }

        // Go through relation links and make Edges
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
      * Get Graph
      *
      * @return graph
      */
    public Graph getGraph() {
        return graph;
    }
}
