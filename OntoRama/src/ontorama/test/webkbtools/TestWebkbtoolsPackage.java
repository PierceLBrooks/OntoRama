package ontorama.test.webkbtools;

import junit.framework.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestWebkbtoolsPackage extends TestCase {

    public TestWebkbtoolsPackage (String name) {
      super(name);
    }

     public static Test suite()
     {
         TestSuite suite = new TestSuite( "ontorama.webkbtools" );

         suite.addTest( ontorama.test.webkbtools.datamodel.TestDatamodelPackage.suite() );

         return suite;
     }
}