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
import ontorama.webkbtools.inputsource.webkb.AmbuguousChoiceDialog;
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
   private Query query;

    /**
     * List used to hold multi RDF document.
     */
    private List docs = new LinkedList();

    /**
     * map type name to corresponding reader
     * keys - string, term name
     * values - corresponding string holding the document
     */
    private Hashtable typeToReaderMap = new Hashtable();

    /**
     * list of types extracted from the multiple readers
     */
    private List typesList = new LinkedList();

    /**
     *  Get a Reader from given uri.
     *  @param  uri - base uri for the WebKB cgi script
     *  @param  query - object Query holding details of a query we are executing
     *  @return reader
     *  @throws Exception
     *
     *
     * @todo not using type to reader mapping - get rid of all occurences!
     */
    public Reader getReader (String uri, Query query) throws Exception {
        this.query = query;

        String fullUri = uri + constructQueryString(query);
        //String fullUri = uri;
        Reader reader = executeWebkbQuery(fullUri);

        BufferedReader br = new BufferedReader( reader );
        checkForMultiRdfDocuments(br);
        if( docs.size() > 1 ) {
            System.out.println("> > > > > multi documents found...");
            getRootTypesFromStreams();

            Frame[] frames = ontorama.view.OntoRamaApp.getFrames();
            //String selectedType = (String) typesList.get(0);
            String selectedType = ( (OntologyType) typesList.get(0)).getName();
            if (frames.length > 0) {
              AmbuguousChoiceDialog dialog = new AmbuguousChoiceDialog(typesList, frames[0]);
              selectedType = dialog.getSelected();
            }
            else {
              AmbuguousChoiceDialog dialog = new AmbuguousChoiceDialog(typesList, null);
              selectedType = dialog.getSelected();
            }
            System.out.println("\n\n\nselectedType = " + selectedType);

            String newTermName = selectedType;

            Query newQuery = new Query(newTermName, query.getRelationLinksList());

            // execute new query to webkb and return new reader
            fullUri = uri + constructQueryString(newQuery);
            Reader selectedReader = executeWebkbQuery(fullUri);

            //String selectedDocument = (String) this.typeToReaderMap.get(selectedType);
            //StringReader selectedReader = new StringReader(selectedDocument);
            return selectedReader;
        }
        reader.close();
        return getInputStreamReader(fullUri);
    }

    /**
     *
     */
    private InputStreamReader getInputStreamReader(String uri) throws Exception {
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
    private Reader executeWebkbQuery (String fullUrl) throws Exception {
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

        System.out.println("Number of documents found = " + docs.size());

        //System.out.println("**********\n\n\n" + (String)docs.remove(0) + "\n\n\n***********");

        Iterator it = docs.iterator();
        while (it.hasNext()) {
          String nextDocStr = (String) it.next();
          StringReader curReader  = new StringReader(nextDocStr);
          System.out.println("query term name = " + query.getQueryTypeName());
          List curTypesList = getTypesListFromRdfStream(curReader, query.getQueryTypeName());
          for (int i = 0; i < curTypesList.size(); i++) {
            OntologyType type = (OntologyType) curTypesList.get(i);
//            String typeName = (String) curTypesList.get(i);
//            if ( ! typesList.contains(typeName)) {
//              typesList.add(typeName);
//            }
//            typeToReaderMap.put(typeName, nextDocStr);
//          }
            if ( ! typesList.contains(type)) {
              //System.out.println ("adding type " + type.getName() + " to the big list");
              typesList.add(type);
            }
            typeToReaderMap.put(type, nextDocStr);
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
              System.out.println("***FOUND: " + curType.getName());
              typeNamesList.add(curType);
            }
          }
          catch (NoSuchPropertyException e) {
            System.out.println("NoSuchPropertyException for property " + synPropName);
            e.printStackTrace();
            System.exit(-1);
          }
        }
//        try {
//            Model model = new ModelMem();
//            model.read(reader, "");
//
//            Property testProperty = new PropertyImpl("http://www.w3.org/TR/1999/PR-rdf-schema-19990303#label");
//            ResIterator resIt = model.listSubjectsWithProperty(testProperty);
//
//            while (resIt.hasNext()) {
//                Resource r = resIt.next();
//                //System.out.println("\nresource = " + r);
//                StmtIterator stIt = r.listProperties(testProperty);
//                while (stIt.hasNext()) {
//                    Statement s = stIt.next();
//                    //System.out.println("\nstatement = " + s);
//                    RDFNode object = s.getObject();
//                    //System.out.println("\nobject = " + object.toString());
//                    if (object.toString().equals(termName)) {
//                      System.out.println("FOUND: " + r);
//                      typeNamesList.add(r.toString());
//                    }
//                }
//            }
//        }
//        catch (AccessControlException secExc) {
//                throw secExc;
//        }
//        catch (RDFException e) {
//            e.printStackTrace();
//            throw new ParserException("Error in parsing RDF: " + e.getMessage());
//        }
        return typeNamesList;
    }



}
