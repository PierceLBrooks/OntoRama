package ontorama.webkbtools.query.parser.rdf;

import java.io.Reader;
//import java.io.FileReader;
//import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
//import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Collection;

import java.security.AccessControlException;

import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.vocabulary.*;
import com.hp.hpl.jena.daml.*;
import com.hp.hpl.jena.daml.common.DAMLModelImpl;
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
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class RdfWebkbParser extends RdfDamlParser {

    /**
     *
     */
    protected void doRelationLinksMapping (Resource resource, Property predicate, RDFNode object) {
      String resourceName = stripUri(resource);
      String objectName = stripUri(object);
      OntologyType subjectType = getOntTypeByName(resourceName);

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
                OntologyType objectType = null;
                if (mappingType.endsWith("url")) {
                  objectType = getOntTypeByName(object.toString());
                }
                else {
                  objectType = getOntTypeByName(objectName);
                }
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

    }

}