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
import java.util.Collection;

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
     * NOTE: query returns an iterator of ontology types. some of those types may
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
     *  (We will call those unconnected nodes 'hanging' nodes).
     *  One way to get around it is: to remove edges that don't have incoming edges (parents).
     *  This is our solution for the present.
     *  However, this solution may not be beneficial for some ontologies (that are not strict
     *  hierarchy. Some of RDF examples found on the web are such ontologies). Solution could be:
     *  instead of removing 'hanging' nodes - introduce artafficial root node and attach all these nodes
     *  to it.
     *
     * @param   queryResult
     * @throws  NoSuchRelationLinkException
     * @throws  NoTypeFoundInResultSetException
     * @throws  NoSuchPropertyException
     * @todo  should never return null!!! need to introduce exception chain up to the parser??
     * @todo consider moving all code in this class into Graph. Graph should get
     *        ontology types Iterator and build GraphNodes and Edges from it. This
     *        way graph would be responsible for clearing out edges if need be.
     */
    public GraphBuilder(QueryResult queryResult)
                throws NoSuchRelationLinkException, NoTypeFoundInResultSetException,
                NoSuchPropertyException {
        debug.message("******************* GraphBuilder constructor start *******************");

        // termName is a query term, node for this ontology type is graph root
        String termName = queryResult.getQuery().getQueryTypeName();
        // get Iterator of OntologyTypes
        Iterator ontIterator = queryResult.getOntologyTypesIterator();

        processedTypes = new LinkedList();

        // remove all edges before building new set of edges
        Edge.removeAllEdges();

        try {
          while (ontIterator.hasNext()) {
              OntologyType ot = (OntologyTypeImplementation) ontIterator.next();
              makeEdges(ot,termName);
          }

          // clean up before we create a graph
          int lastNumOfEdges = -1;
          debug.message("number of Edges = " + Edge.edges.size());
          while (Edge.edges.size() != lastNumOfEdges) {
            debug.message("number of Edges = " + Edge.edges.size() + ", lastNumOfEdges = " + lastNumOfEdges);
            lastNumOfEdges = Edge.edges.size();
            cleanUpEdges();
          }
          debug.message("number of Edges = " + Edge.edges.size());
          debug.message("edgeRoot = " + edgeRoot);

          if (! processedNodes.containsKey(termName)) {
             throw new NoTypeFoundInResultSetException(termName);
          }
          debug.message("graph builder, num of nodes = " + processedNodes.size());

          graph = new Graph( edgeRoot );


        }
        catch (NoSuchRelationLinkException e) {
            throw e;
        }
        catch (NoSuchPropertyException e2) {
            throw e2;
        }
        debug.message("******************* GraphBuilder constructor end *******************");

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
        debug.message("----processing ot = " + ot);
        if (node == null) {
            node = new GraphNode (ot.getName());
            processedNodes.put(ot.getName(), node);
        }
        debug.message("\t corresponding node = " + node);
        Hashtable conceptPropertiesConfig = OntoramaConfig.getConceptPropertiesTable();
        Enumeration e = conceptPropertiesConfig.keys();
        while (e.hasMoreElements()) {
            String propertyName = (String) e.nextElement();
            node.setProperty(propertyName, ot.getTypeProperty(propertyName));
        }

        // check if this is root
        if (rootName.equals(node.getName())) {
            edgeRoot = node;
            debug.message("GraphBuilder edgeRoot = " + edgeRoot.getName());
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
                debug.message("\t edge: " + node + " -> " + relNode + " , type = " + relLink.intValue());
                makeEdges(relatedType, rootName);
            }
        }
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
  private void cleanUpEdges () {
    Iterator allNodes = processedNodes.values().iterator();

    while (allNodes.hasNext()) {
      GraphNode curNode = (GraphNode) allNodes.next();

      if (curNode == edgeRoot) {
        //System.out.println ("-------curNode is ROOT");
        continue;
      }

      //System.out.println("------------curNode = " + curNode);

      // get inbound nodes (parents) and check how many there is.
      // If there is no parents - this node is not attached
      // to anything, hence - 'hanging node'
      Iterator inboundNodes = Edge.getInboundEdges(curNode);
      if (Edge.getIteratorSize(inboundNodes) == 0 ) {

        //System.out.println("size of inbound edges == 0");

        // get outbound edges for this node and remove them
        Iterator curOutEdges = Edge.getOutboundEdges(curNode);
        while (curOutEdges.hasNext()) {
          Edge curEdge = (Edge) curOutEdges.next();
          //System.out.println("curEdge = " + curEdge);
          Edge.removeEdge(curEdge);
        }
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
