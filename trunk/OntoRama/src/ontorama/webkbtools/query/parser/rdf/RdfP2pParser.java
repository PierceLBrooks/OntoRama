package ontorama.webkbtools.query.parser.rdf;

import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.webkbtools.util.ParserException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.Reader;
import java.security.AccessControlException;

import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 27/09/2002
 * Time: 14:44:55
 * To change this template use Options | File Templates.
 */
public class RdfP2pParser implements Parser {
    /**
     * Hashtable to hold all Graph Nodes that we have created
     * keys - strings - graph node names
     * values - graph nodes
     */
    protected Hashtable _nodesHash;
    protected List _edgesList;
    /**
     * Constructor
     */
    public RdfP2pParser() {
        _nodesHash = new Hashtable();
        _edgesList = new LinkedList();
    }

    public ParserResult getResult(Reader reader) throws ParserException, AccessControlException {
        try {
            Model model = new ModelMem();
            model.read(reader, "");
            StmtIterator it = model.listStatements();
            while (it.hasNext()) {
                Statement st = it.next();
                System.out.println(st);
            }
        } catch (AccessControlException secExc) {
            throw secExc;
        } catch (RDFException e) {
            e.printStackTrace();
            throw new ParserException("Error in parsing RDF: " + e.getMessage());
        } catch (RDFError err) {
            err.printStackTrace();
            throw new ParserException("Couldn't parse returned RDF data. Parser error: " + err.getMessage());
        }
        ParserResult result = new ParserResult(new LinkedList(_nodesHash.values()), _edgesList);
        return result;
    }

}
