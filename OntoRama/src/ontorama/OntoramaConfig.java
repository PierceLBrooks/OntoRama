

package ontorama;

import java.util.Properties;
import java.io.*;
import java.io.IOException;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedList;
import java.util.Collection;
import java.util.List;
import java.util.Enumeration;
import java.util.zip.*;
import java.net.URLConnection;
import java.net.URL;

import ontorama.ontologyConfig.RelationLinkDetails;
import ontorama.ontologyConfig.ConceptPropertiesDetails;
import ontorama.ontologyConfig.XmlConfigParser;
import ontorama.ontologyConfig.ConfigParserException;
import ontorama.ontologyConfig.examplesConfig.XmlExamplesConfigParser;
import ontorama.ontologyConfig.examplesConfig.OntoramaExample;


/**
 */
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
     * default ontology root
     */
    public static String ontologyRoot = null;

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
     *
     */
     private static String parserPackagePathSuffix;
     private static String sourcePackagePathSuffix;

    /**
     *
     */
    private static RelationLinkDetails[] allRelationsArray;

    /**
     * Holds defined conceptProperties details. This list can be referred to
     * whenever we need to find out what details are available for each concept
     * type.
     */
    private static Hashtable conceptPropertiesDetails;

    /**
     *
     */
    private static HashSet relationLinksSet;

    /**
     *
     */
    private static List relationRdfMapping;

    /**
     * Holds mapping for concept properties. Keys of this hashtable should
     * correspond to list conceptPropertiesConfig, Values are rdf mappings for
     * the property.
     */
     private static Hashtable conceptPropertiesRdfMapping;

     /**
      * Get current classloader
      */
     private static Class curClass;
     private static ClassLoader classLoader;


    /**
     * Max value for realtionLinks.
     */
    public static int MAXTYPELINK;

    // things needed for java webstart
    //
    private static URL propertiesFileLocation;
    private static URL xmlConfigFileLocation;

    /**
     * examples list
     */
    private static List examplesList;

    /**
     * example that is currently loaded in the app
     * (first on the startup. later on - this var is set to
     * a currently running example).
     */
    private static OntoramaExample mainExample;

    /**
     * debug
     */
    public static boolean DEBUG;

    /**
     *
     */
    private static Properties properties = new Properties();

    /**
     * Values of vars that are set here should be read from
     * java properties file.
     */
     static {
        System.out.println("---------config--------------");

        try {
            curClass = Class.forName("ontorama.OntoramaConfig");
            classLoader = curClass.getClassLoader();
        }
        catch (ClassNotFoundException classException ) {
            System.err.println("ClassNotFoundException : " + classException);
            System.exit(-1);
        }

        loadAllConfig("examplesConfig.xml", "ontorama.properties", "config.xml");

//        System.out.println("---------config--------------");
//        System.out.println("sourceUri = " + sourceUri);
//        System.out.println("ontologyRoot = " + ontologyRoot);
//        System.out.println("queryOutputFormat = " + queryOutputFormat);
//        System.out.println("DEBUG = " + DEBUG);
//        System.out.println("parserPackageName = " + getParserPackageName());
//        System.out.println("sourcePackageName = " + sourcePackageName);
        System.out.println("--------- end of config--------------");
    }

    /**
     *
     */
    private static void fatalExit (String message, Exception e) {
      System.err.println(message);
      System.err.println("Exception: " + e);
      System.exit(1);
    }

    /**
     *
     */
    public static void loadAllConfig (String examplesConfigLocation,
                      String propertiesFileLocation, String configFileLocation) {
        try {
          loadExamples(examplesConfigLocation);
          loadPropertiesFile(propertiesFileLocation);
          loadConfiguration(configFileLocation);
        }
        catch (IOException ioe) {
          fatalExit("Unable to read xml configuration file, IOException", ioe);
        }
        catch ( ConfigParserException cpe ) {
          fatalExit("ConfigParserException: " + cpe.getMessage(), cpe);
        }
        catch ( ArrayIndexOutOfBoundsException arrayExc ) {
          fatalExit("Please make sure relation id's in xml config are ordered from 1 to Max number, ArrayIndexOutOfBoundsException",
                    arrayExc);
        }
        catch (Exception e) {
          fatalExit("Unable to read properties file in", e);
        }
    }

    /**
     * load examples
     */
    private static void loadExamples (String examplesConfigLocation)
                            throws IOException, ConfigParserException {
        // loading examples
        System.out.println("loading examples");
        //InputStream examplesConfigStream = getInputStreamFromResource(classLoader,"examplesConfig.xml");
        InputStream examplesConfigStream = getInputStreamFromResource(examplesConfigLocation);
        XmlExamplesConfigParser examplesConfig = new XmlExamplesConfigParser(examplesConfigStream);
        examplesList = examplesConfig.getExamplesList();
        mainExample = examplesConfig.getMainExample();

        // overwrite sourceUri, ontologyRoot, etc
        ///@todo  fix this later!!!
        setCurrentExample(mainExample);
    }

    /**
     * load properties from ontorama.properties file
     */
    private static void loadPropertiesFile (String propertiesFileLocation)
                            throws IOException {
        //InputStream propertiesFileIn = getInputStreamFromResource(classLoader,"ontorama.properties");
        InputStream propertiesFileIn = getInputStreamFromResource(propertiesFileLocation);
        properties.load(propertiesFileIn);
        DEBUG = (new Boolean ( properties.getProperty("DEBUG"))).booleanValue();
    }

    /**
     * load Config
     */
    private static void loadConfiguration (String configFileLocation)
                            throws IOException, ConfigParserException {
        //InputStream configInStream = getInputStreamFromResource(classLoader,"config.xml");
        InputStream configInStream = getInputStreamFromResource(configFileLocation);

        XmlConfigParser xmlConfig = new XmlConfigParser(configInStream);
        allRelationsArray = xmlConfig.getRelationLinksArray();
        MAXTYPELINK = allRelationsArray.length;
        relationLinksSet = buildRelationLinksSet (allRelationsArray);
        relationRdfMapping = xmlConfig.getRelationRdfMappingList();

        conceptPropertiesDetails = xmlConfig.getConceptPropertiesTable();
        conceptPropertiesRdfMapping = xmlConfig.getConceptPropertiesRdfMappingTable();
    }



    /**
     * @todo: we are assuming that allRelationsArray got all relations id's in order
     * from 1 to n. If this is not a case -> what we are doing here could be wrong
     */
    public static HashSet buildRelationLinksSet (RelationLinkDetails[] allRelationsArray) {
        LinkedList allRelations = new LinkedList ();
        for (int i = 0; i < allRelationsArray.length; i++ ) {
            if ( allRelationsArray[i] != null ) {
                allRelations.add(new Integer (i));
            }
        }
        return new HashSet ((Collection) allRelations);
    }

    /**
     *
     */
    public static HashSet getRelationLinksSet () {
        return relationLinksSet;
    }

    /**
     *
     */
    public static RelationLinkDetails[] getRelationLinkDetails () {
        return allRelationsArray;
    }

    /**
     *
     */
    public static RelationLinkDetails getRelationLinkDetails (int i) {
        return allRelationsArray[i];
    }

    /**
     *
     */
     public static List getRelationRdfMapping () {
        return relationRdfMapping;
     }

     /**
      *
      */
     public static Hashtable getConceptPropertiesTable () {
        return conceptPropertiesDetails;
     }

     /**
      *
      */
     public static ConceptPropertiesDetails getConceptPropertiesDetails (String propertyName) {
        return (ConceptPropertiesDetails) conceptPropertiesDetails.get(propertyName);
     }

     /**
      *
      */
     public static Hashtable getConceptPropertiesRdfMapping () {
        return conceptPropertiesRdfMapping;
     }

     /**
      *
      */
     public static List getExamplesList () {
        return examplesList;
     }

     /**
      * @todo should all OntoramaConfig variables be public? or should they
      *   have setters and getters? (sourceUri, ontologyRoot, queryOutputFormat)
      */
     public static void setCurrentExample (OntoramaExample example) {
      OntoramaConfig.mainExample = example;
      OntoramaConfig.sourceUri = example.getRelativeUri();
      setParserPackageName(example.getParserPackagePathSuffix());
      setSourcePackageName(example.getSourcePackagePathSuffix());
      OntoramaConfig.ontologyRoot = example.getRoot();
      OntoramaConfig.queryOutputFormat = example.getQueryOutputFormat();

      /*
      System.out.println("setNewExampleDetails:");
      System.out.println("OntoramaConfig.sourceUri = " + OntoramaConfig.sourceUri);
      System.out.println("OntoramaConfig.ontologyRoot  = " + OntoramaConfig.ontologyRoot );
      System.out.println("OntoramaConfig.parserPackageName  = " + OntoramaConfig.parserPackageName );
      System.out.println("OntoramaConfig.sourcePackageName  = " + OntoramaConfig.sourcePackageName );
      System.out.println("OntoramaConfig.queryOutputFormat  = " + OntoramaConfig.queryOutputFormat );
      */
     }

     /**
      *
      */
     public static String getParserPackageName () {
      return parserPackageName;
     }

     /**
      *
      */
     public static void setParserPackageName (String parserPackagePathSuffixStr) {
      parserPackageName = parserPackagePathPrefix + "." + parserPackagePathSuffixStr;
     }

     /**
      *
      */
     public static String getSourcePackageName () {
      return sourcePackageName;
     }


     /**
      *
      */
     public static void setSourcePackageName (String sourcePackagePathSuffixStr) {
      sourcePackageName = sourcePackagePathPrefix + "." + sourcePackagePathSuffixStr;
     }

     /**
      *
      */
     public static OntoramaExample getCurrentExample () {
      /*
      System.out.println("getCurrentExample:");
      System.out.println("OntoramaConfig.sourceUri = " + OntoramaConfig.sourceUri);
      System.out.println("OntoramaConfig.ontologyRoot  = " + OntoramaConfig.ontologyRoot );
      System.out.println("OntoramaConfig.parserPackageName  = " + OntoramaConfig.parserPackageName );
      System.out.println("OntoramaConfig.sourcePackageName  = " + OntoramaConfig.sourcePackageName );
      System.out.println("OntoramaConfig.queryOutputFormat  = " + OntoramaConfig.queryOutputFormat );
      */

      return OntoramaConfig.mainExample;
     }

     /**
      *
      */
     public static ClassLoader getClassLoader() {
      return OntoramaConfig.classLoader;
     }


    /**
    *
    * @todo	need an exception for an unknown protocol
    * @todo       maybe there is a better way to handle that hack with stripping off protocol and "://" from url
    */
    //public static InputStream getInputStreamFromResource (ClassLoader classLoader,
    public static InputStream getInputStreamFromResource (String resourceName)
                                               throws IOException {

          InputStream resultStream = null;
          System.out.println("resourceName = " + resourceName);
          //System.out.println("OntoramaConfig.classLoader = " + OntoramaConfig.classLoader);
          URL url = OntoramaConfig.classLoader.getResource(resourceName);
          //URL url = classLoader.getResource(resourceName);
          System.out.println("url = " + url);

          if (url.getProtocol().equalsIgnoreCase("jar")) {
            //System.out.println("found JAR");
            //System.out.println("file = " + url.getFile());
            String pathString = url.getFile();
            int index = pathString.indexOf("!");
            String filePath = pathString.substring(0,index);
            // a hack: strip string 'protocol:/" from the path
            if (filePath.startsWith("file")) {
                    int index1 = pathString.indexOf(":") + 1;
                    filePath = filePath.substring(index1, filePath.length());
            }

            //System.out.println("filePath = " + filePath);
            File file = new File(filePath);
            ZipFile zipFile = new ZipFile (file);
            ZipEntry zipEntry = zipFile.getEntry(resourceName);
            resultStream = (InputStream) zipFile.getInputStream(zipEntry);
          }
          else if (url.getProtocol().equalsIgnoreCase("file")) {
            resultStream = url.openStream();
          }
          else {
            System.err.println("Dont' know about this protocol: " + url.getProtocol());
            System.exit(-1);
          }
          return resultStream;
    }

}

