package ontorama.webkbtools.query.parser.rdf;

import java.io.Reader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Hashtable;
import java.util.StringTokenizer;

import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.vocabulary.*;


import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.util.ParserException;
import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;
import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.*;
import ontorama.webkbtools.util.NoSuchRelationLinkException;


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

    /**
     * RDF constants
     */
    private static final String synonymDef = "label";
    private static final String descriptionDef = "comment";
    private static final String subClassOfDef = "subClassOf";
    private static final String creatorDef = "Creator";
    private static final String partDef = "part";

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
    public Iterator getOntologyTypeIterator(Reader reader) throws ParserException {
        try {
            // create an empty model
            Model model = new ModelMem();
            model.read(reader, "");

            // get Iterator of all subjects, then go through each of them
            // and get Iterator of statements. Process each statement
            ResIterator resIt = model.listSubjects();

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
        OntologyType objectType = getOntTypeByName(objectName);

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
                    }
                }
                catch (NoSuchRelationLinkException e) {
                    System.err.println("NoSuchRelationLinkException: " + e.getMessage());
                    System.exit(-1);
                }
            }
            if (predicate.getLocalName().endsWith("comment")) {
                subjectType.setDescription(object.toString());
            }
            else if (predicate.getLocalName().endsWith("Creator")) {
                subjectType.setCreator(object.toString());
            }
//            else {
//                // ERROR
//                // throw exception here
//                System.out.println("Dont' know about property '" + predicate.getLocalName() + "'");
//            }
        }
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