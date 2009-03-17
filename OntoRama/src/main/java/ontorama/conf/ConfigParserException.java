package ontorama.conf;

@SuppressWarnings("serial")
public class ConfigParserException extends Exception {

    public ConfigParserException(String message) {
        super(message);
    }

	public ConfigParserException(String message, Throwable cause) {
		super(message, cause);
	}
    
}