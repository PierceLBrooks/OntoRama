
package ontorama;

import ontorama.ontologyConfig.*;
import ontorama.ontologyConfig.examplesConfig.OntoramaExample;
import ontorama.ontologyConfig.examplesConfig.XmlExamplesConfigParser;
import ontorama.view.ErrorPopupMessage;
import ontorama.webkbtools.inputsource.JarSource;
import ontorama.webkbtools.util.SourceException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.model.EdgeType;
import ontorama.model.NodeType;
import ontorama.model.NodeTypeImpl;
import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.filemanager.FileBackend;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.awt.*;


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


    private static Hashtable edgesConfig;
    private static List edgesOrdering;

    /**
     *
     */
    private static List relationRdfMapping;


    /**
     * Get current classloader
     */
    private static Class curClass;
    private static ClassLoader classLoader;


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

    private static List backends = new LinkedList();
    private static boolean loadBlankOnStartUp = false;

    private static List nodeTypesList;
    private static Hashtable nodesConfig;

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
        nodeTypesList = buildNodeTypesList();
        nodesConfig = buildNodeTypeDisplayMapping(nodeTypesList);
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
        loadBlankOnStartUp = (new Boolean(properties.getProperty("loadBlankOnStartUp"))).booleanValue();
        /// @todo the way backends are handled is a bit of a hack for now.
        Enumeration e = properties.propertyNames();
        while (e.hasMoreElements()) {
            String str = (String) e.nextElement();
            if (str.startsWith("backend")) {
                backends.add(properties.getProperty(str));
            }
        }

    }

    /**
     * load Config
     */
    private static void loadConfiguration(String configFileLocation)
                                        throws SourceException, ConfigParserException, IOException {
        InputStream configInStream = streamReader.getInputStreamFromResource(configFileLocation);
        XmlConfigParser xmlConfig = new XmlConfigParser(configInStream);
        edgesConfig = xmlConfig.getDisplayInfo();
        edgesOrdering = xmlConfig.getEdgesOrdering();
        relationRdfMapping = xmlConfig.getRelationRdfMappingList();
    }

    /**
     * @todo we are assuming that allRelationsArray got all relations id's in order
     * from 1 to n. If this is not a case -> what we are doing here could be wrong
     */
    public static List getEdgeTypesList() {
        LinkedList allRelations = new LinkedList();

        Enumeration e = edgesConfig.keys();
        while (e.hasMoreElements()) {
            EdgeType edgeType = (EdgeType) e.nextElement();
            allRelations.add(edgeType);
        }
        return allRelations;
    }

    public static EdgeTypeDisplayInfo getEdgeDisplayInfo (EdgeType edgeType) {
        EdgeTypeDisplayInfo displayInfo = (EdgeTypeDisplayInfo) edgesConfig.get(edgeType);
        return displayInfo;
    }



    /**
     *
     */
    public static HashSet getEdgeTypesSet() {
        List allRelations = getEdgeTypesList();
        return new HashSet(allRelations);
    }

    /**
     *  @todo shouldn't just return edgeType for reversed name - user doesn't have a way to know that we switched direction here
     */
    public static EdgeType getEdgeType(String edgeName) throws NoSuchRelationLinkException {
        EdgeType result = null;
        Iterator it = edgesConfig.keySet().iterator();
        while (it.hasNext()) {
            EdgeType edgeType = (EdgeType) it.next();
            if (edgeType.getName().equals(edgeName)) {
                result = edgeType;
            }
            else if ( (edgeType.getReverseEdgeName() != null) && (edgeType.getReverseEdgeName().equals(edgeName)) ){
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
     * @todo this approach is a hack to make distillery work. need to rethink whole process
     * @param root
     * @param url
     */
    public static void overrideExampleRootAndUrl (String root, String url) {
        OntoramaConfig.sourceUri = url;
        OntoramaConfig.ontologyRoot = root;
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

    public static List getEdgesOrdering() {
        return OntoramaConfig.edgesOrdering;
    }


    /**
     *
     */
    public static ClassLoader getClassLoader() {
        return OntoramaConfig.classLoader;
    }

    public static List getBackends () {
        return backends;
    }

    public static boolean loadBlank () {
        return loadBlankOnStartUp;
    }

    private static List buildNodeTypesList () {
        List nodeTypes = new LinkedList();
        NodeType typeConcept = new NodeTypeImpl("concept");
        nodeTypes.add(typeConcept);
        NodeType typeRelation = new NodeTypeImpl("relation");
        nodeTypes.add(typeRelation);
        return nodeTypes;
    }

    /**
     *
     * @param nodeTypesList
     * @return
     * @todo this method is a temp hack untill node types display info can be put into the config file.
     */
    private static Hashtable buildNodeTypeDisplayMapping (List nodeTypesList) {
        Hashtable result = new Hashtable();
        Iterator it = nodeTypesList.iterator();
        while (it.hasNext()) {
            NodeType curNodeType = (NodeType) it.next();
            NodeTypeDisplayInfo displayInfo = new NodeTypeDisplayInfo();
            if (curNodeType.getNodeType().equals("concept")) {
                displayInfo.setColor(Color.blue);
            }
            if (curNodeType.getNodeType().equals("relation")) {
                displayInfo.setColor(Color.green);
            }
            result.put(curNodeType, displayInfo);
        }
        return result;
    }

    public static List getNodeTypesList() {
        return nodeTypesList;
    }

    public static NodeTypeDisplayInfo getNodeTypeDisplayInfo (NodeType nodeType) {
        return (NodeTypeDisplayInfo) nodesConfig.get(nodeType);
    }

}

