package ontorama.webkbtools.query.parser.rdf;

import junit.framework.TestCase;

import java.io.*;
import java.util.*;

import ontorama.util.IteratorUtil;

import ontorama.OntoramaConfig;

import ontorama.webkbtools.inputsource.*;
import ontorama.webkbtools.datamodel.*;
import ontorama.webkbtools.util.*;
import ontorama.webkbtools.query.Query;
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
    Reader r = source.getSourceResult(OntoramaConfig.sourceUri, new Query("test#Chair")).getReader();
    //Reader r = source.getReader(OntoramaConfig.sourceUri, new Query("test#Chair"));

    RdfDamlParser parser = new RdfWebkbParser();
    buildResultCollection(parser, r);

    testType_chair = getOntologyTypeFromList("test#chair",resultCollection);
    //System.out.println("chair ont.type " + testType_chair);
    testType_armchair = getOntologyTypeFromList("test#armchair", resultCollection);
    testType_furniture = getOntologyTypeFromList("test#furniture", resultCollection);
    testType_backrest = getOntologyTypeFromList("test#backrest", resultCollection);
    testType_leg = getOntologyTypeFromList("test#leg", resultCollection);
    testType_myChair = getOntologyTypeFromList("test#my_chair", resultCollection);
    testType_someSubstanceNode = getOntologyTypeFromList("test#some_substance_node", resultCollection);
    testType_table = getOntologyTypeFromList("test#table", resultCollection);
    testType_someLocation = getOntologyTypeFromList("test#some_location", resultCollection);
    testType_url = getOntologyTypeFromList("http://www.webkb.org/_onto_rama", resultCollection);
    testType_someObject = getOntologyTypeFromList("test#some_object", resultCollection);
    testType_allChairs = getOntologyTypeFromList("test#all_chairs", resultCollection);
    testType_ACHRONYM = getOntologyTypeFromList("test#ACHRONYM", resultCollection);

  }

  /**
   *
   */
  public void testTypeNamingForAchronym () {
    assertEquals("testing if test Ontology Type for 'test#ACHRONYM' exists, if not "
                  + ", there is an error when reformatting strings from RDF capitalised"
                  + " format", false, (testType_ACHRONYM == null));
  }

  /**
   * test rel link 'subtype' for type chair
   * id = 1
   */
  public void testTypeRelationLinks_chair_subtype () throws NoSuchRelationLinkException {
    testingRelationLink("subtype", 1, testType_chair, "test#armchair", 1);
    testingRelationLink("supertype", 1, testType_furniture, "test#chair", 1);
  }

  /**
   * test rel link 'similar' for type chair
   * id = 2
   */
  public void testTypeRelationLinks_chair_similar () throws NoSuchRelationLinkException {
    testingRelationLink("similar", 2, testType_chair, "test#other_chairs", 1);
  }

  /**
   * test rel link 'reverse' for type chair
   * id = 3
   */
  public void testTypeRelationLinks_chair_reverse () throws NoSuchRelationLinkException {
    //testingRelationLink("reverse", 5, testType_chair, "", 0);
  }

  /**
   * test rel link 'part' for type chair
   * id = 4
   */
  public void testTypeRelationLinks_chair_part () throws NoSuchRelationLinkException {
    // these links are in reversed order, so we are testing
    // types chair, backrest and leg.
    testingRelationLink("part", 4, testType_chair, "test#backrest", 2);
    //testingRelationLink("part", 4, testType_chair, "leg", 2);
  }
  /**
   * test rel link 'substance' for type chair
   * id = 5
   */
  public void testTypeRelationLinks_chair_substance () throws NoSuchRelationLinkException {
    testingRelationLink("substance", 5, testType_chair, "test#some_substance_node", 1);
    testingRelationLink("substance", 5, testType_chair, "test#some_substance_node", 1);
  }

  /**
   * test rel link 'instance' for type chair
   * id = 6
   */
  public void testTypeRelationLinks_chair_instance () throws NoSuchRelationLinkException {
    testingRelationLink("instance", 6, testType_chair, "test#my_chair", 0);
    // 2 here to account for a fact that rdf resource is an instance of rdf-schema#Class
    // so we have 1 type that instance of type 'chair' + 1 rdf-schema#Class
    testingRelationLink("instance", 6, testType_myChair, "test#chair", 1);
  }

  /**
   * test rel link 'exclusion/complement' for type chair
   * id = 7
   */
  public void testTypeRelationLinks_chair_complement () throws NoSuchRelationLinkException {
    testingRelationLink("exclusion/complement", 7, testType_chair, "test#table", 1);
    testingRelationLink("exclusion/complement", 7, testType_table, "test#chair", 0);
  }

  /**
   * test rel link 'location' for type chair
   * id = 8
   */
  public void testTypeRelationLinks_chair_location () throws NoSuchRelationLinkException {
    testingRelationLink("location", 8, testType_chair, "test#some_location", 1);
    testingRelationLink("location", 8, testType_someLocation, "test#chair", 0);
  }

  /**
   * test rel link 'member' for type chair
   * id = 9
   */
  public void testTypeRelationLinks_chair_member () throws NoSuchRelationLinkException {
    testingRelationLink("member", 9, testType_chair, "test#all_chairs", 1);
    testingRelationLink("member", 9, testType_allChairs, "test#chair", 0);
  }

  /**
   * test rel link 'object' for type chair
   * id = 10
   */
  public void testTypeRelationLinks_chair_object () throws NoSuchRelationLinkException {
    testingRelationLink("object", 10, testType_chair, "test#some_object", 2);
    testingRelationLink("object", 10, testType_someObject, "test#chair", 0);
  }


  /**
   * test rel link 'url' for type chair
   * id = 11
   */
  public void testTypeRelationLinks_chair_url () throws NoSuchRelationLinkException {
    //System.out.println("testType_chair = " + testType_chair);
    //System.out.println("testType_url = " + testType_url);
    testingRelationLink("url", 11, testType_chair, "http://www.webkb.org/_onto_rama", 1);
    testingRelationLink("url", 11, testType_url, "test#chair", 0);
  }
}