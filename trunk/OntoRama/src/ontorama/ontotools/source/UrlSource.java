package ontorama.ontotools.source;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 * @author
 * @version 1.0
 */

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import ontorama.ontotools.CancelledQueryException;
import ontorama.ontotools.SourceException;
import ontorama.ontotools.query.Query;

public class UrlSource implements Source {
    /**
     *  Get a SourceResult from given uri.
     *  @param  uri  this object specified resource file
     *  @return sourceResult
     *  @throws SourceException
     *  @throws CancelledQueryException
     *
     *  @todo implement if needed: throw CancelledQueryException
     */
    public SourceResult getSourceResult(String uri, Query query) throws SourceException, CancelledQueryException {
		try {
			System.out.println("class UrlSource, uri = " + uri);
			URL url = new URL(uri);
			URLConnection connection = url.openConnection();
			InputStreamReader reader = new InputStreamReader(connection.getInputStream());
			return (new SourceResult(true, reader, null));
		} catch (MalformedURLException urlExc) {
			throw new SourceException("Url " + uri + " specified for this ontology source is not well formed, error: " + urlExc.getMessage(), urlExc);
		} catch (IOException ioExc) {
			throw new SourceException("Couldn't read input data source for " + uri + ", error: " + ioExc.getMessage(), ioExc);
		}
    }



}
