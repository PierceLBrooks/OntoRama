package ontorama.webkbtools.inputsource;

import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.net.URL;
//import java.net.URLConnection;
import java.util.zip.*;

import ontorama.OntoramaConfig;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.util.SourceException;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) DSTC 2002</p>
 * <p>Company: DSTC</p>
 * @version 1.0
 */


public class JarSource implements Source {

    /**
     *  Get a Reader from a given location in a jar file.
     *  @param  relativePath  path relative to ClassLoader
     *  @return reader
     *  @throws Exception
     */
    public Reader getReader (String relativePath, Query query) throws SourceException {
        if (OntoramaConfig.DEBUG) {
            System.out.println ("relativePath = " + relativePath);
        }
        if (OntoramaConfig.VERBOSE) {
          System.out.println ("class JarSource relativePath = " + relativePath);
        }
        InputStreamReader reader = null;

//        try {
          //InputStream stream = OntoramaConfig.getInputStreamFromResource(OntoramaConfig.getClassLoader(),relativePath);
          //InputStream stream = OntoramaConfig.getInputStreamFromResource(relativePath);
          InputStream stream = getInputStreamFromResource(relativePath);

          reader = new InputStreamReader(stream);
//        }
//        catch (IOException e) {
//          throw new SourceException("Couldn't read input data source for " + relativePath + ", error: " + e.getMessage());
//        }

        return reader;
    }


    /**
    *
    * @todo	need an exception for an unknown protocol
    * @todo       maybe there is a better way to handle that hack with stripping off protocol and "://" from url
    */
    //public static InputStream getInputStreamFromResource (ClassLoader classLoader,
    public InputStream getInputStreamFromResource (String resourceName)
                                               throws SourceException {

          InputStream resultStream = null;

      try {
          if (OntoramaConfig.VERBOSE) {
            System.out.println("resourceName = " + resourceName);
          }
          //URL url = OntoramaConfig.classLoader.getResource(resourceName);
          URL url = OntoramaConfig.getClassLoader().getResource(resourceName);
          if (OntoramaConfig.VERBOSE) {
            System.out.println("url = " + url);
          }

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
        }
        catch (IOException ioExc) {
          throw new SourceException("Couldn't read input data source for " + resourceName + ", error: " + ioExc.getMessage());
        }
        catch (NullPointerException npe) {
          throw new SourceException("Coudn't load resource for " + resourceName + ", please check if resource exists at this location");
        }
        return resultStream;
    }


}