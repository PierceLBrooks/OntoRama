package ontorama.ontotools.query.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import ontorama.ontotools.parser.cgkb.TestCgKbCsvParser;
import ontorama.ontotools.parser.rdf.test.TestRdfDamlParser;
import ontorama.ontotools.parser.rdf.test.TestRdfP2pParser;
import ontorama.ontotools.parser.rdf.test.TestRdfWebkbParser;

/**
 * <p>Title: </p>
 * <p>Description: Test Query package: ontorama.query </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestQueryPackage extends TestCase {

    public TestQueryPackage(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("ontorama.query");

        suite.addTest(new TestSuite(TestRdfDamlParser.class));

        suite.addTest(new TestSuite(TestQuery.class));
        suite.addTest(new TestSuite(TestQueryEngine.class));
        suite.addTest(new TestSuite(TestQueryResult.class));

        // webkb relevant
        suite.addTest(new TestSuite(TestRdfWebkbParser.class));

        // RJ's KB parser
        suite.addTest(new TestSuite(TestCgKbCsvParser.class));

        suite.addTest(new TestSuite(TestRdfP2pParser.class));

        return suite;
    }
}