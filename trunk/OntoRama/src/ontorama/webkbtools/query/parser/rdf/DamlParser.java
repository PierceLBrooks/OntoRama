package ontorama.webkbtools.query.parser.rdf;

import java.io.Reader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Collection;

import java.security.AccessControlException;

import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.vocabulary.*;
import com.hp.hpl.jena.daml.*;
import com.hp.hpl.jena.daml.common.DAMLModelImpl;
import com.hp.hpl.jena.daml.common.*;

import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.util.ParserException;
import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;
import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.*;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.util.NoSuchPropertyException;



/**
 * @author nataliya
 */
public class DamlParser implements Parser {


    /**
     *
     */
    public Iterator getOntologyTypeIterator(Reader reader) throws ParserException, AccessControlException {
      return getOntologyTypeCollection(reader).iterator();
    }
    /**
     *
     */
    public Collection getOntologyTypeCollection(Reader reader) throws ParserException, AccessControlException {
    	try {
	    	DAMLModel model = new DAMLModelImpl();
	        model.read(reader, "");
	        
	        Iterator classesIterator = model.listDAMLClasses();
	        System.out.println("\nclasses:");
	        while (classesIterator.hasNext()) {
	        	DAMLClass cl = (DAMLClass) classesIterator.next();
	        	System.out.println(cl);
	        	
	        	Iterator definedPropertiesIterator = cl.getDefinedProperties();
	        	System.out.println("defined properties: ");
	        	while (definedPropertiesIterator.hasNext()) {
	        		Property cur = (Property) definedPropertiesIterator.next();
	        		System.out.println("\t" + cur);
	        	}
	        }
	        
	        Iterator propIterator = model.listDAMLProperties();
	        System.out.println("\nproperties:");
	        while (propIterator.hasNext()) {
	        	DAMLProperty pr = (DAMLProperty) propIterator.next();
	        	System.out.println(pr);
	        }

	        Iterator instIterator = model.listDAMLInstances();
	        System.out.println("\ninstances:");
	        while (instIterator.hasNext()) {
	        	DAMLInstance inst = (DAMLInstance) propIterator.next();
	        	System.out.println(inst);
	        }

	        
//            ResIterator resIt = model.listSubjects();
//
//            while (resIt.hasNext()) {
//                Resource r = resIt.next();
//                StmtIterator stIt = r.listProperties();
//                while (stIt.hasNext()) {
//                    Statement s = stIt.next();
//                    System.out.println(s);
//                    //processStatement(s);
//                }
//            }
    	}
        catch (AccessControlException secExc) {
                throw secExc;
        }
        catch (RDFException e) {
            e.printStackTrace();
            throw new ParserException("Error in parsing RDF: " + e.getMessage());
        }
        catch (RDFError err) {
          throw new ParserException("Couldn't parse returned RDF data. Parser error: " + err.getMessage());
        }
      	return null;
    }


}

