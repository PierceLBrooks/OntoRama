package ontorama.ontotools.inputsource;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 * @author
 * @version 1.0
 */

import ontorama.ontotools.query.Query;
import ontorama.ontotools.CancelledQueryException;
import ontorama.ontotools.SourceException;

/**
 * Model a Data Source.
 */
public interface Source {

//    /**
//     *  Get a Reader from given object.
//     *  @param  object  this object specified resource location
//     *  @return reader
//     *  @throws SourceException
//     */
//    public Reader getReader (String uri, Query query) throws SourceException;

    /**
     *  Get a SourceResult from given uri and using given query.
     *  @param  uri -  specified resource location
     *  @return sourceResult
     *  @throws SourceException
     */
    public SourceResult getSourceResult(String uri, Query query) throws SourceException, CancelledQueryException;

}