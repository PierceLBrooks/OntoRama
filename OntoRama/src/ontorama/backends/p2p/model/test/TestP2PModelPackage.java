package ontorama.backends.p2p.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestP2PModelPackage extends TestCase {

    public TestP2PModelPackage(String name) {
        super(name);
    }


    public static Test suite() {
        TestSuite suite = new TestSuite("ontorama.model");
        suite.addTest(new TestSuite(TestP2PEdge.class));
        suite.addTest(new TestSuite(TestP2PNode.class));
        return suite;
    }
}
