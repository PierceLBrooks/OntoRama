package ontorama.webkbtools.query.parser.xml;

/**
 * Title:        OntoRama
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 * @author
 * @version 1.0
 */


import ontorama.OntoramaConfig;
import ontorama.model.EdgeImpl;
import ontorama.model.Edge;
import ontorama.model.Node;
import ontorama.model.NodeImpl;
import ontorama.ontologyConfig.RelationLinkDetails;
import ontorama.util.Debug;
import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.webkbtools.util.NoSuchPropertyException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.util.ParserException;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.Reader;
import java.util.*;
import java.security.AccessControlException;


public class XmlParserFull implements Parser {

    private Hashtable _nodes;
    private List _edges;

    /**
     * debug
     */
    Debug debug = new Debug(false);

    /**
     *
     */
    public XmlParserFull() {
//        ontHash = new Hashtable();
        _nodes = new Hashtable();
        _edges = new LinkedList();

    }

    /**
     * @todo    should return proper ParserResult (not null)
     * @param reader
     * @return
     * @throws ParserException
     * @throws AccessControlException
     */
    public ParserResult getResult(Reader reader) throws ParserException, AccessControlException {
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
        return new ParserResult(new LinkedList(_nodes.values()), _edges);
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

            Node node = (Node) _nodes.get(nameAttr.getValue());

            if (node == null) {
                node = new NodeImpl(nameAttr.getValue());
                // add child to hashtable
                _nodes.put(nameAttr.getValue(), node);
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
                    List propValue = node.getProperty(propName);
                    propValue.add(propEl.getText());
                    node.setProperty(propName, propValue);
                }
            }

            debug.message("XmlParserFull", "readConceptTypes", "created type: " + node);
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
    private void readRelationLinks(Element top) throws ParserException, NoSuchRelationLinkException {
        List relationLinksElementsList = top.getChildren("relationLink");
        Iterator relationLinksElementsIterator = relationLinksElementsList.iterator();

        RelationLinkDetails[] relationLinksConfigArray = OntoramaConfig.getRelationLinkDetails();

        while (relationLinksElementsIterator.hasNext()) {
            Element relLinkEl = (Element) relationLinksElementsIterator.next();
            Attribute nameAttr = relLinkEl.getAttribute("name");
            checkCompulsoryAttr(nameAttr, "name", "relationLink");
            Attribute fromAttr = relLinkEl.getAttribute("from");
            checkCompulsoryAttr(fromAttr, "from", "relationLink");
            Node fromNode = (Node) _nodes.get(fromAttr.getValue());
            if (fromNode == null) {
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
            Node toNode = (Node) _nodes.get(toAttr.getValue());
            if (toNode == null) {
                throw new ParserException("conceptType " + toAttr.getValue() + " is not declared in conceptTypes section");
            }
            debug.message("XmlParserFull", "readRelationLinks", "fromType = " + fromNode.getName() + ", toType = " + toNode.getName() + " , relationLink = " + nameAttr.getValue());
            Edge edge = null;
            for (int i = 0; i < relationLinksConfigArray.length; i++) {
                if (relationLinksConfigArray[i] == null) {
                    continue;
                }
                RelationLinkDetails relationLinkDetails = relationLinksConfigArray[i];
                if ((nameAttr.getValue()).equals(relationLinkDetails.getLinkName())) {
                    debug.message("XmlParserFull", "readRelationLinks", "rel id = " + i);
                    edge = new EdgeImpl(fromNode, toNode, relationLinkDetails);
                } else if ((nameAttr.getValue()).equals(relationLinkDetails.getReversedLinkName())) {
                    debug.message("XmlParserFull", "readRelationLinks", "rel id = " + i);
                    edge = new EdgeImpl(toNode, fromNode, relationLinkDetails);
                }
            }
            if (edge == null) {
                throw new ParserException("Attribute name '" + nameAttr.getValue() + "' describes unknown Relation Link. Check config.xml for declared Relation Links");
            }
            if (!_edges.contains(edge)) {
                 _edges.add(edge);
            }
        }
    }

    /**
     *
     */
    private void checkCompulsoryAttr(Attribute attr, String attrName, String elementName)
            throws ParserException {
        if (attr == null) {
            throw new ParserException("Missing compulsory Attribute '" + attrName + "' in Element '" + elementName + "'");
        }
        if (attr.getValue().trim().equals("")) {
            throw new ParserException("Attribute '" + attrName + "' in Element '" + elementName + "' can't be empty");
        }
    }

}