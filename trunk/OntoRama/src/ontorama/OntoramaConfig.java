

package ontorama;

import java.util.Properties;
import java.io.FileInputStream;

public class OntoramaConfig {

    /**
     * URI for Ontology Source. It can be file or URL to CGI script.
     * If file is used - it is important to formulate correct URI,
     * for example:
     * file:/H:/devel/OntoRama/test/wn_cat-children_rdf.html
     * for file H:/devel/OntoRama/test/wn_cat-children_rdf.html
     */
    public static String sourceUri;

    /**
     * Source Package Name
     * Specify source implementation to use
     */
    public static String sourcePackageName;

    /**
     * Specify Ontology Server Output format
     */
    public static String queryOutputFormat;

    /**
     * Specify Parser to use with queryOutputFormat.
     * All parsers should have ontorama.webkbtools.query.parser as root
     * and implement Parser iterface.
     */
    public static String parserPackageName;

    /**
     * where to find parser
     */
    private static final String parserPackagePathPrefix = "ontorama.webkbtools.query.parser";

    /**
     * where to find source package
     */
    private static final String sourcePackagePathPrefix = "ontorama.webkbtools.inputsource";


    /**
     * All predefined relationLinks constants
     * IMPORTANT: If any new relationLinks are added - make sure
     * MAXTYPELINK is changed
     */
	public static final int SUPERTYPE = 0;
	public static final int SUBTYPE = 1;
	public static final int PARTOF = 2;
	public static final int HASAPART = 3;
	public static final int MEMBEROF = 4;
	public static final int HASAMEMBER = 5;
	public static final int SYNONYMTYPE = 6;
	public static final int INSTANCEOF = 7;
	public static final int HASAINSTANCE = 8;
	public static final int SUBSTANCEOF = 9;
	public static final int HASASUBSTANCE = 10;
	public static final int INCLUSIVETYPE =11;
	public static final int EXCLUSIVETYPE = 12;
    /**
     * consider to have a property typeCreator rather then relationLink CREATOR
     */
	public static final int CREATOR = 13;

    /**
     * Max value for realtionLinks.
     */
	public static final int MAXTYPELINK = 13;

    /**
     * debug
     */
    public static boolean DEBUG;

    /**
     * Values of vars that are set here should be read from
     * java properties file.
     */
     static {

        Properties properties = new Properties();
        try {
          FileInputStream propertiesFileIn = new FileInputStream ("D:/pbecker/projects/OntoRama/src/ontorama.properties");
          //FileInputStream propertiesFileIn = new FileInputStream ("../src/ontorama.properties");
          properties.load(propertiesFileIn);

          sourceUri = properties.getProperty("sourceUri");
          queryOutputFormat = properties.getProperty("queryOutputFormat");
          String parserPackagePathSuffix = properties.getProperty ("parserPackagePathSuffix");
          String sourcePackagePathSuffix = properties.getProperty ("sourcePackagePathSuffix");
          DEBUG = (new Boolean ( properties.getProperty("DEBUG"))).booleanValue();

          parserPackageName = parserPackagePathPrefix + "." + parserPackagePathSuffix;
          sourcePackageName = sourcePackagePathPrefix + "." + sourcePackagePathSuffix;

          System.out.println("---------config--------------");
          System.out.println("sourceUri = " + sourceUri);
          System.out.println("queryOutputFormat = " + queryOutputFormat);
          System.out.println("DEBUG = " + DEBUG);
          System.out.println("parserPackageName = " + parserPackageName);
          System.out.println("sourcePackageName = " + sourcePackageName);
          System.out.println("--------- end of config--------------");

        }
        catch (Exception e) {
          System.err.println("Unable to read properties file in");
          System.err.println("Exception: " + e);
          System.exit(1);
        }

    }
}

