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

  public String queryParamTerm = null;
  public String queryParamLink = null;
  public String queryParamDestTerm = null;
  public String queryParamResursLink = null;
  public String queryParamDepth = null;
  public String queryParamCreator = null;
  public String queryParamNonCreator = null;
  public String queryParamHyperlinks = null;

  public String queryUrl;
  public String queryOutputFormat;
  public Parser parser;

  /**
   * Constractor: initialise some variables on bases of OntoramaConfig
   * and decide which Parser to use depending on input format
   * @throws    Exception
   */
  public TypeQueryBase () throws Exception {
    this.queryUrl = OntoramaConfig.sourceUri;
    this.queryOutputFormat = OntoramaConfig.queryOutputFormat;
    if (OntoramaConfig.DEBUG) {
        System.out.println("OntoramaConfig.sourceUri = " + OntoramaConfig.sourceUri);
        System.out.println("OntoramaConfig.queryOutputFormat = " + OntoramaConfig.queryOutputFormat);
        System.out.println("OntoramaConfig.parserPackageName = " + OntoramaConfig.parserPackageName);
    }
    parser = (Parser) (Class.forName(OntoramaConfig.parserPackageName).newInstance());
  }
}
