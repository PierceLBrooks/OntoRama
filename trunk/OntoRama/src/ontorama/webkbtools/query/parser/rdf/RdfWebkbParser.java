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
 * <p>Description:
 * WebkbRdfParser - should behave the same way as RdfDamlParser except for
 * the treatment of relation link 'uri'.
 * </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class RdfWebkbParser extends RdfDamlParser {

    /**
     * add relation link to type corresponding to given resource
     * In WebKB case we need to be aware of relation link 'url' -
     * it doesn't need uri to be stripped
     *
     * Assumptions: in case of 'url' relation link we are assuming that
     * one of the types is created already!
     * @todo  check if the assumption is safe!
     */
    protected void addRelationLinkToType (RDFNode fromTypeResource, int relLinkId,
                            RDFNode toTypeResource, String linkName)
                            throws NoSuchRelationLinkException {
      String fromTypeName = stripUri(fromTypeResource);
      String toTypeName = stripUri(toTypeResource);

      OntologyType fromType = null;
      OntologyType toType = null;

      if (linkName.equals("url")) {
        if (ontTypeExists(fromTypeName)) {
          toType = getOntTypeByName(toTypeResource.toString());
          fromType = getOntTypeByName(fromTypeName);
        }
        else {
          fromType = getOntTypeByName(fromTypeResource.toString());
          toType = getOntTypeByName(toTypeName);
        }
      }
      else {
        fromType = getOntTypeByName(fromTypeName);
        toType = getOntTypeByName(toTypeName);
      }
      //System.out.println("relLink = " + linkName + ", fromType = " + fromType + ", toType = " + toType);
      fromType.addRelationType(toType,relLinkId);
    }
}