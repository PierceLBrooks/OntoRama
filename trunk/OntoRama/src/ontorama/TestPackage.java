package ontorama;

import junit.framework.Test;
import junit.framework.TestSuite;

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

        suite.addTest(ontorama.model.graph.test.TestModelPackage.suite());
        suite.addTest(ontorama.webkbtools.TestWebkbtoolsPackage.suite());

        return suite;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}

