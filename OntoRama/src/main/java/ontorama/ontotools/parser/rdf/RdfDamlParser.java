package ontorama.ontotools.parser.rdf;

import java.io.FileReader;
import java.io.Reader;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import ontorama.OntoramaConfig;
import ontorama.conf.RdfMapping;
import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Node;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.ParserException;
import ontorama.ontotools.parser.Parser;
import ontorama.ontotools.parser.ParserResult;

import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;
import com.hp.hpl.mesa.rdf.jena.common.ResourceImpl;
import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.Model;
import com.hp.hpl.mesa.rdf.jena.model.NsIterator;
import com.hp.hpl.mesa.rdf.jena.model.Property;
import com.hp.hpl.mesa.rdf.jena.model.RDFError;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.model.RDFNode;
import com.hp.hpl.mesa.rdf.jena.model.ResIterator;
import com.hp.hpl.mesa.rdf.jena.model.Resource;
import com.hp.hpl.mesa.rdf.jena.model.Statement;
import com.hp.hpl.mesa.rdf.jena.model.StmtIterator;
import com.hp.hpl.mesa.rdf.jena.vocabulary.RDFS;


/**
 * Parse a reader in RDF format and create OntologyTypes from RDF statements.
 */
public class RdfDamlParser implements Parser {

    /**
     * map to hold all Graph Nodes that we have created
     * keys - strings - graph node names
     * values - graph nodes
     */
    protected Map<String,Node> _nodesHash;
    protected List<Edge> _edgesList;

    private String _rdfsNamespace = null;
    private String _correctedRdfsNamespace = null;
    private String _rdfSyntaxTypeNamespace = null;
    
    /// @todo this definition doesn't work due to the schema namespace WebKB is using
    //private static final String _rdfsNamespaceSuffix = "rdf-schema#";
    private static final String _rdfsNamespaceSuffix = "rdf-schema";
    private static final String _rdfSyntaxTypeNamespaceSuffix = "rdf-syntax-ns#";

    /*
     * Hold RDF resources that are potentially concept node types
     * or relation node types.
     */
    private List<RDFNode> resourceConceptNodeTypesList;
    private List<RDFNode> resourceRelationNodeTypesList;

    private final List<String> predicatesConnectingPropertyToProperty;
    
    private final String _predicateName_subPropertyOf = "subPropertyOf";
    private final String _predicateName_range = "range";
    private final String _predicateName_domain = "domain"; 
    private final String _predicateName_samePropertyAs = "samePropertyAs";
    
    private final String _predicateName_comment = "comment";

    public RdfDamlParser() {
		init();
        predicatesConnectingPropertyToProperty = new ArrayList<String>();
    }
    
    private void init() {
        _nodesHash = new HashMap<String, Node>();
        _edgesList = new ArrayList<Edge>();    	
    }

    /**
     * Considerations for sorting out rdfs:Classes from rdf:Properties
     * as in RDFS Specification 1.0:
     * section 2.2.3
     * When a schema defines a new class, the resource representing that class must have an rdf:type property
     * whose value is the resource rdfs:Class.
     * section 2.3
     * every RDF model [...] includes core properties. These are instances of the rdf:Property class and
     * provide a mechanism for expressing relationships between classes and their instances or superclasses.
     * section 2.3.2
     * ... Only instances of rdfs:Class can have the rdfs:subClassOf property and the property value
     * is always of rdf:type rdfs:Class. A class may be a subclass of more then one class.
     * section 3.1.3 (rdfs:range)
     * An instance of ConstrainedProperty (rdfs:domain and and rdfs:range) that is used to indicate the (class(es))
     * that the values of a property must me members of. The value of range property is always a Class.
     * ... The rdfs:domain of rdfs:range is the class rdf:Property. This indicates that the range property
     * applies to resources that are themselves properties.
     * The rdfs:range of rdfs:range is the class rdfs:Class. This indicates that any resource that is the value of
     * a range property will be a class.
     * section 3.1.4 (rdfs:domain)
     * ... The rdfs:domain of rdfs:domain is the class rdf:Property. This indicates that the domain property is
     * used on resources that are properties.
     * The rdfs:range of rdfsLdomain is the class rdfs:Class. This indicates that any resource taht is the
     * value of a domain property will be a class.
     *
     *
     * what this all means (at least what I think it means ;)
     * To find classes:
     * - look for all resources declared as Classes
     * - look for all objects that have predicate subClassOf
     * - objects from statements with predicate rdfs:domain or rdfs:range are Classes
     *
     * To find properties:
     * - look for all resources declared as Properties
     * - look for all objects that have predicate subPropertyOf
     * - subjects from statements with predicate rdfs:domain or rdfs:range are Properties
     *
     * other resources:
     * - rdfs:comment ?
     * - rdfs:label ?
     * - pm:part, pm:memberOf, etc - if subject of statement if a Class, consider object Class, the same
     * subject Property.
     * - daml:complementOf for now the same as above.
     *
     */
    public ParserResult getResult(Reader reader) throws ParserException, AccessControlException {
    	init();
        try {
            // create an empty model
            Model model = new ModelMem();
            model.read(reader, "");

            findNamespaces(model);

            /// @todo this list should be built on basis of xml config file
            predicatesConnectingPropertyToProperty.add("subPropertyOf");
            predicatesConnectingPropertyToProperty.add("similar");
            predicatesConnectingPropertyToProperty.add("part");
            predicatesConnectingPropertyToProperty.add("substance");
            predicatesConnectingPropertyToProperty.add("complementOf");
            predicatesConnectingPropertyToProperty.add("wnMember");
            predicatesConnectingPropertyToProperty.add("wnObject");
        	predicatesConnectingPropertyToProperty.add("samePropertyAs");

            sortResourcesIntoClassesAndProperties(model);

            ResIterator resIt = model.listSubjects();

            while (resIt.hasNext()) {
                Resource r = resIt.next();
                StmtIterator stIt = r.listProperties();
                while (stIt.hasNext()) {
                    Statement s = stIt.next();
                    if (s.getPredicate().toString().endsWith("rdf-syntax-ns#type")) {
                        if (s.getObject().toString().endsWith("rdf-syntax-ns#Property")) {
                            continue;
                        }
                        if (s.getObject().toString().endsWith("#Class")) {
                            continue;
                        }
                    }
                    processStatement(s);
                }
            }
        } catch (AccessControlException secExc) {
            throw secExc;
        } catch (RDFException e) {
            throw new ParserException("Error parsing RDF", e);
        } catch (RDFError err) {
            throw new ParserException("Couldn't parse returned RDF data.", err);
        }
        catch (NoSuchRelationLinkException relExc) {
            relExc.printStackTrace();
            throw new ParserException("Unrecognized EdgeType: " + relExc.getMessage());
        }
        ParserResult result = new ParserResult(new ArrayList<Node>(_nodesHash.values()), _edgesList);
        return result;
    }

	private void sortResourcesIntoClassesAndProperties(Model model) throws RDFException {
        /// @todo following is an  attempt to classify rdf objects into Classes
        // and Properties. This may not work very well for some rdf files.
        Property typeProperty = new PropertyImpl(_rdfSyntaxTypeNamespace, "type");

        Resource classResource = new ResourceImpl(_rdfsNamespace, "Class");
        Resource propertyResource = new ResourceImpl(_rdfSyntaxTypeNamespace, "Property");

        resourceConceptNodeTypesList = runSelector(model, typeProperty, classResource);
        resourceRelationNodeTypesList = runSelector(model, typeProperty, propertyResource);

        // get Iterator of all subjects, then go through each of them
        // and get Iterator of statements. Process each statement
        ResIterator resIt = model.listSubjects();

        while (resIt.hasNext()) {
            Resource r = resIt.next();
            StmtIterator stIt = r.listProperties();
            while (stIt.hasNext()) {
                Statement s = stIt.next();
                if (objectIsPropertyResource(s)) {
                	resourceRelationNodeTypesList.add(s.getObject());
                }
                else {
                	resourceConceptNodeTypesList.add(s.getObject());
                }
            }
        }
	}

    private boolean objectIsPropertyResource (Statement s) {
        Resource subject = s.getSubject();
        Property predicate = s.getPredicate();
        if (resourceRelationNodeTypesList.contains(subject)) {
            String predicateLocalName = predicate.getLocalName();
            if (predicatesConnectingPropertyToProperty.contains(predicateLocalName)) {
            	return true;
            }
        }
        return false;
    }

    private void findNamespaces(Model model) throws RDFException {
        NsIterator nsIterator = model.listNameSpaces();
        while (nsIterator.hasNext()) {
            String namespace = nsIterator.next();
            if (namespace.endsWith(_rdfSyntaxTypeNamespaceSuffix)) {
                _rdfSyntaxTypeNamespace = namespace;
            }
            // TODO commented out is the better way to do this,
            // but in WebKB rdfs namespace ends with something
            // like "rdf-shema-199990808#" which is not usefull for us.
            int index = namespace.toString().indexOf(_rdfsNamespaceSuffix);
            if (index > 0) {
                _rdfsNamespace = namespace;
                // TODO this a hack to handle older RDF schema namespaces,
				// such as http://www.w3.org/TR/1999/PR-rdf-schema-19990303
				// for instance (WebKB is still using it). For now we just
				// reassign it to the latest W3C schema.
				// To find full extent of the hack - search for usages of
				// _correctedRdfsNamespace.
                _correctedRdfsNamespace = RDFS.getURI();
            }
        }
    }

    private List<RDFNode> runSelector(Model model, Property p, Object o) throws RDFException  {
        List<RDFNode> result = new ArrayList<RDFNode>();

        StmtIterator stIt = model.listStatements();
        while (stIt.hasNext()) {
            Statement next = stIt.next();
            if (next.getPredicate().equals(p)) {
                if (next.getObject().equals(o)) {
                    result.add( next.getSubject());
                }
            }
        }
        return result;
    }

    /**
     * Process RDF statement and create corresponding graph nodes.
     */
    protected void processStatement(Statement st) throws NoSuchRelationLinkException {
        Property predicate = st.getPredicate();
        Resource resource = st.getSubject();
        RDFNode object = st.getObject();
        
        Node subjectNode = doNodeMapping(resource);
        
		if (predicate.getLocalName().equalsIgnoreCase(_predicateName_comment)) {
			subjectNode.setDescription(object.toString());
			return;
		}
        
        Node objectNode = doNodeMapping(object);
        
        if ((predicate.getLocalName().equals(_predicateName_subPropertyOf)) ||
        		(predicate.getLocalName().equals(_predicateName_samePropertyAs)) ) {
        			
        	subjectNode.setNodeType(OntoramaConfig.RELATION_TYPE);
        	objectNode.setNodeType(OntoramaConfig.RELATION_TYPE);

        	resourceConceptNodeTypesList.remove(st.getSubject());
			resourceRelationNodeTypesList.add(st.getSubject());

        	resourceConceptNodeTypesList.remove(st.getObject());
        	resourceRelationNodeTypesList.add(st.getObject());
        	
        }
        
        if  ( (predicate.getLocalName().equals(_predicateName_range)) ||
        		(predicate.getLocalName().equals(_predicateName_domain))) {
        			
        	subjectNode.setNodeType(OntoramaConfig.RELATION_TYPE);
        	objectNode.setNodeType(OntoramaConfig.CONCEPT_TYPE);

        	resourceConceptNodeTypesList.remove(st.getSubject());
        	resourceRelationNodeTypesList.add(st.getSubject());

        	resourceRelationNodeTypesList.remove(st.getObject());
        	resourceConceptNodeTypesList.add(st.getObject());
        }
        
        doEdgesMapping(subjectNode, predicate, objectNode);
    }

    protected Node doNodeMapping(RDFNode object) {
        String nodeName = stripUri(object);
        Node node;
        if (_nodesHash.containsKey(nodeName)) {
            node = _nodesHash.get(nodeName);
        } else {
            node = OntoramaConfig.getBackend().createNode(nodeName, object.toString());
            if (resourceConceptNodeTypesList.contains(object)) {
                node.setNodeType(OntoramaConfig.CONCEPT_TYPE);
            }
            else if (resourceRelationNodeTypesList.contains(object)) {
                node.setNodeType(OntoramaConfig.RELATION_TYPE);
            }
            else {
            	// TODO don't log to syserr
            	System.err.println("RDF Resource '" + object + "'is neigher concept of property");
            	node.setNodeType(OntoramaConfig.UNKNOWN_TYPE);
            }
            _nodesHash.put(nodeName, node);
        }
        return node;
    }

    protected void doEdgesMapping(Node subjectNode, Property predicate, Node objectNode) throws NoSuchRelationLinkException {
    	
        List<RdfMapping> ontologyRelationRdfMapping = OntoramaConfig.getRelationRdfMapping();
        Iterator<RdfMapping> ontologyRelationRdfMappingIterator = ontologyRelationRdfMapping.iterator();
        boolean foundMapping = false;
        while (ontologyRelationRdfMappingIterator.hasNext()) {
            RdfMapping rdfMapping = ontologyRelationRdfMappingIterator.next();
            Iterator<String> mappingTagsIterator = rdfMapping.getRdfTags().iterator();
            while (mappingTagsIterator.hasNext()) {
                String mappingTag = mappingTagsIterator.next();
                if (predicate.getLocalName().equalsIgnoreCase(mappingTag)) {
                    String mappingType = rdfMapping.getType();
                    EdgeType edgeType = OntoramaConfig.getEdgeType(mappingType);
                    String edgeTypeNamespace = predicate.getNameSpace();
                    if (edgeTypeNamespace.equalsIgnoreCase(_rdfsNamespace)) {
                        edgeTypeNamespace = _correctedRdfsNamespace;
                    }
                    edgeType.setNamespace(edgeTypeNamespace);
                    try {
                        if (mappingType.equals(edgeType.getName())) {
                            addEdge(subjectNode, edgeType, objectNode);
                            foundMapping = true;
                        } else if (mappingType.equals(edgeType.getReverseEdgeName())) {
                            addEdge(objectNode, edgeType, subjectNode);
                            foundMapping = true;
                        } else {
                            // ERROR
                            // throw exception here
                        	// TODO: never do System.exit in lowlevel code
                            System.err.println("Dont' know about RDF Property '" + predicate.getLocalName() + "'");
                            System.exit(-1);
                        }
                    } catch (NoSuchRelationLinkException e) {
                    	// TODO: never do System.exit in lowlevel code
                        e.printStackTrace();
                        System.err.println("NoSuchRelationLinkException: " + e.getMessage());
                        System.exit(-1);
                    }
                }
            }
        }
        if (!foundMapping) {
        	// TODO: don't use syserr where we could log
            System.err.println("unknown RDF tag for predicate: " + predicate.getLocalName());
        }
    }

    protected void addEdge(Node fromNode, EdgeType edgeType, Node toNode)
            throws NoSuchRelationLinkException {
        Edge newEdge = OntoramaConfig.getBackend().createEdge(fromNode, toNode, edgeType);
        _edgesList.add(newEdge);
    }

	/*
	 * TODO need to check if this rdfNode string contains any uri's, otherwise
	 * may strip something that shouldn't be stripped if node happen to contain
	 * "/". for example: description may contain '/': cats/dogs maybe need to
	 * check if string starts with http:// ?
	 */
    public String stripUri(RDFNode rdfNode) {
        return stripUri(rdfNode.toString());
    }

    /*
     * @todo    need to check if this rdfNode string contains any uri's, otherwise
     * may strip something that shouldn't be stripped if node happen to contain "/".
     * for example: description may contain '/': cats/dogs
     * maybe need to check if string starts with http:// ?
     */
    protected static String stripUri(String uriStr) {
        StringTokenizer tokenizer = new StringTokenizer(uriStr, "/");
        int count = 0;
        int tokensNumber = tokenizer.countTokens();
        while (tokenizer.hasMoreTokens()) {
            count++;
            String token = tokenizer.nextToken();
            if (count == tokensNumber) {
                return token;
            }
        }
        return uriStr;
    }

    public Node getGraphNodeByName(String nodeName,
                                        String fullNodeName) {
        Node node;
        if (_nodesHash.containsKey(nodeName)) {
            node = _nodesHash.get(nodeName);
            return node;
        } else {
            node = OntoramaConfig.getBackend().createNode(nodeName, fullNodeName);
            _nodesHash.put(nodeName, node);
            return node;
        }
    }

    public static void main(String args[]) {
        try {
            RdfDamlParser parser = new RdfDamlParser();
            String filename = args[0];
            System.out.println();
            System.out.println("filename = " + filename);
            Reader reader = new FileReader(filename);
            parser.getResult(reader);
            System.out.println();

        } catch (Exception e) {
            System.out.println("Failed: " + e);
            System.exit(-1);
        }
    }
}
