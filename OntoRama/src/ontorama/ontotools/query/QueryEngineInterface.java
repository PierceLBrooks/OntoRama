package ontorama.ontotools.query;

import ontorama.ontotools.CancelledQueryException;
import ontorama.ontotools.NoSuchTypeInQueryResult;
import ontorama.ontotools.QueryFailedException;


/**
 * Implemantations of this interface will be responsible for
 *  taking a Query, executing it and returning QueryResult
 */
public interface QueryEngineInterface {

    public QueryResult executeQuery(Query query) throws NoSuchTypeInQueryResult, 
										QueryFailedException, CancelledQueryException;
}

