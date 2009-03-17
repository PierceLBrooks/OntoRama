package ontorama.ontotools.parser.xml;

/*
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 */


import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessControlException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ontorama.OntoramaConfig;
import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeType;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.ParserException;
import ontorama.ontotools.parser.Parser;
import ontorama.ontotools.parser.ParserResult;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;


public class XmlParserFull implements Parser {
	
	
	private Map<String,Node> _nodes;
    private List<Edge> _edges;

    private Element rootElement;
    private Namespace ns;

	private static final String descriptionElementName = "description";

    private static final String conceptTypeElementName = "conceptType";
    private static final String relationTypeElementName = "relationType";
    
    
	// hardcoded  edge names (creator, relSignature1 and relSignature2) - 
	// will cause a problem when config.xml file changes!
	// However, can't see another solution without making whole OntoRama less dynamic
	// for now will add test cases to test whether or not these edge types
	// are in the config file.
    private static final String edgeTypeName_relSignature1 = "relSignature1";
	private static final String edgeTypeName_relSignature2 = "relSignature2";
	private static final String edgeTypeName_creator = "creator";

    public XmlParserFull() {
		init();
    }
    
    private void init() {
        _nodes = new Hashtable<String, Node>();
        _edges = new LinkedList<Edge>();
    }

    @SuppressWarnings("unchecked")
	public ParserResult getResult(Reader reader) throws ParserException, AccessControlException {
		checkEdgeTypeInConfig(edgeTypeName_creator);
		checkEdgeTypeInConfig(edgeTypeName_relSignature1);
		checkEdgeTypeInConfig(edgeTypeName_relSignature2);
    	init();
        try {
            SAXBuilder builder = new SAXBuilder();
            // Create the document
            Document doc = builder.build(reader);
            rootElement = doc.getRootElement();
            
            ns = rootElement.getNamespace();

            readConceptTypes(rootElement.getChildren(conceptTypeElementName, ns));
        	readRelationTypes(rootElement.getChildren(relationTypeElementName, ns));
        } catch (URISyntaxException e) {
            System.out.println("URISyntaxException: " + e);
            e.printStackTrace();
            throw new ParserException("Expecting valid URI for type creator attribute: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        	throw new ParserException(e.getMessage());            
        }
        return new ParserResult(new LinkedList<Node>(_nodes.values()), _edges);
    }

    private void readConceptTypes(List<Element> conceptTypeElementsList) throws ParserException, URISyntaxException, NoSuchRelationLinkException {
    	for (Element conceptTypeEl : conceptTypeElementsList) {
            Attribute nameAttr = conceptTypeEl.getAttribute("name");
            checkCompulsoryAttr(nameAttr, "name", conceptTypeElementName);
            String nodeName = nameAttr.getValue();

            Node node = makeNode(nodeName, OntoramaConfig.CONCEPT_TYPE);

            processTypeDetails(conceptTypeEl, node);
        }
    }

    private void readRelationTypes(List<Element> relationTypeElementsList) throws ParserException, URISyntaxException, NoSuchRelationLinkException {
    	for (Element relationTypeEl : relationTypeElementsList) {
            Attribute nameAttr = relationTypeEl.getAttribute("name");
            checkCompulsoryAttr(nameAttr, "name", relationTypeElementName);
            String nodeName = nameAttr.getValue();

            Node node = makeNode(nodeName, OntoramaConfig.RELATION_TYPE);
            
            processSignatureItem(node, relationTypeEl, "domain", edgeTypeName_relSignature1);
        	processSignatureItem(node, relationTypeEl, "range", edgeTypeName_relSignature2);

            processTypeDetails(relationTypeEl, node);
        }
    }

	private void processSignatureItem (Node node, Element relationTypeEl,
									String signatureItemAttrName, String edgeName) 
									throws ParserException, NoSuchRelationLinkException {
		Attribute signatureAttr = relationTypeEl.getAttribute(signatureItemAttrName);
		if (signatureAttr != null) {
			Node toNode = makeNode(signatureAttr.getValue(), OntoramaConfig.CONCEPT_TYPE);
			makeEdge(node, toNode, edgeName);
		}
		
	}

    private Node makeNode(String nodeName, NodeType nodeType) {
        Node node = _nodes.get(nodeName);

        if (node == null) {
        	node = OntoramaConfig.getBackend().createNode(nodeName, nodeName);
            node.setNodeType(nodeType);
            _nodes.put(nodeName, node);
        }
        return node;
    }

    @SuppressWarnings("unchecked")
	private NodeType getNodeTypeForDestinationNode (String nodeName) {
        NodeType retVal = null;
        Iterator<Element> conceptTypesIterator = rootElement.getChildren(conceptTypeElementName, ns).iterator();
        while (conceptTypesIterator.hasNext()) {
            Element cur = conceptTypesIterator.next();
            Attribute typeNameAttr = cur.getAttribute("name");
            if (typeNameAttr.getValue().equals(nodeName)) {
                retVal = OntoramaConfig.CONCEPT_TYPE;
            }
        }
        Iterator<Element> relationTypesIterator = rootElement.getChildren(relationTypeElementName, ns).iterator();
        while (relationTypesIterator.hasNext()) {
            Element cur = relationTypesIterator.next();
            Attribute typeNameAttr = cur.getAttribute("name");
            if (typeNameAttr.getValue().equals(nodeName)) {
                retVal = OntoramaConfig.RELATION_TYPE;
            }
        }
        return retVal;
    }

    private void processTypeDetails (Element typeElement, Node node) throws URISyntaxException, NoSuchRelationLinkException, ParserException {
        URI creator = getCreator(typeElement);
        if (creator != null) {
            node.setCreatorUri(creator);
            Node creatorNode = makeNode(creator.toString(), OntoramaConfig.CONCEPT_TYPE);
            makeEdge(node, creatorNode, edgeTypeName_creator);
        }

        String description = getDescription(typeElement);
        node.setDescription(description);

        processTypeProperty(typeElement, node, "synonym");

        processRelationLinks(typeElement, node);
    }

    private void processTypeProperty(Element typeElement, Node node, String typePropertyName)
    						throws NoSuchRelationLinkException, ParserException {
        List<String> descr = getTypeProperty(typeElement, typePropertyName);
        Iterator<String> it = descr.iterator();
        while (it.hasNext()) {
            String cur = it.next();
            Node toNode = makeNode(cur, null);
            makeEdge(node, toNode, typePropertyName);
        }
    }

    private URI getCreator (Element typeElement) throws URISyntaxException {
        Attribute creatorAttr = typeElement.getAttribute("creator");
        if (creatorAttr != null) {
            URI creatorUri = new URI(creatorAttr.getValue());
            return creatorUri;
        }
        return null;
    }

	private String getDescription (Element typeElement)  {
		Element descrElement = typeElement.getChild(descriptionElementName, ns);
		if (descrElement != null) {
			return descrElement.getText();
		}
		return null;
	}

    @SuppressWarnings("unchecked")
	private List<String> getTypeProperty (Element typeElement, String subelementName) {
        List<String> retVal = new LinkedList<String>();
        Iterator<Element> subelements =  typeElement.getChildren(subelementName, ns).iterator();
        while (subelements.hasNext()) {
            Element cur = subelements.next();
            retVal.add(cur.getTextTrim());
        }
        return retVal;
    }

    @SuppressWarnings("unchecked")
	private void processRelationLinks(Element top, Node fromNode) throws ParserException, NoSuchRelationLinkException {
        List<Element> relationLinksElementsList = top.getChildren("relationship", ns);
        Iterator<Element> relationLinksElementsIterator = relationLinksElementsList.iterator();


        while (relationLinksElementsIterator.hasNext()) {
            Element relLinkEl = relationLinksElementsIterator.next();
            Attribute typeAttr = relLinkEl.getAttribute("type");
            checkCompulsoryAttr(typeAttr, "type", "relationship");
            Attribute toAttr = relLinkEl.getAttribute("to");
            checkCompulsoryAttr(toAttr, "to", "relationship");
            NodeType toNodeType = getNodeTypeForDestinationNode(toAttr.getValue());
            Node toNode = makeNode(toAttr.getValue(), toNodeType);
            makeEdge(fromNode, toNode, typeAttr.getValue());
        }
    }

    private Edge makeEdge(Node fromNode, Node toNode, String edgeName) throws NoSuchRelationLinkException, ParserException {
        Iterator<EdgeType> edgeTypesIterator = OntoramaConfig.getEdgeTypesSet().iterator();
        Edge edge = null;
        while (edgeTypesIterator.hasNext()) {
            EdgeType edgeType = edgeTypesIterator.next();
            if ((edgeType.getName()).equals(edgeName)) {
                edge = OntoramaConfig.getBackend().createEdge(fromNode, toNode, edgeType);
            } else if ( (edgeType.getReverseEdgeName() != null) && ((edgeType.getReverseEdgeName()).equals(edgeName)) ) {
                edge = OntoramaConfig.getBackend().createEdge(toNode, fromNode, edgeType);
            }
        }
    	if (edge == null) {
    		throw new ParserException("Attribute name '" + edgeName + "' describes unknown Relation Link. Check config.xml for declared Relation Links");
    	}
    	if (! edgeAlreadyInList(edge)) {
    		 _edges.add(edge);
    	}
        return edge;
    }

    private boolean edgeAlreadyInList (Edge edge) {
        Iterator<Edge> it = _edges.iterator();
        while (it.hasNext()) {
            Edge cur = it.next();
            if (cur.equals(edge)) {
            	return true;
            }
        }
        return false;
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


	private void checkEdgeTypeInConfig (String edgeTypeName) throws ParserException {
		String errMsg = "config xml file should contain edge type " + edgeTypeName;
		try {
			EdgeType edgeType = OntoramaConfig.getEdgeType(edgeTypeName);
			if (edgeType == null) {
				throw new ParserException(errMsg);
			}
		}
		catch (NoSuchRelationLinkException e) {
			e.fillInStackTrace();
			throw new ParserException(errMsg);
		}

	}

}