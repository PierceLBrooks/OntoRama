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
import java.net.URL;
import java.net.URLConnection;

import ontorama.OntoramaConfig;
import ontorama.webkbtools.query.Query;

public class UrlSource implements Source {

    /**
     *  Get a Reader from given uri.
     *  @param  uri  this object specified resource file
     *  @return reader
     *  @throws Exception
     */
    public Reader getReader (String uri, Query query) throws Exception {
        if (OntoramaConfig.DEBUG) {
            System.out.println ("uri = " + uri);
        }
        System.out.println ("class UrlSource, uri = " + uri);
        URL url = new URL (uri);
        URLConnection connection = url.openConnection();
        InputStreamReader reader = new InputStreamReader(connection.getInputStream());

        return reader;
    }

}
