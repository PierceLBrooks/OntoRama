package ontorama.webkbtools.query;

import java.util.*;

/**
 * Title:
 * Description:  Models Query Result
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class QueryResult {

    /**
     *
     */
    Iterator ontologyTypesIterator;

    /**
     *
     */
    Query query;

    List nodes;
    List edges;

//    /**
//     *
//     */
//    public QueryResult(Query query, Iterator ontologyTypesIterator) {
//        this.ontologyTypesIterator = ontologyTypesIterator;
//        this.query = query;
//    }

    /**
     *
     * @param query original query
     * @param nodes list of graph nodes
     * @param edges list of graph edges
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

//    /**
//     *
//     */
//    public Iterator getOntologyTypesIterator() {
//        return this.ontologyTypesIterator;
//    }

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

    /**
     *
     */
    public String toString() {
        String str = "QueryResult";
        str = " for query term = " + query.getQueryTypeName();
        return str;
    }


}