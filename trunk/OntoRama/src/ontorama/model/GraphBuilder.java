
package ontorama.model;


import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

import java.io.File;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

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

    /**
     *
     * @todo: should never return null!!! need to introduce exception chain up to the parser??
     */
    public GraphBuilder(QueryResult queryResult)
                throws NoSuchRelationLinkException, NoTypeFoundInResultSetException {
        String termName = queryResult.getQuery().getQueryTypeName();
        Iterator ontIterator = queryResult.getOntologyTypesIterator();
        try {
          while (ontIterator.hasNext()) {
              OntologyType ot = (OntologyTypeImplementation) ontIterator.next();
              //System.out.println("ot = " + ot);
              parseTypeToNode(ot);
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
          graph = new Graph( collection, root );
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
     * Read nodes into hashtable
     */
    private void parseTypeToNode(OntologyType ot) throws NoSuchRelationLinkException {
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
        Iterator it = ot.getIterator(OntoramaConfig.SUBTYPE);
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
            //conceptNode.addChild(childNode);
            //childNode.addParent(conceptNode);
        }
    }

    public Graph getGraph() {
        return graph;
    }
}
