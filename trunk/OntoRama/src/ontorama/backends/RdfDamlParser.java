package ontorama.backends;

import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;
import com.hp.hpl.mesa.rdf.jena.common.ResourceImpl;
import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.vocabulary.RDFS;
import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.ConceptPropertiesDetails;
import ontorama.ontologyConfig.ConceptPropertiesMapping;
import ontorama.ontologyConfig.RdfMapping;
import ontorama.ontologyConfig.RelationLinkDetails;
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
 * 
 * 
 */

public class RdfDamlParser {

    /**
     * Hashtable to hold all OntologyTypes that we are creating
     * keys - strings - ontology type names
     * values - OntologyType objects
     */
    private Hashtable _ontHash;
	
	private ExtendedGraph _graph;

    //Added by Henrik
    private Hashtable nodes;
    private Hashtable properties;
    private Vector edges; 
    
    //end added
    
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
        _ontHash = new Hashtable();
        //Added by henrik
        nodes = new Hashtable();
        properties = new Hashtable();
        edges = new Vector();
        System.err.println("Starts a new Parser");
        //end added
    }
    

    /**
     *
     * @todo  rewrite part where skipping statements if they are connected to rdf#Property
     * or rdf#Class - these properties shouldn't be hardcoded to start with...
     */
    public void parse(Reader reader, ExtendedGraph graph, String owner) throws ParserException, AccessControlException, NoSuchPropertyException {
        try {
			this._graph = graph;            
            // create an empty model
            //Model model = new ModelMem();
            //model = new DAMLModelImpl();
            Model model = new ModelMem();
            model.read(reader, "");

            findNamespaces(model);
        

            /// @todo following is an  attempt to classify rdf objects into Classes
            // and Properties. This may not work very well for some rdf files.
            Property typeProperty = new PropertyImpl(_rdfSyntaxTypeNamespace, "type");
            System.err.println("property = " + typeProperty);

            Resource classResource = new ResourceImpl(_rdfsNamespace, "Class");
            System.err.println("class resource = " + classResource);
            Resource propertyResource = new ResourceImpl(_rdfSyntaxTypeNamespace, "Property");
            System.err.println("property resource = " + propertyResource);

            //runSelector(model, null, RDF.type, RDFS.Class);
            //runSelector(model, null, RDF.type, RDF.Property);
            List rdfClassesList = runSelector(model, null, typeProperty, classResource);
            List rdfPropertiesList = runSelector(model, null, typeProperty, propertyResource);

            // get Iterator of all subjects, then go through each of them
            // and get Iterator of statements. Process each statement
            ResIterator resIt = model.listSubjects();

            while (resIt.hasNext()) {
                Resource r = resIt.next();
                //Added by Henrik
                //When a resource is found we construct a exended Graphnode
                //to represent it and put this node into a hashtable for later use.
                  //System.err.println("This a resourc found: URI = " + r.getURI());
                  if (nodes.get(r.getURI()) == null){
                     GraphNode newNode = new GraphNode(stripUri(r.getURI()), r.getURI());
                     String key = r.getURI();
                     nodes.put(key, newNode);  
                     }
                //end added   
                StmtIterator stIt = r.listProperties();
                while (stIt.hasNext()) {
                   Statement s = stIt.next();
                      if (s.getPredicate().toString().endsWith("rdf-syntax-ns#type")) {
                        if (s.getObject().toString().endsWith("rdf-syntax-ns#Property")) {
                            //System.out.println("skipping Property statement...");
                            continue;
                        }
                        if (s.getObject().toString().endsWith("#Class")) {
                            //System.out.println("skipping Class statement...");
                            continue;
                        }
                    }


                    /// @todo we are ignoring rdf properties for the moment
                    if (rdfClassesList.contains(s.getSubject())) {
                        if (s.getPredicate().equals(RDFS.subClassOf)) {
                            if (!rdfClassesList.contains(s.getObject())) {
                                System.out.println("***found Class: " + s.getObject());
                                rdfClassesList.add(s.getObject());
                            }
                        }
                        processStatement(s);
                    }
            //processStatement(s);
      
       //Henrik changed
                }
           //All the information are added to a node and we can put it into the graph
             GraphNode tmpNode = (GraphNode)nodes.get(r.getURI());

            //System.out.println(tmpNode.getName());
            /*           
            if (tmpNode.getName().equals("wn#Tail")){
                System.out.println("Set the root node to: " + tmpNode.getFullName());
                graph.setRoot(tmpNode.getFullName());
            }
            */
             graph.addNode(tmpNode);
             properties.clear();
            
            /*
            * System.err.println("The node we are adding is:" + tmpNode);            
            *  try{
            *       List v = tmpNode.getProperty("Synonym");
            *       Iterator it = v.iterator();
            *       while(it.hasNext()){
            *       System.out.println(it.next());
            *       }
            *    } catch (NoSuchPropertyException e) {
            *
            *    }
            */
            }
            
          //Now when all nodes are added we can add the edges 
           Enumeration edgesToAdd = edges.elements();

           while(edgesToAdd.hasMoreElements()){
                EdgeObject edgeToAdd = (EdgeObject) edgesToAdd.nextElement();
                GraphNode fromNode = edgeToAdd.getFromNode();
                GraphNode toNode = edgeToAdd.getToNode();
               
                graph.addEdge(fromNode.getFullName(), toNode.getFullName(), edgeToAdd.getType(), edgeToAdd.getNamespace());
               
           }
              
       //end changes

        } catch (AccessControlException secExc) {
            throw secExc;
        } catch (RDFException e) {
            e.printStackTrace();
            throw new ParserException("Error in parsing RDF: " + e.getMessage());
        } catch (RDFError err) {
            throw new ParserException("Couldn't parse returned RDF data. Parser error: " + err.getMessage());
        }
       }


    /**
     *
     */
    private void findNamespaces(Model model) throws RDFException {
        NsIterator nsIterator = model.listNameSpaces();
        while (nsIterator.hasNext()) {
            String namespace = (String) nsIterator.next();
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
    private List runSelector(Model model, Resource r, Property p, Object o) throws RDFException {
        //Selector selectorClasses = new SelectorImpl(r, p, o);
        //ResIterator it = model.listSubjects();
        LinkedList result = new LinkedList();
        ResIterator it = model.listSubjectsWithProperty(p, o);
        //System.out.println("\n\n\n\n");
        int count = 0;
        while (it.hasNext()) {
            Resource res = it.next();
            //System.out.println(res);
            result.add(res);
            count++;
        }
        //System.out.println("count = " + count + "\n\n\n\n");
        return result;
    }

    /**
     * Process RDF statement and create corresponding Ontology types.
     *
     * @todo shouldn't strip URI's from resource name. This may create problems later if, for example,
     * user wants to specify resource uri for something, he/she will end up with only last component of it.
     */
    protected void processStatement(Statement st) {
        Property predicate = st.getPredicate();
        Resource resource = st.getSubject();
        RDFNode object = st.getObject();
       
        doConceptPropertiesMapping(resource, predicate, object);
        doRelationLinksMapping(resource, predicate, object);
    }

    /**
     *
     */
    protected void doRelationLinksMapping(Resource resource, Property predicate, RDFNode object) {
        List ontologyRelationRdfMapping = OntoramaConfig.getRelationRdfMapping();
        Iterator ontologyRelationRdfMappingIterator = ontologyRelationRdfMapping.iterator();
        while (ontologyRelationRdfMappingIterator.hasNext()) {
            RdfMapping rdfMapping = (RdfMapping) ontologyRelationRdfMappingIterator.next();
            Iterator mappingTagsIterator = rdfMapping.getRdfTags().iterator();
            while (mappingTagsIterator.hasNext()) {
                //String mappingTag = rdfMapping.getRdfTag();
                String mappingTag = (String) mappingTagsIterator.next();
                //System.out.println("mappingTag = " + mappingTag + ", id = " + rdfMapping.getId());
                if (predicate.getLocalName().endsWith(mappingTag)) {
                    int mappingId = rdfMapping.getId();
                    //System.out.println("MATCHED mappingTag = " + mappingTag);
                    String mappingType = rdfMapping.getType();
                    RelationLinkDetails relLinkDetails = OntoramaConfig.getRelationLinkDetails(mappingId);

                    try {

                        if (mappingType.equals(relLinkDetails.getLinkName())) {
                            //System.out.println("case 1");
                            addRelationLinkToType(resource, mappingId, object, relLinkDetails.getLinkName(),predicate.getNameSpace());
                            //subjectType.addRelationType(objectType,mappingId);
                        } else if (mappingType.equals(relLinkDetails.getReversedLinkName())) {
                            //System.out.println("case 2");
                            addRelationLinkToType(object, mappingId, resource, relLinkDetails.getLinkName(),predicate.getNameSpace());
                            
                              
                            //objectType.addRelationType(subjectType, mappingId);
                        } else {
                            // ERROR
                            // throw exception here
                            //System.out.println("case 3");
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
    //Changed by henrik
    //This mehtod will be used to build a relation in a Graph instead on RelationTyp 
    protected void addRelationLinkToType(RDFNode fromResource, int relLinkId,
                                         RDFNode toResource, String linkName, String nameSpace)
            throws NoSuchRelationLinkException {

        if (relLinkId < 0 || relLinkId > OntoramaConfig.MAXTYPELINK) {
            throw new NoSuchRelationLinkException(relLinkId, OntoramaConfig.MAXTYPELINK);
        }


        GraphNode fromNode = (GraphNode) nodes.get(fromResource.toString());
        GraphNode toNode = (GraphNode) nodes.get(toResource.toString());
        //If any of the nodes do not exist we create it and will add information 
        //to it later
        if(fromNode == null){
            fromNode = new GraphNode(stripUri(fromResource), fromResource.toString());
            nodes.put(fromResource.toString(), fromNode);
            this._graph.addNode(fromNode);
        }
        if(toNode == null) {
            toNode = new GraphNode(stripUri(toResource), toResource.toString());
            nodes.put(toResource.toString(), toNode);
			this._graph.addNode(toNode);
        } 
      
        EdgeObject tmpEdge = new EdgeObject(fromNode, toNode, relLinkId, nameSpace);
        
        //The relations should be added after all the nodes and therefor they are
        //save in a Vecor until the nodes are added.
        edges.add(tmpEdge);      
       }
    //end changed

    /**
     *
     */
    //Changed by henrik
    //This mehtod will be used to add properties to nodes in a Graph instead
    //of TypeProperties
    //This method does only consider the namespace for the last property 
    //(with the same property name) added
    protected void addConceptTypeProperty(RDFNode ontResource, String propName,
                                          RDFNode propValueResource, String nameSpace)
            throws NoSuchPropertyException {
       //Check so the property you want to add is a allowed
       if (OntoramaConfig.getConceptPropertiesTable().containsKey(propName)) {  
            String propValueName = propValueResource.toString();
     
            GraphNode node = (GraphNode) nodes.get(ontResource.toString());
            List l = (List) properties.get(propName);
            if (l == null){
               Collections.synchronizedList(l = new LinkedList());   
            }
            else if (l.contains(propValueName)) {
                    // already is in the list
                    return;
            }
            l.add(stripCarriageReturn(propValueName));
            properties.put(propName, l);

            //System.out.println("RDFDamlParser::SetProperty(" + propName +", " + nameSpace);
            node.setProperty(propName, nameSpace, l);
            
            
            //node.setProperty(propName, l);
          
      } else {
            throw new NoSuchPropertyException(propName, OntoramaConfig.getConceptPropertiesTable().keys());
        }
    
     }
     //
    //end changed
    
    /**
     *
     */
    protected void doConceptPropertiesMapping(Resource resource, Property predicate, RDFNode object) {
      
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
                        addConceptTypeProperty(resource, mappingId, object, predicate.getNameSpace());
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
//        //System.out.println("***stripUri, rdfNode = " + rdfNode);
//        StringTokenizer tokenizer = new StringTokenizer(rdfNode.toString(),"/");
//        int count = 0;
//        int tokensNumber = tokenizer.countTokens();
//        while (tokenizer.hasMoreTokens()) {
//            count++;
//            String token = tokenizer.nextToken();
//            if (count == tokensNumber) {
//                //System.out.println("returning token = " + token);
//                return token;
//            }
//        }
//        return rdfNode.toString();

    }

    /**
     * @todo    need to check if this rdfNode string contains any uri's, otherwise
     * may strip something that shouldn't be stripped if node happen to contain "/".
     * for example: description may contain '/': cats/dogs
     * maybe need to check if string starts with http:// ?
     */
    protected static String stripUri(String uriStr) {
        //System.out.println("***stripUri, rdfNode = " + rdfNode);
        StringTokenizer tokenizer = new StringTokenizer(uriStr, "/");
        int count = 0;
        int tokensNumber = tokenizer.countTokens();
        while (tokenizer.hasMoreTokens()) {
            count++;
            String token = tokenizer.nextToken();
            if (count == tokensNumber) {
                //System.out.println("returning token = " + token);
                return token;
            }
        }
        return uriStr;
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
                //System.out.println ("--- " + tok);
                //resultString = resultString + " " + tok.trim();
                resultString = resultString + tok.trim();
                if (spacesTok.hasMoreTokens()) {
                    resultString = resultString + " ";
                }
            }
            //resultString = resultString + " " + nextTok.trim();
        }
        return resultString;
    }

    public static void main(String args[]) {
        try {
            RdfDamlParser parser = new RdfDamlParser();

            //String filename = "H:/projects/OntoRama/test/hacked_comms_comms_object-children.rdf";
            //String filename = "H:/projects/OntoRama/test/comms-comms_object.html.daml";
            //String filename = "H:/projects/OntoRama/test/comms_comms_object-children.rdf.html";
            String filename = "C:/OntoRama/examples/wn_tail2.rdf";
            //String filename = "H:/projects/OntoRama/test/wn_carnivore.rdf";
            System.out.println();
            System.out.println("filename = " + filename);
            Reader reader = new FileReader(filename);
            ExtendedGraph gr = new ExtendedGraph();
            parser.parse(reader, gr, "olle");
            //System.err.println("Now is the XMLgraph printing");
            //System.out.println(gr.printXml());
   
            System.err.println("Now is the graph printing");
            System.out.println(gr.toRDFString());
               
        } catch (Exception e) {
            System.out.println("Failed: ");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}