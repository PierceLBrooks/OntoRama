package ontorama.ontologyConfig;


import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ontorama.OntoramaConfig;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.EdgeTypeImpl;
import ontorama.util.Debug;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class XmlConfigParser extends XmlParserAbstract {

    private static Hashtable edgesConfig;
    private List edgesOrdering;

    /**
     *
     */
    private static LinkedList relationRdfMappingList;

    /**
     *
     */
    private Debug debug = new Debug(false);

    private static final String ontologyElementName = "ontology";
    private static final String displayInfoElementName = "displayInfo";
    private static final String rdfMappingElementName = "rdfMapping";
    private static final String relationElementName = "relation";
    private static final String relationTypeElementName = "relationType";
    private static final String displayInGraphViewElementName = "displayInGraphView";
    private static final String createIconElementName = "createIcon";
    private static final String loadIconElementName = "loadIcon";
    private static final String displayInDescriptionWindowElementName ="displayInDescriptionWindow";

    private static final String idAttributeName = "id";
    private static final String nameAttributeName = "name";
    private static final String colorAttributeName = "color";
    private static final String mappingSymbolAttributeName = "mappingSymbol";
    private static final String pathAttributeName = "path";
    private static final String displayLabelAttributeName = "displayLabel";


    private Element _rootElement;


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
     */
    public XmlConfigParser(InputStream in) throws ConfigParserException, IOException {
        if (OntoramaConfig.VERBOSE) {
            System.out.println("XmlConfigParser");
        }
        relationRdfMappingList = new LinkedList();
        edgesConfig = new Hashtable();
        edgesOrdering = new LinkedList();

        try {
            SAXBuilder builder = new SAXBuilder();

            Document doc = builder.build(in);

            _rootElement = doc.getRootElement();

            parse();
            Element rdfMappingEl = _rootElement.getChild(rdfMappingElementName);
            if (rdfMappingEl != null) {
                parseRelationRdfMappingElement(rdfMappingEl);
            }
        } catch (JDOMException e) {
            System.out.println("JDOMException: " + e);
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public Hashtable getDisplayInfo () {
        return edgesConfig;
    }

    private void parse () throws ConfigParserException {
        Element ontologyElement = _rootElement.getChild(ontologyElementName);
        List relationElementsList = ontologyElement.getChildren(relationElementName);
        if (relationElementsList.size() == 0) {
            throw new ConfigParserException("Element '//" + ontologyElementName +
                    "/" + relationElementName + "' doesn't have any sublements");
        }
        Iterator it = relationElementsList.iterator();
        while (it.hasNext()) {
            Element relationElement = (Element) it.next();
            int relationId = getIdFromIdAttribute(relationElement);
            ontorama.model.graph.EdgeType edgeType = parseRelation(relationElement, relationElement.getAttribute(idAttributeName));
            EdgeTypeDisplayInfo displayInfo = getDisplayInfo(relationId, edgeType);
            edgesConfig.put(edgeType, displayInfo);
            edgesOrdering.add(edgeType);
        }
    }


    private ontorama.model.graph.EdgeType parseRelation (Element relationElement, Attribute idAttr)  throws ConfigParserException {
        List relationTypeElementsList = relationElement.getChildren(relationTypeElementName);
        int listSize = relationTypeElementsList.size();

        if (listSize == 0) {
            throw new ConfigParserException("Expected Element '" + relationTypeElementName
                        + "' in the Element '" + relationElementName + "' with attribute" + idAttr);
        }
        if (listSize > 2) {
            throw new ConfigParserException("Can't have more then 2 of '" + relationElementName
                            + "' Elements in the Element '" + relationElementName + "' with attribute" + idAttr);
        }
        Element relationTypeElement = (Element) relationTypeElementsList.get(0);
        Attribute nameAttr = relationTypeElement.getAttribute(nameAttributeName);
        checkCompulsoryAttr(nameAttr, nameAttributeName, relationTypeElementName);
        ontorama.model.graph.EdgeType edgeType = new ontorama.model.graph.EdgeTypeImpl(nameAttr.getValue());

        if (listSize > 1) {
            Element reverseRelationTypeElement = (Element) relationTypeElementsList.get(1);
            Attribute reverseNameAttr = reverseRelationTypeElement.getAttribute(nameAttributeName);
            checkCompulsoryAttr(nameAttr, nameAttributeName, relationTypeElementName);
            edgeType.setReverseEdgeName(reverseNameAttr.getValue());
        }
        return edgeType;
    }

    private EdgeTypeDisplayInfo getDisplayInfo (int relationId, ontorama.model.graph.EdgeType edgeType) throws ConfigParserException {
        EdgeTypeDisplayInfo result = null;

        Element displayInfoElement = _rootElement.getChild(displayInfoElementName);
        List relationElementsList = displayInfoElement.getChildren(relationElementName);
        Element relationElement = getRelationElementForGivenId(relationElementsList, relationId);

        Element displayInGraphElement = relationElement.getChild(displayInGraphViewElementName);
        Element displayInDescriptionWinElement = relationElement.getChild(displayInDescriptionWindowElementName);
        if ((displayInGraphElement == null) && (displayInDescriptionWinElement == null)) {
            throw new ConfigParserException("Expect at least one of the following children "
                            + displayInGraphViewElementName + " or " + displayInDescriptionWindowElementName
                            + "in the element " + displayInfoElement);
        }
        result = new EdgeTypeDisplayInfo();
        if (displayInGraphElement != null) {
            result.setDisplayInGraph(true);
            Element createIconElement = displayInGraphElement.getChild(createIconElementName);
            if (createIconElement != null) {
                processCreateIconElement(createIconElement, edgeType, result);
            }
            Element loadIconElement = displayInGraphElement.getChild(loadIconElementName);
            if (loadIconElement != null) {
                processLoadIconElement(loadIconElement,  edgeType, result);
            }
        }
        if (displayInDescriptionWinElement != null) {
            processDisplayInDescriptionWind(displayInDescriptionWinElement, edgeType, result);
        }
        return result;
    }

    private void processCreateIconElement(Element createIconElement, ontorama.model.graph.EdgeType edgeType, EdgeTypeDisplayInfo result) throws ConfigParserException {
        Attribute colorAttr = createIconElement.getAttribute(colorAttributeName);
        checkCompulsoryAttr(colorAttr,  colorAttributeName, createIconElementName);
        List relationTypeElementsList = createIconElement.getChildren(relationTypeElementName);
        if (relationTypeElementsList.size() < 1) {
            throw new ConfigParserException("expected element '" + relationTypeElementName + "' in "
                        + "//" + displayInGraphViewElementName + "/"
                        + createIconElementName);
        }
        Color color = Color.decode(colorAttr.getValue());

        Attribute mappingSymbolAttr = getAttributeForGivenRelationName(relationTypeElementsList, edgeType.getName(), mappingSymbolAttributeName);
        checkCompulsoryAttr(mappingSymbolAttr,  mappingSymbolAttributeName, relationTypeElementName);
        result.setImage(color, mappingSymbolAttr.getValue());

        Attribute reverseMappingSymbolAttribute = getAttributeForGivenRelationName(relationTypeElementsList, edgeType.getReverseEdgeName(), mappingSymbolAttributeName);
        if (reverseMappingSymbolAttribute != null) {
            result.setReverseEdgeImage(color, mappingSymbolAttr.getValue());
        }
    }

    private void processLoadIconElement(Element loadIconElement, ontorama.model.graph.EdgeType edgeType, EdgeTypeDisplayInfo result) throws ConfigParserException {
        List relationTypeElementsList = loadIconElement.getChildren(relationTypeElementName);
        if (relationTypeElementsList.size() < 1) {
            throw new ConfigParserException("expected element '" + relationTypeElementName + "' in "
                        + "//" + displayInGraphViewElementName + "/"
                        + loadIconElementName);
        }

        Attribute pathAttr = getAttributeForGivenRelationName(relationTypeElementsList, edgeType.getName(), pathAttributeName);
        checkCompulsoryAttr(pathAttr, pathAttributeName, relationTypeElementName);
        Image image = Toolkit.getDefaultToolkit().createImage(pathAttr.getValue());
        result.setImage(image);

        Attribute reversePathAttr = getAttributeForGivenRelationName(relationTypeElementsList, edgeType.getReverseEdgeName(), pathAttributeName);
        if (reversePathAttr != null ) {
            Image reverseImage = Toolkit.getDefaultToolkit().createImage(reversePathAttr.getValue());
            result.setReverseEdgeImage(reverseImage);
        }
    }

    private void processDisplayInDescriptionWind (Element displayInDescriptionWinElement, ontorama.model.graph.EdgeType edgeType, EdgeTypeDisplayInfo result)
                                            throws ConfigParserException {
        List relationTypeElementsList = displayInDescriptionWinElement.getChildren(relationTypeElementName);
        Iterator it = relationTypeElementsList.iterator();
        while (it.hasNext())  {
            Element relationTypeElement = (Element) it.next();
            Attribute nameAttr = relationTypeElement.getAttribute(nameAttributeName);
            checkCompulsoryAttr(nameAttr, nameAttributeName, relationTypeElementName);
            Attribute labelAttr = relationTypeElement.getAttribute(displayLabelAttributeName);
            checkCompulsoryAttr(labelAttr, displayLabelAttributeName, relationTypeElementName);
            String name = nameAttr.getValue();
            if (edgeType.getName().equals(name)) {
                result.setDisplayInDescription(true);
                result.setDisplayLabel(labelAttr.getValue());
            }
            if ( (edgeType.getReverseEdgeName() != null) &&  (edgeType.getReverseEdgeName().equals(name)) ) {
                result.setDisplayReverseEdgeInDescription(true);
                result.setDisplayLabel(labelAttr.getValue());
            }
        }
    }

    private Attribute getAttributeForGivenRelationName (List relationTypeElementsList, String relName,
                                        String attrName) {
        Attribute result = null;
        Iterator it = relationTypeElementsList.iterator();
        while (it.hasNext()) {
            Element relationTypeElement = (Element) it.next();
            Attribute nameAttr = relationTypeElement.getAttribute(nameAttributeName);
            if (nameAttr.getValue().equals(relName)) {
                result = relationTypeElement.getAttribute(attrName);
            }
        }
        return result;
    }

    private Element getRelationElementForGivenId(List elementsList, int relationId) throws ConfigParserException {
        Iterator it = elementsList.iterator();
        while (it.hasNext()) {
            Element el = (Element) it.next();
            int id = getIdFromIdAttribute(el);
            if (id == relationId) {
                return el;
            }
        }
        return null;
    }

    private int getIdFromIdAttribute (Element el) throws ConfigParserException {
        Attribute idAttr = el.getAttribute(idAttributeName);
        checkCompulsoryAttr(idAttr, idAttributeName, relationElementName);
        try {
            return  idAttr.getIntValue();
        }
        catch (DataConversionException e) {
            throw new ConfigParserException("expected an integer in '" + idAttributeName
                    + "' attribute in element '" + relationElementName + "'");
        }
    }



    /**
     *
     */
    private void parseRelationRdfMappingElement(Element rdfMappingEl) throws ConfigParserException, DataConversionException {
        List mapElementsList = rdfMappingEl.getChildren("map");
        Iterator mapElementsIterator = mapElementsList.iterator();
        while (mapElementsIterator.hasNext()) {
            Element mapEl = (Element) mapElementsIterator.next();
            Attribute idAttr = mapEl.getAttribute("id");
            checkCompulsoryAttr(idAttr, "id", "map");
            Attribute typeAttr = mapEl.getAttribute("type");
            checkCompulsoryAttr(typeAttr, "type", "map");
            Attribute tagAttr = mapEl.getAttribute("tag");
            checkCompulsoryAttr(tagAttr, "tag", "map");
            //System.out.println("idAttr = " + idAttr + ", typeAttr = " + typeAttr + ", tagAttr = " + tagAttr);
            RdfMapping rdfMappingObject = new RdfMapping(idAttr.getIntValue(), typeAttr.getValue(), tagAttr.getValue());
            Iterator tagElementsIterator = mapEl.getChildren("tag").iterator();
            while (tagElementsIterator.hasNext()) {
                Element tagEl = (Element) tagElementsIterator.next();
                //System.out.println("\ttagEl content = " + tagEl.getText());
                rdfMappingObject.addRdfTag(tagEl.getText());
            }
            relationRdfMappingList.add(rdfMappingObject);
        }
    }

    /**
     *
     */
    public List getRelationRdfMappingList() {
        return XmlConfigParser.relationRdfMappingList;
    }

    public List getEdgesOrdering() {
        return this.edgesOrdering;
    }

}
