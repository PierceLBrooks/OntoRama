package ontorama.model;

import junit.framework.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestModelPackage extends TestCase {

    public TestModelPackage(String name) {
      super(name);
    }


    public static Test suite()
    {
       TestSuite suite = new TestSuite( "ontorama.model" );
       suite.addTest( new TestSuite( TestEdge.class ) );
       suite.addTest( new TestSuite( TestGraphNode.class ) );
       suite.addTest( new TestSuite (TestGraph.class) );
       return suite;
    }
}