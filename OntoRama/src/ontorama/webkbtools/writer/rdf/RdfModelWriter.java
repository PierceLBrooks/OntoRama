package ontorama.webkbtools.writer.rdf;

import ontorama.webkbtools.writer.ModelWriter;
import ontorama.webkbtools.writer.ModelWriterException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.model.*;
import ontorama.OntoramaConfig;
import ontorama.backends.p2p.model.P2PNode;
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
    protected Graph _graph;

    /**
     * contains mapping of processed nodes to created rdf resources
     * keys - nodes
     * values - rdf resources
     */
    protected Hashtable _nodeToResource;

    /**
     * contains a list of processed edges that have been added to rdf model
     */
    protected List _processedEdges;

    protected List _processedNodes;

    protected Hashtable _edgeTypesToRdfMapping;

    public void write(Graph graph, Writer out) throws ModelWriterException {
        _graph = graph;
        _nodeToResource = new Hashtable();
        _processedEdges = new LinkedList();
        _processedNodes = new LinkedList();

        try {
            _edgeTypesToRdfMapping = mapEdgeTypesToRdfTags();
            Model rdfModel = toRDFModel();
            //PrettyWriter pr = new PrettyWriter();
            //pr.write(rdfModel, out, null);
            writeModel(rdfModel, out);
            //rdfModel.write(out);
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

    protected Writer writeModel (Model rdfModel, Writer out) throws RDFException, IOException {
        StringWriter stringWriter = new StringWriter();
        rdfModel.write(stringWriter);
        String string = stringWriter.toString();
        Pattern p = Pattern.compile("rdf:Description");
        String replacementString = "rdfs:Class";

        Matcher matcher = p.matcher(string);
        String result = matcher.replaceAll(replacementString);
        out.write(result);
        return out;
    }


    protected Model toRDFModel() throws RDFException, NoSuchRelationLinkException {
//        DAMLModel rdfModel = new DAMLModelImpl();
        Model rdfModel = new ModelMem();


        List nodesList = _graph.getNodesList();
        List edgesList = _graph.getEdgesList();

        writeEdges(edgesList, rdfModel);
        //writeNodes(nodesList, rdfModel);

        return rdfModel;
    }

    private void writeNodes(List nodesList, Model rdfModel) throws RDFException {
        /// @todo need to be able to handle unconnected nodes.
        // stubs are  below.
        Iterator nodesIterator = nodesList.iterator();
        while (nodesIterator.hasNext()) {
            Node curNode = (Node) nodesIterator.next();
            Resource curResource = getResource(curNode);
            boolean needToAddToModel = false;
            if (! _processedNodes.contains(curNode)) {
                needToAddToModel = true;
            }
            if (needToAddToModel) {
                rdfModel.createResource(curResource);
                //DAMLClass damlClass = new DAMLClassImpl(curResource, rdfModel, )
                _processedNodes.add(curNode);
            }
        }
    }

    protected void writeEdges(List edgesList,  Model rdfModel) throws RDFException {
        Iterator edgesIterator = edgesList.iterator();
        while (edgesIterator.hasNext()) {
            Edge curEdge = (Edge) edgesIterator.next();
            SimpleTriple triple = writeEdgeIntoModel(curEdge, rdfModel);
            Resource subject = getResource(triple.getSubject());
            Property predicate = triple.getPredicate();
            if (triple.getObject().getIdentifier().equals(triple.getObject().getName())) {
                /// @todo this is not a good test (testing if node should
                // be resource or literal). this probably should be reflected
                // in a node type object
                rdfModel.add(subject, predicate, curEdge.getToNode().getName());
            }
            else {
                Resource object = getResource(triple.getObject());
                rdfModel.add(subject, predicate, object);
            }

        }
    }

    protected SimpleTriple writeEdgeIntoModel(Edge curEdge, Model rdfModel) throws RDFException {
        SimpleTriple result = null;
        P2PNode fromNode = (P2PNode) curEdge.getFromNode();
        EdgeType edgeType = curEdge.getEdgeType();
        Property predicate = getPropertyForEdgeType(edgeType, _edgeTypesToRdfMapping);
        RdfMapping rdfMapping = (RdfMapping) _edgeTypesToRdfMapping.get(edgeType);
        if (rdfMapping.getType().equals(edgeType.getName()) ) {
            P2PNode toNode = (P2PNode) curEdge.getToNode();
            result =  new SimpleTriple (curEdge.getFromNode(), predicate, curEdge.getToNode());
            _processedNodes.add(fromNode);
        }
        else if (rdfMapping.getType().equals(edgeType.getReverseEdgeName())) {
            result = new SimpleTriple(curEdge.getToNode(), predicate, curEdge.getFromNode());
            _processedNodes.add(curEdge.getToNode());
        }
        return result;
    }

    protected Property getPropertyForEdgeType (EdgeType edgeType, Hashtable edgeTypesToRdfMapping) throws RDFException {
        RdfMapping rdfMapping = (RdfMapping) edgeTypesToRdfMapping.get(edgeType);
        String rdfTag = (String) rdfMapping.getRdfTags().get(0);
        System.out.println("edgeType namespace = " + edgeType.getNamespace() + ", rdfTag = " + rdfTag);
        Property predicate;
        String text = edgeType.getNamespace() + rdfTag;
        predicate = new PropertyImpl(text);
        return predicate;
    }


    protected Hashtable mapEdgeTypesToRdfTags() throws NoSuchRelationLinkException {
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

    protected Resource getResource(Node node) {
        if (_nodeToResource.containsKey(node)) {
            return (Resource) _nodeToResource.get(node);
        } else {
            Resource resource = new ResourceImpl(node.getIdentifier());
            _nodeToResource.put(node, resource);
            return resource;
        }
    }

    class SimpleTriple {
        private Node _subject;
        private Property _predicate;
        private Node _object;

        public SimpleTriple (Node subject, Property predicate, Node object) {
            _subject = subject;
            _predicate = predicate;
            _object = object;
        }

        public Node getSubject() {
            return _subject;
        }

        public Property getPredicate() {
            return _predicate;
        }

        public Node getObject() {
            return _object;
        }
    }


}

