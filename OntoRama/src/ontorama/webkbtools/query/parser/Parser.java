package ontorama.webkbtools.query.parser;

import java.io.Reader;
import java.security.AccessControlException;

import ontorama.webkbtools.util.ParserException;

public interface Parser {

    public ParserResult getResult (Reader reader) throws ParserException, AccessControlException ;

}



