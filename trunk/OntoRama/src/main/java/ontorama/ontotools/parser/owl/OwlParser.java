package ontorama.ontotools.parser.owl;

import java.io.Reader;
import java.net.URI;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ontorama.OntoramaConfig;
import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeImpl;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeImpl;
import ontorama.model.graph.NodeType;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.ParserException;
import ontorama.ontotools.parser.Parser;
import ontorama.ontotools.parser.ParserResult;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.io.ReaderInputSource;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;

public class OwlParser implements Parser {

    @Override
    public ParserResult getResult(final Reader reader) throws ParserException, AccessControlException {
        List<Edge> edges = new ArrayList<Edge>();
        Map<OWLClass, Node> nodesCreated = new HashMap<OWLClass, Node>();
        
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology owlOntology;
        try {
            owlOntology = manager.loadOntology(new ReaderInputSource(reader));
            for (OWLClass oWLClass : owlOntology.getReferencedClasses()) {
                Node node = mapNode(oWLClass, nodesCreated);
                for (OWLDescription superDescription : oWLClass.getSuperClasses(owlOntology)) {
                    if (superDescription.isAnonymous()) {
                        continue;
                    }
                    OWLClass superClass = superDescription.asOWLClass();
                    Node parent = mapNode(superClass, nodesCreated);
                    Edge edge = new EdgeImpl(parent, node, OntoramaConfig.getEdgeType("supertype"));
                    edges.add(edge);
                }
                node.toString();
            }
        } catch (OWLOntologyCreationException ex) {
            throw new ParserException("Error parsing OWL file", ex);
        } catch (NoSuchRelationLinkException e) {
            throw new ParserException("Configuration error: could not find a relation named 'subtype'.");
        }
        return new ParserResult(new ArrayList<Node>(nodesCreated.values()), edges);
    }

    private Node mapNode(OWLClass oWLClass, Map<OWLClass, Node> nodesCreated) {
        Node node = nodesCreated.get(oWLClass);
        if (node == null) {
            node = createNodeFromUri(oWLClass.getURI(), OntoramaConfig.CONCEPT_TYPE);
            nodesCreated.put(oWLClass, node);
        }
        return node;
    }

    private Node createNodeFromUri(URI uri, NodeType node_type) {
        Node result = new NodeImpl(getShortForm(uri), uri.toString());
        result.setNodeType(node_type);
        return result;
    }

    private String getShortForm(URI uri) {
        if (uri.getFragment() != null) {
            return uri.getFragment();
        }
        int lastSlash = uri.getPath().lastIndexOf('/');
        return uri.getPath().substring(lastSlash);
    }
}
