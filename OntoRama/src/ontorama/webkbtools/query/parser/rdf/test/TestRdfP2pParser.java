package ontorama.webkbtools.query.parser.rdf.test;

import junit.framework.TestCase;
import ontorama.OntoramaConfig;
import ontorama.webkbtools.inputsource.Source;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.query.parser.rdf.RdfDamlParser;
import ontorama.webkbtools.query.parser.rdf.RdfP2pParser;
import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.util.TestingUtils;

import java.io.Reader;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 27/09/2002
 * Time: 14:45:18
 * To change this template use Options | File Templates.
 */
public class TestRdfP2pParser extends TestCase {
   public TestRdfP2pParser(String name) {
        super(name);
    }
    protected void setUp() throws Exception {
        System.out.println("\nsetUp method");

        OntoramaConfig.loadAllConfig("examples/test/p2p/examplesConfig.xml",
                "ontorama.properties", "examples/test/p2p/config.xml");
        OntoramaConfig.setCurrentExample(TestingUtils.getExampleByName("p2p"));

        Source source = (Source) (Class.forName(OntoramaConfig.sourcePackageName).newInstance());
        Reader r = source.getSourceResult(OntoramaConfig.sourceUri, new Query("wn#Tail")).getReader();

        Parser parser = new RdfP2pParser();
        ParserResult parserResult = parser.getResult(r);
    }

    public void testSomething () {

    }
}
