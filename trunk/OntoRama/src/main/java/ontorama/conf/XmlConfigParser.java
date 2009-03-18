package ontorama.conf;


import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Toolkit;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ontorama.model.graph.EdgeType;
import ontorama.model.graph.EdgeTypeImpl;
import ontorama.ontotools.source.JarSource;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class XmlConfigParser extends XmlParserAbstract {

    private static Map<EdgeType, EdgeTypeDisplayInfo> edgesConfig;
    private final List<EdgeType> edgesOrdering;
    private NodeTypeDisplayInfo conceptShape = null;
    private NodeTypeDisplayInfo relationShape = null;
    private final JarSource streamReader = new JarSource();

    private static List<RdfMapping> relationRdfMappingList;


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
    private static final String queryOnAttributeName = "queryOn";


    private Element _rootElement;


    /**
     * This class is responsible for parsing XML configuration files, which
     * mainly contain details of relation links for user's ontologies (link id's,
     * names, symbols to represent them with, colors, icons, etc).
     * All relation links read from the config file are stored in a structure
     * in this class.
     *
     * We used array of objects (RelationLinkDetails) to store all
     * Relation Links with index of each object being it's ID read from the
     * config file. For example: if we had relation link with id=0 and relation
     * link with id=1, then: relation link with id=0 is stored at index 0 in
     * the array and relation link with id=1 is stored at index 1.
     *
     * However, this solution has a weakness: we may not get ID's that are in
     * order from 0 to some number n. In this case, we may get an ID number that
     * is greater then array length, which will cause an exception. For example:
     * if we have relation link with id=0 and them relation link with id=2,
     * array will be initialized to be of length = 2, relation link with id=0 will
     * be at index 0 and relation link with id=2 should be at index 2, but
     * array's last index is 1.
     *
     */
    public XmlConfigParser(InputStream in) throws ConfigParserException {
        relationRdfMappingList = new ArrayList<RdfMapping>();
        edgesConfig = new HashMap<EdgeType, EdgeTypeDisplayInfo>();
        edgesOrdering = new ArrayList<EdgeType>();

        try {
            SAXBuilder builder = new SAXBuilder();

            Document doc = builder.build(in);

            _rootElement = doc.getRootElement();

            parseRelations();
            
            conceptShape = parseDisplayShape("conceptShape", "concept");
            relationShape = parseDisplayShape("relationShape", "relation");
            
            Element rdfMappingEl = _rootElement.getChild(rdfMappingElementName);
            if (rdfMappingEl != null) {
                parseRelationRdfMappingElement(rdfMappingEl);
            }
        } catch (JDOMException e) {
        	throw new ConfigParserException("Failed to read configuration file", e);
        }
    }
    
    private NodeTypeDisplayInfo parseDisplayShape(String shapeElementName, String displayName) throws ConfigParserException {
        Element shapeElement = _rootElement.getChild(shapeElementName);
    	if(shapeElement != null) {
    	    String normalColorCode = shapeElement.getAttributeValue("color");
    	    if(normalColorCode == null) {
    	        throw new ConfigParserException("Could not find 'color' attribute on '" + shapeElementName + "'");
    	    }
    	    Color normalColor = Color.decode(normalColorCode);
    	    if(normalColor == null) {
    	        throw new ConfigParserException("Could not decode 'color' attribute on '" + shapeElementName + "'");
    	    }
    	    String cloneColorCode = shapeElement.getAttributeValue("cloneColor");
    	    if(cloneColorCode == null) {
    	        throw new ConfigParserException("Could not find 'cloneColor' attribute on '" + shapeElementName + "'");
    	    }
    	    Color cloneColor = Color.decode(cloneColorCode);
    	    if(cloneColor == null) {
    	        throw new ConfigParserException("Could not decode 'cloneColor' attribute on '" + shapeElementName + "'");
    	    }
    	    String imageFileName = shapeElement.getAttributeValue("imageFile");
    	    if(imageFileName == null) {
    	        throw new ConfigParserException("Could not find 'imageFile' attribute on '" + shapeElementName + "'");
    	    }
    	    String widthString = shapeElement.getAttributeValue("width");
    	    if(widthString == null) {
    	        throw new ConfigParserException("Could not find 'width' attribute on '" + shapeElementName + "'");
    	    }
    	    int width = Integer.parseInt(widthString);
    	    String heightString = shapeElement.getAttributeValue("height");
    	    if(heightString == null) {
    	        throw new ConfigParserException("Could not find 'height' attribute on '" + shapeElementName + "'");
    	    }
    	    boolean forceUpright = true;
    	    String forceUprightString = shapeElement.getAttributeValue("forceUpright");
    	    if( (forceUprightString == null) || (!forceUprightString.equalsIgnoreCase("true")) ) {
    	    	forceUpright = false;
    	    }
    	    int height = Integer.parseInt(heightString);
    	    Shape shape;
    	    try {
    	        SAXBuilder builder = new SAXBuilder(false);

    	        Document doc = builder.build(streamReader.getInputStreamFromResource(imageFileName));
    	        Element svgElem = doc.getRootElement();
    	        shape = SVG2Shape.importShape(svgElem, width, height);
    	    } catch (Exception e) {
    	        throw new ConfigParserException("Could not import the shape file found on 'conceptShape'");
    	    }
    	    return new NodeTypeDisplayInfo(displayName, shape, forceUpright ,normalColor, cloneColor);
    	}
    	return null;
    }

    public Map<EdgeType, EdgeTypeDisplayInfo> getDisplayInfo () {
        return edgesConfig;
    }

    @SuppressWarnings("unchecked")
	private void parseRelations () throws ConfigParserException {
        Element ontologyElement = _rootElement.getChild(ontologyElementName);
        List<Element> relationElementsList = ontologyElement.getChildren(relationElementName);
        if (relationElementsList.size() == 0) {
            throw new ConfigParserException("Element '//" + ontologyElementName +
                    "/" + relationElementName + "' doesn't have any sublements");
        }
        for (Element relationElement : relationElementsList) {
            int relationId = getIdFromIdAttribute(relationElement);
            EdgeType edgeType = parseRelation(relationElement, relationElement.getAttribute(idAttributeName));
            EdgeTypeDisplayInfo displayInfo = getDisplayInfo(relationId, edgeType);
            edgesConfig.put(edgeType, displayInfo);
            edgesOrdering.add(edgeType);
        }
    }


    @SuppressWarnings("unchecked")
	private EdgeType parseRelation (Element relationElement, Attribute idAttr)  throws ConfigParserException {
        List<Element> relationTypeElementsList = relationElement.getChildren(relationTypeElementName);
        int listSize = relationTypeElementsList.size();

        if (listSize == 0) {
            throw new ConfigParserException("Expected Element '" + relationTypeElementName
                        + "' in the Element '" + relationElementName + "' with attribute" + idAttr);
        }
        if (listSize > 2) {
            throw new ConfigParserException("Can't have more then 2 of '" + relationElementName
                            + "' Elements in the Element '" + relationElementName + "' with attribute" + idAttr);
        }
        Element relationTypeElement = relationTypeElementsList.get(0);
        Attribute nameAttr = relationTypeElement.getAttribute(nameAttributeName);
        checkCompulsoryAttr(nameAttr, nameAttributeName, relationTypeElementName);
        EdgeType edgeType = new EdgeTypeImpl(nameAttr.getValue());

        if (listSize > 1) {
            Element reverseRelationTypeElement = relationTypeElementsList.get(1);
            Attribute reverseNameAttr = reverseRelationTypeElement.getAttribute(nameAttributeName);
            checkCompulsoryAttr(nameAttr, nameAttributeName, relationTypeElementName);
            edgeType.setReverseEdgeName(reverseNameAttr.getValue());
        }
        return edgeType;
    }

    @SuppressWarnings("unchecked")
	private EdgeTypeDisplayInfo getDisplayInfo (int relationId, EdgeType edgeType) throws ConfigParserException {
        EdgeTypeDisplayInfo result = null;

        Element displayInfoElement = _rootElement.getChild(displayInfoElementName);
        List<Element> relationElementsList = displayInfoElement.getChildren(relationElementName);
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

    @SuppressWarnings("unchecked")
	private void processCreateIconElement(Element createIconElement, EdgeType edgeType, EdgeTypeDisplayInfo result) throws ConfigParserException {
        Attribute colorAttr = createIconElement.getAttribute(colorAttributeName);
        checkCompulsoryAttr(colorAttr,  colorAttributeName, createIconElementName);
        List<Element> relationTypeElementsList = createIconElement.getChildren(relationTypeElementName);
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

    @SuppressWarnings("unchecked")
	private void processLoadIconElement(Element loadIconElement, EdgeType edgeType, EdgeTypeDisplayInfo result) throws ConfigParserException {
        List<Element> relationTypeElementsList = loadIconElement.getChildren(relationTypeElementName);
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

    @SuppressWarnings("unchecked")
	private void processDisplayInDescriptionWind (Element displayInDescriptionWinElement, EdgeType edgeType, EdgeTypeDisplayInfo result)
                                            throws ConfigParserException {
        List<Element> relationTypeElementsList = displayInDescriptionWinElement.getChildren(relationTypeElementName);
        for (Element relationTypeElement : relationTypeElementsList) {
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
            Attribute queryOnAttr = relationTypeElement.getAttribute(queryOnAttributeName);
            if ((queryOnAttr != null) && (queryOnAttr.getValue().equalsIgnoreCase("yes"))) {
                result.setQueryOn(true);
            }
        }
    }

    private Attribute getAttributeForGivenRelationName (List<Element> relationTypeElementsList, String relName,
                                        String attrName) {
        for (Element relationTypeElement : relationTypeElementsList) {
            Attribute nameAttr = relationTypeElement.getAttribute(nameAttributeName);
            if (nameAttr.getValue().equals(relName)) {
                return relationTypeElement.getAttribute(attrName);
            }
        }
        return null;
    }

    private Element getRelationElementForGivenId(List<Element> elementsList, int relationId) throws ConfigParserException {
    	for (Element el : elementsList) {
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



    @SuppressWarnings("unchecked")
	private void parseRelationRdfMappingElement(Element rdfMappingEl) throws ConfigParserException, DataConversionException {
        List<Element> mapElementsList = rdfMappingEl.getChildren("map");
        for (Element mapEl : mapElementsList) {
            Attribute idAttr = mapEl.getAttribute("id");
            checkCompulsoryAttr(idAttr, "id", "map");
            Attribute typeAttr = mapEl.getAttribute("type");
            checkCompulsoryAttr(typeAttr, "type", "map");
            Attribute tagAttr = mapEl.getAttribute("tag");
            checkCompulsoryAttr(tagAttr, "tag", "map");
            RdfMapping rdfMappingObject = new RdfMapping(idAttr.getIntValue(), typeAttr.getValue(), tagAttr.getValue());
            List<Element> tagElements = mapEl.getChildren("tag");
            for (Element tagEl : tagElements) {
                rdfMappingObject.addRdfTag(tagEl.getText());
            }
            relationRdfMappingList.add(rdfMappingObject);
        }
    }

    public List<RdfMapping> getRelationRdfMappingList() {
        return XmlConfigParser.relationRdfMappingList;
    }

    public List<EdgeType> getEdgesOrdering() {
        return this.edgesOrdering;
    }
    
    public NodeTypeDisplayInfo getConceptShape() {
    	return this.conceptShape;
    }
    
    public NodeTypeDisplayInfo getRelationShape() {
        return this.relationShape;
    }
}
