package ontorama.webkbtools.inputsource;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.util.CancelledQueryException;
import ontorama.webkbtools.util.SourceException;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 23, 2002
 * Time: 11:26:30 AM
 * To change this template use Options | File Templates.
 */
public class FileSource implements Source {

    /**
     *
     * @param filename
     * @param query
     * @return
     * @throws SourceException
     * @throws CancelledQueryException
     * @todo implement if needed: throw CancelledQueryException
     */
    public SourceResult getSourceResult(String filename, Query query) throws SourceException, CancelledQueryException {
        SourceResult sourceResult;
        try {
            Reader reader = new FileReader(filename);
            sourceResult = new SourceResult(true, reader, query);

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new SourceException("Couldn't find file " + filename + ",\n error: " + e.getMessage());
        }
        return sourceResult;
    }
}
