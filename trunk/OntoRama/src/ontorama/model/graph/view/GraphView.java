/*
 * Created by IntelliJ IDEA.
 * User: johang
 * Date: 1/08/2002
 * Time: 16:32:36
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model.graph.view;


public interface GraphView  {
    public void focus(ontorama.model.graph.Node node);
    public void setGraph(ontorama.model.graph.Graph graph);
    public void repaint();
}
