package ontorama.test;

import junit.framework.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author unascribed
 * @version 1.0
 */

public class TestPackage {

     public static Test suite()
     {
         TestSuite suite = new TestSuite( "ontorama" );

         suite.addTest( new TestSuite( TestEdge.class ) );
//         suite.addTest( new TestSuite( ItemListTest.class ) );
//         suite.addTest( new TestSuite( DNodeListTest.class ) );
//         suite.addTest( new TestSuite( SNodeListTest.class ) );

         return suite;
     }

     public static void main( String[] args )
     {
         junit.textui.TestRunner.run( suite() );
     }
}

