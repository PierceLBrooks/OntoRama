package ontorama.webkbtools.writer.rdf;

import ontorama.webkbtools.writer.ModelWriter;
import ontorama.webkbtools.writer.ModelWriterException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.model.*;
import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.RdfMapping;

import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.common.ResourceImpl;
import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;
import com.hp.hpl.mesa.rdf.jena.common.prettywriter.PrettyWriter;
import com.hp.hpl.mesa.rdf.jena.vocabulary.RDFS;
import com.hp.hpl.mesa.rdf.jena.vocabulary.RDF;
import com.hp.hpl.jena.daml.DAMLModel;
import com.hp.hpl.jena.daml.DAMLClass;
import com.hp.hpl.jena.daml.common.DAMLModelImpl;
import com.hp.hpl.jena.daml.common.DAMLClassImpl;

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
//            PrettyWriter prettyWriter = new PrettyWriter();
//            prettyWriter.write(rdfModel, out, null);

//            rdfModel.write(out);
            StringWriter stringWriter = new StringWriter();
            rdfModel.write(stringWriter);
            String string = stringWriter.toString();
            Pattern p = Pattern.compile("rdf:Description");

            StringTokenizer tok = new StringTokenizer(string);
            while (tok.hasMoreElements()) {
                String token = (String) tok.nextElement();
                Matcher m = p.matcher(token);
                boolean found = m.find();
                System.out.println(token);
                if (found) {
                    System.out.println("match: rdf:Description");
                }


            }
            System.out.println("\nstring = " + string);

            System.out.println("match: " + string.matches("rdf:Description"));

            string.replaceAll("rdf:Description","rdfs:Class");
            System.out.println("\nstring = " + string);

            out.write(string);



        } catch (RDFException rdfExc) {
            rdfExc.printStackTrace();
            throw new ModelWriterException("Couldn't create RDF model " + rdfExc.getMessage());
        } catch (NoSuchRelationLinkException relExc) {
            relExc.printStackTrace();
            throw new ModelWriterException(relExc.getMessage());
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            throw new ModelWriterException("couldn't write output stream: " + ioe.getMessage());
        }

    }


    protected Model toRDFModel() throws RDFException, NoSuchRelationLinkException {
//        DAMLModel rdfModel = new DAMLModelImpl();
        Model rdfModel = new ModelMem();


        List nodesList = _graph.getNodesList();
        List edgesList = _graph.getEdgesList();

        Hashtable edgeTypesToRdfMapping = mapEdgeTypesToRdfTags();

        Iterator edgesIterator = edgesList.iterator();
        while (edgesIterator.hasNext()) {
            Edge curEdge = (Edge) edgesIterator.next();
            Node fromNode = curEdge.getFromNode();
            Resource subject = getResource(fromNode);
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
                    Resource object = getResource(curEdge.getToNode());
                    rdfModel.add(subject, predicate, object);
                }
                _processedNodes.add(fromNode);
            }
            else if (rdfMapping.getType().equals(edgeType.getReverseEdgeName())) {
                Resource object = getResource(curEdge.getToNode());
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
//                //DAMLClass damlClass = new DAMLClassImpl(curResource, rdfModel, )
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

    private Resource getResource(Node node) {
        if (_nodeToResource.containsKey(node)) {
            return (Resource) _nodeToResource.get(node);
        } else {
            Resource resource = new ResourceImpl(node.getIdentifier());
            _nodeToResource.put(node, resource);
            return resource;
        }
    }


}

