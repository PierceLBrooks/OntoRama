package ontorama.webkbtools.writer.rdf;

import ontorama.webkbtools.writer.ModelWriter;
import ontorama.webkbtools.writer.ModelWriterException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.model.*;
import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.RdfMapping;

import java.io.Writer;
import java.util.*;

import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.common.ResourceImpl;
import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;
import com.hp.hpl.mesa.rdf.jena.vocabulary.RDFS;
import com.hp.hpl.mesa.rdf.jena.vocabulary.RDF;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 4/10/2002
 * Time: 09:27:15
 * To change this template use Options | File Templates.
 */

public class RdfModelWriter implements ModelWriter {
    private Graph _graph;

    /**
     * contains mapping of processed nodes to created rdf resources
     * keys - nodes
     * values - rdf resources
     */
    private Hashtable _nodeToResource;

    /**
     * contains a list of processed edges that have been added to rdf model
     */
    private List _processedEdges;

    private List _processedNodes;

    public void write(Graph graph, Writer out) throws ModelWriterException {
        _graph = graph;
        _nodeToResource = new Hashtable();
        _processedEdges = new LinkedList();
        _processedNodes = new LinkedList();

        try {
            Model rdfModel = toRDFModel();
            rdfModel.write(out);
        } catch (RDFException rdfExc) {
            rdfExc.printStackTrace();
            throw new ModelWriterException("Couldn't create RDF model " + rdfExc.getMessage());
        } catch (NoSuchRelationLinkException relExc) {
            relExc.printStackTrace();
            throw new ModelWriterException(relExc.getMessage());
        }

    }


    protected Model toRDFModel() throws RDFException, NoSuchRelationLinkException {
        ModelMem rdfModel = new ModelMem();


        List nodesList = _graph.getNodesList();
        List edgesList = _graph.getEdgesList();

        Hashtable edgeTypesToRdfMapping = mapEdgeTypesToRdfTags();

        Iterator edgesIterator = edgesList.iterator();
        while (edgesIterator.hasNext()) {
            Edge curEdge = (Edge) edgesIterator.next();
            Node fromNode = curEdge.getFromNode();
            Resource subject = getResource(fromNode, rdfModel);
            EdgeType edgeType = curEdge.getEdgeType();
            RdfMapping rdfMapping = (RdfMapping) edgeTypesToRdfMapping.get(edgeType);
            String rdfTag = (String) rdfMapping.getRdfTags().get(0);
            Property predicate = new PropertyImpl(edgeType.getNamespace() + rdfTag);
            if (rdfMapping.getType().equals(edgeType.getName()) ) {
                Node toNode = curEdge.getToNode();
                if (toNode.getIdentifier().equals(toNode.getName())) {
                    /// @todo this is not a good test (testing if node should
                    // be resource or literal). this probably should be reflected
                    // in a node type object
                    rdfModel.add(subject, predicate, curEdge.getToNode().getName());
                }
                else {
                    Resource object = getResource(curEdge.getToNode(), rdfModel);
                    rdfModel.add(subject, predicate, object);
                }
                _processedNodes.add(fromNode);
            }
            else if (rdfMapping.getType().equals(edgeType.getReverseEdgeName())) {
                Resource object = getResource(curEdge.getToNode(), rdfModel);
                rdfModel.add(object, predicate, subject);
                _processedNodes.add(curEdge.getToNode());
            }
        }

        /// @todo need to be able to handle unconnected nodes.
        // stubs are in the commented out below.

//        Iterator nodesIterator = nodesList.iterator();
//        while (nodesIterator.hasNext()) {
//            Node curNode = (Node) nodesIterator.next();
//            Resource curResource = getResource(curNode);
//            boolean needToAddToModel = false;
//            if (! _processedNodes.contains(curNode)) {
//                needToAddToModel = true;
//            }
//            if (needToAddToModel) {
//                rdfModel.createResource(curResource);
//                _processedNodes.add(curNode);
//            }
//        }
        return rdfModel;
    }

    private Hashtable mapEdgeTypesToRdfTags() throws NoSuchRelationLinkException {
        Hashtable result = new Hashtable();
        List rdfMappingList = OntoramaConfig.getRelationRdfMapping();
        Iterator it = rdfMappingList.iterator();
        while (it.hasNext()) {
            RdfMapping rdfMapping = (RdfMapping) it.next();
            String edgeTypeName = rdfMapping.getType();
            EdgeType edgeTypeForRdfMapping = OntoramaConfig.getEdgeType(edgeTypeName);
            result.put(edgeTypeForRdfMapping, rdfMapping);
        }
        return result;
    }

    private Resource getResource(Node node, Model model) throws RDFException {
        if (_nodeToResource.containsKey(node)) {
            return (Resource) _nodeToResource.get(node);
        } else {
            Resource resource = model.createResource();
            model.add(resource, RDF.type, RDFS.Class);
//              model.addStatement(res, RDF.type, RDFS.Class);

//            Resource resource = new ResourceImpl(node.getIdentifier());
            _nodeToResource.put(node, resource);
            return resource;
        }
    }


}

