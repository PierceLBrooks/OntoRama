package ontorama.webkbtools.query;

import junit.framework.*;


import ontorama.OntoramaConfig;

import ontorama.util.TestingUtils;

import ontorama.webkbtools.query.parser.rdf.TestRdfDamlParser;
import ontorama.webkbtools.query.parser.rdf.TestRdfWebkbParser;

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

//    OntoramaConfig.loadAllConfig("examples/test/data/examplesConfig.xml",
//                            "ontorama.properties","examples/test/data/config.xml");
     //suite.addTest( new TestSuite(TestTypeQueryImplementation.class) );

     suite.addTest( new TestSuite(TestQuery.class) );
     suite.addTest( new TestSuite(TestQueryEngine.class));
     suite.addTest( new TestSuite(TestQueryResult.class));

     // webkb relevant
    //OntoramaConfig.setCurrentExample(TestingUtils.getExampleByName("test webkb rdf parser"));
    suite.addTest( new TestSuite(TestRdfWebkbParser.class));

     return suite;
  }
}