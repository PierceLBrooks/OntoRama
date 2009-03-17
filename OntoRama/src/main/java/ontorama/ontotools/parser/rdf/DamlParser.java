package ontorama.ontotools.parser.rdf;

import java.io.Reader;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ontorama.OntoramaConfig;
import ontorama.backends.Backend;
import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeType;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.ParserException;
import ontorama.ontotools.parser.Parser;
import ontorama.ontotools.parser.ParserResult;

import com.hp.hpl.jena.daml.DAMLClass;
import com.hp.hpl.jena.daml.DAMLCommon;
import com.hp.hpl.jena.daml.DAMLModel;
import com.hp.hpl.jena.daml.DAMLRestriction;
import com.hp.hpl.jena.daml.common.DAMLModelImpl;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.model.Resource;


public class DamlParser implements Parser {
	Map<String, Node> _nodes = new HashMap<String, Node>();
	
	List<Edge> _edges = new ArrayList<Edge>();
	
	Backend _backend = OntoramaConfig.getBackend();
	
	private final static String sameClassAs = "sameClassAs";
	private final static String restriction = "restriction";
	private final static String subtype = "subtype";
	private final static String supertype = "supertype";

    /*
     * @todo return proper ParserResult (not null)
     */
    @SuppressWarnings("unchecked")
	public ParserResult getResult(Reader reader) throws ParserException, AccessControlException {
    	try {
    		DAMLModel model = new DAMLModelImpl();
    		model.read(reader, "");
    		
    		Iterator<DAMLClass> damlClasses = model.listDAMLClasses();
    		while (damlClasses.hasNext()) {
				DAMLClass curClass = damlClasses.next();
				processClass(curClass);					
			}
    	}
    	catch (RDFException e) {
    		e.fillInStackTrace();
    		throw new ParserException("Parser Failed ");
    	}
    	catch (NoSuchRelationLinkException e) {
    		e.fillInStackTrace();
    		e.printStackTrace();
    		throw new ParserException(e.getMessage());
    	}
    	List<Node> nodes = new ArrayList<Node>(_nodes.values());
        return new ParserResult(nodes, _edges);
    }
    
    @SuppressWarnings("unchecked")
	private void processClass (DAMLClass damlClass) throws NoSuchRelationLinkException, 
    										RDFException  {
		if (damlClass.isAnon()) {
			return;
		}
    	
		Node node = getNode(damlClass, OntoramaConfig.CONCEPT_TYPE);
			
		Iterator<DAMLClass> sameClasses = damlClass.getSameClasses();
		while (sameClasses.hasNext()) {
			DAMLClass cur = sameClasses.next();
			Node connectedNode = getNode(cur, OntoramaConfig.CONCEPT_TYPE);
			if (! node.equals(connectedNode)) {
				getEdge(node, connectedNode, sameClassAs);
			}
		}
		
		Iterator<DAMLCommon> subClasses = damlClass.getSubClasses();
		while (subClasses.hasNext()) {
			DAMLCommon cur = subClasses.next();
			processRelationship(node, cur, subtype);
			
		}
		
		Iterator<Resource> superClasses = damlClass.getSuperClasses();
		while (superClasses.hasNext()) {
			Resource cur = superClasses.next();
			processRelationshipForResource(node, cur, supertype);
		}
    }

	private void processRelationshipForResource (Node node, Resource resource, 
								String edgeTypeName) 
								throws RDFException, NoSuchRelationLinkException {
		if (resource instanceof DAMLCommon) {
			processRelationship(node, (DAMLCommon) resource, edgeTypeName);
		}
		else {
			System.err.println("dont know yet how to deal with resource " + resource + ", class = " + resource.getClass());
		}
	}

	private void processRelationship(Node node, DAMLCommon resource, String edgeTypeName)
							throws RDFException, NoSuchRelationLinkException {
		Node connectedNode = getNode(resource, OntoramaConfig.CONCEPT_TYPE);
		if (resource instanceof DAMLClass) {
			getEdge(node, connectedNode, edgeTypeName);
		}
		if (resource instanceof DAMLRestriction) {
			getEdge(connectedNode, node, restriction);
		}
	}
    
    private Node getNode(DAMLCommon damlCommon, NodeType nodeType) throws RDFException {
		String identifier;
		String name;
		if (damlCommon.getLocalName() == null) {
			identifier = damlCommon.getId().toString();
			name = identifier;	
		}
		else {
			name = damlCommon.getLocalName();
			identifier = damlCommon.getNameSpace() + damlCommon.getLocalName();
		}
		Node node = _nodes.get(identifier);
		if (node == null) {
			node = _backend.createNode(name, identifier);
			node.setNodeType(nodeType);
			_nodes.put(identifier, node);
		}
		return node;
	}
	
	private Edge getEdge (Node fromNode, Node toNode, String edgeTypeName) throws NoSuchRelationLinkException {
		Edge edge = null;
		Iterator<Edge> it = _edges.iterator();
		while (it.hasNext()) {
			Edge cur = it.next();
			if (cur.getFromNode().getIdentifier().equals(fromNode.getIdentifier())) {
				if (cur.getToNode().getIdentifier().equals(toNode.getIdentifier())) {
					if (cur.getEdgeType().getName().equals(edgeTypeName)) {
						edge = cur;
					}
				}
			}
		}
		if (edge == null) {
			boolean foundEdgeType = false;
			Iterator<EdgeType> edgeTypes = OntoramaConfig.getEdgeTypesList().iterator();
			while (edgeTypes.hasNext()) {
				EdgeType cur = edgeTypes.next();
				if (cur.getName().equals(edgeTypeName)) {
					edge = _backend.createEdge(fromNode, toNode, cur);
					System.out.println("\n\ncreating edge: " + fromNode + " -> " + toNode + ": " + cur.getName());
					_edges.add(edge);
				}
				else if (cur.getReverseEdgeName() != null) {
					if (cur.getReverseEdgeName().equals(edgeTypeName)) {
						edge = _backend.createEdge(toNode, fromNode, cur);
						System.out.println("\n\ncreating edge: " + toNode + " -> " + fromNode + ": " + cur.getName());
						_edges.add(edge);
					}
				}
			}
			if (! foundEdgeType) {
				System.err.println("EdgeType is not defined for " + edgeTypeName);
			}
		}
		
		return edge;
	}



}

