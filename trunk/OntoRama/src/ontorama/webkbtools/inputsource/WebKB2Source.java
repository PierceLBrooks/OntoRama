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

import javax.swing.JOptionPane;

import java.security.AccessControlException;

import ontorama.OntoramaConfig;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.util.ParserException;
import ontorama.webkbtools.inputsource.webkb.AmbuguousChoiceDialog;

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
     *  Get a Reader from given uri.
     *  @param  uri  this object specified resource file
     *  @return reader
     *  @throws Exception
     */
    public Reader getReader (String uri, Query query) throws Exception {
        this.query = query;

        String fullUri = uri + constructQueryString();
        Reader reader = executeWebkbQuery(fullUri);

        BufferedReader br = new BufferedReader( reader );
        System.out.println("Marker surported = " + br.markSupported());
        checkForMultiRdfDocuments(br);
        if( docs.size() > 1 ) {
            System.out.println("> > > > > multi documents found...");
            List typesList = getRootTypeFromRdfDocuments();
            //popupAmbuguousChoiceDialog(typesList);
            AmbuguousChoiceDialog dialog = new AmbuguousChoiceDialog(typesList);
        }
        reader.close();

        return getInputStreamReader(uri);
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
    private String constructQueryString () {
      return "";
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
     *
     * @todo  think what to do with exceptions: throw them? then we need
     * to change the Source interface... OR introduce our own SourceException
     * that can take care of all this.
     *
     */
    private List getRootTypeFromRdfDocuments() {
      List typesList = new LinkedList();
      try {

        System.out.println("Number of documents found = " + docs.size());

        //System.out.println("**********\n\n\n" + (String)docs.remove(0) + "\n\n\n***********");

        Iterator it = docs.iterator();
        while (it.hasNext()) {
          String nextDocStr = (String) it.next();
          StringReader curReader  = new StringReader(nextDocStr);
          System.out.println("query term name = " + query.getQueryTypeName());
          typesList.addAll((Collection) getTypesListFromRdfStream(curReader, query.getQueryTypeName()));
        }
      }
      catch (ParserException parserExc) {
        System.out.println("ParserException: " + parserExc);
        parserExc.printStackTrace();
        System.exit(-1);

      }
      return typesList;
    }


   /**
     *
     */
    private List getTypesListFromRdfStream (Reader reader, String termName)
                        throws ParserException, AccessControlException {
        List typeNamesList = new LinkedList();
        try {
            Model model = new ModelMem();
            model.read(reader, "");

            Property testProperty = new PropertyImpl("http://www.w3.org/TR/1999/PR-rdf-schema-19990303#label");
            ResIterator resIt = model.listSubjectsWithProperty(testProperty);

            while (resIt.hasNext()) {
                Resource r = resIt.next();
                //System.out.println("\nresource = " + r);
                StmtIterator stIt = r.listProperties(testProperty);
                while (stIt.hasNext()) {
                    Statement s = stIt.next();
                    //System.out.println("\nstatement = " + s);
                    RDFNode object = s.getObject();
                    //System.out.println("\nobject = " + object.toString());
                    if (object.toString().equals(termName)) {
                      System.out.println("FOUND: " + r);
                      typeNamesList.add(r.toString());
                    }
                }
            }
        }
        catch (AccessControlException secExc) {
                throw secExc;
        }
        catch (RDFException e) {
            e.printStackTrace();
            throw new ParserException("Error in parsing RDF: " + e.getMessage());
        }
        return typeNamesList;
    }

    /**
     *
     */
//    private void popupAmbuguousChoiceDialog (List listOfChoices) {
//      String message = "Search term is ambuguous...Please choose one of the following ";
//      System.out.println("listOfChoices = " + listOfChoices);
//
//      Object [] choices = listOfChoices.toArray();
//      System.out.println("choices = " + choices);
//
//      JOptionPane optionPane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE);
//      optionPane.showOptionDialog(null,message, "title here",
//                          JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
//                          null, choices, choices[0]);
//    }


}
