package ontorama.webkbtools.query.parser.rdf;

import java.io.FileReader;
import java.io.Reader;
import java.security.AccessControlException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import ontorama.OntoramaConfig;
import ontorama.model.Edge;
import ontorama.model.EdgeImpl;
import ontorama.model.EdgeType;
import ontorama.model.Node;
import ontorama.model.NodeImpl;
import ontorama.model.NodeType;
import ontorama.ontologyConfig.RdfMapping;
import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.webkbtools.NoSuchRelationLinkException;
import ontorama.webkbtools.ParserException;

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


/**
 * Title:
 * Description:  Parse a reader in RDF format and create OntologyTypes from
 *                RDF statements
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class RdfDamlParser implements Parser {

    /**
     * Hashtable to hold all Graph Nodes that we have created
     * keys - strings - graph node names
     * values - graph nodes
     */
    protected Hashtable _nodesHash;
    protected List _edgesList;

    /**
     *
     */
    private String _rdfsNamespace = null;
    private String _rdfSyntaxTypeNamespace = null;
    private String _damlNamespace = null;
    private String _pmNamespace = null;

    /// @todo this definition doesnt work due to the schema namespace WebKB is using
    //private static final String _rdfsNamespaceSuffix = "rdf-schema#";
    private static final String _rdfsNamespaceSuffix = "rdf-schema";
    private static final String _rdfSyntaxTypeNamespaceSuffix = "rdf-syntax-ns#";
    private static final String _pmNamespaceSuffix = "pm#";


    private static NodeType nodeTypeConcept;
    private static NodeType nodeTypeRelation;

    /**
     * hold RDF resources that are potentials to be concept node types
     * or relation node types.
     */
    private List resourceConceptNodeTypesList;
    private List resourceRelationNodeTypesList;

    private List predicatesConnectingPropertyToProperty;

    /**
     * Constructor
     */
    public RdfDamlParser() {
        _nodesHash = new Hashtable();
        _edgesList = new LinkedList();

        predicatesConnectingPropertyToProperty = new LinkedList();

        /// @todo a bit of a hack to get node types we need - should be more dynamic, and mapped to rdf tags.
        List nodeTypesList = OntoramaConfig.getNodeTypesList();
        Iterator it = nodeTypesList.iterator();
        while (it.hasNext()) {
            NodeType cur = (NodeType) it.next();
            if (cur.getNodeType().equals("concept")) {
                nodeTypeConcept = cur;
            }
            if (cur.getNodeType().equals("relation")) {
                nodeTypeRelation = cur;
            }
        }
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
     * An instance of ConstrainedProperty (rdfs:domain and and rdfs:range) that is used to indicate the (clas(es))
     * that the values of a property must me members of. The value of range property is alwsys a Class.
     * ... The rdfs:domain of rdfs:range is the class rdf:Property. This indicates that the range property
     * applies to resources that are themselves properties.
     * The rdfs:range of rdfs:range is the class rdfs:Class. This indicates that any resurce that is the value of
     * a range property will be a class.
     * section 3.1.4 (rdfs:domain)
     * ... The rdfs:domain of rdfs:domain is the class rdf:Property. This indicates that the domain property is
     * used on resources that are properties.
     * The rdfs:range of rdfsLdomain is the class rdfs:Class. This indicates that any resource taht is the
     * value of a domain property will be a class.
     *
     *
     * what this all means (at leas what I think it means ;)
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
        try {
            // create an empty model
            //Model model = new ModelMem();
            //model = new DAMLModelImpl();
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
            e.printStackTrace();
            throw new ParserException("Error in parsing RDF: " + e.getMessage());
        } catch (RDFError err) {
            throw new ParserException("Couldn't parse returned RDF data. Parser error: " + err.getMessage());
        }
        catch (NoSuchRelationLinkException relExc) {
            relExc.printStackTrace();
            throw new ParserException("Unrecognized EdgeType: " + relExc.getMessage());
        }
        ParserResult result = new ParserResult(new LinkedList(_nodesHash.values()), _edgesList);
        return result;
    }

	private void sortResourcesIntoClassesAndProperties (Model model) throws RDFException, ParserException {
        /// @todo following is an  attempt to classify rdf objects into Classes
        // and Properties. This may not work very well for some rdf files.
        Property typeProperty = new PropertyImpl(_rdfSyntaxTypeNamespace, "type");

        Resource classResource = new ResourceImpl(_rdfsNamespace, "Class");
        Resource propertyResource = new ResourceImpl(_rdfSyntaxTypeNamespace, "Property");

        Property damlComplementOfProperty = new PropertyImpl(_damlNamespace, "complementOf");

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

    /**
     *
     * @param s
     * @return
     */
    private boolean objectIsPropertyResource (Statement s) {
        Resource subject = s.getSubject();
        Property predicate = s.getPredicate();
        Object object = s.getObject();
        if (resourceRelationNodeTypesList.contains(subject)) {
            String predicateLocalName = predicate.getLocalName();
            if (predicatesConnectingPropertyToProperty.contains(predicateLocalName)) {
            	return true;
            }
        }
        return false;
    }

    /**
     *
     */
    private void findNamespaces(Model model) throws RDFException {
        NsIterator nsIterator = model.listNameSpaces();
        while (nsIterator.hasNext()) {
            String namespace = nsIterator.next();
            if (namespace.endsWith(_rdfSyntaxTypeNamespaceSuffix)) {
                _rdfSyntaxTypeNamespace = namespace;
            }
            /// @todo commented out is the better way to do this,
            // but in WebKB rdfs namespace ends with something
            // like "rdf-shema-199990808#" which is not usefull for us.
            int index = namespace.toString().indexOf(_rdfsNamespaceSuffix);
            if (index > 0) {
                //if (namespace.endsWith(_rdfsNamespaceSuffix)) {
                _rdfsNamespace = namespace;
            }

            /// @todo hacky way to find daml namespace, need better solution
            /// assuming that daml namespace looks something like: http://www.daml.org/.../../daml-ont#
            int damlIndex1 = namespace.toString().indexOf("daml");
            int damlIndex2 = namespace.toString().lastIndexOf("daml");
            if ((damlIndex1 > 0) && (damlIndex2 > 0) && (damlIndex1 != damlIndex2)) {
                _damlNamespace = namespace;
            }
        }
    }

    /**
     *
     */
    private List runSelector(Model model, Property p, Object o) throws RDFException, ParserException {
        LinkedList result = new LinkedList();

        StmtIterator stIt = model.listStatements();
        while (stIt.hasNext()) {
            Statement next = stIt.next();
            if (next.getPredicate().equals(p)) {
                if (next.getObject().equals(o)) {
                    result.add((Resource) next.getSubject());
                }
            }
        }
        return result;
    }

    /**
     * Process RDF statement and create corresponding graph nodes.
     */
    protected void processStatement(Statement st) throws NoSuchRelationLinkException, ParserException {
        Property predicate = st.getPredicate();
        Resource resource = st.getSubject();
        RDFNode object = st.getObject();
        Node subjectNode = doNodeMapping(resource);
        Node objectNode = doNodeMapping(object);
        doEdgesMapping(subjectNode, predicate, objectNode);
    }


    /**
     *
     */
    protected Node doNodeMapping (RDFNode object) throws ParserException {
        String nodeName = stripUri(object);
        Node node;
        if (_nodesHash.containsKey(nodeName)) {
            node = (Node) _nodesHash.get(nodeName);
        } else {
            node = new NodeImpl(nodeName, object.toString());
            if ((resourceRelationNodeTypesList.contains(object)) && (resourceConceptNodeTypesList.contains(object)) ) {
            	System.out.println("node " + node.getName() + " is CLASS and PROPERTY");
            }
            if (resourceConceptNodeTypesList.contains(object)) {
                // set node type to be concept node type
                node.setNodeType(nodeTypeConcept);
            }
            else if (resourceRelationNodeTypesList.contains(object)) {
                // set node type to be relation node type
                node.setNodeType(nodeTypeRelation);
            }
            else {
                System.out.println("resourceConceptNodeTypesList = " + resourceConceptNodeTypesList);
                throw new ParserException("RDF Resource '" + object + "'is neigher concept of property");
            }
            _nodesHash.put(nodeName, node);
        }
        return node;
    }

    /**
     *
     */
    protected void doEdgesMapping(Node subjectNode, Property predicate, Node objectNode) throws NoSuchRelationLinkException {
        List ontologyRelationRdfMapping = OntoramaConfig.getRelationRdfMapping();
        Iterator ontologyRelationRdfMappingIterator = ontologyRelationRdfMapping.iterator();
        boolean foundMapping = false;
        while (ontologyRelationRdfMappingIterator.hasNext()) {
            RdfMapping rdfMapping = (RdfMapping) ontologyRelationRdfMappingIterator.next();
            Iterator mappingTagsIterator = rdfMapping.getRdfTags().iterator();
            while (mappingTagsIterator.hasNext()) {
                String mappingTag = (String) mappingTagsIterator.next();
                if (predicate.getLocalName().endsWith(mappingTag)) {
                    String mappingType = rdfMapping.getType();
                    EdgeType edgeType = OntoramaConfig.getEdgeType(mappingType);
                    edgeType.setNamespace(predicate.getNameSpace());
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
                            System.out.println("Dont' know about RDF Property '" + predicate.getLocalName() + "'");
                            System.exit(-1);
                        }
                    } catch (NoSuchRelationLinkException e) {
                        e.printStackTrace();
                        System.err.println("NoSuchRelationLinkException: " + e.getMessage());
                        System.exit(-1);
                    }
                }
            }
        }
        if (!foundMapping) {
            System.err.println("unknown RDF tag for predicate: " + predicate.getLocalName());
        }
    }

    /**
     *
     */
    protected void addEdge(Node fromNode, EdgeType edgeType, Node toNode)
            throws NoSuchRelationLinkException {
//        String fromNodeName = stripUri(fromNode);
//        String toNodeName = stripUri(toNode);
//        Node fromNode = getGraphNodeByName(fromNodeName, fromNode.toString());
//        Node toNode = getGraphNodeByName(toNodeName, toNode.toString());

        Edge newEdge = new EdgeImpl(fromNode, toNode, edgeType);
        //System.out.println("creating edge: " + fromNode + ", " + toNode + ", edgeType = " + edgeType);
        _edgesList.add(newEdge);
    }


    /**
     * @todo    need to check if this rdfNode string contains any uri's, otherwise
     * may strip something that shouldn't be stripped if node happen to contain "/".
     * for example: description may contain '/': cats/dogs
     * maybe need to check if string starts with http:// ?
     */
    public String stripUri(RDFNode rdfNode) {
        return stripUri(rdfNode.toString());
    }

    /**
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


    /**
     *
     * @param nodeName
     * @param fullNodeName
     * @return
     */
    public Node getGraphNodeByName(String nodeName,
                                        String fullNodeName) {
        Node node;
        if (_nodesHash.containsKey(nodeName)) {
            node = (Node) _nodesHash.get(nodeName);
            return node;
        } else {
            node = new NodeImpl(nodeName, fullNodeName);
            _nodesHash.put(nodeName, node);
            return node;
        }
    }



    /**
     * Replace carriage returns and leading tabs in a string
     * so when time comes to display it we don't get funny characters
     * in the labels.
     * For example: if we have a comment spanning over a few lines
     * and formated to be indented in xml indentation fashion:
     * we will end up will all these white spaces in the labels.
     * The idea is: to break a string into lines, then remove all
     * leading and trailing white spaces replacing them with a single
     * space.
     *
     * @todo  there has to be a way to do this better
     */
    private String stripCarriageReturn(String inString) {
        String resultString = "";
        StringTokenizer stringTok = new StringTokenizer(inString, "\n");
        while (stringTok.hasMoreTokens()) {
            String nextTok = stringTok.nextToken();
            // break up into words. This accounts for a fact that sometimes
            // there are a few spaces grouped together. We want to remove them.
            StringTokenizer spacesTok = new StringTokenizer(nextTok, " ");
            while (spacesTok.hasMoreTokens()) {
                String tok = spacesTok.nextToken();
                resultString = resultString + tok.trim();
                if (spacesTok.hasMoreTokens()) {
                    resultString = resultString + " ";
                }
            }
        }
        return resultString;
    }

    public static void main(String args[]) {
        try {
            RdfDamlParser parser = new RdfDamlParser();
            String filename = "H:/projects/OntoRama/test/comms_comms_object-children.rdf";
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
