package ontorama.ontotools.source;

import ontorama.ontotools.query.Query;
import ontorama.ontotools.SourceException;
import ontorama.ontotools.CancelledQueryException;

import java.io.Reader;
import java.io.StringReader;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 16, 2002
 * Time: 12:56:40 PM
 * To change this template use Options | File Templates.
 * @todo this class is here for one purpose - to be used from p2p backend.
 */
public class StringSource implements Source {
    /**
     *
     * @param textString
     * @param query
     * @return
     * @throws SourceException
     * @throws CancelledQueryException
     */
    public SourceResult getSourceResult(String textString, Query query) throws SourceException, CancelledQueryException {
        Reader reader = new StringReader(textString);
        return new SourceResult(true, reader, query);
    }
}
