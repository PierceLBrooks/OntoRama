package ontorama.webkbtools.datamodel;

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

public class TestDatamodelPackage extends TestCase {

    public TestDatamodelPackage(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("ontorama.webkbtools.datamodel");
        suite.addTest(new TestSuite(TestOntologyTypeImplementation.class));
        return suite;
    }

}
