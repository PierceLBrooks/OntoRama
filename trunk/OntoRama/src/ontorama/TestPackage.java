package ontorama;

import junit.framework.Test;
import junit.framework.TestSuite;

import ontorama.model.test.TestModelPackage; 
import ontorama.ontotools.TestWebkbtoolsPackage;
import ontorama.backends.test.TestBackendsPackage;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestPackage {
	
    public static Test suite() {

//        System.out.println("loading config files for tests");
//        OntoramaConfig.loadAllConfig("examples/test/data/testCase-examplesConfig.xml",
//               "ontorama.properties","examples/test/data/testCase-config.xml");

        TestSuite suite = new TestSuite("ontorama");

        suite.addTest(TestModelPackage.suite());
        suite.addTest(TestWebkbtoolsPackage.suite());
        suite.addTest(TestBackendsPackage.suite());

        return suite;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}

