package ontorama.webkbtools.query;

import java.util.Iterator;

/**
 * Implemantations of this interface will be responsible for
 *  taking a Query, executing it and returning QueryResult
 */
public interface QueryEngineInterface {

    /**
     */
    public QueryResult getQueryResult();
}

