package ontorama.backends.p2p.p2pmodule.test;


import ontorama.backends.p2p.model.Change;
import ontorama.backends.p2p.model.EdgeChange;
import ontorama.backends.p2p.model.NodeChange;
import ontorama.backends.p2p.p2pmodule.XmlMessageProcessor;
import junit.framework.TestCase;


/**
 * Copyright: Copyright (c) 2002
 * Company: DSTC
 * @author nataliya
 * @version 1.0
 */

public class TestXmlMessageProcessor extends TestCase {

	String _modelChangeNode;
	NodeChange _nodeChange;
	
	String _modelChangeEdge;
	EdgeChange _edgeChange;
	
    public TestXmlMessageProcessor(String name) {
        super(name);
    }

    protected void setUp()  {
		_modelChangeNode = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<message>" +
				"<action type=\"assert\" initiatorUri=\"someone@ontorama.org\" />" + 
				"<node identifier=\"node1\" type=\"CONCEPT_TYPE\"/>" +
				"</message>";		
    	_nodeChange = new NodeChange("node1","CONCEPT_TYPE","assert","someone@ontorama.org");

		_modelChangeEdge = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<message>" +
				"<action type=\"reject\" initiatorUri=\"someone@ontorama.org\" />" + 
				"<edge fromNodeIdentifier=\"node1\" toNodeIdentifier=\"node2\" type=\"subtype\"/>" +
				"</message>";		

		_edgeChange = new EdgeChange("node1", "node2", "subtype", "reject", "someone@ontorama.org");
    }

    public void testParsingNode () throws Exception {
    	Change change = XmlMessageProcessor.parseXmlMessage(_modelChangeNode);
    	NodeChange res = (NodeChange) change;
		checkNodeChange(_nodeChange, res);
    }

    
    public void testParsingEdge () throws Exception {
    	Change change = XmlMessageProcessor.parseXmlMessage(_modelChangeEdge);
    	EdgeChange res = (EdgeChange) change;
		checkEdgeChange(_edgeChange, res);
    }

    
    public void testCreateNodeMessage() throws Exception {
    	String message = XmlMessageProcessor.createMessage(_nodeChange);
    	
    	// read and parse resulting string and check if 
    	// resulting NodeChange is the same as our original node change 
		Change change = XmlMessageProcessor.parseXmlMessage(message);
		NodeChange res = (NodeChange) change;
    	checkNodeChange(_nodeChange, res);
    }


	public void testCreateEdgeMessage() throws Exception {
		String message = XmlMessageProcessor.createMessage(_edgeChange);
    	
		// read and parse resulting string and check if 
		// resulting EdgeChange is the same as our original edge change 
		Change change = XmlMessageProcessor.parseXmlMessage(message);
		EdgeChange res = (EdgeChange) change;
		checkEdgeChange(_edgeChange, res);
	}

	private void checkNodeChange(NodeChange expected, NodeChange result) {
		assertEquals(expected.getAction(), result.getAction());
		assertEquals(expected.getInitiatorUri(), result.getInitiatorUri());
		assertEquals(expected.getNodeName(), result.getNodeName());
		assertEquals(expected.getNodeType(), result.getNodeType());
	}

	private void checkEdgeChange(EdgeChange expected, EdgeChange res) {
		assertEquals(expected.getAction(), res.getAction());
		assertEquals(expected.getInitiatorUri(), res.getInitiatorUri());
		assertEquals(expected.getFromNode(), res.getFromNode());
		assertEquals(expected.getToNode(), res.getToNode());
		assertEquals(expected.getEdgeType(), res.getEdgeType());
	}

}