/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 24/08/2002
 * Time: 11:53:20
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.ontotools.source.cgkb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import ontorama.model.graph.EdgeType;
import ontorama.ontotools.source.Source;
import ontorama.ontotools.source.SourceResult;
import ontorama.ontotools.source.UrlQueryStringConstructor;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.CancelledQueryException;
import ontorama.ontotools.SourceException;

public class CgKbSource implements Source {

    private static final int _defaultDepth = 1;

    public SourceResult getSourceResult(String uri, Query query) throws SourceException, CancelledQueryException {
        UrlQueryStringConstructor queryStringConstructor = new UrlQueryStringConstructor();
        List relLinksList = query.getRelationLinksList();
        if (relLinksList.isEmpty()) {
            System.err.println("Query relation links list is empty!");
        }
        Iterator it = relLinksList.iterator();
        String allReadersString="";
        while (it.hasNext()) {
            ontorama.model.graph.EdgeType relDetails = (ontorama.model.graph.EdgeType) it.next();

            String readerString = "";
            Hashtable paramTable = new Hashtable();

            paramTable.put("node",query.getQueryTypeName());

            paramTable.put("rel", relDetails.getName());
            int depth = query.getDepth();
            if ((depth < 1) || (depth > 3)) {
                depth=_defaultDepth;
            }
            Integer depthInt = new Integer(depth);
            paramTable.put("depth",depthInt.toString());

            String queryString = queryStringConstructor.getQueryString(paramTable);
            String fullUrl = uri + queryString;
            URL url = null;
            try {
                //Reader reader = doCgiFormGet(fullUrl);
                Reader reader = doCgiFormPost(uri, paramTable);
                BufferedReader br = new BufferedReader(reader);
                String line;
                while ((line=br.readLine()) != null) {
                    readerString = readerString + line + "\n";
                }
            }
            catch ( MalformedURLException mue) {
                throw new SourceException("Source Url " + url +
                " is malformed");
            }
            catch ( IOException ioe) {
                throw new SourceException("Couldn't retrieve data from source " + fullUrl);
            }
            allReadersString = allReadersString + readerString;
        }
        StringReader strReader = new StringReader(allReadersString);
        return new SourceResult(true, strReader, query);
    }


    private Reader doCgiFormPost (String urlLoc, Hashtable parameters) throws IOException {
        URL url = new URL(urlLoc);
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);

        PrintWriter out = new PrintWriter(connection.getOutputStream());
        Enumeration e = parameters.keys();
        String paramString = "";
        while (e.hasMoreElements()) {
            String paramName = (String) e.nextElement();
            String paramValue = (String) parameters.get(paramName);
            paramString =  paramString + paramName + "=" + paramValue + "&";
        }
        out.print(paramString);
        System.out.println("POST param: " + paramString);
        out.close();

       InputStreamReader in = new InputStreamReader(connection.getInputStream());
        return in;
    }

    private Reader doCgiFormGet (String urlLoc)  throws MalformedURLException, IOException {
        URL  url = new URL(urlLoc);
        URLConnection connection = url.openConnection();
        Reader reader = new InputStreamReader(connection.getInputStream());
        return reader;
    }
}
