
package ontorama;

import java.awt.Color;
import java.awt.Frame;
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

import ontorama.backends.Backend;
import ontorama.conf.ConfigParserException;
import ontorama.conf.DataFormatConfigParser;
import ontorama.conf.DataFormatMapping;
import ontorama.conf.EdgeTypeDisplayInfo;
import ontorama.conf.NodeTypeDisplayInfo;
import ontorama.conf.XmlConfigParser;
import ontorama.conf.examplesConfig.OntoramaExample;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.NodeType;
import ontorama.model.graph.NodeTypeImpl;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.SourceException;
import ontorama.ontotools.source.JarSource;
import ontorama.ui.ErrorPopupMessage;


/**
 */
public class OntoramaConfig {


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
	
    public static JarSource streamReader = new JarSource();;

    private static Properties properties = new Properties();

    private static List backends = new LinkedList();
    private static boolean loadBlankOnStartUp = false;

    private static Hashtable nodesConfig = new Hashtable();
    
    public static NodeType CONCEPT_TYPE;
    public static NodeType RELATION_TYPE;
    public static NodeType UNKNOWN_TYPE;
    
    
    public static String examplesConfigLocation = "examplesConfig.xml";
    public static final String examplesBackendPackageName = "ontorama.backends.examplesmanager.ExamplesBackend";

	/// @todo not sure if this should be static - need to check
	private static Backend _activeBackend;
	
	/// @todo need some thought for default backend and if there is a need for one
	/// at all. Presently this is used so TestCases can use this backend if 
	/// nothing else is initialised by OntoRamaApp. Probably dont' need this at all?...
	private static Backend _defaultBackend;
	
	private static List _dataFormatsMappingList;

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

        buildDefaultNodeTypes();
        
        loadAllConfig(examplesConfigLocation, "ontorama.properties", "config.xml");
        System.out.println("--------- end of config--------------");
    }

    private static void fatalExit(String message, Exception e) {
        new ErrorPopupMessage(message, null);
        System.err.println("Exception: " + e);
        e.printStackTrace();
        System.exit(1);
    }

    public static void loadAllConfig(String examplesConfigLocation,
                                     String propertiesFileLocation, String configFileLocation) {

		/// @todo perhaps ontorama.properties and dataFormatsConfig.xml only need to be loaded once
        try {
            loadPropertiesFile(propertiesFileLocation);
        	loadDataFormatsConfig("dataFormatsConfig.xml");
            loadConfiguration(configFileLocation);
            OntoramaConfig.examplesConfigLocation = examplesConfigLocation;
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

	private static void loadDataFormatsConfig (String configFileLocation)
						throws SourceException, ConfigParserException {
		InputStream inStream= streamReader.getInputStreamFromResource(configFileLocation);
		DataFormatConfigParser config = new DataFormatConfigParser(inStream);
		_dataFormatsMappingList = config.getDataFormatMappings();
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

    private static void loadConfiguration(String configFileLocation)
                                        throws SourceException, ConfigParserException, IOException {
        InputStream configInStream = streamReader.getInputStreamFromResource(configFileLocation);
        XmlConfigParser xmlConfig = new XmlConfigParser(configInStream);
        edgesConfig = xmlConfig.getDisplayInfo();
        edgeTypesList = xmlConfig.getEdgesOrdering();
        relationRdfMapping = xmlConfig.getRelationRdfMappingList();
        NodeTypeDisplayInfo shape = xmlConfig.getRelationShape();
        if(shape != null){
            nodesConfig.put(RELATION_TYPE, shape);
        }
        shape = xmlConfig.getConceptShape();
        if(shape != null){
            nodesConfig.put(CONCEPT_TYPE, shape);
        }
    }

    public static EdgeTypeDisplayInfo getEdgeDisplayInfo (EdgeType edgeType) {
        EdgeTypeDisplayInfo displayInfo = (EdgeTypeDisplayInfo) edgesConfig.get(edgeType);
        return displayInfo;
    }


    public static HashSet getEdgeTypesSet() {
        List allRelations = getEdgeTypesList();
        return new HashSet(allRelations);
    }

    /**
     * @todo shouldn't just return edgeType for reversed name - user doesn't
     * have a way to know that we switched direction here
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

    public static List getRelationRdfMapping() {
        return relationRdfMapping;
    }


    public static List getEdgeTypesList() {
        return OntoramaConfig.edgeTypesList;
    }


    public static ClassLoader getClassLoader() {
        return OntoramaConfig.classLoader;
    }

    public static List getBackends () {
        return backends;
    }

    public static boolean loadBlank () {
        return loadBlankOnStartUp;
    }
    
    public static List getDataFormatsMapping () {
    	return _dataFormatsMappingList;
    }
    
    public static DataFormatMapping getDataFormatMapping(String name) {
    	Iterator it = _dataFormatsMappingList.iterator();
		while (it.hasNext()) {
			DataFormatMapping cur = (DataFormatMapping) it.next();
			if (cur.getName().equals(name)) {
				return cur;
			}
		}
		return null;
    }


    private static void buildDefaultNodeTypes() {
    	///@todo no need to create default shapes if we don't use them
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

        CONCEPT_TYPE = new NodeTypeImpl();
        nodesConfig.put(CONCEPT_TYPE, new NodeTypeDisplayInfo("concept", conceptShape, false, Color.BLUE, Color.RED));
        RELATION_TYPE = new NodeTypeImpl(); 
        nodesConfig.put(RELATION_TYPE, new NodeTypeDisplayInfo("relation", relationShape, false, Color.GREEN, Color.YELLOW));
        UNKNOWN_TYPE = new NodeTypeImpl();
        nodesConfig.put(UNKNOWN_TYPE, new NodeTypeDisplayInfo("unknown", unknownShape, false, Color.WHITE, Color.BLACK));
    }

    public static NodeTypeDisplayInfo getNodeTypeDisplayInfo (NodeType nodeType) {
        return (NodeTypeDisplayInfo) nodesConfig.get(nodeType);
    }

	public static void activateBackend (Backend backend) {
		/// @todo should have some more functionality: closing off previously active backend, 
		/// perhaps saving data, etc.
		OntoramaConfig._activeBackend = backend;
	}

	public static Backend getBackend () {
		return OntoramaConfig._activeBackend;
	}

	public static Backend instantiateBackend(String backendName, Frame parentFrame) {
		try {
			Backend backend = (Backend) Class.forName(backendName).newInstance();
			OntoramaConfig.activateBackend(backend);
			System.out.println("OntoramaConfig::instantiateBackend: " + backend);
			return backend;
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		    new ErrorPopupMessage(
		        "Couldn't find class for backendName " + backendName,
		        parentFrame);
		} catch (InstantiationException instExc) {
		    instExc.printStackTrace();
		    new ErrorPopupMessage(
		        "Couldn't instantiate backendName " + backendName,
		        parentFrame);
		} catch (IllegalAccessException illegalAccExc) {
		    illegalAccExc.printStackTrace();
		    new ErrorPopupMessage(
		        "Couldn't load backend "
		            + backendName
		            + " (Illegal Access Exception)",
		        parentFrame);
		} catch (Exception e) {
		    e.printStackTrace();
		    new ErrorPopupMessage(
		        "Couldn't load backend "
		            + backendName
		            + ": "
		            + e.getMessage(),
		        parentFrame);
		}
		return null;
	}
}

