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

import ontorama.OntoramaConfig;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.CancelledQueryException;
import ontorama.ontotools.SourceException;

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
        Reader reader = getReader(uri, query);
        return (new SourceResult(true, reader, null));
    }

    /**
     *  Get a Reader from given uri.
     *  @param  uri  this object specified resource file
     *  @return reader
     *  @throws SourceException
     */
    private Reader getReader(String uri, Query query) throws SourceException {
        if (OntoramaConfig.DEBUG) {
            System.out.println("uri = " + uri);
        }
        InputStreamReader reader = null;

        try {
            System.out.println("class UrlSource, uri = " + uri);
            URL url = new URL(uri);
            URLConnection connection = url.openConnection();
            reader = new InputStreamReader(connection.getInputStream());
        } catch (MalformedURLException urlExc) {
            throw new SourceException("Url " + uri + " specified for this ontology source is not well formed, error: " + urlExc.getMessage());
        } catch (IOException ioExc) {
            throw new SourceException("Couldn't read input data source for " + uri + ", error: " + ioExc.getMessage());
        }

        return reader;
    }


}
