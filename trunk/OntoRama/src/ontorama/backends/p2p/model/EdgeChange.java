package ontorama.backends.p2p.model;

/**
 * 
 * Created on 7/03/2003
 * @author nataliya
 *
 */
public class EdgeChange extends Change {
	private String _edgeType;
	private String _fromNode;
	private String _toNode;
	
	public EdgeChange ( String fromNode, String toNode, String edgeType, 
						String action, String initiatorUri) {
		_edgeType = edgeType;
		_fromNode = fromNode;
		_toNode = toNode;
		_action = action;
		_initiatorUri = initiatorUri;
	}
	
	public String getEdgeType() {
		return _edgeType;
	}

	public String getFromNode() {
		return _fromNode;
	}

	public String getToNode() {
		return _toNode;
	}

}
