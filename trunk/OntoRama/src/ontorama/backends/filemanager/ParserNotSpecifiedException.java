package ontorama.backends.filemanager;

/**
 * @author nataliya
 * Created on 26/03/2003
 *
 */
public class ParserNotSpecifiedException extends Exception {
	
	public ParserNotSpecifiedException(String fileExtention) {
		super("Parser is not specified for file extention " 
				+ fileExtention + " in dataFormatsMapping");
	}


}
