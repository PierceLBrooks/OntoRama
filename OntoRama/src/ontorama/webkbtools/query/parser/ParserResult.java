/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Sep 11, 2002
 * Time: 11:51:13 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.webkbtools.query.parser;


import java.util.*;

public class ParserResult {
    List nodes;
    List edges;


    /**
     *
     * @param nodes list of graph nodes
     * @param edges list of graph edges
     */
    public ParserResult(List nodes, List edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    /**
     * Get list of returned graph nodes
     * @return nodes list
     */
    public List getNodesList () {
        return this.nodes;
    }

    /**
     * Get list of returned graph edges
     * @return edges list
     */
    public List getEdgesList () {
        return this.edges;
    }

}
