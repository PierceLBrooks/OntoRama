/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 22/08/2002
 * Time: 12:32:55
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.webkbtools.query.parser.csv;

import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.util.ParserException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.inputsource.*;
import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;
import ontorama.ontologyConfig.RelationLinkDetails;
import ontorama.OntoramaConfig;

import java.util.*;
import java.io.*;

public class CgKbCsvParser implements Parser {

    /**
     * Hashtable to hold all OntologyTypes that we are creating
     */
    private Hashtable ontHash;

    public Iterator getOntologyTypeIterator(Reader reader) throws ParserException {
        return getOntologyTypeCollection(reader).iterator();
    }

    public Collection getOntologyTypeCollection(Reader reader) throws ParserException {
        ontHash = new Hashtable();
        BufferedReader br = new BufferedReader(reader);
        try {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = new String[3];
                int count = 0;
                StringTokenizer st = new StringTokenizer(line, ",");
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    //System.out.println("tok = " + tok);
                    tokens[count] = tok;
                    count++;
                }
                processLineTokens(tokens);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ParserException(e.getMessage());
        }

        Iterator it = ontHash.values().iterator();
        while (it.hasNext()) {
            OntologyType ot = (OntologyType) it.next();
            System.out.println("ot = " + ot);
        }

        return ontHash.values();
    }

    private void processLineTokens(String[] tokens) throws ParserException {
        String obj1 = tokens[0].trim();
        String rel = tokens[1].trim();
        String obj2 = tokens[2].trim();
        System.out.println("* ." + obj1 + ".  ." + rel + ".  ." + obj2 + ".");

        try {
            RelationLinkDetails[] relationLinksConfigArray = OntoramaConfig.getRelationLinkDetails();
            OntologyType fromType = (OntologyTypeImplementation) ontHash.get(obj1);
            if (fromType == null) {
                fromType = new OntologyTypeImplementation(obj1);
                ontHash.put(obj1, fromType);
            }
            OntologyType toType = (OntologyTypeImplementation) ontHash.get(obj2);
            if (toType == null) {
                toType = new OntologyTypeImplementation(obj2);
                ontHash.put(obj2, toType);
            }

            boolean foundRelationLink = false;
            for (int i = 0; i < relationLinksConfigArray.length; i++) {
                if (relationLinksConfigArray[i] == null) {
                    continue;
                }
                RelationLinkDetails relationLinkDetails = relationLinksConfigArray[i];
                if (rel.equals(relationLinkDetails.getLinkName())) {
                    fromType.addRelationType(toType, i);
                    foundRelationLink = true;
                } else if (rel.equals(relationLinkDetails.getReversedLinkName())) {
                    toType.addRelationType(fromType, i);
                    foundRelationLink = true;
                }
            }
            if (foundRelationLink == false) {
                throw new ParserException ("Attribute name '" + rel + "' describes unknown Relation Link. Check config.xml for declared Relation Links");
            }
        }
        catch (NoSuchRelationLinkException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }


    public static void main(String[] args) {
        try {
            Source source = new JarSource();
            SourceResult sr = source.getSourceResult("examples/cgkb/test.csv", new Query("KVO"));
            Reader r = sr.getReader();

            Parser parser = new CgKbCsvParser();
            parser.getOntologyTypeCollection(r);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

}
