/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 22/08/2002
 * Time: 12:56:36
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.webkbtools.query.parser.csv;

import junit.framework.TestCase;
import ontorama.OntoramaConfig;
import ontorama.webkbtools.inputsource.*;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.query.parser.rdf.RdfDamlParser;
import ontorama.webkbtools.query.parser.Parser;
import ontorama.util.TestingUtils;

import java.io.Reader;

public class TestCgKbCsvParser extends TestCase {
    public TestCgKbCsvParser(String s) {
        super(s);
    }

    protected void setUp() throws Exception {

//        OntoramaConfig.loadAllConfig("examples/test/data/testCase-examplesConfig.xml",
//                "ontorama.properties", "examples/test/data/testCase-config.xml");
//        System.out.println('2');
//        OntoramaConfig.setCurrentExample(TestingUtils.getExampleByName("testCSV"));
//
//        Source source = (Source) (Class.forName(OntoramaConfig.sourcePackageName).newInstance());
//        //Reader r = source.getReader(OntoramaConfig.sourceUri, new Query("test#Chair"));
        Source source = new JarSource();
        SourceResult sr = source.getSourceResult("examples/cgkb/test.cgkb", new Query("KVO"));
        Reader r = sr.getReader();

        Parser parser = new CgKbCsvParser();
        parser.getOntologyTypeCollection(r);

    }

    public void testParser () {

    }

}
