

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
 * @todo    Implement different method to introduce relation links:
 *      rather then having them hardcoded in this class -
 *      read from a config file, for example:
 *      <ontology>
 *          <relation id="1">
 *              <>supertype</>
 *              <>subtype</>
 *          </relation>
 *          .....
 *      </ontology>
 *      <map>
 *          <map from="<" to="^" />
 *      </map>
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
    //private static final String configsDirLocation = "./classes";
    private static final String configsDirLocation = "./";

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

    //private static String propertiesFileLocation = configsDirLocation + "/ontorama.properties";
    //private static String xmlConfigFileLocation = configsDirLocation + "/config.xml";


    // things needed for java webstart
    //
    private static URL propertiesFileLocation;
    private static URL xmlConfigFileLocation;

    private static List examplesList;
    private static OntoramaExample mainExample;


    /**
     * debug
     */
    public static boolean DEBUG;

    /**
     * Values of vars that are set here should be read from
     * java properties file.
     * @todo    check if we should read conceptPropeties details from xml config file????...
     * if answer is 'yes', then OntologyType and GraphNode should be changed as they have
     * description and creator hardcoded.
     */
     static {

        try {
            curClass = Class.forName("ontorama.OntoramaConfig");
            classLoader = curClass.getClassLoader();
        }
        catch (ClassNotFoundException classException ) {
            System.err.println("ClassNotFoundException : " + classException);
            System.exit(-1);
        }


        Properties properties = new Properties();

        //propertiesFileLocation = classLoader.getResource("ontorama.properties");
    	//xmlConfigFileLocation = classLoader.getResource("config.xml");

        //Properties properties = new Properties(System.getProperties());
        try {
          //FileInputStream propertiesFileIn = new FileInputStream (propertiesFileLocation);

		  //InputStream propertiesFileIn = propertiesFileLocation.openConnection().getInputStream();
		  InputStream propertiesFileIn = getInputStreamFromResource(classLoader,"ontorama.properties");

          properties.load(propertiesFileIn);

          sourceUri = properties.getProperty("sourceUri");
          ontologyRoot = properties.getProperty("ontologyRoot");
          queryOutputFormat = properties.getProperty("queryOutputFormat");
          parserPackagePathSuffix = properties.getProperty ("parserPackagePathSuffix");
          sourcePackagePathSuffix = properties.getProperty ("sourcePackagePathSuffix");
          DEBUG = (new Boolean ( properties.getProperty("DEBUG"))).booleanValue();

          //parserPackageName = parserPackagePathPrefix + "." + parserPackagePathSuffix;
          //sourcePackageName = sourcePackagePathPrefix + "." + sourcePackagePathSuffix;

          System.out.println("sourceUri = " + sourceUri);


        }
        catch (Exception e) {
          System.err.println("Unable to read properties file in");
          System.err.println("Exception: " + e);
          System.exit(1);
        }

        try {
            //FileInputStream configInStream = new FileInputStream(configsDirLocation + "/config.xml");

            //FileInputStream configInStream = new FileInputStream(xmlConfigFileLocation);
            //InputStream configInStream = xmlConfigFileLocation.openConnection().getInputStream();
            //InputStream configInStream = classLoader.getResourceAsStream("config.xml");
            InputStream configInStream = getInputStreamFromResource(classLoader,"config.xml");
            //System.out.println("input stream = " + configInStream.getClass());

            XmlConfigParser xmlConfig = new XmlConfigParser(configInStream);
            allRelationsArray = xmlConfig.getRelationLinksArray();
            MAXTYPELINK = allRelationsArray.length;
            relationLinksSet = buildRelationLinksSet (allRelationsArray);
            relationRdfMapping = xmlConfig.getRelationRdfMappingList();

            conceptPropertiesDetails = xmlConfig.getConceptPropertiesTable();
            conceptPropertiesRdfMapping = xmlConfig.getConceptPropertiesRdfMappingTable();
            //xmlConfig.printConceptPropertiesRdfMapping();

            // loading examples
            System.out.println("loading examples");
            InputStream examplesConfigStream = getInputStreamFromResource(classLoader,"examplesConfig.xml");
            XmlExamplesConfigParser examplesConfig = new XmlExamplesConfigParser(examplesConfigStream);
            examplesList = examplesConfig.getExamplesList();
            mainExample = examplesConfig.getMainExample();

            // overwrite sourceUri, ontologyRoot, etc
            ///@todo  fix this later!!!
            sourceUri = mainExample.getRelativeUri();
            ontologyRoot = mainExample.getRoot();
            queryOutputFormat = mainExample.getQueryOutputFormat();
            parserPackagePathSuffix = mainExample.getParserPackagePathSuffix();
        }
        catch (IOException ioe) {
          System.err.println("Unable to read xml configuration file");
          System.err.println("IOException: " + ioe);
          System.exit(1);
        }
        catch ( ConfigParserException cpe ) {
            System.err.println("ConfigParserException: " + cpe.getMessage());
            System.exit(-1);
        }
        catch ( ArrayIndexOutOfBoundsException arrayExc ) {
            System.err.println("Please make sure relation id's in xml config are ordered from 1 to Max number");
            System.err.println("ArrayIndexOutOfBoundsException: " + arrayExc);
            System.exit(-1);
        }
        //parserPackageName = parserPackagePathPrefix + "." + parserPackagePathSuffix;
        setParserPackageName(parserPackagePathSuffix);
        sourcePackageName = sourcePackagePathPrefix + "." + sourcePackagePathSuffix;

        System.out.println("---------config--------------");
        System.out.println("sourceUri = " + sourceUri);
        System.out.println("ontologyRoot = " + ontologyRoot);
        System.out.println("queryOutputFormat = " + queryOutputFormat);
        System.out.println("DEBUG = " + DEBUG);
        System.out.println("parserPackageName = " + getParserPackageName());
        System.out.println("sourcePackageName = " + sourcePackageName);
        /*
        for (int i = 0; i < allRelationsArray.length; i++ ) {
            if ( allRelationsArray[i] != null ) {
                System.out.println("i = " + i + ", object = " + allRelationsArray[i].getLinkName());
            }
        }
        */
        System.out.println("--------- end of config--------------");
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
     public static void setNewExampleDetails (OntoramaExample example) {
      OntoramaConfig.sourceUri = example.getRelativeUri();
      setParserPackageName(example.getParserPackagePathSuffix());
      OntoramaConfig.ontologyRoot = example.getRoot();
      OntoramaConfig.queryOutputFormat = example.getQueryOutputFormat();
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
    * @todo	need an exception for an unknown protocol
    * @todo       maybe there is a better way to handle that hack with stripping off protocol and "://" from url
    */
    private static InputStream getInputStreamFromResource (ClassLoader classLoader,
                                String resourceName) throws IOException {

          InputStream resultStream = null;
          URL url = classLoader.getResource(resourceName);
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

