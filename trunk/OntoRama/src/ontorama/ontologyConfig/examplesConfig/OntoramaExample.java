
package ontorama.ontologyConfig.examplesConfig;

/**
 * Description:  Hold all details of an example (such as name, ontology root,
 *              uri where to find it, parser details, etc).
 * Copyright:    Copyright (c) DSTC 2001
 * Company: DSTC
 * @author  nataliya
 * @version 1.0
 */



public class OntoramaExample {

  /**
   * name of this example - appers in the Examples Menu in the
   * MainApp
   */
  private String name;

  /**
   * ontology root
   */
  private String root;

  /**
   * uri
   */
  private String relativeUri;

  /**
   * query output format
   * For example, can be RDF or XML
   */
  private String queryOutputFormat;

  /**
   * package suffix, package that can parse this ontology.
   * For example, rdf.RdfParser.
   * Suffix is appended to prefix that lives in OntoramaConfig
   */
  private String parserPackagePathSuffix;

  /**
   * if need to group examples - can use subfolders in Menu
   */
  private String menuSubfolder;

  /**
   * if this example should be loaded first.
   */
  private boolean loadFirst = false;

  /**
   *
   */
  public OntoramaExample (String name, String root, String relativeUri,
                        String queryOutputFormat, String parserPackagePathSuffix) {
    this.name = name;
    this.root = root;
    this.relativeUri = relativeUri;
    this.queryOutputFormat = queryOutputFormat;
    this.parserPackagePathSuffix = parserPackagePathSuffix;
  }

  /**
   *
   */
  public String getName () {
    return this.name;
  }

  /**
   *
   */
  public String getRoot() {
    return this.root;
  }

  /**
   *
   */
  public String getRelativeUri() {
    return this.relativeUri;
  }

  /**
   *
   */
  public String getQueryOutputFormat() {
    return this.queryOutputFormat;
  }

  /**
   *
   */
  public String getParserPackagePathSuffix() {
    return this.parserPackagePathSuffix;
  }

  /**
   *
   */
  public void setMenuSubfolderName (String subfolderName) {
    this.menuSubfolder = subfolderName;
  }

  /**
   *
   */
  public String getMenuSubfolderName () {
    return this.menuSubfolder;
  }

  /**
   *
   */
  public void setLoadFirst (boolean loadFirst) {
    this.loadFirst = loadFirst;
  }

  /**
   *
   */
  public boolean isLoadFirst () {
    return this.loadFirst;
  }
}