package ontorama.ontotools.parser;

import java.io.Reader;
import java.security.AccessControlException;

import ontorama.ontotools.ParserException;

public interface Parser {

    public ParserResult getResult (Reader reader) throws ParserException, AccessControlException ;

}



