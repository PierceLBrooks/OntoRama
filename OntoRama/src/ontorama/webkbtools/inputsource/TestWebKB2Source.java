package ontorama.webkbtools.inputsource;

import junit.framework.TestCase;

import ontorama.OntoramaConfig;
import ontorama.webkbtools.query.Query;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestWebKB2Source extends TestCase {

  public TestWebKB2Source(String name) {
    super (name);
  }

  /**
   *
   */
  protected void setUp() throws Exception {
    OntoramaConfig.loadAllConfig("examples/test/data/testWebkb-AmbuguousCase-cat-examplesConfig.xml",
               "ontorama.properties","examples/test/data/testCase-config.xml");

    String sourceUri = OntoramaConfig.sourceUri;
    //String parserPackage = OntoramaConfig.getParserPackageName();

    WebKB2Source webkbSource = new WebKB2Source();
    webkbSource.getReader(sourceUri, new Query("cat"));
  }

  /**
   *
   */
  public void testSomething () {
  }
}