package ontorama.webkbtools.query;

import java.io.Reader;
import java.io.FileReader;
import java.util.Iterator;
import java.io.IOException;

import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.util.ParserException;
import ontorama.webkbtools.inputsource.Source;
import ontorama.OntoramaConfig;

/**
 *  Formulate a query for Ontology Server and return
 *  an Iterator of OntologyTypes.
 */
public class TypeQueryImplementation extends TypeQueryBase {

    public TypeQueryImplementation () throws Exception {
        super();
   }

   /**
    * Get Iterator of All iterators of ontology types related to the type
    * corresponding to given termName
    *
    * @todo    think what should happen with ParserException
    * @todo   formulate and execute query to webkb and return Reader
    */
    public Iterator getTypeRelative(Query query) throws Exception {
        String termName = query.getQueryTypeName();
        Iterator iterator = null;

        try {
            Source source = (Source) (Class.forName(OntoramaConfig.sourcePackageName).newInstance());
            //System.out.println("\ngetTypeRelative: OntoramaConfig.sourceUri = " + OntoramaConfig.sourceUri + ", source = " + source);
            Reader r = source.getReader(queryUrl);

            iterator = parser.getOntologyTypeIterator(r);
            r.close();
        }
        catch (ParserException pe ) {
            //System.err.println("ParserException: " + pe.getMessage());
            //pe.printStackTrace();
            //System.exit(1);
            throw new Exception(pe.getMessage());
        }
        catch (IOException io) {
            //System.err.println("IOException: " + io);
            //System.exit(1);
            throw new Exception(io.getMessage());
        }
//        catch (Exception e) {
//            //System.err.println("Exception: " + e);
//            //System.exit(1);
//            throw new Exception(e.getMessage());
//        }
        return iterator;

    }

    /**
     * Get Iterator of of types related to given type via specified
     * relationLink
     * @todo    think what should happen with ParserException
    public Iterator getTypeRelative(String termName, int relationLink) {
        return null;
    }
     */

    public String toString() {
        return ("queryUrl = " + queryUrl +
            	", queryOutputFormat = " + queryOutputFormat);
    }

}



