package ontorama.model.graph;

public class NodeTypeImpl implements NodeType {
	private String _name;
	
	public NodeTypeImpl(String name) {
		_name = name;
	}
	
	public String getName() {
		return _name;
	}

}
