package ontorama.ontotools.source;

import java.io.Reader;

import ontorama.ontotools.query.Query;

/**
 * <p>Description: Holds result returned from Ontology Server or
 * read from static ontology file.
 * The result would have following characteristics:
 * - if query have been successfull or not.
 * - a stream describing one ontology term
 * - new Query (corresponding to the choice user has made)
 * </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class SourceResult {

    /**
     *
     */
    private boolean queryIsSuccess = false;

    /**
     *
     */
    private Reader reader = null;

    /**
     *
     */
    private Query query = null;

    /**
     *
     */
    public SourceResult(boolean queryIsSuccess, Reader reader, Query query) {
        this.reader = reader;
        this.queryIsSuccess = queryIsSuccess;
        this.query = query;
    }

    /**
     *
     */
    public boolean queryWasSuccess() {
        return this.queryIsSuccess;
    }

    /**
     *
     */
    public Reader getReader() {
        return this.reader;
    }

    /**
     *
     */
    public Query getNewQuery() {
        return this.query;
    }

    /**
     *
     */
    public String toString() {
        String str = "Source Result";

        str = str + ": queryIsSuccess = " + queryIsSuccess;
        str = str + ", reader = " + reader;
        str = str + ", query = " + query;

        return str;
    }
}