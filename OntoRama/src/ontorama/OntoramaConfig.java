
package ontorama;

import ontorama.ontologyConfig.*;
import ontorama.ontologyConfig.examplesConfig.OntoramaExample;
import ontorama.ontologyConfig.examplesConfig.XmlExamplesConfigParser;
import ontorama.view.ErrorPopupMessage;
import ontorama.webkbtools.inputsource.JarSource;
import ontorama.webkbtools.util.SourceException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.model.EdgeType;

import java.io.IOException;
import java.io.InputStream;
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
//    private static RelationLinkDetails[] allRelationsArray;

    private static Hashtable edgesConfig;

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


//    /**
//     * Max value for realtionLinks.
//     */
//    public static int MAXTYPELINK;

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
    private static JarSource streamReader = new JarSource();;


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
            classException.printStackTrace();
            System.exit(-1);
        }

        loadAllConfig("examplesConfig.xml", "ontorama.properties", "config.xml");
        System.out.println("--------- end of config--------------");
    }

    /**
     *
     */
    private static void fatalExit(String message, Exception e) {
        new ErrorPopupMessage(message, null);
        System.err.println("Exception: " + e);
        e.printStackTrace();
        System.exit(1);
    }

    /**
     *
     */
    public static void loadAllConfig(String examplesConfigLocation,
                                     String propertiesFileLocation, String configFileLocation) {

        try {
            loadPropertiesFile(propertiesFileLocation);
            loadConfiguration(configFileLocation);
            loadExamples(examplesConfigLocation);
        }
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
        InputStream examplesConfigStream = streamReader.getInputStreamFromResource(examplesConfigLocation);
        XmlExamplesConfigParser examplesConfig = new XmlExamplesConfigParser(examplesConfigStream);
        examplesList = examplesConfig.getExamplesList();
        mainExample = examplesConfig.getMainExample();

        setCurrentExample(mainExample);
    }

    /**
     * load properties from ontorama.properties file
     */
    private static void loadPropertiesFile(String propertiesFileLocation)
                                        throws SourceException, IOException {
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
        InputStream configInStream = streamReader.getInputStreamFromResource(configFileLocation);
        XmlConfigParser xmlConfig = new XmlConfigParser(configInStream);
        edgesConfig = xmlConfig.getDisplayInfo();
        //allRelationsArray = xmlConfig.getRelationLinksArray();
        //MAXTYPELINK = allRelationsArray.length;
        relationLinksSet = buildRelationLinksSet();
        relationRdfMapping = xmlConfig.getRelationRdfMappingList();

        conceptPropertiesDetails = xmlConfig.getConceptPropertiesTable();
        conceptPropertiesRdfMapping = xmlConfig.getConceptPropertiesRdfMappingTable();
    }

    /**
     * @todo we are assuming that allRelationsArray got all relations id's in order
     * from 1 to n. If this is not a case -> what we are doing here could be wrong
     */
    public static List getRelationLinksList() {

        LinkedList allRelations = new LinkedList();

        Enumeration e = edgesConfig.keys();
        while (e.hasMoreElements()) {
            EdgeType edgeType = (EdgeType) e.nextElement();
            allRelations.add(edgeType);
        }
//
//        for (int i = 0; i < allRelationsArray.length; i++) {
//            if (allRelationsArray[i] != null) {
//                allRelations.add(new Integer(i));
//            }
//        }
        return allRelations;
    }

    public static EdgeTypeDisplayInfo getEdgeDisplayInfo (EdgeType edgeType) {
        EdgeTypeDisplayInfo displayInfo = (EdgeTypeDisplayInfo) edgesConfig.get(edgeType);
        return displayInfo;
    }

    /**
     * @todo added this method while refactoring - shouldn't need this when finished. Remove!
     * @return
     */
    public static HashSet getTempRelationLinksSet () {
        List allRel = getRelationLinksList();
        return new HashSet(allRel);
    }


    /**
     * @todo we are assuming that allRelationsArray got all relations id's in order
     * from 1 to n. If this is not a case -> what we are doing here could be wrong
     */
    private static HashSet buildRelationLinksSet() {

        List allRelations = getRelationLinksList();
        return new HashSet(allRelations);
    }

    /**
     *
     */
    public static HashSet getRelationLinksSet() {
        return relationLinksSet;
    }

//    /**
//     *
//     */
//    public static RelationLinkDetails[] getRelationLinkDetails() {
//        return allRelationsArray;
//    }

//    public static List getRelationLinkDetailsList () {
//        LinkedList allRelations = new LinkedList();
//        for (int i = 0; i < allRelationsArray.length; i++) {
//            if (allRelationsArray[i] != null) {
//                allRelations.add(allRelationsArray[i]);
//            }
//        }
//        return allRelations;
//    }

    /**
     *
     */
    public static EdgeType getRelationLinkDetails(String edgeName) throws NoSuchRelationLinkException {
        EdgeType result = null;
        Iterator it = edgesConfig.keySet().iterator();
        while (it.hasNext()) {
            EdgeType edgeType = (EdgeType) it.next();
            if (edgeType.getName().equals(edgeName)) {
                result = edgeType;
            }
        }
        if (result == null) {
            throw new NoSuchRelationLinkException(edgeName);
        }
        return result;
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
    public static OntoramaExample getCurrentExample() {
        return OntoramaConfig.mainExample;
    }

    /**
     *
     */
    public static ClassLoader getClassLoader() {
        return OntoramaConfig.classLoader;
    }
}

