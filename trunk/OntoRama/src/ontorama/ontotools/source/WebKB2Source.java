package ontorama.ontotools.source;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 * @author
 * @version 1.0
 */

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessControlException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import ontorama.OntoramaConfig;
import ontorama.model.graph.Edge;
import ontorama.model.graph.Node;
import ontorama.ontotools.CancelledQueryException;
import ontorama.ontotools.ParserException;
import ontorama.ontotools.SourceException;
import ontorama.ontotools.parser.ParserResult;
import ontorama.ontotools.parser.rdf.RdfWebkbParser;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.source.webkb.AmbiguousChoiceDialog;
import ontorama.ontotools.source.webkb.WebkbQueryStringConstructor;
import ontorama.ui.OntoRamaApp;


public class WebKB2Source implements Source {


    /**
     * query we want to post to webkb
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
     * holds string representing all reader data
     * returned from webkb query
     */
    private String readerString = "";

    /**
     * Patterns to look  for if webkb query was unsuccessfull
     */
    private String webkbErrorStartPattern = "<br><b>";
    private String webkbErrorEndPattern = "</b><br>";

    /**
     * name of property 'Synonym'
     *
     * @todo  shouldn't hard code synonym property name, because if someone changes
     * it in the config.xml file - the whole thing will crash without reasonable
     * explanation. find a better way to do this!
     */
    private String synPropName = "synonym";

    /**
     *  Get a SourceResult from given uri. First, get a reader and check ir.
     *  If result is ambiguous - propmpt user
     *  to make a choice and return new formulated query. If result is not
     *  ambiguous - return reader.
     *
     *  To check if webkb returned error, we check if there were RDF end of
     *  element tags in the returned data (to see if we got RDF document back).
     *  If not - we check for error patterns trying to extract the error message.
     *
     *  @param  uri - base uri for the WebKB cgi script
     *  @param  query - object Query holding details of a query we are executing
     *  @return sourceResult
     *  @throws SourceException
     *  @throws CancelledQueryException
     *
     *  @todo mechanism for stopping interrupted queries seems hacky. at the moment
     *  we only check in the one method if thread is interrupted (because this loop is most time consuming)
     *  what if tread is interrupted somewhere else? it won't work untill process is finised! does this
     *  mean we should check in each method if thread is interrupted? then it seems even more hacky!
     */
    public SourceResult getSourceResult(String uri, Query query) throws SourceException, CancelledQueryException {
        this.query = query;

        // reinitialise all global vars
        this.docs = new LinkedList();
        this.typesList = new LinkedList();
        this.readerString = "";

        int queryDepth = query.getDepth();

        Query testQuery = query;
        testQuery.setDepth(1);
        String fullUri = constructQueryUrl(uri, testQuery);
        query.setDepth(queryDepth);


        Reader resultReader = null;
        BufferedReader br = null;

        try {
            Reader reader = executeWebkbQuery(fullUri);
            br = new BufferedReader(reader);

            // check for multiple documents. If the documents list
            // size == 0, this means that we didn't find RDF documents
            // in the reader. In this case - look for error message
            checkForMultiRdfDocuments(br);
            if (docs.size() == 0) {
                String webkbError = checkForWebkbErrors(readerString);
                throw new SourceException("WebKB Error: " + webkbError);
            }
            if (resultIsAmbiguous()) {
                Query newQuery = processAmbiguousResultSet();
                return (new SourceResult(false, null, newQuery));
            }
            reader.close();
            //resultReader = new StringReader((String) docs.get(0));
            resultReader = executeWebkbQuery(constructQueryUrl(uri, query));
        } catch (IOException ioExc) {
            throw new SourceException("Couldn't read input data source for \n" 
            							+ fullUri + ",\nerror: \n" 
            							+ ioExc.getMessage(), ioExc);
        } catch (ParserException parserExc) {
            throw new SourceException("Error parsing returned RDF data, here is error provided by parser: " + parserExc.getMessage(), parserExc);
        } catch (InterruptedException intExc) {
            throw new CancelledQueryException();
        }
        return (new SourceResult(true, resultReader, null));
    }

    /**
     * Get a reader from given url
     */
    private InputStreamReader getInputStreamReader(String uri) throws MalformedURLException, IOException {
        URL url = new URL(uri);
        URLConnection connection = url.openConnection();
        return new InputStreamReader(connection.getInputStream());
    }

    /**
     * construct query string ready to use with webkb
     */
    private String constructQueryUrl(String uri, Query query) {
        WebkbQueryStringConstructor queryConstructor = new WebkbQueryStringConstructor();
        String resultUrl = uri + queryConstructor.getQueryString(query, "RDF");
        return resultUrl;
    }

    /**
     * execute webkb query
     */
    private Reader executeWebkbQuery(String fullUrl) throws IOException {
        InputStreamReader reader = getInputStreamReader(fullUrl);
        return reader;
    }

    /**
     * check for errors returned by webkb
     *
     * Check if EITHER of error patterns appear in the document,
     * rather then if both of them have to appear. This is more
     * flexible - if webkb is changes or some other error returns
     * some slightly different patterns - we should still be able to
     * catch it.
     */
    private String checkForWebkbErrors(String doc) {
    	System.out.println("\n\ndoc string = \n" + doc + "\n\n");
        String extractedErrorStr = doc;
        
        extractedErrorStr = extractedErrorStr.replaceFirst(webkbErrorStartPattern, "");
        extractedErrorStr = extractedErrorStr.replaceAll(webkbErrorEndPattern, "");
        
        extractedErrorStr = extractedErrorStr.replaceFirst("<\\?xml version=\".*\"\\?>","");
        
        // taking into account 'internal error' error when webkb returns half of
        // rdf document interrupted by strings with comments in c style: '//'
        // and corresponding explanations. This error at this moment (17.02.03) 
        // occurs when searching for 'dog'
        // this particular reges is looking for DOCTYPE declaration. 
        String regex = "<!DOCTYPE\\s+rdf:RDF\\s+\\[\\s+(<!ENTITY\\s+.*\\s+\".*\"\\s?>\\s?\n?)*\\]>";
        extractedErrorStr = extractedErrorStr.replaceFirst(regex,"");
        
        regex = "<rdf:RDF\\s+\n?(xmlns:\\w+=\"&\\w+;(\\w+#)?\"(\\s+)?\\n?)*>";
    	extractedErrorStr = extractedErrorStr.replaceFirst(regex,"");
        
                
        // doing this because webkb returns error in html and there seems to be no
        // carriage returns which causes error message to be too wide on the screen.
    	extractedErrorStr = extractedErrorStr.replaceAll("<html>","");
    	extractedErrorStr = extractedErrorStr.replaceAll("</html>","");
    	extractedErrorStr = extractedErrorStr.replaceAll("<body>","");
    	extractedErrorStr = extractedErrorStr.replaceAll("</body>","");
    	extractedErrorStr = extractedErrorStr.replaceAll("<pre>","");
    	extractedErrorStr = extractedErrorStr.replaceAll("</pre>","");
    	
    	regex = ">//";
    	String replacement = ">\n//";
    	extractedErrorStr = extractedErrorStr.replaceAll(regex, replacement);

    	regex = "\\.//";
    	replacement = ".\n//";
    	extractedErrorStr = extractedErrorStr.replaceAll(regex, replacement);
    	
    	String splitRes = "";
    	String[] tok = extractedErrorStr.split("<");
    	for (int i = 0; i < tok.length; i++) {
    		if (i == 0) {
    			splitRes = tok[i];
    		}
    		else {
    			splitRes = splitRes + "\n<" + tok[i];
    		}
    	}
		
		
		//String res = breakLongLine(extractedErrorStr, desiredStringLenght);
		
		
    	System.out.println("res = res");
    	
        extractedErrorStr = splitRes;
        return extractedErrorStr;
    }


    /**
     * Read RDF documents into list and build a string that
     * will represent the whole document's data.
     * If the list contains more then one document, the query
     * is ambugious. i.e "cat" can be (big_cat, Caterpillar, true_cat, etc).
     *
     * Result: docs - list of RDF documents
     *         readerString - string holding all data from this reader
     *
     * @todo remove count and debugging print statement
     */
    private void checkForMultiRdfDocuments(BufferedReader br)
            throws IOException, InterruptedException {
        String token;
        String buf = "";
        String line = br.readLine();
        StringTokenizer st;
        while (line != null) {
            System.out.print(".");
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("Query was cancelled");
            }
            readerString = readerString + line;

            st = new StringTokenizer(line, "<", true);

            while (st.hasMoreTokens()) {
                token = st.nextToken();
                buf = buf + token;
                if (token.equals("/rdf:RDF>")) {
                    docs.add(new String(buf));
                    buf = "";
                }
            }
            buf = buf + "\n";
            line = br.readLine();
        }
    }

    /**
     * Deal with case when result is ambiguous: extract list of choices
     * from the list of received documents and popup dialog box prompting
     * user to make a choice.
     */
    private Query processAmbiguousResultSet() throws ParserException {
        getRootTypesFromStreams();

        Frame frame = OntoRamaApp.getMainFrame();
        String selectedType = ((Node) typesList.get(0)).getName();
        if (frame != null) {
        	// if we are running test cases - no need to popup dialog box.
        	AmbiguousChoiceDialog dialog = new AmbiguousChoiceDialog(typesList, frame);
        	selectedType = dialog.getSelected();
        }

        String newTermName = selectedType;
        
        this.query.setQueryTypeName(newTermName);
        return this.query;
    }


    /**
     * Build list of top/root types extracted from the multiple documents,
     * and build a mapping between types and documents themselfs;
     *
     * The way we do this is: iterate through streams and extract list of types
     * for each stream, than add contents of each list to the global  list of
     * possible query candidates.
     *
     */
    private void getRootTypesFromStreams() throws ParserException {

        Iterator it = docs.iterator();
        while (it.hasNext()) {
            String nextDocStr = (String) it.next();
            StringReader curReader = new StringReader(nextDocStr);
            List curTypesList = getTypesListFromRdfStream(curReader, query.getQueryTypeName());
            for (int i = 0; i < curTypesList.size(); i++) {
                Node node = (Node) curTypesList.get(i);
                Iterator typesListIt = typesList.iterator();
                boolean foundNode = false;
                while (typesListIt.hasNext()) {
                	Node curNode = (Node) typesListIt.next();
                	if (curNode.getName().equals(node.getName())) {
                		foundNode = true;
                	}
                }
                if (!foundNode) {
                	typesList.add(node);
                }
            }
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
     */
    private List getTypesListFromRdfStream(Reader reader, String termName)
            throws ParserException, AccessControlException {

        List typeNamesList = new LinkedList();

        RdfWebkbParser parser = new RdfWebkbParser();
        ParserResult parserResult = parser.getResult(reader);
        List nodesList = parserResult.getNodesList();
        Iterator typesIt = nodesList.iterator();
        while (typesIt.hasNext()) {
            Node curNode = (Node) typesIt.next();
            List synonyms = getSynonyms(curNode, parserResult.getEdgesList());
            if (synonyms.contains(termName)) {
                typeNamesList.add(curNode);
            }
        }
        return typeNamesList;
    }

    private List getSynonyms (Node node, List edgesList) {
        List result = new LinkedList();
        Iterator it = edgesList.iterator();
        while (it.hasNext()) {
            Edge edge = (Edge) it.next();
            if (edge.getFromNode().equals(node)) {
                if (edge.getEdgeType().getName().equals(synPropName)) {
                    Node synonymNode = edge.getToNode();
                    if (! result.contains(synonymNode.getName())) {
                    	result.add(synonymNode.getName());
                    }
                }
            }
        }
        return result;
    }

    /**
     * used for tests
     */
    protected boolean resultIsAmbiguous() {
        if (docs.size() > 1) {
            return true;
        }
        return false;
    }

    /**
     * used for tests
     */
    protected int getNumOfChoices() {
        return typesList.size();
    }

    /**
     * used for tests
     */
    protected List getChoicesList() {
        return typesList;
    }
}
