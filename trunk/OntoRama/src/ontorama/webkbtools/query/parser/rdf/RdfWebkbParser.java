package ontorama.webkbtools.query.parser.rdf;

import java.io.Reader;
//import java.io.FileReader;
//import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Set;
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
     * keys - original ontology type name,
     * values  - new name
     */
    private Hashtable namesMapping = new Hashtable();

    /**
     * keys - name of newly created ontology type
     * values - new ontology type itself
     */
    private Hashtable newTypesMapping = new Hashtable();

    /**
     * name of url relation link (special case)
     */
    private String urlLinkName = "url";

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

      if (linkName.equals(urlLinkName)) {
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

    /**
     * Rewrite Iterator returned by the super class to a new
     * Iterator with Ontology Type names in the different format
     * (Get rid of RDF capitalization).
     *
     * First, we will build a mapping of old names to new names.
     * Then, process every OntologyType and create a new one with a new
     * name and change all relation links so they include new
     * named ontology types. (this is the reason for the first step - name
     * conversion is better achieved if there is already a mapping for names).
     *
     * @todo  think what to do with NoSuchPropertyException and NoSuchRelationLinkException
     */
    public Collection getOntologyTypeCollection(Reader reader) throws ParserException, AccessControlException {
      namesMapping = new Hashtable();
      newTypesMapping = new Hashtable();

      Collection col = super.getOntologyTypeCollection(reader);
      //System.out.println("col size = " + col.size());

      mapNewNames(col);

      try {
        List resultList = rewriteOntologyTypes(col);
        //System.out.println("new collection size: " + resultList.size());
        return (Collection) resultList;
      }
      catch (NoSuchPropertyException e1) {
        System.out.println("NoSuchPropertyException: " + e1);
        System.exit(-1);
      }
      catch (NoSuchRelationLinkException e2) {
        System.out.println("NoSuchRelationLinkException: " + e2);
        System.exit(-1);
      }
      //System.out.println("new collection size: null");
      return null;
    }

    /**
     * write out hashtable mapping old names to new
     */
    protected void mapNewNames (Collection col)  {
      Iterator it = col.iterator();
      while (it.hasNext()) {
        OntologyType cur = (OntologyType) it.next();
        //System.out.println("cur type = " + cur);
        mapNewName (cur);
      }
    }

    /**
     * find a new name for the given ontology type.
     * First, check if this name is already in the hashtable, if not - then
     * get a new name and put it into the hashtable.
     */
    protected void mapNewName ( OntologyType origType)  {
      String newTypeName = (String) namesMapping.get(origType.getName());
      if (newTypeName == null) {
        newTypeName = createNewNameForType(origType);
        //System.out.println("new name for " + origType.getName() + " is " + newTypeName);
        namesMapping.put(origType.getName(), newTypeName);
      }
    }

    /**
     * rewrite ontology types
     */
    protected List rewriteOntologyTypes (Collection col)
            throws NoSuchPropertyException, NoSuchRelationLinkException {
      LinkedList resultList = new LinkedList();

      Iterator it = col.iterator();
      while (it.hasNext()) {
        OntologyType cur = (OntologyType) it.next();
        OntologyType newType = rewriteOntologyType (cur);
        resultList.add(newType);
      }
      //System.out.println("result list size = " + resultList.size());
      return resultList;
    }

    /**
     * Create new ontology type that mirrors given ontology type, the
     * only difference is type naming - all names in the new ontology type
     * are in the new format.
     * This involves not only recreating ontology type itself, but mirroring
     * all relation links with destination types having new naming format as well.
     *
     * @todo  bug: types related by url rel link should not be decapitalized!!!
     *        for example: http://www.webkb.org/OntoRama is turned into
     *        http://www.webkb.org/_onto_rama. These types should have their
     *        original names intact!
     */
    protected OntologyType rewriteOntologyType ( OntologyType origType)
                  throws NoSuchPropertyException, NoSuchRelationLinkException {

      OntologyType newType = getNewTypeForOldByName(origType);

      // rewrite concept properties
      Enumeration propNamesEnum = OntoramaConfig.getConceptPropertiesTable().keys();
      while (propNamesEnum.hasMoreElements()) {
        String propName = (String) propNamesEnum.nextElement();
        List propValuesList = origType.getTypeProperty(propName);
        Iterator propValuesIterator = propValuesList.iterator();
        while (propValuesIterator.hasNext()) {
          String propValue = (String) propValuesIterator.next();
          newType.addTypeProperty(propName, propValue);
        }
      }

      // rewrite rel.links
      Set relLinksList = OntoramaConfig.getRelationLinksSet();
      Iterator relLinksIterator = relLinksList.iterator();
      while (relLinksIterator.hasNext()) {
        Integer relLink = (Integer) relLinksIterator.next();
        RelationLinkDetails relLinkDetails = OntoramaConfig.getRelationLinkDetails(relLink.intValue());
        Iterator relations = origType.getIterator(relLink.intValue());
        while (relations.hasNext()) {
          OntologyType relatedOrigType = (OntologyType) relations.next();
          OntologyType relatedNewType = null;
          /*
          if (relLinkDetails.getLinkName().equals(urlLinkName)) {
            // if relation link is of type 'url' relation link, then we shouldn't do
            // any case capitalization changes.
            relatedNewType = new OntologyTypeImplementation(relatedOrigType.getName());
          }
          else {
            relatedNewType = getNewTypeForOldByName(relatedOrigType);
          }
          */
          relatedNewType = getNewTypeForOldByName(relatedOrigType);
          //System.out.println("adding link " + newType.getName() + " -> " + relatedNewType.getName() + ", rel: + " + relLinkDetails.getLinkName());
          newType.addRelationType(relatedNewType, relLink.intValue());
        }
      }
      return newType;
    }

    /**
     * get new Ontology Type for given Ontology Type from namesMapping and
     * newTypesMapping hashtables.
     */
    protected OntologyType getNewTypeForOldByName (OntologyType origType) {
      String newTypeName = (String) namesMapping.get(origType.getName());
      if (newTypeName == null) {
        System.out.println("newTypeName = null, this shouldn't happen. Program has a logical error somewhere :(");
        System.exit(-1);
      }
      OntologyType newType = (OntologyType) newTypesMapping.get(newTypeName);
      if (newType == null) {
        newType = new OntologyTypeImplementation(newTypeName);
      }
      return newType;
    }

    /**
     *
     * Assumptions:
     * - if there is no hash character in the string, we are assuming
     * that this string doesn't need to be processed and just return it
     * - if string equals 'rdf-schema#Class', it shouldn't be reformatted,
     * just return it.
     *
     * @todo think what to do with NoSuchPropertyException
     */
    protected String createNewNameForType (OntologyType type) {

      String typeName = type.getName();

      if (typeName.equals("rdf-schema#Class")) {
        return typeName;
      }

      List synonyms = null;

      String typeNamePreffix = null;
      String typeNameSuffix = null;
      int hashIndex = typeName.indexOf("#");
      //System.out.println("typeName = " + typeName + ", hashIndex = " + hashIndex);
      if (hashIndex == -1 ) {
        typeNamePreffix = null;
        typeNameSuffix = typeName;
      }
      else {
        typeNamePreffix = typeName.substring( 0, hashIndex);
        typeNameSuffix = typeName.substring( hashIndex + 1, typeName.length());
      }
      //System.out.println("typeNamePreffix = " + typeNamePreffix + ", typeNameSuffix = " + typeNameSuffix);

      try {
        synonyms = type.getTypeProperty("Synonym");
        //System.out.println("synonyms list = " + synonyms);
      }
      catch (NoSuchPropertyException e) {
        System.out.println("property 'Synonyms' doesn't exist. Check config.xml file ");
        System.out.println("NoSuchPropertyException: " + e);
        System.exit(-1);
      }

      if (! synonyms.isEmpty()) {
        typeNameSuffix = (String) synonyms.iterator().next();
      }
      else {
        typeNameSuffix = reformatString(typeNameSuffix);
      }
      String res = typeNamePreffix + "#" + typeNameSuffix;
      if  (typeNamePreffix == null) {
        res = typeNameSuffix;
      }
      //System.out.println("__________returning " + res);
      return res;
    }


    /**
     * rewrite string that has java-style names, for
     * example: typeName, into string of words separated
     * by undescores, for example: type_name
     *
     */
    protected String reformatString (String in) {
      String res = "";

      if (in.length() == -1) {
          return in;
      }

      if (checkIfAllCharsAreCapitalized(in)) {
        return in;
      }

      for (int i = 0; i < in.length(); i++) {
        char ch = in.charAt(i);
        Character chObj = new Character (ch);
        //System.out.println("i = " + i + ", char = " + ch);
        if ( chObj.isUpperCase(ch) ) {
          if (i == 0) {
            // need this so we don't end up with something like
            // "_true_cat" instead of "true_cat"
            res = res + (chObj.toString()).toLowerCase();
          }
          else {
            res = res + "_" + (chObj.toString()).toLowerCase();
          }
        }
        else {
          res = res + chObj.toString();
        }
      }
      return res;
    }

    /**
     * check if all chars in given string are capitalized.
     * need this check to assolate cases like "ADSL", etc.
     */
    protected  boolean checkIfAllCharsAreCapitalized (String str) {
      boolean allCapitals = true;
      for (int i = 0; i < str.length(); i++) {
        char ch = str.charAt(i);
        Character chObj = new Character (ch);
        if ( chObj.isLowerCase(ch) ) {
          allCapitals = false;
        }
      }
      return allCapitals;
    }


}