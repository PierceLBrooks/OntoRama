package ontorama.webkbtools.query.parser.rdf;

import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;
import com.hp.hpl.mesa.rdf.jena.common.ResourceImpl;
import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.vocabulary.RDFS;
import ontorama.OntoramaConfig;
import ontorama.model.EdgeImpl;
import ontorama.model.GraphNode;
import ontorama.model.Edge;
import ontorama.ontologyConfig.ConceptPropertiesDetails;
import ontorama.ontologyConfig.ConceptPropertiesMapping;
import ontorama.ontologyConfig.RdfMapping;
import ontorama.ontologyConfig.RelationLinkDetails;
import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.webkbtools.util.NoSuchPropertyException;
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
    /// @this definition doesnt work due to the schema namespace WebKB is using
    //private static final String _rdfsNamespaceSuffix = "rdf-schema#";
    private static final String _rdfsNamespaceSuffix = "rdf-schema";
    private static final String _rdfSyntaxTypeNamespaceSuffix = "rdf-syntax-ns#";

    /**
     * @todo these should not be here
     */
    private static final int CLASS = 1;
    private static final int PROPERTY = 2;


    /**
     * Constructor
     */
    public RdfDamlParser() {
        _nodesHash = new Hashtable();
        _edgesList = new LinkedList();
    }


    /**
     *
     * @todo  rewrite part where skipping statements if they are connected to rdf#Property
     * or rdf#Class - these properties shouldn't be hardcoded to start with...
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

            List rdfClassesList = runSelector(model, typeProperty, classResource);
            List rdfPropertiesList = runSelector(model, typeProperty, propertyResource);

            // get Iterator of all subjects, then go through each of them
            // and get Iterator of statements. Process each statement
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

                    /// @todo we are ignoring rdf properties for the moment
                    if (rdfClassesList.contains(s.getSubject())) {
                        if (s.getPredicate().equals(RDFS.subClassOf)) {
                            if (!rdfClassesList.contains(s.getObject())) {
                                rdfClassesList.add(s.getObject());
                            }
                        }
                        processStatement(s);
                    }
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
        }
    }

    /**
     *
     */
    private List runSelector(Model model, Property p, Object o) throws RDFException {
        //Selector selectorClasses = new SelectorImpl(r, p, o);
        //ResIterator it = model.listSubjects();
        LinkedList result = new LinkedList();
        ResIterator it = model.listSubjectsWithProperty(p, o);
        while (it.hasNext()) {
            Resource res = it.next();
            result.add(res);
        }
        return result;
    }

    /**
     * Process RDF statement and create corresponding graph nodes.
     */
    protected void processStatement(Statement st) {
        Property predicate = st.getPredicate();
        Resource resource = st.getSubject();
        RDFNode object = st.getObject();
        doNodePropertiesMapping(resource, predicate, object);
        doEdgesMapping(resource, predicate, object);
    }

    /**
     *
     */
    protected void doEdgesMapping(Resource resource, Property predicate, RDFNode object) {
        List ontologyRelationRdfMapping = OntoramaConfig.getRelationRdfMapping();
        Iterator ontologyRelationRdfMappingIterator = ontologyRelationRdfMapping.iterator();
        while (ontologyRelationRdfMappingIterator.hasNext()) {
            RdfMapping rdfMapping = (RdfMapping) ontologyRelationRdfMappingIterator.next();
            Iterator mappingTagsIterator = rdfMapping.getRdfTags().iterator();
            while (mappingTagsIterator.hasNext()) {
                String mappingTag = (String) mappingTagsIterator.next();
                if (predicate.getLocalName().endsWith(mappingTag)) {
                    int mappingId = rdfMapping.getId();
                    String mappingType = rdfMapping.getType();
                    RelationLinkDetails relLinkDetails = OntoramaConfig.getRelationLinkDetails(mappingId);

                    try {
                        if (mappingType.equals(relLinkDetails.getLinkName())) {
                            addEdge(resource, relLinkDetails, object);
                        } else if (mappingType.equals(relLinkDetails.getReversedLinkName())) {
                            addEdge(object, relLinkDetails, resource);
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
    protected void addEdge(RDFNode fromNodeResource, RelationLinkDetails edgeType, RDFNode toNodeResource)
            throws NoSuchRelationLinkException {
        String fromNodeName = stripUri(fromNodeResource);
        String toNodeName = stripUri(toNodeResource);
        GraphNode fromNode = getGraphNodeByName(fromNodeName, fromNodeResource.toString());
        GraphNode toNode = getGraphNodeByName(toNodeName, toNodeResource.toString());

        Edge newEdge = new EdgeImpl(fromNode, toNode, edgeType);
        _edgesList.add(newEdge);
    }

    /**
     *
     */
    protected void addNodeProperty(RDFNode ontNodeResource, String propName,
                                   RDFNode propValueResource)
            throws NoSuchPropertyException {

        String resourceName = stripUri(ontNodeResource);
        String propValueName = propValueResource.toString();
        GraphNode node = getGraphNodeByName(resourceName, ontNodeResource.toString());
        List nodePropertiesList = node.getProperty(propName);
        nodePropertiesList.add(stripCarriageReturn(propValueName));
        node.setProperty(propName, nodePropertiesList);
    }


    /**
     *
     */
    protected void doNodePropertiesMapping(Resource resource, Property predicate, RDFNode object) {
        Hashtable conceptPropertiesRdfMapping = OntoramaConfig.getConceptPropertiesRdfMapping();
        Enumeration e = conceptPropertiesRdfMapping.elements();
        while (e.hasMoreElements()) {
            ConceptPropertiesMapping conceptRdfMapping = (ConceptPropertiesMapping) e.nextElement();
            String mappingTag = conceptRdfMapping.getRdfTag();
            if (predicate.getLocalName().endsWith(mappingTag)) {
                // found rdf element/resource that is matching mapping tag. Now
                // need to find out what concept property name/id corresponds
                // to this mapping tag.
                String mappingId = conceptRdfMapping.getId();
                // now we need to map this id/name to ConceptPropertiesDetails
                ConceptPropertiesDetails conceptPropertiesDetails = OntoramaConfig.getConceptPropertiesDetails(mappingId);
                try {
                    if (conceptPropertiesDetails != null) {
                        // add this info as a property of ontology type
                        addNodeProperty(resource, mappingId, object);
                    } else {
                        // ERROR
                        // throw exception here
                        System.out.println("Dont' know about property '" + predicate.getLocalName() + "'");
                    }
                } catch (NoSuchPropertyException propExc) {
                    System.err.println("NoSuchPropertyException: " + propExc);
                    System.exit(-1);
                }
            }
        }
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
    public GraphNode getGraphNodeByName(String nodeName,
                                        String fullNodeName) {
        GraphNode node;
        if (_nodesHash.containsKey(nodeName)) {
            node = (GraphNode) _nodesHash.get(nodeName);
            return node;
        } else {
            node = new GraphNode(nodeName, fullNodeName);
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
