package ontorama.backends;

import java.util.List;
import ontorama.webkbtools.query.Query;

/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface Backend{
    public ExtendedGraph search(Query query);
    public void assertRelation(GraphNode fromNode, GraphNode toNode, int edgeType,String nameSpaceForRelation); 
    public void assertConcept(GraphNode fromNode, GraphNode node, int edgeType,String nameSpaceForRelation); 
    public void rejectRelation(GraphNode fromNode, GraphNode toNode, int edgeType,String nameSpaceForRelation);
    public void updateConcept(GraphNode node);
    public List getPanels();
    public Menu getMenu();
 }
