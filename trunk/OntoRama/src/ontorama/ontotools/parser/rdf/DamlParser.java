package ontorama.ontotools.parser.rdf;

import java.io.Reader;
import java.security.AccessControlException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
import com.hp.hpl.jena.daml.DAMLInstance;
import com.hp.hpl.jena.daml.DAMLModel;
import com.hp.hpl.jena.daml.DAMLProperty;
import com.hp.hpl.jena.daml.DAMLRestriction;
import com.hp.hpl.jena.daml.common.DAMLModelImpl;
import com.hp.hpl.mesa.rdf.jena.model.AnonId;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.model.Resource;


/**
 * @author nataliya
 */
public class DamlParser implements Parser {
	/**
	 * key - identifier, value - node
	 */
	Hashtable _nodes = new Hashtable();
	
	List _edges = new LinkedList();
	
	Backend _backend = OntoramaConfig.getBackend();
	
	private final static String sameClassAs = "sameClassAs";
	private final static String restriction = "restriction";
	private final static String subtype = "subtype";
	private final static String supertype = "supertype";

    /**
     * @todo return proper ParserResult (not null)
     * @param reader
     * @return
     * @throws ParserException
     * @throws AccessControlException
     */
    public ParserResult getResult(Reader reader) throws ParserException, AccessControlException {
    	try {
    		DAMLModel model = new DAMLModelImpl();
    		model.read(reader, "");
    		
    		Iterator damlClasses = model.listDAMLClasses();
    		while (damlClasses.hasNext()) {
				DAMLClass curClass = (DAMLClass) damlClasses.next();
				System.out.println("class: " + curClass + ", local name: " + curClass.getLocalName());
				processClass(curClass);					
			}
			Iterator damlInstances = model.listDAMLInstances();
			while (damlInstances.hasNext()) {
				DAMLInstance cur = (DAMLInstance) damlInstances.next();
				System.out.println("instance: " + cur + ", local name: " + cur.getLocalName());
			}
			Iterator damlProperties = model.listDAMLProperties();
			while (damlProperties.hasNext()) {
				DAMLProperty cur = (DAMLProperty) damlProperties.next();
				System.out.println("property: " + cur + ", local name: " + cur.getLocalName());
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
    	List nodes = new LinkedList(_nodes.values());
    	System.out.println("\n returning nodes: " + nodes);
        return new ParserResult(nodes, _edges);
    }
    
    private void processClass (DAMLClass damlClass) throws NoSuchRelationLinkException, 
    										RDFException  {
		if (damlClass.isAnon()) {
			System.out.println("\tanonymous!    id = " + damlClass.getId() );
			AnonId id = damlClass.getId();
			return;
		}
    	
		Node node = getNode(damlClass, OntoramaConfig.CONCEPT_TYPE);
			
		Iterator sameClasses = damlClass.getSameClasses();
		while (sameClasses.hasNext()) {
			DAMLClass cur = (DAMLClass) sameClasses.next();
			System.out.println("\t\tsameClass: " + cur.getLocalName());
			Node connectedNode = getNode(cur, OntoramaConfig.CONCEPT_TYPE);
			if (! node.equals(connectedNode)) {
				getEdge(node, connectedNode, sameClassAs);
			}
		}
		
		Iterator subClasses = damlClass.getSubClasses();
		while (subClasses.hasNext()) {
			DAMLCommon cur = (DAMLCommon) subClasses.next();
			System.out.println("\t\tsubClass: " + cur);
			processRelationship(node, cur, subtype);
			
		}
		
		Iterator superClasses = damlClass.getSuperClasses();
		while (superClasses.hasNext()) {
			Resource cur = (Resource) superClasses.next();
			System.out.println("\t\tsuperClass: " + cur);
			processRelationshipForResource(node, cur, supertype);
		}
		
		Iterator instances = damlClass.getInstances();
		while (instances.hasNext()) {
			//DAMLInstance cur = (DAMLInstance) instances.next();
			System.out.println("\t\tinstance: " + instances.next());
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
			DAMLClass cl = (DAMLClass) resource;
			getEdge(node, connectedNode, edgeTypeName);
		}
		if (resource instanceof DAMLRestriction) {
			DAMLRestriction restr = (DAMLRestriction) resource;
			//getEdge(node, connectedNode, restriction);
			getEdge(connectedNode, node, restriction);
		}
	}
    
    private void processInstance (DAMLInstance damlInstance) throws RDFException {
    	String identifier = damlInstance.getNameSpace() + damlInstance.getLocalName();
    	getNode(damlInstance, OntoramaConfig.CONCEPT_TYPE);
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
		Node node = (Node) _nodes.get(identifier);
		if (node == null) {
			node = _backend.createNode(name, identifier);
			node.setNodeType(nodeType);
			_nodes.put(identifier, node);
		}
		return node;
	}
	
	private Edge getEdge (Node fromNode, Node toNode, String edgeTypeName) throws NoSuchRelationLinkException {
		Edge edge = null;
		Iterator it = _edges.iterator();
		while (it.hasNext()) {
			Edge cur = (Edge) it.next();
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
			Iterator edgeTypes = OntoramaConfig.getEdgeTypesList().iterator();
			while (edgeTypes.hasNext()) {
				EdgeType cur = (EdgeType) edgeTypes.next();
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

