package ontorama.ontotools.source;

/**
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 */

import ontorama.ontotools.CancelledQueryException;
import ontorama.ontotools.SourceException;
import ontorama.ontotools.query.Query;

/**
 * Model a Data Source.
 */
public interface Source {

    /**
     *  Get a SourceResult from given uri and using given query.
     *  @param  uri -  specified resource location
     */
    public SourceResult getSourceResult(String uri, Query query) throws SourceException, CancelledQueryException;

}