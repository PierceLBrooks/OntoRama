package ontorama.model.tree.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

public class TestTreePackage extends TestCase {

    public TestTreePackage(String name) {
        super(name);
    }


    public static Test suite() {
        TestSuite suite = new TestSuite("ontorama.model.tree");
        suite.addTest(new TestSuite(TestTreeNode.class));
        suite.addTest(new TestSuite(TestTree.class));
        return suite;
    }
}
