package ontorama.ontologyConfig;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Vector;

import org.jdom.*;
import org.jdom.input.*;
//import org.jdom.output.*;

import ontorama.util.Debug;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class XmlConfigParser {

    /**
     *
     */
    private static RelationLinkDetails[] relationLinkConfig;

    /**
     *
     */
    private static Hashtable conceptPropertiesConfig;

    /**
     *
     */
    private static LinkedList relationRdfMappingList;

    /**
     *
     */
     private Debug debug = new Debug(false);


    /**
     * This class is responsible for parsing Xml Configuration file, which
     * mainly contains details of Relation Links for user's ontology (link id's,
     * names, symbols to represent them with, colors, icons, etc).
     * All Relation Links read from the config file are stored in a structure
     * in this class.
     *
     * We used array of objects (RelationLinkDetails) to store all
     * Relation Links with index of each object being it's ID read from the
     * config file. For example: if we had relation link with id=0 and relation
     * link with id=1, then: relation link with id=0 is stored at index 0 in
     * the array and relaition link with id=1 is stored at index 1.
     *
     * However, this solution has a weekness: we may not get ID's that are in
     * order from 0 to some number n. In this case, we may get an ID number that
     * is greater then array length, which will cause an exception. For example:
     * if we have relation link with id=0 and them relation link with id=2,
     * array will be initialised to be of length = 2, relation link with id=0 will
     * be at index 0 and relation link with id=2 should be at index 2, but
     * array's last index is 1.
     *
     * @todo    read in contents of tag conceptProperty and deal with them
     *
     */
    public XmlConfigParser(InputStream in) throws ConfigParserException {
        conceptPropertiesConfig = new Hashtable();
        relationRdfMappingList = new LinkedList();

        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(in);
            Element rootEl = doc.getRootElement();

            Element ontologyEl = rootEl.getChild("ontology");
            Element rdfMappingEl = rootEl.getChild("rdfMapping");

            parseOntologyElement(ontologyEl);

            parseRelationRdfMappingElement(rdfMappingEl);
        }
        catch (JDOMException e) {
            System.out.println("JDOMException: " + e);
            System.exit(-1);
        }
    }

    /**
     *
     */
    public static RelationLinkDetails[] getRelationLinksArray () {
        return relationLinkConfig;
    }

    /**
     *
     */
    private void parseOntologyElement (Element ontologyEl)
                 throws ConfigParserException, ArrayIndexOutOfBoundsException {
        List relationElementsList = ontologyEl.getChildren("relation");
        if ( relationElementsList.size() == 0) {
            throw new ConfigParserException("Element 'relation' doesn't have any sublements");
        }
        initialiseRelationLinkConfigArray(relationElementsList.size());


        Iterator relationElementsIterator = relationElementsList.iterator();
        while (relationElementsIterator.hasNext()) {
            Element relationElement = (Element) relationElementsIterator.next();
            Attribute idAttr = relationElement.getAttribute("id");
            checkCompulsoryAttr(idAttr, "id", "relation");

            List relationTypeElementsList = relationElement.getChildren("relationType");
            int listSize = relationTypeElementsList.size();

            if (listSize == 0) {
                throw new ConfigParserException("Expected Element 'relationType' in the Element 'relation' with attribute" + idAttr);
            }
            if (listSize > 2) {
                throw new ConfigParserException("Can't have more then 2 of 'relationType' Elements in the Element 'relation' with attribute" + idAttr);
            }
            int i = 0;
            RelationLinkDetails relationLinkDetails = null;
            while (i < listSize) {
                Element relationTypeElement = (Element) relationTypeElementsList.get(i);
                Attribute nameAttr = relationTypeElement.getAttribute("name");
                checkCompulsoryAttr(nameAttr, "name", "relationType");
                Attribute mappingSymbolAttr = relationTypeElement.getAttribute("mappingSymbol");
                //checkCompulsoryAttr(mappingSymbolAttr, "mappingSymbol", "relationType");
                if (i == 0)  {
                    relationLinkDetails = new RelationLinkDetails(nameAttr.getValue());
                    if (mappingSymbolAttr != null) {
                        relationLinkDetails.setLinkSymbol( mappingSymbolAttr.getValue());
                    }
                }
                if (i==1) {
                    relationLinkDetails.setReversedLinkName(nameAttr.getValue());
                    if (mappingSymbolAttr != null) {
                        relationLinkDetails.setReversedLinkSymbol(mappingSymbolAttr.getValue());
                    }
                }
                i++;
            }
            Element displayElement = relationElement.getChild("display");

            Attribute colorAttr = displayElement.getAttribute("color");
            checkCompulsoryAttr(colorAttr, "color", "display");
            relationLinkDetails.setDisplayColor(colorAttr.getValue());

            Attribute iconAttr = displayElement.getAttribute("icon");
            if ( iconAttr != null) {
                relationLinkDetails.setDisplayImage(iconAttr.getValue());
            }

            try {
                relationLinkConfig[idAttr.getIntValue()] = relationLinkDetails;
            }
            catch (DataConversionException e) {
                throw new ConfigParserException ("Invalid number for Attribute 'id', received: " + idAttr.getValue());
            }
        }
    }

    /**
     *
     */
    private void parseRelationRdfMappingElement (Element rdfMappingEl) throws ConfigParserException, DataConversionException {
        Element relationLinksEl = rdfMappingEl.getChild("relationLinks");
        List mapElementsList = relationLinksEl.getChildren("map");
        Iterator mapElementsIterator = mapElementsList.iterator();
        while (mapElementsIterator.hasNext()) {
            Element mapEl = (Element) mapElementsIterator.next();
            Attribute idAttr = mapEl.getAttribute("id");
            checkCompulsoryAttr(idAttr, "id", "map");
            Attribute typeAttr = mapEl.getAttribute("type");
            checkCompulsoryAttr(typeAttr, "type", "map");
            Attribute tagAttr = mapEl.getAttribute("tag");
            checkCompulsoryAttr(tagAttr, "tag", "map");
            RdfMapping rdfMappingObject = new RdfMapping (idAttr.getIntValue(), typeAttr.getValue(), tagAttr.getValue());
            relationRdfMappingList.add(rdfMappingObject);
        }
    }

    /**
     *
     */
    public List getRelationRdfMappingList () {
        return this.relationRdfMappingList;
    }

    /**
     *
     */
    private void parseConceptPropertyElements (Element ontologyEl) throws ConfigParserException {
        List conceptPropElementsList = ontologyEl.getChildren("conceptProperty");
        Iterator conceptPropElementsIterator = conceptPropElementsList.iterator();
        while (conceptPropElementsIterator.hasNext()) {
            Element conceptPropEl = (Element) conceptPropElementsIterator.next();
            Attribute idAttr = conceptPropEl.getAttribute("id");
            checkCompulsoryAttr(idAttr, "id", "conceptProperty");
        }
    }

    /**
     *
     */
    private void checkCompulsoryAttr (Attribute attr, String attrName, String elementName)
                                    throws ConfigParserException {
        if ( attr == null) {
            throw new ConfigParserException("Missing compulsory Attribute '" + attrName + "' in Element '" + elementName + "'");
        }
        if ( attr.getValue().trim().equals("")) {
            throw new ConfigParserException("Attribute '" + attrName + "' in Element '" + elementName + "' can't be empty");
        }
    }

    /**
     *
     */
    private void initialiseRelationLinkConfigArray (int arraySize) {
        relationLinkConfig = new RelationLinkDetails[arraySize+1];
        for (int i=0; i < arraySize; i++ ) {
            relationLinkConfig[i] = null;
        }
    }
}