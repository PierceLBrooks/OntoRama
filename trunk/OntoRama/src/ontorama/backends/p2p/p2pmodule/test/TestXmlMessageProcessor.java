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
		_modelChangeNode = "<message>" +
				"<action type=\"assert\" initiatorUri=\"someone@ontorama.org\" />" + 
				"<node identifier=\"node1\" type=\"CONCEPT_TYPE\"/>" +
				"</message>";		
    	_nodeChange = new NodeChange("node1","CONCEPT_TYPE","assert","someone@ontorama.org");

		_modelChangeEdge = "<message>" +
				"<action type=\"reject\" initiatorUri=\"someone@ontorama.org\" />" + 
				"<edge fromNodeIdentifier=\"node1\" toNodeIdentifier=\"node2\" type=\"subtype\"/>" +
				"</message>";		

		_edgeChange = new EdgeChange("node1", "node2", "subtype", "reject", "someone@otnorama.org");
    }

    public void testParsingNode () throws Exception {
    	Change change = XmlMessageProcessor.parseXmlMessage(_modelChangeNode);
    	NodeChange res = (NodeChange) change;
    	assertEquals(_nodeChange.getAction(), res.getAction());
    	assertEquals(_nodeChange.getInitiatorUri(), res.getInitiatorUri());
    	assertEquals(_nodeChange.getNodeName(), res.getNodeName());
    	assertEquals(_nodeChange.getNodeType(), res.getNodeType());
    }
    
    public void testParsingEdge () throws Exception {
    	Change change = XmlMessageProcessor.parseXmlMessage(_modelChangeEdge);
    	EdgeChange res = (EdgeChange) change;
		assertEquals(_edgeChange.getAction(), res.getAction());
		assertEquals(_edgeChange.getInitiatorUri(), res.getInitiatorUri());
		assertEquals(_edgeChange.getFromNode(), res.getFromNode());
		assertEquals(_edgeChange.getToNode(), res.getToNode());
		assertEquals(_edgeChange.getEdgeType(), res.getEdgeType());
    }

}