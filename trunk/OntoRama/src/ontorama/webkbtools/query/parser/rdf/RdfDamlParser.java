package ontorama.webkbtools.query.parser.rdf;

import java.io.Reader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Enumeration;

import java.security.AccessControlException;

import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.vocabulary.*;
import com.hp.hpl.jena.daml.*;
import com.hp.hpl.jena.daml.common.*;


import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.util.ParserException;
import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;
import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.*;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.util.NoSuchPropertyException;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class RdfDamlParser implements Parser {

    /**
     * Hashtable to hold all OntologyTypes that we are creating
     */
    private Hashtable ontHash;


    // default rdf type
    private static final String typeDef = "type";

    /**
     * Constructor
     */
    public RdfDamlParser() {
        ontHash = new Hashtable();
    }

    /**
     *
     */
    public Iterator getOntologyTypeIterator(Reader reader) throws ParserException, AccessControlException {
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("RDFDamlParser, method getOntologyTypeIterator");
        try {
            DAMLModelImpl model;

            System.out.println("...will try to create rdf model DAMLModelImpl");
            // create an empty model
            //Model model = new ModelMem();
            model = new DAMLModelImpl();
            System.out.println("...created rdf model, will call reader now");
            model.read(reader, "");
            System.out.println("...called reader, will try to get iterator of subjects");

            // get Iterator of all subjects, then go through each of them
            // and get Iterator of statements. Process each statement
            ResIterator resIt = model.listSubjects();

            System.out.println("... iterating");

            while (resIt.hasNext()) {
                Resource r = resIt.next();
                //System.out.println("----------------------------------------");
                //System.out.println("resource: " + r);
                StmtIterator stIt = r.listProperties();
                while (stIt.hasNext()) {
                    Statement s = stIt.next();
                    processStatement(s);
                }
            }
        }
		catch (AccessControlException secExc) {
			throw secExc;
		}		
        catch (RDFException e) {
            throw new ParserException("Error in parsing RDF: " + e.getMessage());
        }
        return ontHash.values().iterator();
    }

    /**
     *
     */
    private void processStatement (Statement st) {
        Property predicate = st.getPredicate();
        Resource resource = st.getSubject();
        String resourceName = stripUri(resource);
        RDFNode object = st.getObject();
        String objectName = stripUri(object);

        //System.out.println("predicate = " + predicate + ", resource = " + resourceName + ", object = " + objectName);

        OntologyType subjectType = getOntTypeByName(resourceName);
        //System.out.println("created subjectType = " + subjectType.getName());


        List ontologyRelationRdfMapping = OntoramaConfig.getRelationRdfMapping();
        Iterator ontologyRelationRdfMappingIterator = ontologyRelationRdfMapping.iterator();
        while ( ontologyRelationRdfMappingIterator.hasNext() ) {
            RdfMapping rdfMapping = (RdfMapping) ontologyRelationRdfMappingIterator.next();
            String mappingTag = rdfMapping.getRdfTag();
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
                  OntologyType objectType = getOntTypeByName(objectName);
                  //System.out.println("created objectType = " + objectType.getName());

                    if ( mappingType.equals(relLinkDetails.getLinkName()) ) {
                        //System.out.println("case 1");
                        subjectType.addRelationType(objectType,mappingId);
                        //System.out.println(subjectType.getName() + " -> " + objectType.getName() + ", rel = " + mappingId);
                    }
                    else if (mappingType.equals(relLinkDetails.getReversedLinkName()) ) {
                        //System.out.println("case 2");
                        objectType.addRelationType(subjectType, mappingId);
                        //System.out.println(objectType.getName() + " -> " + subjectType.getName() + ", rel = " + mappingId);
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
                        subjectType.addTypeProperty(mappingId,object.toString());
                        //System.out.println("type = " + subjectType.getName() + ", adding propertyName = " + mappingId + ", value = " + object.toString());
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
//            if (predicate.getLocalName().endsWith("comment")) {
//                subjectType.setDescription(object.toString());
//            }
//            else if (predicate.getLocalName().endsWith("Creator")) {
//                subjectType.setCreator(object.toString());
//            }
    }

    /**
     * @todo    need to check if this rdfNode string contains any uri's, otherwise
     * may strip something that shouldn't be stripped if node happen to contain "/".
     * for example: description may contain '/': cats/dogs
     * maybe need to check if string starts with http:// ?
     */
     public String stripUri (RDFNode rdfNode) {
        //System.out.println("***stripUri, rdfNode = " + rdfNode);
        StringTokenizer tokenizer = new StringTokenizer(rdfNode.toString(),"/");
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
        return rdfNode.toString();

     }

    /**
     * Get OntologyType for given name. If this OntologyType is already created -
     * return it, otherwise - create a new one.
     * @param   ontTypeName
     * @return  OntologyType
     * @todo    need an exception if can't get OntologyType for some reason
     */
      public OntologyType getOntTypeByName (String ontTypeName) {
        OntologyType ontType = null;
        if (ontHash.containsKey(ontTypeName)) {
          ontType = (OntologyType) ontHash.get(ontTypeName);
        }
        else {
          ontType = new OntologyTypeImplementation(ontTypeName);
          ontHash.put(ontTypeName,ontType);
        }
        return ontType;
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
