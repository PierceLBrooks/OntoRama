package ontorama.webkbtools.query;

import java.lang.reflect.Constructor;

import java.io.InputStreamReader;
import java.io.Reader;

import ontorama.OntoramaConfig;
import ontorama.webkbtools.query.parser.Parser;

/**
 * This class implements TypeQuery Interface and sets
 * some variables needed to formulate OntologyServer Query.
 * This class is also responsible for figuring out which Parser
 * Implementation should be  used depending on output format settings
 * in ontorama.OntoramaConfig.
 *
 */
abstract public class TypeQueryBase implements TypeQuery {

  /**
   * url of ontology server cgi script
   */
  public String queryUrl;
  /**
   * output format of query, we use RDF, but there are others
   */
  public String queryOutputFormat;

  /**
   * what parser to use
   */
  public Parser parser;

  /**
   * Constractor: initialise some variables on bases of OntoramaConfig
   * and decide which Parser to use depending on input format
   * @throws    Exception
   */
  public TypeQueryBase () throws Exception {
    queryUrl = OntoramaConfig.sourceUri;
    queryOutputFormat = OntoramaConfig.queryOutputFormat;
    parser = (Parser) (Class.forName(OntoramaConfig.getParserPackageName()).newInstance());
    if (OntoramaConfig.DEBUG) {
        System.out.println("OntoramaConfig.sourceUri = " + OntoramaConfig.sourceUri);
        System.out.println("OntoramaConfig.queryOutputFormat = " + OntoramaConfig.queryOutputFormat);
        System.out.println("OntoramaConfig.parserPackageName = " + OntoramaConfig.getParserPackageName());
    }
  }
}
