package ontorama.webkbtools.query.parser.rdf;

import java.io.FileReader;
import java.io.Reader;
import java.security.AccessControlException;
import java.util.*;

import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.*;
import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;
import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.util.*;

import com.hp.hpl.mesa.rdf.jena.common.*;
import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.Model;
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.vocabulary.RDF;
import com.hp.hpl.mesa.rdf.jena.vocabulary.RDFS;



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
     * Hashtable to hold all OntologyTypes that we are creating
     * keys - strings - ontology type names
     * values - OntologyType objects
     */
    private Hashtable _ontHash;
    
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
    }

    /**
     *
     */
    public Iterator getOntologyTypeIterator(Reader reader) throws ParserException, AccessControlException {
      return getOntologyTypeCollection(reader).iterator();
    }


    /**
     *
     * @todo  rewrite part where skipping statements if they are connected to rdf#Property
     * or rdf#Class - these properties shouldn't be hardcoded to start with...
     */
    public Collection getOntologyTypeCollection(Reader reader) throws ParserException, AccessControlException {
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
    		System.out.println("property = " + typeProperty);
    		
    		Resource classResource = new ResourceImpl(_rdfsNamespace, "Class");
    		System.out.println("class resource = " + classResource);
    		Resource propertyResource = new ResourceImpl(_rdfSyntaxTypeNamespace, "Property");
    		System.out.println("property resource = " + propertyResource);
            
			//runSelector(model, null, RDF.type, RDFS.Class);
			//runSelector(model, null, RDF.type, RDF.Property);
			List rdfClassesList = runSelector(model, null, typeProperty, classResource);
			List rdfPropertiesList = runSelector(model, null, typeProperty, propertyResource);

            // get Iterator of all subjects, then go through each of them
            // and get Iterator of statements. Process each statement           
            ResIterator resIt = model.listSubjects();

            while (resIt.hasNext()) {
                Resource r = resIt.next();
                StmtIterator stIt = r.listProperties();
                while (stIt.hasNext()) {
                    Statement s = stIt.next();

                    //System.out.println(s);
                    if (s.getPredicate().toString().endsWith("rdf-syntax-ns#type")) {
                    	if  (s.getObject().toString().endsWith("rdf-syntax-ns#Property")) {
                    		//System.out.println("skipping Property statement...");
                    		continue;
	                   	}
                    	if (s.getObject().toString().endsWith("#Class"))  {
                    		//System.out.println("skipping Class statement...");
                    		continue;
	                   	}
                	}

                    
                    /// @todo we are ignoring rdf properties for the moment
                    if (rdfClassesList.contains(s.getSubject())) {
                    	if (s.getPredicate().equals(RDFS.subClassOf)) {
                    		if ( !rdfClassesList.contains(s.getObject()) ) {
	                    		System.out.println("***found Class: " + s.getObject());
	                    		rdfClassesList.add(s.getObject());
                    		}
                    	}
                		processStatement(s);
                    }

//                    processStatement(s);
                }
            }
        }
        catch (AccessControlException secExc) {
                throw secExc;
        }
        catch (RDFException e) {
            e.printStackTrace();
            throw new ParserException("Error in parsing RDF: " + e.getMessage());
        }
        catch (RDFError err) {
          throw new ParserException("Couldn't parse returned RDF data. Parser error: " + err.getMessage());
        }
        //System.out.println("\n\nreturning result collection: " + ontHash.values().size());
        return _ontHash.values();
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
		ResIterator it = model.listSubjectsWithProperty(p,o);
		System.out.println("\n\n\n\n");
		int count = 0;
		while (it.hasNext()) {
			Resource res = it.next();
			System.out.println(res);
			result.add(res);
			count++;
		}
		System.out.println("count = " + count + "\n\n\n\n");
		return result;
	}

    /**
     * Process RDF statement and create corresponding Ontology types.
     *
     * @todo shouldn't strip URI's from resource name. This may create problems later if, for example,
     * user wants to specify resource uri for something, he/she will end up with only last component of it.
     */
    protected void processStatement (Statement st) {
      Property predicate = st.getPredicate();
      Resource resource = st.getSubject();
      RDFNode object = st.getObject();
      

      //System.out.println( "resource = " + resource + ", predicate = " + predicate + ", object = " + object);

      //System.out.println ("resource: local name = " + resource.getLocalName() + ", namespace = " + resource.getNameSpace()
      //                  + ", uri = " + resource.getURI());

      doConceptPropertiesMapping(resource, predicate, object);
      doRelationLinksMapping(resource, predicate, object);
   }

    /**
     *
     */
    protected void doRelationLinksMapping (Resource resource, Property predicate, RDFNode object) {
      List ontologyRelationRdfMapping = OntoramaConfig.getRelationRdfMapping();
      Iterator ontologyRelationRdfMappingIterator = ontologyRelationRdfMapping.iterator();
      while ( ontologyRelationRdfMappingIterator.hasNext() ) {
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

                //System.out.println("mappingType = " + mappingType);
                //System.out.println("relLinkDetails.getLinkName() = " + relLinkDetails.getLinkName());
                //System.out.println("relLinkDetails.getReversedLinkName() = " + relLinkDetails.getReversedLinkName());

                try {

                    if ( mappingType.equals(relLinkDetails.getLinkName()) ) {
                        //System.out.println("case 1");
                        addRelationLinkToType(resource, mappingId, object, relLinkDetails.getLinkName());
                        //subjectType.addRelationType(objectType,mappingId);
                    }
                    else if (mappingType.equals(relLinkDetails.getReversedLinkName()) ) {
                        //System.out.println("case 2");
                        addRelationLinkToType(object, mappingId, resource, relLinkDetails.getLinkName());
                        //objectType.addRelationType(subjectType, mappingId);
                    }
                    else {
                        // ERROR
                        // throw exception here
                        //System.out.println("case 3");
                        System.out.println("Dont' know about property '" + predicate.getLocalName() + "'");
                        java.awt.Toolkit.getDefaultToolkit().beep();
                        System.exit(-1);
                    }
                }
                catch (NoSuchRelationLinkException e) {
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
    protected void addRelationLinkToType (RDFNode fromTypeResource, int relLinkId,
                            RDFNode toTypeResource, String linkName)
                            throws NoSuchRelationLinkException {
      String fromTypeName = stripUri(fromTypeResource);
      String toTypeName = stripUri(toTypeResource);

      OntologyType fromType = getOntTypeByName(fromTypeName, fromTypeResource.toString());
      OntologyType toType = getOntTypeByName(toTypeName, toTypeResource.toString());

      fromType.addRelationType(toType,relLinkId);
    }

    /**
     *
     */
    protected void addConceptTypeProperty (RDFNode ontTypeResource, String propName,
                            RDFNode propValueResource)
                            throws NoSuchPropertyException {

      //
      String resourceName = stripUri(ontTypeResource);
      String propValueName = propValueResource.toString();
      OntologyType ontType = getOntTypeByName(resourceName, ontTypeResource.toString());
      ontType.addTypeProperty(propName, stripCarriageReturn(propValueName));
    }

    /**
     *
     */
    protected void doConceptPropertiesMapping (Resource resource, Property predicate, RDFNode object) {
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
                      addConceptTypeProperty(resource, mappingId, object);
                  }
                  else {
                     // ERROR
                      // throw exception here
                      System.out.println("Dont' know about property '" + predicate.getLocalName() + "'");
                  }
              }
              catch (NoSuchPropertyException propExc ) {
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
     public String stripUri (RDFNode rdfNode) {
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
     protected static String stripUri (String uriStr) {
        //System.out.println("***stripUri, rdfNode = " + rdfNode);
        StringTokenizer tokenizer = new StringTokenizer(uriStr,"/");
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
     * Return true if ontology type with given name is already created
     * @param ontTypeName
     * @return boolean
     */
    protected boolean ontTypeExists (String ontTypeName) {
      if (_ontHash.containsKey(ontTypeName)) {
        return true;
      }
      return false;
    }

    /**
     * Get OntologyType for given name. If this OntologyType is already created -
     * return it, otherwise - create a new one.
     * @param   ontTypeName
     * @return  OntologyType
     * @todo    need an exception if can't get OntologyType for some reason
     */
      public OntologyType getOntTypeByName (String ontTypeName,
      							String fullOntTypeName) {
        OntologyType ontType = null;
        if (_ontHash.containsKey(ontTypeName)) {
          ontType = (OntologyType) _ontHash.get(ontTypeName);
        }
        else {
          ontType = new OntologyTypeImplementation(ontTypeName, fullOntTypeName);
          //System.out.println("created type: " + ontType.toString());
          //System.out.println("full name = " + ontType.getFullName());
          _ontHash.put(ontTypeName,ontType);
        }
        return ontType;
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
    private String stripCarriageReturn (String inString) {
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

    public static void main (String args[]) {
        try {
            RdfDamlParser parser = new RdfDamlParser();

            //String filename = "H:/projects/OntoRama/test/hacked_comms_comms_object-children.rdf";
            //String filename = "H:/projects/OntoRama/test/comms-comms_object.html.daml";
            //String filename = "H:/projects/OntoRama/test/comms_comms_object-children.rdf.html";
            String filename = "H:/projects/OntoRama/test/comms_comms_object-children.rdf";
            //String filename = "H:/projects/OntoRama/test/wn_carnivore.rdf";
            System.out.println();
            System.out.println("filename = " + filename);
            Reader reader = new FileReader(filename);
            parser.getOntologyTypeIterator(reader);
            System.out.println();

        } catch (Exception e) {
            System.out.println("Failed: " + e);
            System.exit(-1);
        }
    }

}
