package ontorama.webkbtools.query.parser;

import java.util.Iterator;
import java.util.Collection;
import java.io.Reader;

import ontorama.webkbtools.util.ParserException;

public interface Parser {
    /**
     * get an Iterator of OntologyTypes from the given stream
     * @param	reader
     * @return	Iterator of OntologyTypes
     * @throws  ParserException
     */
    public Iterator getOntologyTypeIterator (Reader reader)throws ParserException;

    public Collection getOntologyTypeCollection (Reader reader)throws ParserException;

}



