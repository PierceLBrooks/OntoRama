package ontorama.ontotools.parser.rdf;

import java.io.Reader;
import java.security.AccessControlException;
import java.util.Collection;
import java.util.Iterator;

import ontorama.ontotools.ParserException;
import ontorama.ontotools.parser.Parser;
import ontorama.ontotools.parser.ParserResult;

import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;
import com.hp.hpl.mesa.rdf.jena.common.ResourceImpl;
import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.Model;
import com.hp.hpl.mesa.rdf.jena.model.NsIterator;
import com.hp.hpl.mesa.rdf.jena.model.Property;
import com.hp.hpl.mesa.rdf.jena.model.RDFError;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.model.ResIterator;
import com.hp.hpl.mesa.rdf.jena.model.Resource;
import com.hp.hpl.mesa.rdf.jena.model.Statement;
import com.hp.hpl.mesa.rdf.jena.model.StmtIterator;


/**
 * @author nataliya
 */
public class DamlParser implements Parser {

    /**
     * @todo return proper ParserResult (not null)
     * @param reader
     * @return
     * @throws ParserException
     * @throws AccessControlException
     */
    public ParserResult getResult(Reader reader) throws ParserException, AccessControlException {
        return null;
    }

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

            Model model = new ModelMem();
            model.read(reader, "");


//    		StmtIterator stIterator = model.listStatements();
//    		while (stIterator.hasNext()) {
//    			Statement st = stIterator.next();
//    			System.out.println(st);
//    		}
//    		System.out.println("\n----------------------------------------------\n");

            NsIterator nsIterator = model.listNameSpaces();
            String rdfsNamespace = null;
            String rdfSyntaxTypeNamespace = null;
            String rdfsNamespaceSuffix = "rdf-schema#";
            String rdfSyntaxTypeNamespaceSuffix = "rdf-syntax-ns#";
            while (nsIterator.hasNext()) {
                String namespace = (String) nsIterator.next();
                //System.out.println("namespace: " + namespace);
                if (namespace.endsWith(rdfSyntaxTypeNamespaceSuffix)) {
                    rdfSyntaxTypeNamespace = namespace;
                }
                if (namespace.endsWith(rdfsNamespaceSuffix)) {
                    rdfsNamespace = namespace;
                }
            }
            System.out.println("\nrdfsNamespace = " + rdfsNamespace);
            System.out.println("rdfSyntaxTypeNamespace = " + rdfSyntaxTypeNamespace);

            Property typeProperty = new PropertyImpl(rdfSyntaxTypeNamespace, "type");
            System.out.println("property = " + typeProperty);

            Resource classResource = new ResourceImpl(rdfsNamespace, "Class");
            System.out.println("class resource = " + classResource);
            Resource propertyResource = new ResourceImpl(rdfSyntaxTypeNamespace, "Property");
            System.out.println("property resource = " + propertyResource);


            System.out.println("\nsubjects with property 'type' and object 'class'");
            ResIterator resIterator = model.listSubjectsWithProperty(typeProperty, classResource);
            int count1 = 0;
            while (resIterator.hasNext()) {
                Resource resource = (Resource) resIterator.next();
                System.out.println(resource);
                StmtIterator it = resource.listProperties();
                while (it.hasNext()) {
                    Statement st = it.next();
                    System.out.println("\t" + st);
                }
                count1++;
            }
            System.out.println("total = " + count1 + "\n");

            System.out.println("\n\n\nsubjects with property 'type' and object 'property'");
            ResIterator resIterator2 = model.listSubjectsWithProperty(typeProperty, propertyResource);
            int count2 = 0;
            while (resIterator2.hasNext()) {
                Resource curR = resIterator2.next();
                System.out.println(curR);
                StmtIterator it = curR.listProperties();
                while (it.hasNext()) {
                    Statement st = it.next();
                    System.out.println("\t" + st);
                }
                count2++;
            }
            System.out.println("total = " + count2 + "\n");


//	    	DAMLModel model = new DAMLModelImpl();
//	        model.read(reader, "");
//
//	        Iterator classesIterator = model.listDAMLClasses();
//	        System.out.println("\nclasses:");
//	        while (classesIterator.hasNext()) {
//	        	DAMLClass cl = (DAMLClass) classesIterator.next();
//	        	System.out.println(cl);
//
//	        	Iterator definedPropertiesIterator = cl.getDefinedProperties();
//	        	System.out.println("defined properties: ");
//	        	while (definedPropertiesIterator.hasNext()) {
//	        		Property cur = (Property) definedPropertiesIterator.next();
//	        		System.out.println("\t" + cur);
//	        	}
//	        }
//
//	        Iterator propIterator = model.listDAMLProperties();
//	        System.out.println("\nproperties:");
//	        while (propIterator.hasNext()) {
//	        	DAMLProperty pr = (DAMLProperty) propIterator.next();
//	        	System.out.println(pr);
//	        }
//
//	        Iterator instIterator = model.listDAMLInstances();
//	        System.out.println("\ninstances:");
//	        while (instIterator.hasNext()) {
//	        	DAMLInstance inst = (DAMLInstance) propIterator.next();
//	        	System.out.println(inst);
//	        }


        } catch (AccessControlException secExc) {
            throw secExc;
        } catch (RDFException e) {
            e.printStackTrace();
            throw new ParserException("Error in parsing RDF: " + e.getMessage());
        } catch (RDFError err) {
            throw new ParserException("Couldn't parse returned RDF data. Parser error: " + err.getMessage());
        }
        return null;
    }


}

