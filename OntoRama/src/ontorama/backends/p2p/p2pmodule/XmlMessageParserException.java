package ontorama.backends.p2p.p2pmodule;

/**
 * 
 * @author nataliya
 * Created on 10/03/2003
 */
public class XmlMessageParserException extends Exception {

	public XmlMessageParserException(String message) {
		super(message);
	}

	public XmlMessageParserException(Throwable exception) {
		super(exception);
		exception.fillInStackTrace();
	}

}
