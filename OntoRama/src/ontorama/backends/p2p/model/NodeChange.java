package ontorama.backends.p2p.model;


/**
 * 
 * Created on 7/03/2003
 * @author nataliya
 *
 */
public class NodeChange extends Change {
	private String _nodeName;
	private String _nodeType;
	
	public NodeChange (String nodeName, String nodeType, 
							String action, String initiatorUri) {
		_nodeName = nodeName;
		_nodeType = nodeType;
		_action = action;
		_initiatorUri = initiatorUri;
	}
	
	
	public String getNodeName() {
		return _nodeName;
	}

	public String getNodeType() {
		return _nodeType;
	}

}
