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
import java.io.StringReader;
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import java.util.StringTokenizer;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Enumeration;

import java.awt.Frame;

//import javax.swing.JOptionPane;

import java.security.AccessControlException;

import ontorama.OntoramaConfig;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.query.parser.rdf.RdfWebkbParser;
import ontorama.webkbtools.util.ParserException;
import ontorama.webkbtools.util.NoSuchPropertyException;
import ontorama.webkbtools.util.SourceException;
import ontorama.webkbtools.inputsource.webkb.AmbiguousChoiceDialog;
import ontorama.webkbtools.inputsource.webkb.WebkbQueryStringConstructor;
import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;

import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.common.*;
import com.hp.hpl.mesa.rdf.jena.vocabulary.*;
import com.hp.hpl.jena.daml.*;
import com.hp.hpl.jena.daml.common.DAMLModelImpl;
import com.hp.hpl.jena.daml.common.*;


public class WebKB2Source implements Source {

  /**
   *
   */
  private String uri;

    /**
     *
     */
   private Query query;

    /**
     * List used to hold multi RDF document.
     */
    private List docs = new LinkedList();

    /**
     * list of types extracted from the multiple readers
     */
    private List typesList = new LinkedList();

    /**
     * this is hacky
     */
    private Query newQuery = null;

    /**
     *  Get a SourceResult from given uri. First, get a reader and check ir.
     *  If result is ambiguous - propmpt user
     *  to make a choice and return new formulated query. If result is not
     *  ambiguous - return reader.
     *  @param  uri - base uri for the WebKB cgi script
     *  @param  query - object Query holding details of a query we are executing
     *  @return sourceResult
     *  @throws SourceException
     *
     * @todo should throuw some specialised exceptions rather then a general exception!
     */
    public SourceResult getSourceResult (String uri, Query query) throws SourceException {
        this.query = query;
        this.uri = uri;

        this.docs = new LinkedList();
        this.typesList = new LinkedList();
        this.newQuery = null;

        String fullUri = uri + constructQueryString(query);
        Reader resultReader = null;
        BufferedReader br = null;

        try {
          Reader reader = executeWebkbQuery(fullUri);
          System.out.println("got stream back from webkb");

          br = new BufferedReader( reader );

          System.out.println("checking for multi documents");
          checkForMultiRdfDocuments(br);
          System.out.println("docs size = " + docs.size());
          if( resultIsAmbiguous() ) {
            System.out.println("\n\nresult is ambiguous");
            this.newQuery = processAmbiguousResultSet();
            return ( new SourceResult(false, null, this.newQuery));
          }
          reader.close();
          //resultReader = getInputStreamReader(fullUri);
//          System.out.println("docs size = " + docs.size());
//          System.out.println("*********************************");
//          System.out.println((String) docs.get(0));
//          System.out.println("*********************************");
          resultReader = new StringReader((String) docs.get(0));

        }
        catch (IOException ioExc) {
          throw new SourceException("Couldn't read input data source for " + fullUri + ", error: " + ioExc.getMessage());
        }
        System.out.println("resultReader = " + resultReader);
       return (new SourceResult (true, resultReader, null));
       //return (new SourceResult (true, br, null));
    }

    /**
     * @todo this is a hack to avoid using SourceResult for now.
     */
    public Query getNewQuery () {
      return this.newQuery;
    }

    /**
     *
     */
    private InputStreamReader getInputStreamReader(String uri) throws MalformedURLException, IOException {
        URL url = new URL (uri);
        URLConnection connection = url.openConnection();
        return new InputStreamReader(connection.getInputStream());
    }

    /**
     *
     */
    private String constructQueryString (Query query) {
      WebkbQueryStringConstructor queryConstructor = new WebkbQueryStringConstructor();
      return queryConstructor.getQueryString(query, OntoramaConfig.queryOutputFormat);
    }

    /**
     *
     */
    private Reader executeWebkbQuery (String fullUrl) throws IOException {
        if (OntoramaConfig.DEBUG) {
            System.out.println ("fullUrl = " + fullUrl);
        }
        System.out.println ("class WebKB2Source, fullUrl = " + fullUrl);
        InputStreamReader reader = getInputStreamReader(fullUrl);
        return reader;
    }


    /**
     * Read RDF documents into list.
     * If the list contains more then one document, the query
     * is ambugious. i.e "cat" can be (big_cat, Caterpillar, true_cat, etc)
     *
     * @todo remove count and debugging print statement
     */
    private void checkForMultiRdfDocuments(BufferedReader br)
                          throws IOException {
        String token;
        String buf = "";
        String line = br.readLine();
        StringTokenizer st;
        int count = 1;
        while(line != null) {
          System.out.print(count + ".");
          count++;
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

    /**
     * deal with case when result is ambiguous: extract list of choices
     * from the list of received documents and popup dialog box prompting
     * user to make a choice.
     */
    private Query processAmbiguousResultSet () {
      getRootTypesFromStreams();

      Frame[] frames = ontorama.view.OntoRamaApp.getFrames();
      //String selectedType = (String) typesList.get(0);
      String selectedType = ( (OntologyType) typesList.get(0)).getName();
      if (frames.length > 0) {
        AmbiguousChoiceDialog dialog = new AmbiguousChoiceDialog(typesList, frames[0]);
        selectedType = dialog.getSelected();
      }
      else {
        //AmbiguousChoiceDialog dialog = new AmbiguousChoiceDialog(typesList, null);
        //selectedType = dialog.getSelected();
      }
      System.out.println("\n\n\nselectedType = " + selectedType);

      String newTermName = selectedType;

      Query newQuery = new Query(newTermName, this.query.getRelationLinksList());
      return newQuery;

//      // execute new query to webkb and return new reader
//      String fullUri = this.uri + constructQueryString(newQuery);
//      //Reader selectedReader = executeWebkbQuery(fullUri);
//
//      //String selectedDocument = (String) this.typeToReaderMap.get(selectedType);
//      //StringReader selectedReader = new StringReader(selectedDocument);
//
//      //return selectedReader;
    }


    /**
     * Build list of top/root types extracted from the multiple documents,
     * and build a mapping between types and documents themselfs;
     *
     * The way we do this is: iterate through streams and extract list of types
     * for each stream, than add contents of each list to the global  list of
     * possible query candidates.
     *
     * @todo  think what to do with exceptions: throw them? then we need
     * to change the Source interface... OR introduce our own SourceException
     * that can take care of all this.
     *
     */
    private void getRootTypesFromStreams() {
      try {

        //System.out.println("Number of documents found = " + docs.size());

        //System.out.println("**********\n\n\n" + (String)docs.remove(0) + "\n\n\n***********");

        Iterator it = docs.iterator();
        while (it.hasNext()) {
          String nextDocStr = (String) it.next();
          StringReader curReader  = new StringReader(nextDocStr);
          //System.out.println("query term name = " + query.getQueryTypeName());
          List curTypesList = getTypesListFromRdfStream(curReader, query.getQueryTypeName());
          for (int i = 0; i < curTypesList.size(); i++) {
            OntologyType type = (OntologyType) curTypesList.get(i);
            if ( ! typesList.contains(type)) {
              //System.out.println ("adding type " + type.getName() + " to the big list");
              typesList.add(type);
            }
          }

        }
      }
      catch (ParserException parserExc) {
        System.out.println("ParserException: " + parserExc);
        parserExc.printStackTrace();
        System.exit(-1);
      }
    }

    /**
     * Get list of types that we think user may have meant to search for
     * from the given reader.
     *
     * The way we do this: we parse each reader into iterator of ontology types
     * using corresponding webkb parser, then we go through this iterator and
     * look for types with synonym equals to 'termName' (term name that user
     * searched for).
     *
     * Another way to do this: use rdf parser and do pretty much the same:
     * go through rdf statements that have 'label' propertyr value that
     * equals 'termName'. We use 'label' property because it is describing
     * synonyms.
     *
     * Assumption: we assume that in WebKB2 each ambuguous result has
     * an original search term as a synonym. Otherwise, it is not clear
     * how to extract these 'wanted' terms from the list of ontology terms
     * returned from webkb for each ambuguous choice.
     *
     * @todo  check if this assumption (above) is fair
     *
     * @todo  shouldn't hard code synonym property name, because if someone changes
     * it in the config.xml file - the whole thing will crash without reasonable
     * explanation. find a better way to do this!
     */
    private List getTypesListFromRdfStream (Reader reader, String termName)
                        throws ParserException, AccessControlException {

        List typeNamesList = new LinkedList();

        RdfWebkbParser parser = new RdfWebkbParser();
        Collection colOfTypes = parser.getOntologyTypeCollection(reader);
        Iterator typesIt = colOfTypes.iterator();
        while (typesIt.hasNext()) {
          OntologyType curType = (OntologyType) typesIt.next();
          String synPropName = "Synonym";
          try {
            List synonyms = curType.getTypeProperty(synPropName);
            if (synonyms.contains(termName)) {
              //System.out.println("***FOUND: " + curType.getName());
              typeNamesList.add(curType);
            }
          }
          catch (NoSuchPropertyException e) {
            System.out.println("NoSuchPropertyException for property " + synPropName);
            e.printStackTrace();
            System.exit(-1);
          }
        }
        return typeNamesList;
    }

    /**
     * used for tests
     */
    protected boolean resultIsAmbiguous () {
       if( docs.size() > 1 ) {
        System.out.println("docs.size = " + docs.size());
        return true;
       }
       return false;
    }

    /**
     * used for tests
     */
    protected int getNumOfChoices () {
      return typesList.size();
    }

    /**
     * used for tests
     */
    protected List getChoicesList () {
      return typesList;
    }
}
