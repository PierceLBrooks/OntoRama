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

public class TestRdfDamlParserComprehensive extends TestCase {

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
  public TestRdfDamlParserComprehensive(String name) {
    super(name);
  }

  /**
   *
   */
  protected void setUp () throws Exception {
    //System.out.println("\nsetUp method");

    OntoramaConfig.loadAllConfig("examples/test/data/testCase-examplesConfig.xml",
               "ontorama.properties","examples/test/data/testCase-config.xml");
    OntoramaConfig.setCurrentExample(TestingUtils.getExampleByName("comprehensive test"));

    source = (Source) (Class.forName(OntoramaConfig.sourcePackageName).newInstance());
    Reader r = source.getSourceResult(OntoramaConfig.sourceUri, new Query("root")).getReader();

    parser = new DamlParser();
    buildResultCollection(parser, r);
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
  public void testSomething () {
  }
  




}