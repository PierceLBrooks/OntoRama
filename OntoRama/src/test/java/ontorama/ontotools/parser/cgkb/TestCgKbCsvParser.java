package ontorama.ontotools.parser.cgkb;

import java.io.Reader;

import junit.framework.TestCase;
import ontorama.OntoramaConfig;
import ontorama.ontotools.parser.Parser;
import ontorama.ontotools.parser.ParserResult;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.source.JarSource;
import ontorama.ontotools.source.Source;
import ontorama.ontotools.source.SourceResult;

public class TestCgKbCsvParser extends TestCase {

    private ParserResult parserResult;

    public TestCgKbCsvParser(String s) {
        super(s);
    }

    @Override
    protected void setUp() throws Exception {

        OntoramaConfig.instantiateBackend(OntoramaConfig.defaultBackend, null);
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
