package ontorama.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ontorama.model.graph.test.TestGraphPackage;
import ontorama.model.tree.test.TestTreePackage;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestModelPackage extends TestCase {

    public TestModelPackage(String name) {
        super(name);
    }


    public static Test suite() {
        TestSuite suite = new TestSuite("ontorama.model");
        suite.addTest(TestGraphPackage.suite());
        suite.addTest(TestTreePackage.suite());
        return suite;
    }
}
