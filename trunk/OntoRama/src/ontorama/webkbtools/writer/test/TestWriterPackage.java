package ontorama.webkbtools.writer.test;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import ontorama.webkbtools.writer.rdf.test.TestRdfWriter;

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
        return suite;
    }
}