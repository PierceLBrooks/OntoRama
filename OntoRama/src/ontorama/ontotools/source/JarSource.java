package ontorama.ontotools.source;

import java.io.InputStream;
import java.io.InputStreamReader;

import ontorama.ontotools.CancelledQueryException;
import ontorama.ontotools.SourceException;
import ontorama.ontotools.query.Query;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) DSTC 2002</p>
 * <p>Company: DSTC</p>
 * @version 1.0
 */


public class JarSource implements Source {
    /**
     *  Get a SourceResult from a given location in a jar file.
     *  This will work for any location relative to Class Loader.
     *  @param  relativePath  path relative to ClassLoader
     *  @return SourceResult
     *  @throws SourceException
     *
     * @todo implement if needed: throw CancelledQueryException
     */
    public SourceResult getSourceResult(String relativePath, Query query) throws SourceException, CancelledQueryException {
		InputStream stream = getInputStreamFromResource(relativePath);
		InputStreamReader reader = new InputStreamReader(stream);
        return new SourceResult(true, reader, null);
    }

	/**
	 * @todo check if need this class at all.
	 */
    public InputStream getInputStreamFromResource(String resourceName) throws SourceException {
		return this.getClass().getClassLoader().getResourceAsStream(resourceName);
    }


}