package ontorama.ontotools.query;

import java.util.List;

import ontorama.model.graph.Edge;
import ontorama.model.graph.Node;

public class QueryResult {

    Query query;

    List<Node> nodes;
    List<Edge> edges;

    /**
     * @param query original query
     * @param nodes list of graph nodes
     * @param edges list of graph _graphEdges
     */
    public QueryResult(Query query, List<Node> nodes, List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
        this.query = query;
    }

    public Query getQuery() {
        return this.query;
    }

    /**
     * Get list of returned graph nodes
     * @return nodes list
     */
    public List<Node> getNodesList () {
        return this.nodes;
    }

    /**
     * Get list of returned graph _graphEdges
     * @return _graphEdges list
     */
    public List<Edge> getEdgesList () {
        return this.edges;
    }

    @Override
    public String toString() {
        String str = "QueryResult";
        str = str +  " for query term = " + query.getQueryTypeName() + ", nodes = " + getNodesList() + ", edges = " + getEdgesList();
        return str;
    }
}