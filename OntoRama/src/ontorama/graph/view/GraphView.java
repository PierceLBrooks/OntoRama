/*
 * Created by IntelliJ IDEA.
 * User: johang
 * Date: 1/08/2002
 * Time: 16:32:36
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.graph.view;

import ontorama.model.*;

public interface GraphView  {
    public void focus(GraphNode node);
    public void setGraph(GraphInterface graph);
    public GraphInterface getGraph();
    public void repaint();
//    public void nodeIsInCurrentBranch(GraphNode node);
//    public void displayBranch(GraphNode brachRootNode);
}
