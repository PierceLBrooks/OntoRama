package ontorama.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ontorama.model.graph.test.TestEdge;
import ontorama.model.graph.test.TestNode;
import ontorama.model.graph.test.TestGraph;
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
        suite.addTest(new TestSuite(TestGraphPackage.class));
        suite.addTest(new TestSuite(TestTreePackage.class));
        return suite;
    }
}
