package ontorama.webkbtools.query.parser.rdf;

/**
 * Title:        OntoRama
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 * @author
 * @version 1.0
 */


import java.io.FileReader;
import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Collection;

import com.megginson.sax.rdf.RDFReader;
import com.megginson.sax.rdf.RDFHandler;
import com.megginson.sax.rdf.RDFException;

import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;

import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;
import ontorama.OntoramaConfig;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.util.ParserException;


/**
 * RDF filter
 *
 * <p>This application will read RDF events, together with select XML events
 * (start/end element and character-related events). RDF statements are
 * translated into OntologyTypes and are held in Iterator.</p>
 *
 * <p>The intention for  this program is to get RDF Reader and produce and
 * Iterator of OntologyTypes.</p>
 *
 * <p>It will also display the amount
 * of memory used before and after each RDF document processing;
 * that number goes to standard error rather than standard output.</p>
 *
 * <p>This application will use the org.xml.sax.driver property, if
 * set, to select a SAX2 XML reader.  If that property is not set,
 * the application will check the value of the org.xml.sax.parser
 * property and wrap an XMLParserAdapter around it, using
 * AElfred by default if no org.xml.sax.parser property is
 * specified.</p>
 *
 * <p>You must have SAX2beta2 and a SAX driver installed to use this
 * test class.</p>
 *
 * <p>This application is largerly based on David Megginson's
 * TestRdfFilter application.
 *
 * @author David Megginson, david@megginson.com
 * @version 1.0alpha
 * @see com.megginson.sax.rdf.RDFFilter
 * @see com.megginson.sax.rdf.RDFHandler
 */

public class RdfParser extends DefaultHandler implements RDFHandler, Parser
{
    ////////////////////////////////////////////////////////////////////
    // Internal state.
    ////////////////////////////////////////////////////////////////////
    /**
     * Dont need this?????
     * @todo  check this. possibly throw exceptions or handle errors some other way
     */
    static int errorStatus = 0;

    /**
     * Hashtable to hold all OntologyTypes that we are creating
     */
    private Hashtable ontHash;

    /**
     * RDF constants
     */
    private static final String synonymDef = "label";
    private static final String descriptionDef = "comment";
    private static final String subClassOfDef = "subClassOf";
    private static final String creatorDef = "dublin_core#Creator";


    /**
     * Take a reader and call RDF filter on it.
     */
    public RdfParser () {
      ontHash = new Hashtable();

    }

    /**
     * Parse RDF. Set all corresponding handlers and then read given
     * reader. This will report all RDF and XML events as they are
     * encountered. So, to have something happen methods handling these
     * events have to be ovewritten. In this case: literalStatement and
     * resourceStatement.
     * @param   reader
     * @throws  ParserException
     * @todo    think what to do with IO and SAX exceptions - should throw
     *          exception so applet or other client can handle it?
     */
    private void parseRdf (Reader reader) throws ParserException {
        RdfParser handler = this;
        RDFReader rdf = new RDFReader();
        rdf.setRDFHandler(handler);
        rdf.setContentHandler(handler);
        rdf.setErrorHandler(handler);
        try {
          try  {
              rdf.readRDF(reader);
          }
          catch (IOException ioe) {
            throw new ParserException("IOException: " + ioe);
          }
        }
        catch (SAXException e) {
            throw new ParserException("SAXException: " + e);
        }
    }

    /**
     * Implementation of interface Parser method getOntologyTypeIterator
     * Returns Iterator of OntologyTypes
     * @param   reader
     * @throws  ParserException
     * @return  iterator
     * @todo    check if need to have reader as an argument. (Constructor also is
     *          taking reader as argument and method parseRDF.)
     */
    public Iterator getOntologyTypeIterator (Reader reader) throws ParserException {

        parseRdf(reader);

        Collection ontTypesCollection = (Collection) ontHash.values();
        Iterator ontTypesIterator = ontTypesCollection.iterator();

        System.out.println("\nontHash size = " + ontHash.size() + "\n");
        System.out.println("\nontTypesIterator = " + ontTypesIterator);

        return ontTypesIterator;
    }

    /**
     * This method is called each time parser is found an RDF statement/triple.
     * Take this statement, create OntologyType and put it into hashtable
     * that is temporarily holding all OntologyTypes found in RDF stream/reader.
     *
     * rdfSubject corresponds to name of type described by the triple.
     * rdfObject corresponds to name of type connected to rdfSubject.
     * rdfPredicate specifies relationship link between rdfSubject and rdfObject.
     *
     * rdfObject can be resource or literal (resource object is handled by
     * resourceStatement method and describes a resource. Literal object is handled
     * by literalStatement method and is just some string not necessarily linked to
     * any resource). For more info on resource and literal statements see
     * W3C RDF spec.
     *
     * @param   rdfSubject  RDF triple subject
     * @param   rdfPredicate    RDF triple predicate
     * @param   rdfObject   RDF triple rdfObject
     *
     * @todo    check if we should make use of subject type property
     * @todo    if triple's predicate is not recognized by us - throw an exception
     */
    private void populateOntologyType (String rdfSubject,
                                  String rdfPredicate, String rdfObject)
                                  {
      if (OntoramaConfig.DEBUG) {
        System.out.println("{ " + rdfSubject + ", " + rdfPredicate + ", " + rdfObject + " }");
      }

      OntologyType subjectType = getOntTypeByName(rdfSubject);
      OntologyType objectType;

      if (rdfPredicate.endsWith(descriptionDef)) {
        if (subjectType.getDescription() == null) {
            subjectType.setDescription(rdfObject);
        }
        return;
      }
      if (rdfPredicate.endsWith(subClassOfDef)) {
        // this rdfSubject is subclass of rdfObject
        // this means that we need to add rdfObject as a child for rdfSubject
        // also add rdfSubject as a parent for rdfObject
        objectType = getOntTypeByName(rdfObject);
        try {
            if ( !subjectType.isRelationType(objectType, OntoramaConfig.SUPERTYPE)) {
              subjectType.addRelationType(objectType, OntoramaConfig.SUPERTYPE);
            }
            if ( !objectType.isRelationType(subjectType, OntoramaConfig.SUBTYPE)) {
              objectType.addRelationType(subjectType, OntoramaConfig.SUBTYPE);
            }
        }
        catch (NoSuchRelationLinkException e) {
            System.err.println("NoSuchRelationLinkException: " + e.getMessage());
        }
      }
      if (rdfPredicate.endsWith(synonymDef)) {
        // got a synonym for this type
        objectType = getOntTypeByName(rdfObject);
        try {
            subjectType.addRelationType(objectType, OntoramaConfig.SYNONYMTYPE);
        }
        catch (NoSuchRelationLinkException e) {
            System.err.println("NoSuchRelationLinkException: " + e.getMessage());
        }
      }

      if (rdfPredicate.endsWith(creatorDef)) {
        // got a Creator for this type

        // @todo: shouldn't put this object into hashtable and return it in iterator!!!!
        objectType = getOntTypeByName(rdfObject);
        try {
            subjectType.addRelationType(objectType, OntoramaConfig.CREATOR);
        }
        catch (NoSuchRelationLinkException e) {
            System.err.println("NoSuchRelationLinkException: " + e.getMessage());
        }
      }

    }

    /**
     * Get OntologyType for given name. If this OntologyType is already created -
     * return it, otherwise - create a new one.
     * @param   ontTypeName
     * @return  OntologyType
     * @todo    need an exception if can't get OntologyType for some reason
     */
      public OntologyType getOntTypeByName (String ontTypeName) {
        OntologyType ontType = null;
        if (ontHash.containsKey(ontTypeName)) {
          ontType = (OntologyType) ontHash.get(ontTypeName);
        }
        else {
          ontType = new OntologyTypeImplementation(ontTypeName);
          ontHash.put(ontTypeName,ontType);
        }
        return ontType;
      }

    ////////////////////////////////////////////////////////////////////
    // Implementation of RDFHandler.
    //
    // The RDF events are reported through callbacks.  In serious
    // applications, these callbacks would be implemented in a
    // separate class, but since this is a simple demo, they're
    // included here.  Note that the application has to register
    // itself to receive these events.
    ////////////////////////////////////////////////////////////////////


    /**
     * Report a literal statement.
     *
     * <p>The RDF filter will invoke this method for every statement
     * in the RDF document that has a literal value.  An RDF
     * statement is, essentially, a single NAME=VALUE pair for
     * an object of some sort.</p>
     *
     * <p>Note that the subject may or may not be a proper URI;
     * the subjectType property informs the application how to
     * interpret it.</p>
     *
     * <p>Statement is passed to method (populateOntologyType) that is
     * responsible for interpreting it and creating OntologyTypes for this
     * statement.</p>
     *
     * @param subjectType The type of subject (from RDFHandler).
     * @param subject The subject of the statement (the identifier
     *        of the object being described).
     * @param predicate The predicate of the statement (the name
     *        of the object's property).
     * @param object The object of the statement (the value of the
     *        object's property), which is a literal string.
     * @param lang The language of the object (the property's value),
     *        as specified by an xml:lang attribute.
     * @see com.megginson.sax.rdf.RDFHandler#literalStatement
     * @todo    Information about the subject's type is ignored
     */
    public void literalStatement (int subjectType, String subject,
				 String predicate, String object,
				 String lang)
    {
        char ch[] = object.toCharArray();
        String charStr = showCharacters(ch, 0, ch.length);

        String statementDebugString = "\n--------------literal statement-------------------------------\n";
        statementDebugString = statementDebugString + "predicate = " + predicate + "\n";
        statementDebugString = statementDebugString + "subject = " + subject + "\n";
        statementDebugString = statementDebugString + "char[] = " + charStr + "\n";
        statementDebugString = statementDebugString + "lang = " + lang + "\n";
        statementDebugString = statementDebugString + "subjectType = " + subjectType + "\n";

        if (OntoramaConfig.DEBUG) {
            System.out.println(statementDebugString);
        }

        populateOntologyType(subject, predicate,charStr);
    }


    /**
     * Report a resource statement.
     *
     * <p>The RDF filter will invoke this method for every statement
     * in the RDF document that is a link to another resource.  An
     * RDF statement is, essentially, a single NAME=VALUE pair for
     * an object of some sort.</p>
     *
     * <p>Note that the subject may or may not be a proper URI;
     * the subjectType property informs the application how to
     * interpret it.</p>
     *
     * <p>Statement is passed to method (populateOntologyType) that is
     * responsible for interpreting it and creating OntologyTypes for this
     * statement.</p>
     *
     * @param subjectType The type of subject (from RDFHandler).
     * @param subject The subject of the statement (the identifier
     *        of the object being described).
     * @param predicate The predicate of the statement (the name
     *        of the object's property).
     * @param object The object of the statement (the value of the
     *        object's property), which is the identifier of another
     *        object.
     * @see com.megginson.sax.rdf.RDFHandler#resourceStatement
     * @todo    Information about the subject's type is ignored
     */
    public void resourceStatement (int subjectType, String subject,
				  String predicate, String object)
    {
        String statementDebugString = "\n--------------resource statement-------------------------------\n";
        statementDebugString = statementDebugString + "predicate = " + predicate + "\n";
        statementDebugString = statementDebugString + "subject = " + subject + "\n";
        statementDebugString = statementDebugString + "object = " + object + "\n";
        statementDebugString = statementDebugString + "subjectType = " + subjectType + "\n";

        if (OntoramaConfig.DEBUG) {
            System.out.println(statementDebugString);
        }

        populateOntologyType(subject, predicate,object);
    }


    /**
     * Report the start of a literal XML statement.
     *
     * <p>The RDF filter will invoke this method for every statement
     * in the RDF document that has a literal XML value (i.e.
     * rdf:parseType="Literal").  The statement is, essentially, a
     * single NAME=VALUE pair for an object of some sort.</p>
     *
     * <p>Note that the subject may or may not be a proper URI;
     * the subjectType property informs the application how to
     * interpret it.</p>
     *
     * <p>In this demonstration, the start message is simply printed
     * to standard output, but without any information about the
     * subject's type (normally it's not safe to ignore this).
     * The XML markup inside the property is reported through
     * regular SAX2 events, followed by an endXMLStatement
     * event.</p>
     *
     * @param subjectType The type of subject (from RDFHandler).
     * @param subject The subject of the statement (the identifier
     *        of the object being described).
     * @param predicate The predicate of the statement (the name
     *        of the object's property).
     * @param lang The language of the object (the property's value),
     *        as specified by an xml:lang attribute.
     * @see #endXMLStatement
     * @see com.megginson.sax.rdf.RDFHandler#startXMLStatement
     */
    public void startXMLStatement(int subjectType, String subject,
				  String predicate, String lang)
    {
        String statementDebugString = "{" + predicate +  ", " + subject +
                                 ", [start xml lang=" + lang + "]}";
        if (OntoramaConfig.DEBUG) {
            System.out.println(statementDebugString);
        }
    }


    /**
     * Report the end of a literal XML statement.
     *
     * <p>This method is invoked after the XML content of a statement
     * has been reported through regular SAX2 callbacks.</p>
     *
     * @see #startXMLStatement
     * @see com.megginson.sax.rdf.RDFHandler#endXMLStatement
     */
    public void endXMLStatement ()
    {
        if (OntoramaConfig.DEBUG) {
            System.out.println("{[end xml]}");
        }
    }


    ////////////////////////////////////////////////////////////////////
    // Partial implementation of org.xml.sax.ContentHandler.
    //
    // Since this application class is derived from DefaultHandler,
    // there are already default implementations for all of the
    // content handler methods.
    //
    // This section overrides some of the defaults to show the
    // XML markup outside and inside of the RDF blocks.
    //
    // Note that the application has to register itself to receive
    // these events.
    ////////////////////////////////////////////////////////////////////


    /**
     * Show the start of an XML element.
     *
     * <p>For now, show only the local name and no attributes.</p>
     *
     * @param uri The Namespace URI.
     * @param localName The element's local name.
     * @param rawName The element's raw name.
     * @param atts The element's attributes.
     * @see org.xml.sax.ContentHandler#startElement
     */
    public void startElement (String uri, String localName,
			      String rawName, Attributes atts)
    {
        if (OntoramaConfig.DEBUG) {
            System.out.println("XML start element: " + localName);
        }

    }


    /**
     * Show the end of an XML element.
     *
     * <p>For now, show only the local name.</p>
     *
     * @param uri The Namespace URI.
     * @param localName The element's local name.
     * @param rawName The element's raw name.
     * @param atts The element's attributes.
     * @see org.xml.sax.ContentHandler#endElement
     */
    public void endElement (String uri, String localName, String rawName)
    {
        if (OntoramaConfig.DEBUG) {
            System.out.println("XML end element: " + localName);
        }
    }


    /**
     * Show a chunk of character data.
     *
     * @param ch The array of characters.
     * @param start The starting position.
     * @param length The number of characters to use.
     * @see org.xml.sax.ContentHandler#characters
     */
    public void characters (char ch[], int start, int length)
    {
       if (OntoramaConfig.DEBUG) {
           System.out.print("XML characters: ");
           System.out.println(showCharacters(ch, 0, ch.length));
           //showCharacters(ch, start, length);
           System.out.print("\n");
        }
    }


    /**
     * Show a chunk of whitespace in element content.
     *
     * @param ch The array of characters.
     * @param start The starting position.
     * @param length The number of characters to use.
     * @see org.xml.sax.ContentHandler#ignorableWhitespace
     */
    public void ignorableWhitespace (char ch[], int start, int length)
    {
       if (OntoramaConfig.DEBUG) {
        System.out.print("XML whitespace: ");
        System.out.println(showCharacters(ch, 0, ch.length));
        //showCharacters(ch, start, length);
        System.out.print("\n");
       }
    }

    ////////////////////////////////////////////////////////////////////
    // Partial implementation of org.xml.sax.ErrorHandler
    //
    // Make sure that non-fatal errors get reported, because that's
    // how RDF problems arrive.
    //
    // Note that the application has to register itself to receive
    // these events.
    ////////////////////////////////////////////////////////////////////

    /**
     * Handle a warning.
     *
     * @param e The warning information in an exception object.
     * @see org.xml.sax.ErrorHandler#warning
     */
    public void warning (SAXParseException e)
    {
	if (e instanceof RDFException) {
	    System.err.println("*** RDF warning: " + e.getMessage());
	} else {
	    System.err.println("*** XML warning: " + e.getMessage());
	}
	showLocation(e);
	errorStatus = 1;
    }


    /**
     * Handle a non-fatal XML error.
     *
     * @param e The error information in an exception object.
     * @see org.xml.sax.ErrorHandler#error
     */
    public void error (SAXParseException e)
	throws SAXException
    {
	if (e instanceof RDFException) {
	    System.err.println("*** RDF ERROR: " + e.getMessage());
	} else {
	    System.err.println("*** XML ERROR: " + e.getMessage());
	}
	showLocation(e);
	errorStatus = 1;
    }


    /**
     * Handle a fatal XML error.
     *
     * @param e The error information in an exception object.
     * @see org.xml.sax.ErrorHandler#error
     */
    public void fatalError (SAXParseException e)
	throws SAXException
    {
	System.err.println("*** FATAL XML ERROR: " + e.getMessage());
	showLocation(e);
	errorStatus = 1;
    }




    ////////////////////////////////////////////////////////////////////
    // Internal utility methods.
    ////////////////////////////////////////////////////////////////////


    /**
     * Show the location of an error.
     *
     * @param e The SAXParseException with the location information.
     */
    private void showLocation (SAXParseException e)
    {
	String systemId = e.getSystemId();
	int line = e.getLineNumber();
	int col = e.getColumnNumber();
	if (systemId != null || line > -1 || col > -1) {
	    System.out.print("*** (near");
	    if (systemId != null) {
		System.out.print(" " + systemId);
	    }
	    if (line > -1) {
		System.out.print(" line " + line);
	    }
	    if (col > -1) {
		System.out.print(" column " + col);
	    }
	    System.out.print(")\n");
	}
    }


    /**
     * Display characters with some escaping.
     * <p>
     * 20.08.01 NR
     * Changed this to return a string of chars rather then
     * printing them out.
     * </p>
     *
     * @param ch The array of characters to escape.
     * @param start The starting position.
     * @param length The number of characters to escape.
     */
    private String showCharacters (char ch[], int start, int length)
    {
      String charStr = "";
      System.out.println();
      for (int i = start; i < start + length; i++) {
          switch (ch[i]) {
          case '\\':
              //System.out.print("\\\\");
              charStr = charStr + "\\\\";
              break;
          case '"':
              //System.out.print("\\\"");
              charStr = charStr + "\\\"";
              break;
          case '\n':
              //System.out.print("\\n");
              charStr = charStr + "\\n";
              break;
          case '\r':
              //System.out.print("\\r");
              charStr = charStr + "\\r";
              break;
          case '\t':
              //System.out.print("\\t");
              charStr = charStr + "\\t";
              break;
          default:
              //System.out.print(ch[i]);
              charStr = charStr + ch[i];
              break;
          }
      }
      //System.out.println();
      return charStr;
    }


    /**
     * Main entry point.
     *
     * <p>Process all XML documents supplied on the command line
     * (local file names, not URIs), and report the events.</p>
     *
     * @param args The filenames to process.
     */
    public static void main (String args[])
	                                throws Exception
    {

        if (args.length != 1) {
            System.err.println("Usage: java RdfParser <file-or-URL>");
            System.exit(2);
        }
        String sourceUri = args[0];

        Reader r = new FileReader(sourceUri);
        RdfParser testRdf = new RdfParser();

        System.out.println("finished parsing, trying to get iterator");
        Iterator ontIterator = testRdf.getOntologyTypeIterator(r);
        r.close();

        System.out.println("-------------Iterator returned---------------");
        while (ontIterator.hasNext()) {
            OntologyType ot = (OntologyTypeImplementation) ontIterator.next();
            System.out.println("---ontology type: \n" + ot);
        }
    }

}

// end of RdfParser.java

