package ontorama.ontotools.query.parser.xml;

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
import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeImpl;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeImpl;
import ontorama.model.graph.NodeType;
import ontorama.util.Debug;
import ontorama.ontotools.query.parser.Parser;
import ontorama.ontotools.query.parser.ParserResult;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.ParserException;

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
    private ontorama.model.graph.NodeType relationNodeType;
    private ontorama.model.graph.NodeType conceptNodeType;

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
                ontorama.model.graph.NodeType nodeType = (ontorama.model.graph.NodeType) it.next();
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

            ontorama.model.graph.Node node = makeNode(nodeName, conceptNodeType);

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

            ontorama.model.graph.Node node = makeNode(nodeName, relationNodeType);
            
            /// @todo hardcoded relSignature1 and relSignature2 edge names - will cause a problem when config.xml file changes!
            processSignatureItem(node, relationTypeEl, "domain", "relSignature1");
        	processSignatureItem(node, relationTypeEl, "range", "relSignature2");

            processTypeDetails(relationTypeEl, node);
        }
    }

	private void processSignatureItem (ontorama.model.graph.Node node, Element relationTypeEl,
									String signatureItemAttrName, String edgeName) 
									throws ParserException, NoSuchRelationLinkException {
		Attribute signatureAttr = relationTypeEl.getAttribute(signatureItemAttrName);
		if (signatureAttr != null) {
			ontorama.model.graph.Node toNode = makeNode(signatureAttr.getValue(), conceptNodeType);
			ontorama.model.graph.Edge edge = makeEdge(node, toNode, edgeName);
		}
		
	}

    private ontorama.model.graph.Node makeNode(String nodeName, ontorama.model.graph.NodeType nodeType) {
        ontorama.model.graph.Node node = (ontorama.model.graph.Node) _nodes.get(nodeName);

        if (node == null) {
            node = new ontorama.model.graph.NodeImpl(nodeName);
            node.setNodeType(nodeType);
            _nodes.put(nodeName, node);
        }
        return node;
    }

    private ontorama.model.graph.NodeType getNodeTypeForDestinationNode (String nodeName) {
        ontorama.model.graph.NodeType retVal = null;
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

    private void processTypeDetails (Element typeElement, ontorama.model.graph.Node node) throws URISyntaxException, NoSuchRelationLinkException, ParserException {
        URI creator = getCreator(typeElement);
        if (creator != null) {
            node.setCreatorUri(creator);
            /// @todo dont think it is a good idea to hardcode "creator" 
            // edge name here. A way to fix it would be to have
            // description ui explicitely displaying creator node property
            // and not to have creator as edge type. not sure though
            // if this would work with the rest of the application well. at least
            // it may not be as dynamic.
            ontorama.model.graph.Node creatorNode = makeNode(creator.toString(), conceptNodeType);
            makeEdge(node, creatorNode, "creator");
        }

        processTypeProperty(typeElement, node, "description");
        processTypeProperty(typeElement, node, "synonym");

        processRelationLinks(typeElement, node);
    }

    private void processTypeProperty(Element typeElement, ontorama.model.graph.Node node, String typePropertyName)
    						throws NoSuchRelationLinkException, ParserException {
        List descr = getTypeProperty(typeElement, typePropertyName);
        Iterator it = descr.iterator();
        while (it.hasNext()) {
            String cur = (String) it.next();
            ontorama.model.graph.Node toNode = makeNode(cur, null);
            ontorama.model.graph.Edge edge = makeEdge(node, toNode, typePropertyName);
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
    private void processRelationLinks(Element top, ontorama.model.graph.Node fromNode) throws ParserException, NoSuchRelationLinkException {
        List relationLinksElementsList = top.getChildren("relationship", ns);
        Iterator relationLinksElementsIterator = relationLinksElementsList.iterator();


        while (relationLinksElementsIterator.hasNext()) {
            Element relLinkEl = (Element) relationLinksElementsIterator.next();
            Attribute typeAttr = relLinkEl.getAttribute("type");
            checkCompulsoryAttr(typeAttr, "type", "relationship");
            Attribute toAttr = relLinkEl.getAttribute("to");
            checkCompulsoryAttr(toAttr, "to", "relationship");
            ontorama.model.graph.NodeType toNodeType = getNodeTypeForDestinationNode(toAttr.getValue());
            ontorama.model.graph.Node toNode = makeNode(toAttr.getValue(), toNodeType);
            debug.message("XmlParserFull", "processRelationLinks", "fromType = " + fromNode.getName() + ", toType = " + toNode.getName() + " , relationLink = " + typeAttr.getValue());
            ontorama.model.graph.Edge edge = makeEdge(fromNode, toNode, typeAttr.getValue());
        }
    }

    private ontorama.model.graph.Edge makeEdge(ontorama.model.graph.Node fromNode, ontorama.model.graph.Node toNode, String edgeName) throws NoSuchRelationLinkException, ParserException {
        Iterator edgeTypesIterator = OntoramaConfig.getEdgeTypesSet().iterator();
        ontorama.model.graph.Edge edge = null;
        while (edgeTypesIterator.hasNext()) {
            ontorama.model.graph.EdgeType edgeType = (ontorama.model.graph.EdgeType) edgeTypesIterator.next();
            if ((edgeType.getName()).equals(edgeName)) {
                edge = new ontorama.model.graph.EdgeImpl(fromNode, toNode, edgeType);
            } else if ( (edgeType.getReverseEdgeName() != null) && ((edgeType.getReverseEdgeName()).equals(edgeName)) ) {
                edge = new ontorama.model.graph.EdgeImpl(toNode, fromNode, edgeType);
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

    private boolean edgeAlreadyInList (ontorama.model.graph.Edge edge) {
        Iterator it = _edges.iterator();
        while (it.hasNext()) {
            ontorama.model.graph.Edge cur = (ontorama.model.graph.Edge) it.next();
            if (cur.equals(edge)) {
            	return true;
            }
            ontorama.model.graph.Node fromNode = cur.getFromNode();
            ontorama.model.graph.Node toNode = cur.getToNode();
            ontorama.model.graph.EdgeType edgeType = cur.getEdgeType();
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