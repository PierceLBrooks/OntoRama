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
   * query term (a word entered into search field)
   */
  public String queryParamTerm = null;
  /**
   * relation link
   */
  public String queryParamLink = null;
  /**
   * used to find connection between two terms, used in
   * conjunction with queryParamLink
   */
  public String queryParamDestTerm = null;
  /**
   * Used to set constrains on the search for a given term.
   * If this parameter is not set - WebKB will return search term and all
   * terms connected to it via direct relations.
   * Otherwise recursLink can be set to any of valid relation links .
   * This will return the above set of data plus all terms connected to
   * search term via recursLink.
   * However, only a limited section of a large search will be displayed
   */
  public String queryParamRecursLink = null;
  /**
   * depth of recursion
   */
  public String queryParamDepth = null;
  /**
   * restriction on search by creator
   */
  public String queryParamCreator = null;
  /**
   * restriction on search by non-creator
   */
  public String queryParamNonCreator = null;
  /**
   * if query result should contain hyperlinks or not. if this parameter
   * is simply present => result will have hyperlinks in it
   */
  public String queryParamHyperlinks = null;

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
