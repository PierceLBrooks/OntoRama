package ontorama.webkbtools;

import junit.framework.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 *
 * Testing:
 * - package datamodel
 *
 * No tests for:
 * - all interfaces
 * - package inputsource
 * - package util
 *
 */

public class TestWebkbtoolsPackage extends TestCase {

    public TestWebkbtoolsPackage (String name) {
      super(name);
    }

     public static Test suite()
     {
         TestSuite suite = new TestSuite( "ontorama.webkbtools" );

         suite.addTest( ontorama.webkbtools.datamodel.TestDatamodelPackage.suite() );
         suite.addTest( ontorama.webkbtools.inputsource.TestSourcePackage.suite() );
         suite.addTest( ontorama.webkbtools.query.TestQueryPackage.suite() );

         return suite;
     }
}