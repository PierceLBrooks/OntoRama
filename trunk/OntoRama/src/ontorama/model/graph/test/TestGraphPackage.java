package ontorama.model.graph.test;

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

public class TestGraphPackage extends TestCase {

	public static final String edgeName_subtype = "subtype";
	public static final String edgeName_similar = "similar";
	public static final String edgeName_reverse = "reverse";
	

    public TestGraphPackage(String name) {
        super(name);
    }


    public static Test suite() {
        TestSuite suite = new TestSuite("ontorama.model.graph");
        suite.addTest(new TestSuite(TestEdge.class));
        suite.addTest(new TestSuite(TestNode.class));
        suite.addTest(new TestSuite(TestGraph.class));
        return suite;
    }
}
