package ontorama.model.tree.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import ontorama.model.graph.test.TestEdge;
import ontorama.model.graph.test.TestNode;
import ontorama.model.graph.test.TestGraph;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Nov 28, 2002
 * Time: 12:05:18 PM
 * To change this template use Options | File Templates.
 */
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
