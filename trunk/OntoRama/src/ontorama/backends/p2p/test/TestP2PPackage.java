package ontorama.backends.p2p.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ontorama.backends.p2p.model.test.TestP2PModelPackage;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestP2PPackage extends TestCase {

    public TestP2PPackage(String name) {
        super(name);
    }


    public static Test suite() {
        TestSuite suite = new TestSuite("ontorama.backends.p2p");
        suite.addTest(TestP2PModelPackage.suite());
        suite.addTest(new TestSuite(TestP2PBackend2.class));
        return suite;
    }
}
