

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
     private static ClassLoader cl;


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
            cl = curClass.getClassLoader();
        }
        catch (ClassNotFoundException classException ) {
            System.err.println("ClassNotFoundException : " + classException);
            System.exit(-1);
        }


        Properties properties = new Properties();
		
		propertiesFileLocation = cl.getResource("ontorama.properties");
    	xmlConfigFileLocation = cl.getResource("config.xml");
		
        //Properties properties = new Properties(System.getProperties());
        try {
          //FileInputStream propertiesFileIn = new FileInputStream (propertiesFileLocation);
		  
		  //InputStream propertiesFileIn = propertiesFileLocation.openConnection().getInputStream();
		  InputStream propertiesFileIn = getInputStreamFromResource(cl,"ontorama.properties");

          properties.load(propertiesFileIn);

          sourceUri = properties.getProperty("sourceUri");
          ontologyRoot = properties.getProperty("ontologyRoot");
          queryOutputFormat = properties.getProperty("queryOutputFormat");
          String parserPackagePathSuffix = properties.getProperty ("parserPackagePathSuffix");
          String sourcePackagePathSuffix = properties.getProperty ("sourcePackagePathSuffix");
          DEBUG = (new Boolean ( properties.getProperty("DEBUG"))).booleanValue();

          parserPackageName = parserPackagePathPrefix + "." + parserPackagePathSuffix;
          sourcePackageName = sourcePackagePathPrefix + "." + sourcePackagePathSuffix;
		  
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
			//InputStream configInStream = cl.getResourceAsStream("config.xml");
			InputStream configInStream = getInputStreamFromResource(cl,"config.xml");
			//System.out.println("input stream = " + configInStream.getClass());
			
			XmlConfigParser xmlConfig = new XmlConfigParser(configInStream);
            allRelationsArray = xmlConfig.getRelationLinksArray();
            MAXTYPELINK = allRelationsArray.length;
            relationLinksSet = buildRelationLinksSet (allRelationsArray);
            relationRdfMapping = xmlConfig.getRelationRdfMappingList();

            conceptPropertiesDetails = xmlConfig.getConceptPropertiesTable();
            conceptPropertiesRdfMapping = xmlConfig.getConceptPropertiesRdfMappingTable();
            //xmlConfig.printConceptPropertiesRdfMapping();
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
        System.out.println("---------config--------------");
        System.out.println("sourceUri = " + sourceUri);
        System.out.println("ontologyRoot = " + ontologyRoot);
        System.out.println("queryOutputFormat = " + queryOutputFormat);
        System.out.println("DEBUG = " + DEBUG);
        System.out.println("parserPackageName = " + parserPackageName);
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
	  * @todo	need an exception for an unknown protocol
	  */
	 private static InputStream getInputStreamFromResource (ClassLoader cl,
	 											String resourceName)  
												throws IOException {
	 	InputStream resultStream = null;
		String className = cl.getResourceAsStream(resourceName).getClass().getName();
		System.out.println("getResourceAsStream class name = " + className);
		String className2 = cl.getResource(resourceName).getClass().getName();
		System.out.println("getResource class name = " + className2);	
		
		Enumeration e = cl.getResources(resourceName);
		while (e.hasMoreElements() ) {
			Object obj = e.nextElement();
			System.out.println("---obj class name = " + obj.getClass().getName());
			URL url = (URL) obj;
			System.out.println("url = " + url.toString());
			System.out.println("protocol = " + url.getProtocol());
			if (url.getProtocol().equalsIgnoreCase("jar")) {
				System.out.println("found JAR");
				System.out.println("path = " + url.getPath());
				System.out.println("file = " + url.getFile());
				System.out.println("content = " + url.getContent());
				Object content = url.getContent();
				System.out.println("content class: " + content.getClass().getName());
				
				//ZipFile zipFile = (ZipFile) content;
				String pathString = url.getPath();
				int index = pathString.indexOf("!");
				String filePath = pathString.substring(0,index);
				// a hack: strip string 'protocol:/" from the path
				if (filePath.startsWith("file")) {
					int index1 = pathString.indexOf(":") + 1;
					filePath = filePath.substring(index1, filePath.length());
				}
				
				System.out.println("filePath = " + filePath);
				File file = new File(filePath);
				System.out.println("file absolute path = " + file.getAbsolutePath());
				ZipFile zipFile = new ZipFile (file);
				
				//Enumeration en = zipFile.entries();
				//while (en.hasMoreElements()) {
				//	System.out.println("next entry = " + en.nextElement());
				//}
				
				ZipEntry zipEntry = zipFile.getEntry(resourceName);
				//System.out.println("zip entry for resourceName = " + resourceName + ", " + zipEntry);
				
				resultStream = (InputStream) zipFile.getInputStream(zipEntry);
				System.out.println("resultStream class name = " + resultStream.getClass().getName());
				zipFile.close();

			}
			else if (url.getProtocol().equalsIgnoreCase("file")) {
				System.out.println("found FILE");
				resultStream = url.openStream();
			}
			else {
				System.err.println("Dont' know about this protocol: "
				+ url.getProtocol());
				System.exit(-1);
			}
		}
/*
		if (className.startsWith("ZipFile")) {
			ZipFile zipFile = (ZipFile) cl.getResourceAsStream(resourceName);
			//ZipInputStream zipStream = (ZipInputStream) cl.getResourceAsStream(resourceName);
			System.out.println("clas name = " + zipFile.getClass().getName());
			//ZipEntry zipEntry = zipFile.getEntry(resourceName);
			//resultStream = zipFile.getInputStream(zipFile.getEntry(resourceName));
		}
		else {
			resultStream = cl.getResourceAsStream(resourceName);
		}
		*/
		return resultStream;
	 }
}

