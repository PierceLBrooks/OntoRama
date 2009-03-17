package ontorama;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import ontorama.backends.Backend;
import ontorama.conf.ConfigParserException;
import ontorama.conf.DataFormatConfigParser;
import ontorama.conf.DataFormatMapping;
import ontorama.conf.EdgeTypeDisplayInfo;
import ontorama.conf.NodeTypeDisplayInfo;
import ontorama.conf.RdfMapping;
import ontorama.conf.XmlConfigParser;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.NodeType;
import ontorama.model.graph.NodeTypeImpl;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.SourceException;
import ontorama.ontotools.source.JarSource;
import ontorama.ui.ErrorDialog;


public class OntoramaConfig {


    private static Map<EdgeType, EdgeTypeDisplayInfo> edgesConfig;
    private static List<EdgeType> edgeTypesList;
       
    private static List<RdfMapping> relationRdfMapping;

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
	 * flag to determine whether or not we are running in restricted environment
	 * (java webstart for example).
	 */
	public static boolean SECURITY_RESTRICTED = false;
	
    public static JarSource streamReader = new JarSource();

    private static Properties properties = new Properties();

    private static boolean loadBlankOnStartUp = false;

    private static Map<NodeType, NodeTypeDisplayInfo> nodesConfig = new HashMap<NodeType, NodeTypeDisplayInfo>();
    
    public static NodeType CONCEPT_TYPE;
    public static NodeType RELATION_TYPE;
    public static NodeType UNKNOWN_TYPE;
    
    
    public static String examplesConfigLocation = "examplesConfig.xml";
    
    public static String defaultBackend = "ontorama.backends.examplesmanager.ExamplesBackend";

	private static String backendName;
	
	/**
	 * current backend. Static so we can have static methods that use this var.
	 */
	private static Backend _activeBackend;
	
	private static List<DataFormatMapping> _dataFormatsMappingList;

    /**
     * Values of vars that are set here should be read from
     * java properties file.
     */
    static {
        buildDefaultNodeTypes();
        
        loadAllConfig(examplesConfigLocation, "ontorama.properties", "config.xml");
        
    	try {
    		System.getProperties();
    	}
    	catch (AccessControlException e) {
    		OntoramaConfig.SECURITY_RESTRICTED = true;
    	}
    }

    private static void fatalExit(String message, Exception e) {
    	ErrorDialog.showError(null, e, "Error", message);
        System.err.println("Exception: " + e);
        e.printStackTrace();
        System.exit(1);
    }

    public static void loadAllConfig(String examplesConfigLocation,
                                     String propertiesFileLocation, String configFileLocation) {

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
        VERBOSE = (new Boolean(properties.getProperty("VERBOSE"))).booleanValue();
        FOUNTAINS = (new Boolean(properties.getProperty("FOUNTAINS"))).booleanValue();
		EDIT_ENABLED = (new Boolean(properties.getProperty("EDIT_ENABLED"))).booleanValue();
        loadBlankOnStartUp = (new Boolean(properties.getProperty("loadBlankOnStartUp"))).booleanValue();
        backendName = properties.getProperty("backend");
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
        EdgeTypeDisplayInfo displayInfo = edgesConfig.get(edgeType);
        return displayInfo;
    }


    public static Set<EdgeType> getEdgeTypesSet() {
        List<EdgeType> allRelations = getEdgeTypesList();
        return new HashSet<EdgeType>(allRelations);
    }

    /**
     * Find an edge type that corresponds to given edgeName.
     * Basically we match name of edge type (both directions: 
     * forward and reverse) to given edgeName. After that
     * it is responsibility of method user to figure out
     * which direction they want to use.
     */
    public static EdgeType getEdgeType(String edgeName) throws NoSuchRelationLinkException {
        EdgeType result = null;
        Iterator<EdgeType> it = edgesConfig.keySet().iterator();
        while (it.hasNext()) {
            EdgeType edgeType = it.next();
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

    public static List<RdfMapping> getRelationRdfMapping() {
        return relationRdfMapping;
    }


    public static List<EdgeType> getEdgeTypesList() {
        return OntoramaConfig.edgeTypesList;
    }


    public static ClassLoader getClassLoader() {
        return OntoramaConfig.getClassLoader();
    }

    public static String getBackendPackageName () {
        return backendName;
    }

    public static boolean loadBlank () {
        return loadBlankOnStartUp;
    }
    
    public static List<DataFormatMapping> getDataFormatsMapping () {
    	return _dataFormatsMappingList;
    }
    
    public static DataFormatMapping getDataFormatMapping(String name) {
    	Iterator<DataFormatMapping> it = _dataFormatsMappingList.iterator();
		while (it.hasNext()) {
			DataFormatMapping cur = it.next();
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

        CONCEPT_TYPE = new NodeTypeImpl("CONCEPT_TYPE");
        nodesConfig.put(CONCEPT_TYPE, new NodeTypeDisplayInfo("concept", conceptShape, false, Color.BLUE, Color.RED));
        RELATION_TYPE = new NodeTypeImpl("RELATION_TYPE"); 
        nodesConfig.put(RELATION_TYPE, new NodeTypeDisplayInfo("relation", relationShape, false, Color.GREEN, Color.YELLOW));
        UNKNOWN_TYPE = new NodeTypeImpl("UNKNOWN_TYPE");
        nodesConfig.put(UNKNOWN_TYPE, new NodeTypeDisplayInfo("unknown", unknownShape, false, Color.WHITE, Color.BLACK));
    }

    public static NodeTypeDisplayInfo getNodeTypeDisplayInfo (NodeType nodeType) {
        return nodesConfig.get(nodeType);
    }


	public static Backend getBackend () {
		return OntoramaConfig._activeBackend;
	}

	public static Backend instantiateBackend(String backendName, Frame parentFrame) {
		try {
			Backend backend = (Backend) Class.forName(backendName).newInstance();
			OntoramaConfig._activeBackend = backend;
			return backend;
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		    ErrorDialog.showError(parentFrame, e, "Error instantiating Backend", "Couldn't find class for backendName " + backendName);
		} catch (InstantiationException instExc) {
		    instExc.printStackTrace();
			ErrorDialog.showError(parentFrame, instExc, "Error instantiating Backend", "Couldn't instantiate backendName " + backendName);
		} catch (IllegalAccessException illegalAccExc) {
		    illegalAccExc.printStackTrace();
			ErrorDialog.showError(parentFrame, illegalAccExc, "Error loading Backend", "Couldn't load backend " + backendName);
		} catch (Exception e) {
		    e.printStackTrace();
			ErrorDialog.showError(parentFrame, e, "Error loading Backend", "Couldn't load backend " + backendName);
		}
		return null;
	}
	
	public static Collection<NodeType> getNodeTypesCollection () {
		Collection<NodeType> res = new HashSet<NodeType>();
		res.add(OntoramaConfig.CONCEPT_TYPE);
		res.add(OntoramaConfig.RELATION_TYPE);
		res.add(OntoramaConfig.UNKNOWN_TYPE);
		return res;
	} 
}

