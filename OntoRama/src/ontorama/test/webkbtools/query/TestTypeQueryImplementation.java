package ontorama.test.webkbtools.query;

import junit.framework.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ontorama.OntoramaConfig;

import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;

import ontorama.webkbtools.query.TypeQuery;
import ontorama.webkbtools.query.TypeQueryBase;
import ontorama.webkbtools.query.TypeQueryImplementation;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 *
 * Test if returned iterator of ontology types contains the same
 * types as expected. Using example of wn#wood_mouse to test this.
 */

public class TestTypeQueryImplementation extends TestCase {

  private TypeQuery typeQuery;

  private List expectedTypesList = new LinkedList();

  /**
   *
   */
  public TestTypeQueryImplementation (String name)  throws Exception{
    super(name);
  }

  /**
   *
   */
  protected void setUp() throws Exception {
    typeQuery = new TypeQueryImplementation();

    // expected ontology types:


  }

  /**
   *
   */
  public void testGetTypeRelative () throws Exception {
    Iterator queryIterator = typeQuery.getTypeRelative(OntoramaConfig.ontologyRoot);

    while (queryIterator.hasNext()) {
      Object cur = (Object) queryIterator.next();
      System.out.println("---" + cur);
    }

  }
}