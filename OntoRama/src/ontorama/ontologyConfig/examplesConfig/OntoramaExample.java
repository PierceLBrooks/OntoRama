
package ontorama.ontologyConfig.examplesConfig;


public class OntoramaExample {
  private String name;
  private String root;
  private String relativeUri;
  private String queryOutputFormat;
  private String parserPackagePathSuffix;
  private String menuSubfolder;

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
}