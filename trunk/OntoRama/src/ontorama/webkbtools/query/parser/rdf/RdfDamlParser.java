package ontorama.webkbtools.query.parser.rdf;

import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;
import com.hp.hpl.mesa.rdf.jena.common.ResourceImpl;
import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.DAML_OIL;
import com.hp.hpl.jena.daml.DAMLClass;
import com.hp.hpl.jena.daml.DAMLOntology;
import ontorama.OntoramaConfig;
import ontorama.model.*;
import ontorama.ontologyConfig.RdfMapping;
import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.util.ParserException;

import java.io.FileReader;
import java.io.Reader;
import java.security.AccessControlException;
import java.util.*;


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

    /// @todo this definition doesnt work due to the schema namespace WebKB is using
    //private static final String _rdfsNamespaceSuffix = "rdf-schema#";
    private static final String _rdfsNamespaceSuffix = "rdf-schema";
    private static final String _rdfSyntaxTypeNamespaceSuffix = "rdf-syntax-ns#";


    private static NodeType nodeTypeConcept;
    private static NodeType nodeTypeRelation;

    /**
     * hold RDF resources that are potentials to be concept node types
     * or relation node types.
     */
    private List resourceConceptNodeTypesList;
    private List resourceRelationNodeTypesList;

    /**
     * Constructor
     */
    public RdfDamlParser() {
        _nodesHash = new Hashtable();
        _edgesList = new LinkedList();

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

            /// @todo following is an  attempt to classify rdf objects into Classes
            // and Properties. This may not work very well for some rdf files.
            Property typeProperty = new PropertyImpl(_rdfSyntaxTypeNamespace, "type");

            Resource classResource = new ResourceImpl(_rdfsNamespace, "Class");
            Resource propertyResource = new ResourceImpl(_rdfSyntaxTypeNamespace, "Property");

            Property damlComplementOfProperty = new PropertyImpl(_damlNamespace, "complementOf");
            System.out.println("daml compelemtn of prop " + damlComplementOfProperty);

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
                    System.out.println("\n" + s);

                    System.out.println("processing object " + s.getObject());

                    // @todo we are assuming the only properties that could connect concept and relation types
                    // is DAML_OIL.compelementOf, RDFS.domain and RDFS.range. Not sure if this is a fair assumption!
                    if (resourceConceptNodeTypesList.contains(s.getSubject())) {
                        if (s.getPredicate().equals(damlComplementOfProperty)) {
                            resourceRelationNodeTypesList.add(s.getObject());
                            System.out.println("1");
                            System.out.println("relation");
                        }
                        else {
                            if (!resourceConceptNodeTypesList.contains(s.getObject())) {
                                resourceConceptNodeTypesList.add(s.getObject());
                                System.out.println("concept");
                                System.out.println("2");
                            }
                        }
                    }
                    if (resourceRelationNodeTypesList.contains(s.getSubject())) {
                        //if ( (s.getPredicate().equals(damlComplementOfProperty))
                        //                    || (s.getPredicate().equals(RDFS.domain))
                        //                    || (s.getPredicate().equals(RDFS.range)) ) {
                        if ( ! s.getPredicate().equals(RDFS.subPropertyOf)) {
                            resourceConceptNodeTypesList.add(s.getObject());
                            System.out.println("concept");
                            System.out.println("3");
                        }
                        else {
                            resourceRelationNodeTypesList.add(s.getObject());
                            System.out.println("relation");
                            System.out.println("4");
                        }
                    }
                }
            }

            resIt = model.listSubjects();

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
        while (ontologyRelationRdfMappingIterator.hasNext()) {
            RdfMapping rdfMapping = (RdfMapping) ontologyRelationRdfMappingIterator.next();
            Iterator mappingTagsIterator = rdfMapping.getRdfTags().iterator();
            while (mappingTagsIterator.hasNext()) {
                String mappingTag = (String) mappingTagsIterator.next();
                if (predicate.getLocalName().endsWith(mappingTag)) {
                    String mappingType = rdfMapping.getType();
                    EdgeType edgeType = OntoramaConfig.getEdgeType(mappingType);
                    try {
                        if (mappingType.equals(edgeType.getName())) {
                            addEdge(subjectNode, edgeType, objectNode);
                        } else if (mappingType.equals(edgeType.getReverseEdgeName())) {
                            addEdge(objectNode, edgeType, subjectNode);
                        } else {
                            // ERROR
                            // throw exception here
                            System.out.println("Dont' know about property '" + predicate.getLocalName() + "'");
                            java.awt.Toolkit.getDefaultToolkit().beep();
                            System.exit(-1);
                        }
                    } catch (NoSuchRelationLinkException e) {
                        System.err.println("NoSuchRelationLinkException: " + e.getMessage());
                        System.exit(-1);
                    }
                }
            }
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
