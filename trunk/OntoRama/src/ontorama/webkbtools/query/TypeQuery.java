package ontorama.webkbtools.query;

import java.util.Iterator;

/**
 * Implemantations of this interface will be responsible for
 *  formulating a query for Ontology Server and returning
 *  an Iterator of OntologyTypes
 */
public interface TypeQuery {
    /**
     * Get all terms related to given term via any available
     * relative links.
     * @param	termName
     * @return	an iterator of OntologyTypes
     */
    Iterator getTypeRelative(Query query) throws Exception;

    /**
     * Get all terms related to given term via given relationLink.
     * @param	termName
     * @param	relationLink
     * @return	an iterator of OntologyTypes
     *
     * comment this method out for now because presently Query() controlls this
     */
    //Iterator getTypeRelative(String termName, int relationLink);
}

