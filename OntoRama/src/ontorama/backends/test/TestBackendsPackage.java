package ontorama.backends.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import ontorama.backends.p2p.test.TestP2PPackage;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 16, 2002
 * Time: 11:46:52 AM
 * To change this template use Options | File Templates.
 */
public class TestBackendsPackage {
    public static Test suite() {
        TestSuite suite = new TestSuite("ontorama.backends");
        suite.addTest(TestP2PPackage.suite());
        return suite;
    }

}
