package ontorama.test;

import junit.framework.*;

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

         TestSuite suite = new TestSuite( "ontorama" );

         suite.addTest( ontorama.test.model.TestModelPackage.suite() );
         suite.addTest( ontorama.test.webkbtools.TestWebkbtoolsPackage.suite());

         return suite;
     }

     public static void main( String[] args )
     {
         junit.textui.TestRunner.run( suite() );
     }
}

