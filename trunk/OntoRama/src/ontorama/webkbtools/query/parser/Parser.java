package ontorama.webkbtools.query.parser;

import ontorama.webkbtools.util.ParserException;

import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.security.AccessControlException;

public interface Parser {

    public ParserResult getResult (Reader reader) throws ParserException, AccessControlException ;

}



