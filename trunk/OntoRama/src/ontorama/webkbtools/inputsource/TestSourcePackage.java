package ontorama.ontotools.source;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import ontorama.ontotools.source.webkb.TestWebkbQueryStringConstructor;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestSourcePackage extends TestCase {

    public TestSourcePackage(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("ontorama.source");

        suite.addTest(new TestSuite(TestWebKB2Source.class));
        suite.addTest(new TestSuite(TestWebkbQueryStringConstructor.class));

        return suite;
    }
}