
package ontorama;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import ontorama.conf.ConfigParserException;
import ontorama.conf.EdgeTypeDisplayInfo;
import ontorama.conf.NodeTypeDisplayInfo;
import ontorama.conf.XmlConfigParser;
import ontorama.conf.examplesConfig.OntoramaExample;
import ontorama.conf.examplesConfig.XmlExamplesConfigParser;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.NodeType;
import ontorama.model.graph.NodeTypeImpl;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.SourceException;
import ontorama.ontotools.source.JarSource;
import ontorama.ui.ErrorPopupMessage;
import ontorama.util.SVG2Shape;


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
     * All parsers should have ontorama.ontotools.parser as root
     * and implement Parser iterface.
     */
    public static String parserPackageName;

    /**
     * where to find parser
     */
    private static final String parserPackagePathPrefix = "ontorama.ontotools.parser";

    /**
     * where to find source package
     */
    private static final String sourcePackagePathPrefix = "ontorama.ontotools.source";


    private static Hashtable edgesConfig;
    private static List edgeTypesList;

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
     * turns funny edges on
     */
    public static boolean FOUNTAINS;

    /**
     * verbose flag
     */
    public static boolean VERBOSE;

	/**
	 * flag to enable editing mode.
	 */
	public static boolean EDIT_ENABLED;


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
        FOUNTAINS = (new Boolean(properties.getProperty("FOUNTAINS"))).booleanValue();
		EDIT_ENABLED = (new Boolean(properties.getProperty("EDIT_ENABLED"))).booleanValue();
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
        edgeTypesList = xmlConfig.getEdgesOrdering();
        relationRdfMapping = xmlConfig.getRelationRdfMappingList();
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
        System.out.println("Overriden sourceUri = " + OntoramaConfig.sourceUri + ", root = " + OntoramaConfig.ontologyRoot);
        OntoramaConfig.mainExample.setRoot(root);
        OntoramaConfig.mainExample.setRelativeUri(url);
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

    public static List getEdgeTypesList() {
        return OntoramaConfig.edgeTypesList;
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
    	/// @todo check why we do only half the width here
    	int width = 30;
    	int height = 30;

        Shape conceptShape = new Ellipse2D.Double(-width/2, -height/2, width, height);
        
        int x[] = {-width/2, -width/2, width/2};
        int y[] = {-height/2, height/2 -1, height/2 -1};
        Shape relationShape = new Polygon(x, y, 3);

        int x0 = -width/2;
        int x1 = -(width * 1)/4;
        int x2 = (width * 1)/4;
        int x3 = width/2;
        int y0 = -height/2;
        int y1 = -(height * 1)/4;
        int y2 = (height * 1)/4;
        int y3 = height/2;
        int xb[] = {x0,x1,x2,x3,x3,x2,x1,x0};
        int yb[] = {y1,y0,y0,y1,y2,y3,y3,y2};
        Shape unknownShape = new Polygon(xb, yb, 8); 

        int xair[] = {-23,-21,-23,-20,-15,-13,-4,-4,-4,-3,-3,-3,-2,-2,-2,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-2,-8,-7,-6,-6,-6,-5,-5,-4,-4,-4,-4,-4,-4,-3,-3,-2,-2,0,0,1,1,1,1,2,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,2,2,2,1,1,1,0,3,6,6,6,7,7,7,7,7,7,8,8,8,8,8,8,8,8,8,8,8,7,7,7,7,7,7,7,6,6,5,10,14,19,20,21,21,22,22,22,22,22,23,23,23,23,23,23,23,23,23,23,23,23,23,23,23,22,22,22,22,21,21,21,19,18,12,10,5,6,7,7,7,7,8,8,8,8,8,8,8,8,8,8,8,7,7,7,7,7,7,6,6,6,5,3,0,1,2,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,2,2,2,1,1,1,1,0,0,-2,-2,-3,-3,-4,-4,-4,-4,-4,-5,-5,-6,-6,-6,-7,-8,-3,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-2,-2,-2,-3,-3,-4,-4,-4,-15,-20,-23,-21,-23};
        int yair[] = {0,0,8,7,1,1,1,1,1,2,2,2,2,2,2,2,2,3,3,3,3,3,3,3,3,4,4,4,4,5,5,6,9,23,23,23,23,23,23,23,23,22,22,22,22,22,22,21,20,19,18,18,18,18,18,18,18,18,17,17,17,17,17,16,16,16,16,16,16,16,16,16,15,15,15,15,15,15,15,15,10,10,11,11,11,11,10,10,10,10,10,10,10,10,10,10,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,2,2,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-1,-1,-1,-1,-1,-1,-1,-2,-2,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-11,-11,-11,-11,-11,-11,-15,-15,-15,-15,-15,-15,-16,-16,-16,-16,-16,-16,-16,-16,-16,-16,-17,-17,-17,-17,-17,-17,-17,-18,-18,-18,-18,-18,-18,-18,-18,-19,-20,-21,-21,-22,-22,-22,-22,-22,-23,-23,-23,-23,-23,-23,-23,-10,-6,-5,-4,-4,-4,-4,-3,-3,-3,-3,-3,-3,-3,-3,-2,-2,-2,-2,-2,-2,-2,-2,-2,-2,-1,-7,-8,0,0};
		Shape airplaneShape = new Polygon(xair, yair, xair.length);
		
		// try finding a special node shape
        try {
            SAXBuilder builder = new SAXBuilder(false);

            Document doc = builder.build(streamReader.getInputStreamFromResource("node.svg"));
            Element svgElem = doc.getRootElement();
            airplaneShape = SVG2Shape.importShape(svgElem, 35, 35);
        } catch (Exception e) {
        	// we just stick with the default
        }
		
        List nodeTypes = new LinkedList();
        NodeType typeConcept = new NodeTypeImpl("concept", airplaneShape , false, Color.BLUE);
        nodeTypes.add(typeConcept);
        NodeType typeRelation = new NodeTypeImpl("relation", relationShape, false, Color.GREEN);
        nodeTypes.add(typeRelation);
        NodeType typeUnknown = new NodeTypeImpl("unknown", unknownShape, false, Color.WHITE);
        nodeTypes.add(typeUnknown);

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
            displayInfo.setColor(curNodeType.getDisplayColor());
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

