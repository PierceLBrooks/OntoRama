package ontorama.webkbtools.inputsource;

import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
//import java.net.URL;
//import java.net.URLConnection;

import ontorama.OntoramaConfig;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) DSTC 2002</p>
 * <p>Company: DSTC</p>
 *  unascribed
 * @version 1.0
 */


public class JarSource implements Source {

    /**
     *  Get a Reader from a given location in a jar file.
     *  @param  relativePath  path relative to ClassLoader
     *  @return reader
     *  @throws Exception
     */
    public Reader getReader (String relativePath) throws Exception {
        if (OntoramaConfig.DEBUG) {
            System.out.println ("relativePath = " + relativePath);
        }
        if (OntoramaConfig.VERBOSE) {
          System.out.println ("class JarSource relativePath = " + relativePath);
        }
        //InputStream stream = OntoramaConfig.getInputStreamFromResource(OntoramaConfig.getClassLoader(),relativePath);
        InputStream stream = OntoramaConfig.getInputStreamFromResource(relativePath);

        InputStreamReader reader = new InputStreamReader(stream);

        return reader;
    }

}