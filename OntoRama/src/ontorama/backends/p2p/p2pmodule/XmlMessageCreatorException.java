package ontorama.backends.p2p.p2pmodule;

/**
 * 
 * @author nataliya
 * Created on 10/03/2003
 */
public class XmlMessageCreatorException extends Exception {

	public XmlMessageCreatorException(String message) {
		super(message);
	}

	public XmlMessageCreatorException(Throwable exception) {
		super(exception);
		exception.fillInStackTrace();
	}

}
