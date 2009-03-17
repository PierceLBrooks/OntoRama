package ontorama.ontotools.writer.rdf;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ontorama.OntoramaConfig;
import ontorama.conf.RdfMapping;
import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Graph;
import ontorama.model.graph.Node;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.writer.ModelWriter;
import ontorama.ontotools.writer.ModelWriterException;

import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;
import com.hp.hpl.mesa.rdf.jena.common.ResourceImpl;
import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.Model;
import com.hp.hpl.mesa.rdf.jena.model.Property;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.model.Resource;

public class RdfModelWriter implements ModelWriter {
    protected Graph _graph;

    /**
     * contains mapping of processed nodes to created rdf resources
     */
    protected Map<Node, Resource> _nodeToResource;

    /**
     * contains a list of processed edges that have been added to rdf model
     */
    protected List<Edge> _processedEdges;

    protected List<Node> _processedNodes;

    protected Map<EdgeType, RdfMapping> _edgeTypesToRdfMapping;

    public void write(Graph graph, Writer out) throws ModelWriterException {
        _graph = graph;
        _nodeToResource = new HashMap<Node, Resource>();
        _processedEdges = new ArrayList<Edge>();
        _processedNodes = new ArrayList<Node>();

        try {
            _edgeTypesToRdfMapping = mapEdgeTypesToRdfTags();
            Model rdfModel = toRDFModel();
            writeModel(rdfModel, out);
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
        Model rdfModel = new ModelMem();

        writeEdges(_graph.getEdgesList(), rdfModel);

        return rdfModel;
    }

    protected void writeEdges(List<Edge> edgesList,  Model rdfModel) throws RDFException {
        Iterator<Edge> edgesIterator = edgesList.iterator();
        while (edgesIterator.hasNext()) {
            Edge curEdge = edgesIterator.next();
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
        Node fromNode = (Node) curEdge.getFromNode();
        EdgeType edgeType = curEdge.getEdgeType();
        Property predicate = getPropertyForEdgeType(edgeType, _edgeTypesToRdfMapping);
        RdfMapping rdfMapping = _edgeTypesToRdfMapping.get(edgeType);
        if (rdfMapping.getType().equals(edgeType.getName()) ) {
            result =  new SimpleTriple (curEdge.getFromNode(), predicate, curEdge.getToNode());
            _processedNodes.add(fromNode);
        }
        else if (rdfMapping.getType().equals(edgeType.getReverseEdgeName())) {
            result = new SimpleTriple(curEdge.getToNode(), predicate, curEdge.getFromNode());
            _processedNodes.add(curEdge.getToNode());
        }
        return result;
    }

    protected Property getPropertyForEdgeType (EdgeType edgeType, Map<EdgeType, RdfMapping> edgeTypesToRdfMapping) throws RDFException {
        RdfMapping rdfMapping = edgeTypesToRdfMapping.get(edgeType);
        String rdfTag = rdfMapping.getRdfTags().get(0);
        Property predicate;
        String text = edgeType.getNamespace() + rdfTag;
        predicate = new PropertyImpl(text);
        return predicate;
    }


    protected Map<EdgeType, RdfMapping> mapEdgeTypesToRdfTags() throws NoSuchRelationLinkException {
        Map<EdgeType, RdfMapping> result = new HashMap<EdgeType, RdfMapping>();
        List<RdfMapping> rdfMappingList = OntoramaConfig.getRelationRdfMapping();
        Iterator<RdfMapping> it = rdfMappingList.iterator();
        while (it.hasNext()) {
            RdfMapping rdfMapping = it.next();
            String edgeTypeName = rdfMapping.getType();
            EdgeType edgeTypeForRdfMapping = OntoramaConfig.getEdgeType(edgeTypeName);
            result.put(edgeTypeForRdfMapping, rdfMapping);
        }
        return result;
    }

    protected Resource getResource(Node node) {
        if (_nodeToResource.containsKey(node)) {
            return _nodeToResource.get(node);
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

