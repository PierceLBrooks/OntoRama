package ontorama.webkbtools.query.parser;

import ontorama.webkbtools.util.ParserException;

import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.security.AccessControlException;

public interface Parser {
//    /**
//     * get an Iterator of OntologyTypes from the given stream
//     * @param	reader
//     * @return	Iterator of OntologyTypes
//     * @throws  ParserException
//     */
//    public Iterator getOntologyTypeIterator(Reader reader) throws ParserException;
//
//    public Collection getOntologyTypeCollection(Reader reader) throws ParserException;

    public ParserResult getResult (Reader reader) throws ParserException, AccessControlException ;

}



