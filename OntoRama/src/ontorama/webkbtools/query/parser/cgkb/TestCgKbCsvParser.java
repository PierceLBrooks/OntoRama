/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 22/08/2002
 * Time: 12:56:36
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.webkbtools.query.parser.cgkb;

import junit.framework.TestCase;
import ontorama.OntoramaConfig;
import ontorama.webkbtools.inputsource.*;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.query.parser.rdf.RdfDamlParser;
import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.util.TestingUtils;

import java.io.Reader;

public class TestCgKbCsvParser extends TestCase {

    private ParserResult parserResult;

    public TestCgKbCsvParser(String s) {
        super(s);
    }

    protected void setUp() throws Exception {

        OntoramaConfig.loadAllConfig("cgkb/examplesConfig.xml", "ontorama.properties", "cgkb/config.xml");

        Source source = new JarSource();
        SourceResult sr = source.getSourceResult("examples/cgkb/test1.csv", new Query("KVO"));
        Reader r = sr.getReader();

        Parser parser = new CgKbCsvParser();
        parserResult = parser.getResult(r);

    }

    public void testNodesSize () {
        assertEquals("number of nodes returned ", 15, parserResult.getNodesList().size());
    }

    public void testEdgesSize () {
        assertEquals("number of edges returned ", 14, parserResult.getEdgesList().size());
    }

}
