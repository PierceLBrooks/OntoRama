
package ontorama;

import ontorama.ontologyConfig.ConceptPropertiesDetails;
import ontorama.ontologyConfig.ConfigParserException;
import ontorama.ontologyConfig.RelationLinkDetails;
import ontorama.ontologyConfig.XmlConfigParser;
import ontorama.ontologyConfig.examplesConfig.OntoramaExample;
import ontorama.ontologyConfig.examplesConfig.XmlExamplesConfigParser;
import ontorama.view.ErrorPopupMessage;
import ontorama.webkbtools.inputsource.JarSource;
import ontorama.webkbtools.util.SourceException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;


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
     * Specify query string constructor to use for dynamic ontology servers
     */
    public static String queryStringConstructorPackageName;
    /**
     * boolean that specifies if input ontology source is static (file) or dynamic (cgi)
     */
    public static boolean isSourceDynamic;

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
     * where to find package that builds query string in case of dynamic ontology server
     */
    private static final String queryStringConstructorPackagePathPrefix = "ontorama.webkbtools.query.cgi";
    /**
     * name of the package itself (supplied by the user in examples config file)
     */
    private static String queryStringConstructorPackagePathSuffix;

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
     * verbose flag
     */
    public static boolean VERBOSE;

    /**
     *
     */
    private static JarSource streamReader = new JarSource();


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
        } catch (ClassNotFoundException classException) {
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
    private static void fatalExit(String message, Exception e) {
        ErrorPopupMessage errorPopup = new ErrorPopupMessage(message, null);
        //System.err.println(message);
        System.err.println("Exception: " + e);
        System.exit(1);
    }

    /**
     *
     */
    public static void loadAllConfig(String examplesConfigLocation,
                                     String propertiesFileLocation, String configFileLocation) {
        try {
            loadExamples(examplesConfigLocation);
            loadPropertiesFile(propertiesFileLocation);
            loadConfiguration(configFileLocation);
        }
//        catch (IOException ioe) {
//          fatalExit("Unable to read xml configuration file, IOException", ioe);
//        }
        catch (SourceException sourceExc) {
            sourceExc.printStackTrace();
            fatalExit("Unable to read properties or configuration file " + ". Error: " + sourceExc.getMessage(), sourceExc);
        } catch (ConfigParserException cpe) {
            cpe.printStackTrace();
            fatalExit("ConfigParserException: " + cpe.getMessage(), cpe);
        } catch (ArrayIndexOutOfBoundsException arrayExc) {
            arrayExc.printStackTrace();
            fatalExit("Please make sure relation id's in xml config are ordered from 1 to Max number, ArrayIndexOutOfBoundsException",
                    arrayExc);
        } catch (Exception e) {
            e.printStackTrace();
            fatalExit("Unable to read properties file in", e);
        }
    }

    /**
     * load examples
     */
    private static void loadExamples(String examplesConfigLocation)
            throws SourceException, ConfigParserException, IOException {
        // loading examples
        if (VERBOSE) {
            System.out.println("loading examples");
        }
        //InputStream examplesConfigStream = getInputStreamFromResource(classLoader,"examplesConfig.xml");
        //InputStream examplesConfigStream = getInputStreamFromResource(examplesConfigLocation);
        InputStream examplesConfigStream = streamReader.getInputStreamFromResource(examplesConfigLocation);
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
    private static void loadPropertiesFile(String propertiesFileLocation)
            throws SourceException, IOException {
        //InputStream propertiesFileIn = getInputStreamFromResource(classLoader,"ontorama.properties");
        //InputStream propertiesFileIn = getInputStreamFromResource(propertiesFileLocation);
        InputStream propertiesFileIn = streamReader.getInputStreamFromResource(propertiesFileLocation);
        properties.load(propertiesFileIn);
        DEBUG = (new Boolean(properties.getProperty("DEBUG"))).booleanValue();
        VERBOSE = (new Boolean(properties.getProperty("VERBOSE"))).booleanValue();
    }

    /**
     * load Config
     */
    private static void loadConfiguration(String configFileLocation)
            throws SourceException, ConfigParserException, IOException {
        //InputStream configInStream = getInputStreamFromResource(classLoader,"config.xml");
        //InputStream configInStream = getInputStreamFromResource(configFileLocation);
        InputStream configInStream = streamReader.getInputStreamFromResource(configFileLocation);

        XmlConfigParser xmlConfig = new XmlConfigParser(configInStream);
        allRelationsArray = xmlConfig.getRelationLinksArray();
        MAXTYPELINK = allRelationsArray.length;
        relationLinksSet = buildRelationLinksSet();
        relationRdfMapping = xmlConfig.getRelationRdfMappingList();

        conceptPropertiesDetails = xmlConfig.getConceptPropertiesTable();
        conceptPropertiesRdfMapping = xmlConfig.getConceptPropertiesRdfMappingTable();
    }

    /**
     * @todo: we are assuming that allRelationsArray got all relations id's in order
     * from 1 to n. If this is not a case -> what we are doing here could be wrong
     */
    public static List getRelationLinksList() {
        LinkedList allRelations = new LinkedList();
        for (int i = 0; i < allRelationsArray.length; i++) {
            if (allRelationsArray[i] != null) {
                allRelations.add(new Integer(i));
            }
        }
        //System.out.println("\n\n\n returning list of relations: " + allRelations);
        return allRelations;
    }


    /**
     * @todo: we are assuming that allRelationsArray got all relations id's in order
     * from 1 to n. If this is not a case -> what we are doing here could be wrong
     */
    public static HashSet buildRelationLinksSet() {
        List allRelations = getRelationLinksList();
        return new HashSet((Collection) allRelations);
    }

    /**
     *
     */
    public static HashSet getRelationLinksSet() {
        return relationLinksSet;
    }

    /**
     *
     */
    public static RelationLinkDetails[] getRelationLinkDetails() {
        return allRelationsArray;
    }

    /**
     *
     */
    public static RelationLinkDetails getRelationLinkDetails(int i) {
        return allRelationsArray[i];
    }

    /**
     *
     */
    public static List getRelationRdfMapping() {
        return relationRdfMapping;
    }

    /**
     *
     */
    public static Hashtable getConceptPropertiesTable() {
        return conceptPropertiesDetails;
    }

    /**
     *
     */
    public static ConceptPropertiesDetails getConceptPropertiesDetails(String propertyName) {
        return (ConceptPropertiesDetails) conceptPropertiesDetails.get(propertyName);
    }

    /**
     *
     */
    public static Hashtable getConceptPropertiesRdfMapping() {
        return conceptPropertiesRdfMapping;
    }

    /**
     *
     */
    public static List getExamplesList() {
        return examplesList;
    }

    /**
     * @todo should all OntoramaConfig variables be public? or should they
     *   have setters and getters? (sourceUri, ontologyRoot, queryOutputFormat)
     */
    public static void setCurrentExample(OntoramaExample example) {
        OntoramaConfig.mainExample = example;
        OntoramaConfig.sourceUri = example.getRelativeUri();
        setParserPackageName(example.getParserPackagePathSuffix());
        setSourcePackageName(example.getSourcePackagePathSuffix());
        OntoramaConfig.isSourceDynamic = example.getIsSourceDynamic();
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
    public static String getParserPackageName() {
        return parserPackageName;
    }

    /**
     *
     */
    public static void setParserPackageName(String parserPackagePathSuffixStr) {
        parserPackageName = parserPackagePathPrefix + "." + parserPackagePathSuffixStr;
    }

    /**
     *
     */
    public static String getSourcePackageName() {
        return sourcePackageName;
    }


    /**
     *
     */
    public static void setSourcePackageName(String sourcePackagePathSuffixStr) {
        sourcePackageName = sourcePackagePathPrefix + "." + sourcePackagePathSuffixStr;
    }

    /**
     *
     */
    public static String getQueryStringCostructorPackageName() {
        return queryStringConstructorPackageName;
    }

    /**
     *
     */
    public static void setQueryStringConstructorPackageName(String queryStringConstructorPackagePathSuffix) {
        queryStringConstructorPackageName = queryStringConstructorPackagePathPrefix + "." + queryStringConstructorPackagePathSuffix;
    }

    /**
     *
     */
    public static OntoramaExample getCurrentExample() {
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
}

