package ontorama.ontotools.query;

import java.util.Iterator;
import java.util.List;

/**
 * Title:
 * Description:  Models Query Result
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class QueryResult {

    Iterator ontologyTypesIterator;

    Query query;

    List nodes;
    List edges;

    /**
     *
     * @param query original query
     * @param nodes list of graph nodes
     * @param edges list of graph _graphEdges
     */
    public QueryResult(Query query, List nodes, List edges) {
        this.nodes = nodes;
        this.edges = edges;
        this.query = query;
    }

    /**
     *
     */
    public Query getQuery() {
        return this.query;
    }

    /**
     * Get list of returned graph nodes
     * @return nodes list
     */
    public List getNodesList () {
        return this.nodes;
    }

    /**
     * Get list of returned graph _graphEdges
     * @return _graphEdges list
     */
    public List getEdgesList () {
        return this.edges;
    }

    /**
     *
     */
    public String toString() {
        String str = "QueryResult";
        str = " for query term = " + query.getQueryTypeName();
        return str;
    }
}