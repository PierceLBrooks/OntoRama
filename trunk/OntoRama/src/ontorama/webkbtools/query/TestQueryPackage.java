package ontorama.webkbtools.query;

import junit.framework.*;


import ontorama.OntoramaConfig;
import ontorama.webkbtools.query.parser.rdf.TestRdfDamlParser;
import ontorama.webkbtools.query.parser.rdf.TestRdfWebkbParser;
import ontorama.webkbtools.query.cgi.TestWebkbQueryStringConstructor;

/**
 * <p>Title: </p>
 * <p>Description: Test Query package: ontorama.query </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestQueryPackage extends TestCase {

  public TestQueryPackage(String name) {
    super (name);
  }

  public static Test suite() {
     TestSuite suite = new TestSuite( "ontorama.query" );

     suite.addTest( new TestSuite(TestRdfDamlParser.class));

     // webkb relevant
     suite.addTest( new TestSuite(TestRdfWebkbParser.class));
     suite.addTest( new TestSuite(TestWebkbQueryStringConstructor.class) );

//    OntoramaConfig.loadAllConfig("examples/test/data/examplesConfig.xml",
//                            "ontorama.properties","examples/test/data/config.xml");
     //suite.addTest( new TestSuite(TestTypeQueryImplementation.class) );

     suite.addTest( new TestSuite(TestQuery.class) );
     suite.addTest( new TestSuite(TestQueryEngine.class));
     suite.addTest( new TestSuite(TestQueryResult.class));

     return suite;
  }
}