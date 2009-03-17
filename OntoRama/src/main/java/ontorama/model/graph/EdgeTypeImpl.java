package ontorama.model.graph;


public class EdgeTypeImpl implements EdgeType {

    private String _name;
    private String _reverseEdgeName;
    private String _namespace;

    public EdgeTypeImpl(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    public String getReverseEdgeName() {
        return _reverseEdgeName;
    }

    public void setReverseEdgeName(String reverseEdgeName) {
        _reverseEdgeName = reverseEdgeName;
    }

    public void setNamespace (String namespace) {
        _namespace = namespace;
    }

    public String getNamespace() {
        return _namespace;
    }

    public String toString() {
        String str = "EdgeType: ";
        str = str + " name = " + _name + ", reversedEdgeName = " + _reverseEdgeName;
        return str;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_name == null) ? 0 : _name.hashCode());
		result = prime * result
				+ ((_reverseEdgeName == null) ? 0 : _reverseEdgeName.hashCode());
		return result;
	}

	/*
	 * We don't include _namespace in equals/hashcode since that gets changed after the
	 * object is used as key in hash structure - we would get hash misses otherwise.
	 * 
	 * TODO: look into the namespace question, the way namespaces are used here seems
	 *       rather wrong
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EdgeTypeImpl other = (EdgeTypeImpl) obj;
		if (_name == null) {
			if (other._name != null)
				return false;
		} else if (!_name.equals(other._name))
			return false;
		if (_reverseEdgeName == null) {
			if (other._reverseEdgeName != null)
				return false;
		} else if (!_reverseEdgeName.equals(other._reverseEdgeName))
			return false;
		return true;
	}
}
