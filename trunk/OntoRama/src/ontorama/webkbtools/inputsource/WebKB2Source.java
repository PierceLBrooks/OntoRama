package ontorama.webkbtools.inputsource;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 * @author
 * @version 1.0
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import java.util.StringTokenizer;
import java.util.List;
import java.util.LinkedList;

import ontorama.OntoramaConfig;

public class WebKB2Source implements Source {

    /**
     * List used to hold multi RDF document.
     */
    private List docs = new LinkedList();

    /**
     *  Get a Reader from given uri.
     *  @param  uri  this object specified resource file
     *  @return reader
     *  @throws Exception
     */
    public Reader getReader (String uri) throws Exception {
        if (OntoramaConfig.DEBUG) {
            System.out.println ("uri = " + uri);
        }
        System.out.println ("class UrlSource, uri = " + uri);
        InputStreamReader reader = getInputStreamReader(uri);

        BufferedReader br = new BufferedReader( reader );
        System.out.println("Marker surported = " + br.markSupported());
        checkForMultiRdfDocuments(br);
        if( docs.size() > 1 ) {
            System.out.println("> > > > > multi documents found...");
            getRootTypeFromRdfDocuments();
        }
        reader.close();

        return getInputStreamReader(uri);
    }

    private InputStreamReader getInputStreamReader(String uri) throws Exception {
        URL url = new URL (uri);
        URLConnection connection = url.openConnection();
        return new InputStreamReader(connection.getInputStream());
    }


    /**
     * Read RDF documents into list.
     * If the list contains more then one document, the query
     * is ambugious. i.e "cat" can be (big_cat, Caterpillar, true_cat, etc)
     */
    private void checkForMultiRdfDocuments(BufferedReader br) {
        String token;
        String buf = "";
        try {
            String line = br.readLine();
            StringTokenizer st;
            while(line != null) {
                st = new StringTokenizer(line, "<", true);

                while(st.hasMoreTokens()) {
                    token = st.nextToken();
                    buf = buf + token;
                    if(token.equals("/rdf:RDF>")) {
                        docs.add(new String(buf));
                        buf = "";
                    }
                }
                buf = buf + "\n";
                line = br.readLine();
            }
        }
        catch(IOException ioe){}
    }

    private void getRootTypeFromRdfDocuments() {
        // load parser

        //get query terms

        System.out.println("Number of documents found = " + docs.size());

        System.out.println("**********\n\n\n" + (String)docs.remove(0) + "\n\n\n***********");

    }

}
