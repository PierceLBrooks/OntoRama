package ontorama.webkbtools.query.parser.xml;

/**
 * Title:        OntoRama
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 * @author
 * @version 1.0
 */


import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessControlException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ontorama.OntoramaConfig;
import ontorama.model.Edge;
import ontorama.model.EdgeImpl;
import ontorama.model.EdgeType;
import ontorama.model.Node;
import ontorama.model.NodeImpl;
import ontorama.model.NodeType;
import ontorama.util.Debug;
import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.webkbtools.NoSuchRelationLinkException;
import ontorama.webkbtools.ParserException;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;


public class XmlParserFull implements Parser {

    private Hashtable _nodes;
    private List _edges;

    /**
     * debug
     */
    Debug debug = new Debug(false);
    private NodeType relationNodeType;
    private NodeType conceptNodeType;

    private Element rootElement;
    private Namespace ns;

    private static final String conceptTypeElementName = "conceptType";
    private static final String relationTypeElementName = "relationType";

    /**
     *
     */
    public XmlParserFull() {
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
            rootElement = doc.getRootElement();
            
            ns = rootElement.getNamespace();

            /// @todo hack to get concept node type
            Iterator it = OntoramaConfig.getNodeTypesList().iterator();
            while (it.hasNext()) {
                NodeType nodeType = (NodeType) it.next();
                if (nodeType.getNodeType().equals("concept")) {
                    conceptNodeType = nodeType;
                }
                else if (nodeType.getNodeType().equals("relation")) {
                    relationNodeType = nodeType;
                }
            }

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
        System.out.println("\n\nreturning nodes: " + new LinkedList(_nodes.values()));
        System.out.println("returning edges: " + _edges);
        return new ParserResult(new LinkedList(_nodes.values()), _edges);
    }

    private void readConceptTypes(List conceptTypeElementsList) throws ParserException, URISyntaxException, NoSuchRelationLinkException {
        Iterator conceptTypeElementsIterator = conceptTypeElementsList.iterator();
        while (conceptTypeElementsIterator.hasNext()) {
            Element conceptTypeEl = (Element) conceptTypeElementsIterator.next();
            Attribute nameAttr = conceptTypeEl.getAttribute("name");
            checkCompulsoryAttr(nameAttr, "name", conceptTypeElementName);
            String nodeName = nameAttr.getValue();

            Node node = makeNode(nodeName, conceptNodeType);

            processTypeDetails(conceptTypeEl, node);
        }
    }

    private void readRelationTypes(List relationTypeElementsList) throws ParserException, URISyntaxException, NoSuchRelationLinkException {
        Iterator relationTypeElementsIterator = relationTypeElementsList.iterator();
        while (relationTypeElementsIterator.hasNext()) {
            Element relationTypeEl = (Element) relationTypeElementsIterator.next();
            Attribute nameAttr = relationTypeEl.getAttribute("name");
            checkCompulsoryAttr(nameAttr, "name", relationTypeElementName);
            String nodeName = nameAttr.getValue();

            Node node = makeNode(nodeName, relationNodeType);
            
            /// @todo hardcoded relSignature1 and relSignature2 edge names - will cause a problem when config.xml file changes!
            processSignatureItem(node, relationTypeEl, "domain", "relSignature1");
        	processSignatureItem(node, relationTypeEl, "range", "relSignature2");

            processTypeDetails(relationTypeEl, node);
        }
    }

	private void processSignatureItem (Node node, Element relationTypeEl, 
									String signatureItemAttrName, String edgeName) 
									throws ParserException, NoSuchRelationLinkException {
		Attribute signatureAttr = relationTypeEl.getAttribute(signatureItemAttrName);
		if (signatureAttr != null) {
			Node toNode = makeNode(signatureAttr.getValue(), conceptNodeType);
			Edge edge = makeEdge(node, toNode, edgeName);
		}
		
	}

    private Node makeNode(String nodeName, NodeType nodeType) {
        Node node = (Node) _nodes.get(nodeName);

        if (node == null) {
            node = new NodeImpl(nodeName);
            node.setNodeType(nodeType);
            _nodes.put(nodeName, node);
        }
        return node;
    }

    private NodeType getNodeTypeForDestinationNode (String nodeName) {
        NodeType retVal = null;
        Iterator conceptTypesIterator = rootElement.getChildren(conceptTypeElementName, ns).iterator();
        while (conceptTypesIterator.hasNext()) {
            Element cur = (Element) conceptTypesIterator.next();
            Attribute typeNameAttr = cur.getAttribute("name");
            if (typeNameAttr.getValue().equals(nodeName)) {
                retVal = conceptNodeType;
            }
        }
        Iterator relationTypesIterator = rootElement.getChildren(relationTypeElementName, ns).iterator();
        while (relationTypesIterator.hasNext()) {
            Element cur = (Element) relationTypesIterator.next();
            Attribute typeNameAttr = cur.getAttribute("name");
            if (typeNameAttr.getValue().equals(nodeName)) {
                retVal = relationNodeType;
            }
        }
        return retVal;
    }

    private void processTypeDetails (Element typeElement, Node node) throws URISyntaxException, NoSuchRelationLinkException, ParserException {
        URI creator = getCreator(typeElement);
        if (creator != null) {
            node.setCreatorUri(creator);
            /// @todo dont think it is a good idea to hardcode "creator" 
            // edge name here. A way to fix it would be to have
            // description view explicitely displaying creator node property 
            // and not to have creator as edge type. not sure though
            // if this would work with the rest of the application well. at least
            // it may not be as dynamic.
            Node creatorNode = makeNode(creator.toString(), conceptNodeType);
            makeEdge(node, creatorNode, "creator");
        }

        processTypeProperty(typeElement, node, "description");
        processTypeProperty(typeElement, node, "synonym");

        processRelationLinks(typeElement, node);
    }

    private void processTypeProperty(Element typeElement, Node node, String typePropertyName) 
    						throws NoSuchRelationLinkException, ParserException {
        List descr = getTypeProperty(typeElement, typePropertyName);
        Iterator it = descr.iterator();
        while (it.hasNext()) {
            String cur = (String) it.next();
            Node toNode = makeNode(cur, null);
            Edge edge = makeEdge(node, toNode, typePropertyName);
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

    private List getTypeProperty (Element typeElement, String subelementName) {
        List retVal = new LinkedList();
        Iterator subelements =  typeElement.getChildren(subelementName, ns).iterator();
        while (subelements.hasNext()) {
            Element cur = (Element) subelements.next();
            retVal.add(cur.getTextTrim());
        }
        return retVal;
    }

    /**
     *
     */
    private void processRelationLinks(Element top, Node fromNode) throws ParserException, NoSuchRelationLinkException {
        List relationLinksElementsList = top.getChildren("relationship", ns);
        Iterator relationLinksElementsIterator = relationLinksElementsList.iterator();


        while (relationLinksElementsIterator.hasNext()) {
            Element relLinkEl = (Element) relationLinksElementsIterator.next();
            Attribute typeAttr = relLinkEl.getAttribute("type");
            checkCompulsoryAttr(typeAttr, "type", "relationship");
            Attribute toAttr = relLinkEl.getAttribute("to");
            checkCompulsoryAttr(toAttr, "to", "relationship");
            NodeType toNodeType = getNodeTypeForDestinationNode(toAttr.getValue());
            Node toNode = makeNode(toAttr.getValue(), toNodeType);
            debug.message("XmlParserFull", "processRelationLinks", "fromType = " + fromNode.getName() + ", toType = " + toNode.getName() + " , relationLink = " + typeAttr.getValue());
            Edge edge = makeEdge(fromNode, toNode, typeAttr.getValue());
        }
    }

    private Edge makeEdge(Node fromNode, Node toNode, String edgeName) throws NoSuchRelationLinkException, ParserException {
        Iterator edgeTypesIterator = OntoramaConfig.getEdgeTypesSet().iterator();
        Edge edge = null;
        while (edgeTypesIterator.hasNext()) {
            EdgeType edgeType = (EdgeType) edgeTypesIterator.next();
            if ((edgeType.getName()).equals(edgeName)) {
                edge = new EdgeImpl(fromNode, toNode, edgeType);
            } else if ( (edgeType.getReverseEdgeName() != null) && ((edgeType.getReverseEdgeName()).equals(edgeName)) ) {
                edge = new EdgeImpl(toNode, fromNode, edgeType);
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
        Iterator it = _edges.iterator();
        while (it.hasNext()) {
            Edge cur = (Edge) it.next();
            if (cur.equals(edge)) {
            	return true;
            }
            Node fromNode = cur.getFromNode();
            Node toNode = cur.getToNode();
            EdgeType edgeType = cur.getEdgeType();
            if (edge.getFromNode().getName().equals(fromNode.getName())) {
                if (edge.getToNode().getName().equals(toNode.getName())) {
                    if (edge.getEdgeType().getName().equals(cur.getEdgeType().getName())) {
                        return true;
                    }
                }
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

}