package ontorama.webkbtools.query;

import java.util.Iterator;

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

    /**
     *
     */
    public QueryResult(Query query, Iterator ontologyTypesIterator) {
        this.ontologyTypesIterator = ontologyTypesIterator;
        this.query = query;
    }

    /**
     *
     */
    public Query getQuery() {
        return this.query;
    }

    /**
     *
     */
    public Iterator getOntologyTypesIterator() {
        return this.ontologyTypesIterator;
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