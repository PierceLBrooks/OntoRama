package ontorama.ontotools.source;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import ontorama.OntoramaConfig;
import ontorama.ontotools.CancelledQueryException;
import ontorama.ontotools.SourceException;
import ontorama.ontotools.query.Query;
import ontorama.ui.ErrorDialog;
import ontorama.ui.OntoRamaApp;

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
		InputStream stream = getInputStreamFromResource(relativePath);
		InputStreamReader reader = new InputStreamReader(stream);
        return new SourceResult(true, reader, null);
    }

    /**
     *
     * @todo	need an exception for an unknown protocol
     * @todo       maybe there is a better way to handle that hack with stripping off protocol and "://" from url
     */
    public InputStream getInputStreamFromResource(String resourceName) throws SourceException {
        try {
            URL url = OntoramaConfig.getClassLoader().getResource(resourceName);

            if (url.getProtocol().equalsIgnoreCase("jar")) {
                String pathString = url.getFile();
                int index = pathString.indexOf("!");
                String filePath = pathString.substring(0, index);
                // a hack: strip string 'protocol:/" from the path
                if (filePath.startsWith("file")) {
                    int index1 = pathString.indexOf(":") + 1;
                    filePath = filePath.substring(index1, filePath.length());
                }
                File file = new File(filePath);
                ZipFile zipFile = new ZipFile(file);
                ZipEntry zipEntry = zipFile.getEntry(resourceName);
                return (InputStream) zipFile.getInputStream(zipEntry);
            } else if (url.getProtocol().equalsIgnoreCase("file")) {
                return url.openStream();
            } else {
            	String errMessage = "Dont' know about this protocol: " + url.getProtocol(); 
                System.err.println(errMessage);
                ErrorDialog.showError(OntoRamaApp.getMainFrame(), "Error reading source", errMessage);
                System.exit(-1);
            }
        } catch (IOException ioExc) {
            throw new SourceException("Couldn't read input data source for " + resourceName + ", error: " + ioExc.getMessage(), ioExc);
        } catch (NullPointerException npe) {
            throw new SourceException("Coudn't load resource for " + resourceName + ", please check if resource exists at this location", npe);
        }
        return null;
    }


}