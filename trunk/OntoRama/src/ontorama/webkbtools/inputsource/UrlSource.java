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
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import ontorama.OntoramaConfig;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.util.SourceException;

public class UrlSource implements Source {
    /**
     *  Get a SourceResult from given uri.
     *  @param  uri  this object specified resource file
     *  @return sourceResult
     *  @throws SourceException
     */
    public SourceResult getSourceResult (String uri, Query query) throws SourceException {
      Reader reader = getReader(uri, query);
      return ( new SourceResult(true, reader, null) );
    }

    /**
     *  Get a Reader from given uri.
     *  @param  uri  this object specified resource file
     *  @return reader
     *  @throws Exception
     */
    private Reader getReader (String uri, Query query) throws SourceException {
        if (OntoramaConfig.DEBUG) {
            System.out.println ("uri = " + uri);
        }
        InputStreamReader reader = null;

        try {
          System.out.println ("class UrlSource, uri = " + uri);
          URL url = new URL (uri);
          URLConnection connection = url.openConnection();
          reader = new InputStreamReader(connection.getInputStream());
        }
        catch (MalformedURLException urlExc) {
          throw new SourceException("Url " + uri + " specified for this ontology source is not well formed, error: " + urlExc.getMessage());
        }
        catch (IOException ioExc) {
          throw new SourceException("Couldn't read input data source for " + uri + ", error: " + ioExc.getMessage());
        }

        return reader;
    }


}