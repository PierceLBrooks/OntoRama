package ontorama.webkbtools.query.parser.rdf;

import junit.framework.TestCase;

import java.io.*;
import java.util.*;

import ontorama.util.IteratorUtil;

import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.examplesConfig.OntoramaExample;

import ontorama.util.TestingUtils;

import ontorama.webkbtools.query.parser.rdf.RdfDamlParser;
import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.inputsource.*;
import ontorama.webkbtools.datamodel.*;
import ontorama.webkbtools.util.*;

/**
 * <p>Title: </p>
 * <p>Description:
 * test if parser can produce valid collection of ontology types.
 * <br/>
 * Note: we only test method getOntologyTypeCollection because
 * this the main method doing all the work, method getOntologyTypeIterator
 * is only returning getOntologyTypeCollection.iterator(). So, we think
 * it should be sufficient to test getOntologyTypeCollection.
 * </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 *
 */

public class TestRdfDamlParser extends TestCase {

  protected Collection resultCollection;


  protected String propName_descr = "Description";
  protected String propName_creator = "Creator";
  protected String propName_synonym = "Synonym";

  protected OntologyType testType_chair;
  protected OntologyType testType_armchair;
  protected OntologyType testType_furniture;
  protected OntologyType testType_backrest;
  protected OntologyType testType_leg;
  protected OntologyType testType_myChair;
  protected OntologyType testType_someSubstanceNode;
  protected OntologyType testType_table;
  protected OntologyType testType_someLocation;
  protected OntologyType testType_url;
  protected OntologyType testType_someObject;
  protected OntologyType testType_allChairs;
  protected OntologyType testType_ACHRONYM;

  private Source source;
  private Parser parser;


  /**
   *
   */
  public TestRdfDamlParser(String name) {
    super(name);
  }

  /**
   *
   */
  protected void setUp () throws Exception {
    //System.out.println("\nsetUp method");

    OntoramaConfig.loadAllConfig("examples/test/data/testCase-examplesConfig.xml",
               "ontorama.properties","examples/test/data/testCase-config.xml");
    OntoramaConfig.setCurrentExample(TestingUtils.getExampleByName("testCase"));

    source = (Source) (Class.forName(OntoramaConfig.sourcePackageName).newInstance());
    //Reader r = source.getReader(OntoramaConfig.sourceUri, new Query("test#Chair"));
    Reader r = source.getSourceResult(OntoramaConfig.sourceUri, new Query("test#Chair")).getReader();

    parser = new RdfDamlParser();
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
    testType_url = getOntologyTypeFromList("OntoRama", resultCollection);
    testType_someObject = getOntologyTypeFromList("test#SomeObject", resultCollection);
    testType_allChairs = getOntologyTypeFromList("test#AllChairs", resultCollection);
  }


  /**
   *
   */
  protected void buildResultCollection (Parser parser, Reader r) throws ParserException {
    resultCollection = parser.getOntologyTypeCollection(r);
  }

  /**
   *
   */
  public void testInvalidRDF_extraColumnInElementName () throws java.lang.ClassNotFoundException,
                            java.lang.IllegalAccessException, java.lang.InstantiationException,
                            SourceException, CancelledQueryException {

    OntoramaExample testCaseToLoad = TestingUtils.getExampleByName("testCase: invalid RDF 1");
    OntoramaConfig.setCurrentExample(testCaseToLoad);

    source = (Source) (Class.forName(OntoramaConfig.sourcePackageName).newInstance());
    Reader r = source.getSourceResult(OntoramaConfig.sourceUri, new Query("test#Chair")).getReader();
    parser = new RdfDamlParser();
    try {
      parser.getOntologyTypeCollection(r);
      fail("failed to catch ParserException for invalid RDF file");
    }
    catch (ParserException e) {
      //System.out.println("caught parser exception as expected, message: \n" + e.getMessage());
    }
  }

  /**
   *
   */
  public void testInvalidRDF_DoubleSlashComments () throws java.lang.ClassNotFoundException,
                            java.lang.IllegalAccessException, java.lang.InstantiationException,
                            SourceException, CancelledQueryException {

    OntoramaExample testCaseToLoad = TestingUtils.getExampleByName("testCase: invalid RDF 2");
    OntoramaConfig.setCurrentExample(testCaseToLoad);

    source = (Source) (Class.forName(OntoramaConfig.sourcePackageName).newInstance());
    Reader r = source.getSourceResult(OntoramaConfig.sourceUri, new Query("test#Chair")).getReader();
    parser = new RdfDamlParser();
    try {
      parser.getOntologyTypeCollection(r);
      fail("failed to catch ParserException for invalid RDF file");
    }
    catch (ParserException e) {
      //System.out.println("caught parser exception as expected, message: \n" + e.getMessage());
    }

  }

  /**
   * @todo  HACK: this test is here to reset setup to default values.
   * I think the problem is when we use other examples, and then
   * try to use default one - we need to get a new instance of source or get a new
   * reader or get a new instance of parser. not sure how to fix this
   */
  public void testNothing() {
  }

  /**
   *
   */
  public void testResultSize () {
    // expecting 15 types in the result: 14 are ours and 1 is
    // default produced by rdf parser: rdf-schema#Class
    assertEquals(15,resultCollection.size());
  }
  
  /**
   * 
   */
  public void testFullName () {
  	String message = "full name of the ontology type";
  	assertEquals(message, "http://www.webkb.org/ontorama/test#Chair", testType_chair.getFullName());
  	assertEquals(message, "http://www.webkb.org/ontorama/test#Furniture", testType_furniture.getFullName());
  	assertEquals(message, "http://www.webkb.org/ontorama/test#Backrest", testType_backrest.getFullName());
  	assertEquals(message, "http://www.webkb.org/ontorama/test#Leg", testType_leg.getFullName());
  	assertEquals(message, "http://www.webkb.org/ontorama/test#MyChair", testType_myChair.getFullName());
  	assertEquals(message, "http://www.webkb.org/ontorama/test#AllChairs", testType_allChairs.getFullName());
  	assertEquals(message, "http://www.webkb.org/ontorama/test#SomeSubstanceNode", testType_someSubstanceNode.getFullName());
  	assertEquals(message, "http://www.webkb.org/ontorama/test#Table", testType_table.getFullName());
  	assertEquals(message, "http://www.webkb.org/OntoRama", testType_url.getFullName());
  	assertEquals(message, "http://www.webkb.org/ontorama/test#SomeLocation", testType_someLocation.getFullName());
  	assertEquals(message, "http://www.webkb.org/ontorama/test#SomeObject", testType_someObject.getFullName());
  	assertEquals(message, "http://www.webkb.org/ontorama/test#Armchair", testType_armchair.getFullName()); 	
  }

  /**
   *
   */
  public void testTypePropertyDescr_chair () throws NoSuchPropertyException {
    //System.out.println("\n\n\n testing property description");
    LinkedList expectedValueList = new LinkedList();
    expectedValueList.add("test term 'chair'");
    testingTypeProperty(propName_descr, expectedValueList, testType_chair);
  }

  /**
   *
   */
  public void testTypePropertyCreator_chair () throws NoSuchPropertyException {
    LinkedList expectedValueList = new LinkedList();
    expectedValueList.add("nataliya@dstc.edu.au");
    testingTypeProperty(propName_creator, expectedValueList, testType_chair);
  }

  /**
   *
   */
  public void testTypePropertySyn_chair () throws NoSuchPropertyException {
    LinkedList expectedValueList = new LinkedList();
    expectedValueList.add("chair");
    expectedValueList.add("sit");
    testingTypeProperty(propName_synonym, expectedValueList, testType_chair);
  }

  /**
   *
   */
  protected void testingTypeProperty (String propName, List expectedPropValueList,
                          OntologyType type) throws NoSuchPropertyException {
    //
    assertEquals("ontology type should never be null here, check setUp() method" +
                      " for type " + type, false, (type == null));
    String message = "checking concept type property '" + propName;
    message = message + "' for type '" + type.getName() + "'";

    List propValue = testType_chair.getTypeProperty(propName);
    assertEquals(message + ", number of prop. values", expectedPropValueList.size(),  propValue.size());

    for (int i = 0; i < expectedPropValueList.size(); i++ ) {
      assertEquals(message, expectedPropValueList.get(i), propValue.get(i));
    }
  }

  /**
   * test rel link 'subtype' for type chair
   * id = 1
   */
  public void testTypeRelationLinks_chair_subtype () throws NoSuchRelationLinkException {
    testingRelationLink("subtype", 1, testType_chair, "test#Armchair", 1);
    testingRelationLink("supertype", 1, testType_furniture, "test#Chair", 1);
  }

  /**
   * test rel link 'similar' for type chair
   * id = 2
   */
  public void testTypeRelationLinks_chair_similar () throws NoSuchRelationLinkException {
    testingRelationLink("similar", 2, testType_chair, "test#OtherChairs", 1);
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
    testingRelationLink("part", 4, testType_chair, "test#Backrest", 2);
    //testingRelationLink("part", 4, testType_chair, "leg", 2);
  }
  /**
   * test rel link 'substance' for type chair
   * id = 5
   */
  public void testTypeRelationLinks_chair_substance () throws NoSuchRelationLinkException {
    testingRelationLink("substance", 5, testType_chair, "test#SomeSubstanceNode", 1);
    testingRelationLink("substance", 5, testType_chair, "test#SomeSubstanceNode", 1);
  }

  /**
   * test rel link 'instance' for type chair
   * id = 6
   */
  public void testTypeRelationLinks_chair_instance () throws NoSuchRelationLinkException {
    testingRelationLink("instance", 6, testType_chair, "test#MyChair", 0);
    // 2 here to account for a fact that rdf resource is an instance of rdf-schema#Class
    // so we have 1 type that instance of type 'chair' + 1 rdf-schema#Class
    testingRelationLink("instance", 6, testType_myChair, "test#Chair", 1);
  }

  /**
   * test rel link 'exclusion/complement' for type chair
   * id = 7
   */
  public void testTypeRelationLinks_chair_complement () throws NoSuchRelationLinkException {
    testingRelationLink("exclusion/complement", 7, testType_chair, "test#Table", 1);
    testingRelationLink("exclusion/complement", 7, testType_table, "test#Chair", 0);
  }

  /**
   * test rel link 'location' for type chair
   * id = 8
   */
  public void testTypeRelationLinks_chair_location () throws NoSuchRelationLinkException {
    testingRelationLink("location", 8, testType_chair, "test#SomeLocation", 1);
    testingRelationLink("location", 8, testType_someLocation, "test#Chair", 0);
  }

  /**
   * test rel link 'member' for type chair
   * id = 9
   */
  public void testTypeRelationLinks_chair_member () throws NoSuchRelationLinkException {
    testingRelationLink("member", 9, testType_chair, "test#AllChairs", 1);
    testingRelationLink("member", 9, testType_allChairs, "test#Chair", 0);
  }

  /**
   * test rel link 'object' for type chair
   * id = 10
   */
  public void testTypeRelationLinks_chair_object () throws NoSuchRelationLinkException {
    testingRelationLink("object", 10, testType_chair, "test#SomeObject", 2);
    testingRelationLink("object", 10, testType_someObject, "test#Chair", 0);
  }

  /**
   * test rel link 'url' for type chair
   * id = 11
   */
  public void testTypeRelationLinks_chair_url () throws NoSuchRelationLinkException {
    //System.out.println("testType_chair = " + testType_chair);
    //System.out.println("testType_url = " + testType_url);
    testingRelationLink("url", 11, testType_chair, "OntoRama", 1);
    testingRelationLink("url", 11, testType_url, "test#Chair", 0);
  }



  /**
   *
   */
  protected void testingRelationLink (String relLinkName, int relLinkId,
                        OntologyType fromType, String nameOfDestinationType,
                        int expectedIteratorSize)
                        throws NoSuchRelationLinkException {

      assertEquals("ontology type should never be null here, check setUp() method" +
                      " for type " + fromType + " (checking rel link '" + relLinkName + "')",
                      false, (fromType == null));

      String message1 = "number of '" + relLinkName + "' links from '";
      message1 = message1 + fromType.getName() + "'";
      assertEquals(message1, expectedIteratorSize,
                IteratorUtil.getIteratorSize(fromType.getIterator(relLinkId)));

      Iterator it = fromType.getIterator(relLinkId);
      if (! it.hasNext()) { return; }
      OntologyType destinationType = (OntologyType) it.next();
      String message2 = "related type (rel.link: " + nameOfDestinationType + ")";
      assertEquals(message2, nameOfDestinationType, destinationType.getName());
  }

  /**
   *
   */
  protected OntologyType getOntologyTypeFromList (String name, Collection collection) {
    List list = new LinkedList ( collection);
    return IteratorUtil.getOntologyTypeFromList (name, list);
  }





}