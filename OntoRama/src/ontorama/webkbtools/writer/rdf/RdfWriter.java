package ontorama.webkbtools.writer.rdf;

import ontorama.webkbtools.writer.ModelWriter;
import ontorama.webkbtools.writer.ModelWriterException;
import ontorama.model.*;
import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.RdfMapping;

import java.io.Writer;
import java.util.*;

import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.common.ResourceImpl;
import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 4/10/2002
 * Time: 09:27:15
 * To change this template use Options | File Templates.
 */
public class RdfWriter implements ModelWriter {
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

    public void write(Graph graph, Writer out) throws ModelWriterException {
        _graph = graph;
        _nodeToResource = new Hashtable();
        _processedEdges = new LinkedList();

        try {
            Model rdfModel = toRDFModel();
            rdfModel.write(out);
        }
        catch (RDFException rdfExc) {
            rdfExc.printStackTrace();
            throw new ModelWriterException("Couldn't create RDF model " + rdfExc.getMessage());
        }

    }


    protected Model toRDFModel() throws RDFException {
        ModelMem rdfModel = new ModelMem();

        List nodesList = _graph.getNodesList();
        List edgesList = _graph.getEdgesList();

        Iterator edgesIterator = edgesList.iterator();
        while (edgesIterator.hasNext()) {
            Edge curEdge = (Edge) edgesIterator.next();
            Node fromNode = curEdge.getFromNode();
            Resource subject = getResource(fromNode);
            Node toNode = curEdge.getToNode();
            Resource object = getResource(toNode);
            EdgeType edgeType = curEdge.getEdgeType();
            System.out.println("edge type: " + edgeType.getNamespace() + edgeType.getName());
            Property predicate = new PropertyImpl(edgeType.getNamespace() + edgeType.getName());
            rdfModel.add(subject, predicate, object);
        }
        return rdfModel;
    }




    private Hashtable createMappingFromRelationIDtoRDF() {
        Hashtable retVal = new Hashtable();

        List ontologyRelationRdfMapping = OntoramaConfig.getRelationRdfMapping();
        Iterator ontologyRelationRdfMappingIterator = ontologyRelationRdfMapping.iterator();
        while (ontologyRelationRdfMappingIterator.hasNext()) {
            RdfMapping rdfMapping = (RdfMapping) ontologyRelationRdfMappingIterator.next();
            Iterator mappingTagsIterator = rdfMapping.getRdfTags().iterator();
            while (mappingTagsIterator.hasNext()) {
                String mappingTag = (String) mappingTagsIterator.next();
                //System.out.println("mappingTag = " + mappingTag + ", id = " + rdfMapping.getId());
                retVal.put(new Integer(rdfMapping.getId()).toString(), mappingTag);
            }
        }
        return retVal;
    }

    protected Resource getResource(Node node) {
        if (_nodeToResource.containsKey(node)) {
            return (Resource) _nodeToResource.get(node);
        } else {
            Resource resource = new ResourceImpl(node.getIdentifier());
            _nodeToResource.put(node,resource);
            return resource;
        }
    }

}

