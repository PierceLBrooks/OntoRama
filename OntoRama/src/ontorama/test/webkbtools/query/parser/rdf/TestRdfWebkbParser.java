package ontorama.test.webkbtools.query.parser.rdf;

import junit.framework.TestCase;

import java.io.*;
import java.util.*;

import ontorama.test.IteratorUtil;

import ontorama.OntoramaConfig;

import ontorama.webkbtools.inputsource.*;
import ontorama.webkbtools.datamodel.*;
import ontorama.webkbtools.util.*;
import ontorama.webkbtools.query.parser.rdf.*;
import ontorama.webkbtools.query.parser.Parser;

/**
 * <p>Title: </p>
 * <p>Description:
 * Overwrite TestRdfDamlParser because the only diffefence is treatment of
 * relation link 'url', all other results should be identical to results
 * in TestRdfDamlParser.
 * </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestRdfWebkbParser extends TestRdfDamlParser {

  /**
   *
   */
  public TestRdfWebkbParser(String name) {
    super(name);
  }

  /**
   *
   */
  protected void setUp() throws ClassNotFoundException, IllegalAccessException,
                          InstantiationException, Exception, ParserException {
    OntoramaConfig.loadAllConfig("examples/test/data/testCaseWebkb-examplesConfig.xml",
               "ontorama.properties","examples/test/data/testCase-config.xml");

    Source source = (Source) (Class.forName(OntoramaConfig.sourcePackageName).newInstance());
    Reader r = source.getReader(OntoramaConfig.sourceUri);

    RdfDamlParser parser = new RdfWebkbParser();
    buildResultCollection(parser, r);

    testType_chair = getOntologyTypeFromList("test#Chair",resultCollection);
    //System.out.println("chair ont.type " + testType_chair);
    testType_armchair = getOntologyTypeFromList("test#Armchair", resultCollection);
    testType_furniture = getOntologyTypeFromList("test#Furniture", resultCollection);
    testType_backrest = getOntologyTypeFromList("test#Backrest", resultCollection);
    testType_leg = getOntologyTypeFromList("test#Leg", resultCollection);
    testType_myChair = getOntologyTypeFromList("test#MyChair", resultCollection);
    testType_someSubstanceNode = getOntologyTypeFromList("test#SomeSubstanceNode", resultCollection);
    testType_table = getOntologyTypeFromList("test#Table", resultCollection);
    testType_someLocation = getOntologyTypeFromList("test#SomeLocation", resultCollection);
    testType_url = getOntologyTypeFromList("http://www.webkb.org/ontorama", resultCollection);
    testType_someObject = getOntologyTypeFromList("test#SomeObject", resultCollection);
    testType_allChairs = getOntologyTypeFromList("test#AllChairs", resultCollection);

  }

  /**
   * test rel link 'url' for type chair
   * id = 11
   */
  public void testTypeRelationLinks_chair_url () throws NoSuchRelationLinkException {
    //System.out.println("testType_chair = " + testType_chair);
    //System.out.println("testType_url = " + testType_url);
    testingRelationLink("url", 11, testType_chair, "http://www.webkb.org/ontorama", 1);
    testingRelationLink("url", 11, testType_url, "test#Chair", 0);
  }
}