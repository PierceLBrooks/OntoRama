package ontorama.model.graph;

public interface GraphView  {
    public void focus(Node node);
    public void setGraph(Graph graph);
    public void repaint();
}
