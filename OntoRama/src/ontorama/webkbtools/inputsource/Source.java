package ontorama.webkbtools.inputsource;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 * @author
 * @version 1.0
 */

import java.io.Reader;

/**
 * Model a Data Source.
 */
public interface Source {

    /**
     *  Get a Reader from given object.
     *  @param  object  this object specified resource location
     *  @return reader
     *  @throws Exception
     */
    public Reader getReader (String uri) throws Exception;

}