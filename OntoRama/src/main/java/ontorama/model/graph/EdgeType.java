package ontorama.model.graph;

public interface EdgeType {
    public String getName();
    public String getReverseEdgeName();
    public void setReverseEdgeName(String reverseEdgeName);
    public String getNamespace();
    public void setNamespace (String namespace);
}
