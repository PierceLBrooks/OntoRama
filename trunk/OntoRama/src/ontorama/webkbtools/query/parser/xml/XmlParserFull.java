
package ontorama.webkbtools.query.parser.xml;

/**
 * Title:        OntoRama
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 * @author
 * @version 1.0
 */


import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.Enumeration;
import java.io.Reader;

import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;
import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.*;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.util.NoSuchPropertyException;
import ontorama.webkbtools.util.ParserException;

import ontorama.util.Debug;




public class XmlParserFull implements Parser {

  /**
   * Hashtable to hold all OntologyTypes that we are creating
   */
  private Hashtable ontHash;

  /**
   * debug
   */
   Debug debug = new Debug(false);

   /**
    *
    */
  public XmlParserFull () {
    ontHash = new Hashtable();

  }

  /**
   *
   */
  public Iterator getOntologyTypeIterator (Reader reader) throws ParserException {
    try {
      SAXBuilder builder = new SAXBuilder();
      // Create the document
      Document doc = builder.build(reader);
      Element rootElement = doc.getRootElement();

      Element conceptTypesElement = rootElement.getChild("conceptTypes");
      Element relationLinksElement = rootElement.getChild("relationLinks");

      readConceptTypes(conceptTypesElement);
      readRelationLinks(relationLinksElement);
    } catch (Exception e) {
        System.out.println("Exception: " + e);
        e.printStackTrace();
        System.exit(-1);
    }
    return ontHash.values().iterator();
  }

  /**
   *
   * @todo change: harcoded addTypeProperty("description", descriptionEl.getText())
   * and addTypeProperty("creator", creatorEl.getText()) so they are dinamically read
   * from OntoramaConfig.
   */
    private void readConceptTypes(Element top) throws ParserException, NoSuchPropertyException {
        List conceptTypeElementsList = top.getChildren("conceptType");
        Iterator conceptTypeElementsIterator = conceptTypeElementsList.iterator();

        while (conceptTypeElementsIterator.hasNext()) {
            Element conceptTypeEl = (Element) conceptTypeElementsIterator.next();
            Attribute nameAttr = conceptTypeEl.getAttribute("name");
            checkCompulsoryAttr(nameAttr, "name", "conceptType");

            OntologyType type = (OntologyTypeImplementation) ontHash.get (nameAttr.getValue());

            if( type == null ) {
                type = new OntologyTypeImplementation(nameAttr.getValue());
                // add child to hashtable
                ontHash.put(nameAttr.getValue(), type );
            }
            Enumeration conceptPropertiesConfig = OntoramaConfig.getConceptPropertiesTable().keys();
            while (conceptPropertiesConfig.hasMoreElements()) {
              String propName = (String) conceptPropertiesConfig.nextElement();
              Element propEl = conceptTypeEl.getChild(propName);
              // this is a hack!!!!
              if (propEl == null) {
                propEl = conceptTypeEl.getChild(propName.toLowerCase());
              }
              if (propEl != null) {
                type.addTypeProperty(propName,propEl.getText());
              }
            }

            debug.message("XmlParserFull", "readConceptTypes", "created type: " + type);
        }
    }

    /**
     *
     * @todo    when relation links array is build - if a user started with id =1
     * in config.xml - then we have a null element at index=0. To avoid trouble -
     * had to add following lines of code:
     * if (relationLinksConfigArray[i] == null) {
     *  continue;
     * }
     * This should be fixed in config.xml and XmlConfigParser!!!
     */
    private void readRelationLinks (Element top) throws ParserException, NoSuchRelationLinkException {
        List relationLinksElementsList = top.getChildren("relationLink");
        Iterator relationLinksElementsIterator = relationLinksElementsList.iterator();

        RelationLinkDetails[]  relationLinksConfigArray = OntoramaConfig.getRelationLinkDetails();

        while (relationLinksElementsIterator.hasNext()) {
            Element relLinkEl = (Element) relationLinksElementsIterator.next();
            Attribute nameAttr = relLinkEl.getAttribute("name");
            checkCompulsoryAttr(nameAttr, "name", "relationLink");
            Attribute fromAttr = relLinkEl.getAttribute("from");
            checkCompulsoryAttr(fromAttr, "from", "relationLink");
            OntologyType fromType = (OntologyTypeImplementation) ontHash.get (fromAttr.getValue());
            if( fromType == null ) {
                // Won't throw exception for now. consider example:
                // wn#Cat with children generated from webkb rdf output.
                // Input file will have all types declared, except wn#TrueCat,
                // however it will have supertype link from
                // wn#TrueCat to wn#Cat.
                // For now - we simply skip it.
                continue;
                //throw new ParserException ("conceptType " + fromAttr.getValue() + " is not declared in conceptTypes section");
            }

            Attribute toAttr = relLinkEl.getAttribute("to");
            checkCompulsoryAttr(toAttr, "to", "relationLink");
            OntologyType toType = (OntologyTypeImplementation) ontHash.get (toAttr.getValue());
            if( toType == null ) {
                throw new ParserException ("conceptType " + toAttr.getValue() + " is not declared in conceptTypes section");
            }
            debug.message("XmlParserFull", "readRelationLinks", "fromType = " + fromType.getName() + ", toType = " + toType.getName() + " , relationLink = " + nameAttr.getValue());
            boolean foundRelationLink = false;
            for (int i = 0; i < relationLinksConfigArray.length; i++) {
                if (relationLinksConfigArray[i] == null) {
                    continue;
                }
                RelationLinkDetails relationLinkDetails = relationLinksConfigArray[i];
                if ( (nameAttr.getValue()).equals(relationLinkDetails.getLinkName()) ) {
                    debug.message("XmlParserFull", "readRelationLinks", "rel id = " + i);
                    fromType.addRelationType(toType,i);
                    foundRelationLink = true;
                }
                else if ( (nameAttr.getValue()).equals(relationLinkDetails.getReversedLinkName()) ) {
                    debug.message("XmlParserFull", "readRelationLinks", "rel id = " + i);
                    toType.addRelationType(fromType,i);
                    foundRelationLink = true;
                }
            }
            if ( foundRelationLink == false) {
                throw new ParserException("Attribute name '" + nameAttr.getValue() + "' describes unknown Relation Link. Check config.xml for declared Relation Links");
            }
        }
    }

    /**
     *
     */
    private void checkCompulsoryAttr (Attribute attr, String attrName, String elementName)
                                    throws ParserException {
        if ( attr == null) {
            throw new ParserException("Missing compulsory Attribute '" + attrName + "' in Element '" + elementName + "'");
        }
        if ( attr.getValue().trim().equals("")) {
            throw new ParserException("Attribute '" + attrName + "' in Element '" + elementName + "' can't be empty");
        }
    }

}