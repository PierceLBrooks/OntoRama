package ontorama.webkbtools.writer.test;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import ontorama.webkbtools.writer.rdf.test.TestRdfWriter;
import ontorama.webkbtools.writer.rdf.test.TestRdfP2PWriter;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 4/10/2002
 * Time: 10:44:37
 * To change this template use Options | File Templates.
 */
public class TestWriterPackage  extends TestCase {
    public TestWriterPackage(String s) {
        super(s);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("ontorama.webkbtools.writer");
        suite.addTest(new TestSuite(TestRdfWriter.class));
        suite.addTest(new TestSuite(TestRdfP2PWriter.class));
        return suite;
    }
}