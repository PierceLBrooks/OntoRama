package ontorama.test.webkbtools.query.parser.rdf;

import junit.framework.TestCase;

import java.io.*;
import java.util.*;

import ontorama.test.IteratorUtil;

import ontorama.OntoramaConfig;

import ontorama.webkbtools.query.parser.rdf.RdfDamlParser;
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

  private Collection resultCollection;


  private String propName_descr = "Description";
  private String propName_creator = "Creator";
  private String propName_synonym = "Synonym";

  private OntologyType testType_chair;
  private OntologyType testType_armchair;
  private OntologyType testType_furniture;
  private OntologyType testType_backrest;
  private OntologyType testType_leg;
  private OntologyType testType_myChair;
  private OntologyType testType_someSubstanceNode;
  private OntologyType testType_table;
  private OntologyType testType_someLocation;
  private OntologyType testType_url;
  private OntologyType testType_someObject;
  private OntologyType testType_allChairs;


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

    OntoramaConfig.loadAllConfig("examples/test/data/testCase-examplesConfig.xml",
               "ontorama.properties","examples/test/data/testCase-config.xml");

    Source source = (Source) (Class.forName(OntoramaConfig.sourcePackageName).newInstance());
    Reader r = source.getReader(OntoramaConfig.sourceUri);

    RdfDamlParser parser = new RdfDamlParser();
    resultCollection = parser.getOntologyTypeCollection(r);

    testType_chair = getOntologyTypeFromList("chair",resultCollection);
    //System.out.println("chair ont.type " + testType_chair);
    testType_armchair = getOntologyTypeFromList("armchair", resultCollection);
    testType_furniture = getOntologyTypeFromList("furniture", resultCollection);
    testType_backrest = getOntologyTypeFromList("backrest", resultCollection);
    testType_leg = getOntologyTypeFromList("leg", resultCollection);
    testType_myChair = getOntologyTypeFromList("myChair", resultCollection);
    testType_someSubstanceNode = getOntologyTypeFromList("someSubstanceNode", resultCollection);
    testType_table = getOntologyTypeFromList("table", resultCollection);
    testType_someLocation = getOntologyTypeFromList("someLocation", resultCollection);
    //testType_url = getOntologyTypeFromList("http://www.webkb.org/ontorama", resultCollection);
    testType_url = getOntologyTypeFromList("ontorama", resultCollection);
    testType_someObject = getOntologyTypeFromList("someObject", resultCollection);
    testType_allChairs = getOntologyTypeFromList("allChairs", resultCollection);


  }

  /**
   *
   */
  public void testResultSize () {
    // expecting 14 types in the result: 13 are ours and 1 is
    // default produced by rdf parser: rdf-schema#Class
    assertEquals(14,resultCollection.size());
  }

  /**
   *
   */
  public void testTypePropertyDescr_chair () throws NoSuchPropertyException {
    List propValue_descr = testType_chair.getTypeProperty(propName_descr);
    assertEquals(1,propValue_descr.size());
    assertEquals("description property", "test term 'chair'" , propValue_descr.get(0));
  }

  /**
   *
   */
  public void testTypePropertyCreator_chair () throws NoSuchPropertyException {
    List propValue_creator = testType_chair.getTypeProperty(propName_creator);
    assertEquals(1,propValue_creator.size());
    assertEquals("creator property", "nataliya@dstc.edu.au", propValue_creator.get(0));
  }

  /**
   *
   */
  public void testTypePropertySyn_chair () throws NoSuchPropertyException {
    List propValue_syn = testType_chair.getTypeProperty(propName_synonym);
    assertEquals(2,propValue_syn.size());
    assertEquals("synonym property", "a chair", propValue_syn.get(0));
    assertEquals("synonym property", "a sit", propValue_syn.get(1));
  }

  /**
   * test rel link 'subtype' for type chair
   * id = 1
   */
  public void testTypeRelationLinks_chair_subtype () throws NoSuchRelationLinkException {
    testingRelationLink("subtype", 1, testType_chair, "armchair", 1);
    testingRelationLink("supertype", 1, testType_furniture, "chair", 1);
  }

  /**
   * test rel link 'similar' for type chair
   * id = 2
   */
  public void testTypeRelationLinks_chair_similar () throws NoSuchRelationLinkException {
    testingRelationLink("similar", 2, testType_chair, "otherChairs", 1);
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
    testingRelationLink("part", 4, testType_chair, "backrest", 2);
    //testingRelationLink("part", 4, testType_chair, "leg", 2);
  }
  /**
   * test rel link 'substance' for type chair
   * id = 5
   */
  public void testTypeRelationLinks_chair_substance () throws NoSuchRelationLinkException {
    testingRelationLink("substance", 5, testType_chair, "someSubstanceNode", 1);
    testingRelationLink("substance", 5, testType_chair, "someSubstanceNode", 1);
  }

  /**
   * test rel link 'instance' for type chair
   * id = 6
   */
  public void testTypeRelationLinks_chair_instance () throws NoSuchRelationLinkException {
    testingRelationLink("instance", 6, testType_chair, "myChair", 0);
    // 2 here to account for a fact that rdf resource is an instance of rdf-schema#Class
    // so we have 1 type that instance of type 'chair' + 1 rdf-schema#Class
    testingRelationLink("instance", 6, testType_myChair, "chair", 1);
  }

  /**
   * test rel link 'exclusion/complement' for type chair
   * id = 7
   */
  public void testTypeRelationLinks_chair_complement () throws NoSuchRelationLinkException {
    testingRelationLink("exclusion/complement", 7, testType_chair, "table", 1);
    testingRelationLink("exclusion/complement", 7, testType_table, "chair", 0);
  }

  /**
   * test rel link 'location' for type chair
   * id = 8
   */
  public void testTypeRelationLinks_chair_location () throws NoSuchRelationLinkException {
    testingRelationLink("location", 8, testType_chair, "someLocation", 1);
    testingRelationLink("location", 8, testType_someLocation, "chair", 0);
  }

  /**
   * test rel link 'member' for type chair
   * id = 9
   */
  public void testTypeRelationLinks_chair_member () throws NoSuchRelationLinkException {
    testingRelationLink("member", 9, testType_chair, "allChairs", 1);
    testingRelationLink("member", 9, testType_allChairs, "chair", 0);
  }

  /**
   * test rel link 'object' for type chair
   * id = 10
   */
  public void testTypeRelationLinks_chair_object () throws NoSuchRelationLinkException {
    testingRelationLink("object", 10, testType_chair, "someObject", 1);
    testingRelationLink("object", 10, testType_someObject, "chair", 0);
  }

  /**
   * test rel link 'url' for type chair
   * id = 11
   */
  public void testTypeRelationLinks_chair_url () throws NoSuchRelationLinkException {
    //System.out.println("testType_chair = " + testType_chair);
    //System.out.println("testType_url = " + testType_url);
    testingRelationLink("url", 11, testType_chair, "ontorama", 1);
    testingRelationLink("url", 11, testType_url, "chair", 0);
  }

  /**
   *
   */
  private void testingRelationLink (String relLinkName, int relLinkId,
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



  private OntologyType getOntologyTypeFromList (String name, Collection collection) {
    List list = new LinkedList ( collection);
    return IteratorUtil.getOntologyTypeFromList (name, list);
  }



}