package ontorama.ontotools.source;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import ontorama.ontotools.CancelledQueryException;
import ontorama.ontotools.SourceException;
import ontorama.ontotools.query.Query;

public class FileSource implements Source {

    /*
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
            throw new SourceException("Couldn't find file " + filename + ",\n error: " + e.getMessage(), e);
        }
        return sourceResult;
    }
}
