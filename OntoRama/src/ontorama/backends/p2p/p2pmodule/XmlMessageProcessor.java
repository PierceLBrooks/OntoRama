package ontorama.backends.p2p.p2pmodule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import ontorama.backends.p2p.model.Change;
import ontorama.backends.p2p.model.EdgeChange;
import ontorama.backends.p2p.model.NodeChange;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This class contains static methods responsible for
 * creating an XML message for assertion and rejection
 * of nodes/edges and vice-versa: for parsing them. 
 * 
 * @author nataliya
 */
public class XmlMessageProcessor {
	
	private static final String MESSAGE = "message";
	private static final String ACTION = "action";
	private static final String TYPE = "type";
	private static final String IDENTIFIER = "identifier";
	private static final String INITIATOR_URI = "initiatorUri";
	private static final String NODE = "node";
	private static final String EDGE = "edge";
	private static final String FROM = "fromNodeIdentifier";
	private static final String TO = "toNodeIdentifier";
	
	
	public static Change parseXmlMessage (String modelChange) throws 
											XmlMessageParserException {
												
		Change result = null;
		try {
			InputStream stream = new DataInputStream(
										new ByteArrayInputStream(modelChange.getBytes()));
	
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse( stream );
			
			Element root = document.getDocumentElement();
			
			NodeList actionElements = root.getElementsByTagName(ACTION);
	
			Node actionEl = actionElements.item(0);
			NamedNodeMap attr = actionEl.getAttributes();
	
			String actionTypeAttr = attr.getNamedItem(TYPE).getNodeValue();
			String initiatorAttr = attr.getNamedItem(INITIATOR_URI).getNodeValue();
			
	
			NodeList nodeElements = root.getElementsByTagName(NODE);
			if (nodeElements.getLength() != 0) {
				Node nodeEl = nodeElements.item(0);
	
				NamedNodeMap nodeAttr = nodeEl.getAttributes();
				String identifierAttr = nodeAttr.getNamedItem(IDENTIFIER).getNodeValue();
				String nodeTypeAttr = nodeAttr.getNamedItem(TYPE).getNodeValue();
	
				result = new NodeChange(identifierAttr, nodeTypeAttr, actionTypeAttr, initiatorAttr);
			}
			else {
				NodeList edgeElements = root.getElementsByTagName(EDGE);
				Node edgeEl = edgeElements.item(0);
	
				NamedNodeMap edgeAttr = edgeEl.getAttributes();
				String fromNodeAttr = edgeAttr.getNamedItem(FROM).getNodeValue();
				String toNodeAttr = edgeAttr.getNamedItem(TO).getNodeValue();
				String edgeTypeAttr = edgeAttr.getNamedItem(TYPE).getNodeValue();
	
				result = new EdgeChange(fromNodeAttr, toNodeAttr, edgeTypeAttr, actionTypeAttr, initiatorAttr);
			}
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new XmlMessageParserException(e);
		}
		catch (SAXException e) {
			e.printStackTrace();
			throw new XmlMessageParserException(e);
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new XmlMessageParserException(e);
		}

		return result;
	}
	
	public static String createMessage(Change modelChange) throws 
											XmlMessageCreatorException {
		String res = "";

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			
			Element root = document.createElement(MESSAGE);
			
			Element actionNode = document.createElement(ACTION);
			actionNode.setAttribute(INITIATOR_URI, modelChange.getInitiatorUri());
			actionNode.setAttribute(TYPE, modelChange.getAction());
			
			document.appendChild(root);
			root.appendChild(actionNode);
			
			Element itemNode = null;
			if (modelChange instanceof NodeChange) {
				NodeChange nodeChange = (NodeChange) modelChange;
				itemNode = document.createElement(NODE);
				itemNode.setAttribute(IDENTIFIER, nodeChange.getNodeName());
				itemNode.setAttribute(TYPE, nodeChange.getNodeType());
			}
			else {
				EdgeChange edgeChange = (EdgeChange) modelChange;
				itemNode = document.createElement(EDGE);
				itemNode.setAttribute(FROM, edgeChange.getFromNode());
				itemNode.setAttribute(TO, edgeChange.getToNode());
				itemNode.setAttribute(TYPE, edgeChange.getEdgeType());
			}
			root.appendChild(itemNode);
			
			DOMSource domSource = new DOMSource(document);
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();

			OutputStream outStream = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(outStream);
			transformer.transform(domSource, result);
			
			res = outStream.toString();		
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new XmlMessageCreatorException(e);
		}
		catch (TransformerConfigurationException e) {
			e.printStackTrace();
			throw new XmlMessageCreatorException(e);
		}
		catch (TransformerException e) {
			e.printStackTrace();
			throw new XmlMessageCreatorException(e);			
		}
		System.out.println("stream result = " + res);

		return res;
	}
	
}
