package ontorama.ontotools.source;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import ontorama.OntoramaConfig;
import ontorama.ontotools.CancelledQueryException;
import ontorama.ontotools.SourceException;
import ontorama.ontotools.query.Query;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) DSTC 2002</p>
 * <p>Company: DSTC</p>
 * @version 1.0
 */


public class JarSource implements Source {
    /**
     *  Get a SourceResult from a given location in a jar file.
     *  This will work for any location relative to Class Loader.
     *  @param  relativePath  path relative to ClassLoader
     *  @return SourceResult
     *  @throws SourceException
     *
     * @todo implement if needed: throw CancelledQueryException
     */
    public SourceResult getSourceResult(String relativePath, Query query) throws SourceException, CancelledQueryException {
        Reader reader = getReader(relativePath, query);
        return new SourceResult(true, reader, null);
    }

    /**
     *  Get a Reader from a given location in a jar file.
     *  @param  relativePath  path relative to ClassLoader
     *  @return reader
     *  @throws SourceException
     */
    private Reader getReader(String relativePath, Query query) throws SourceException {
        if (OntoramaConfig.DEBUG) {
            System.out.println("relativePath = " + relativePath);
        }
        if (OntoramaConfig.VERBOSE) {
            System.out.println("class JarSource relativePath = " + relativePath);
        }
        InputStream stream = getInputStreamFromResource(relativePath);
        InputStreamReader reader = new InputStreamReader(stream);

        return reader;
    }


    /**
     *
     * @todo	need an exception for an unknown protocol
     * @todo       maybe there is a better way to handle that hack with stripping off protocol and "://" from url
     */
    //public static InputStream getInputStreamFromResource (ClassLoader classLoader,
    public InputStream getInputStreamFromResource(String resourceName)
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
                String filePath = pathString.substring(0, index);
                // a hack: strip string 'protocol:/" from the path
                if (filePath.startsWith("file")) {
                    int index1 = pathString.indexOf(":") + 1;
                    filePath = filePath.substring(index1, filePath.length());
                }

                //System.out.println("filePath = " + filePath);
                File file = new File(filePath);
                ZipFile zipFile = new ZipFile(file);
                ZipEntry zipEntry = zipFile.getEntry(resourceName);
                resultStream = (InputStream) zipFile.getInputStream(zipEntry);
            } else if (url.getProtocol().equalsIgnoreCase("file")) {
                resultStream = url.openStream();
            } else {
                System.err.println("Dont' know about this protocol: " + url.getProtocol());
                System.exit(-1);
            }
        } catch (IOException ioExc) {
            throw new SourceException("Couldn't read input data source for " + resourceName + ", error: " + ioExc.getMessage());
        } catch (NullPointerException npe) {
            throw new SourceException("Coudn't load resource for " + resourceName + ", please check if resource exists at this location");
        }
        return resultStream;
    }


}