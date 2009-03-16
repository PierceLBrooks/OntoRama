package ontorama.backends.filemanager;

/**
 * @author nataliya
 * Created on 26/03/2003
 *
 */
public class WriterNotSpecifiedException extends Exception {
	
	public WriterNotSpecifiedException(String fileExtention) {
		super("Writer is not specified for file extention " 
				+ fileExtention + " in dataFormatsMapping");
	}


}
