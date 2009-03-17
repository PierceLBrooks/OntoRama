package ontorama.backends.filemanager;

@SuppressWarnings("serial")
public class ParserNotSpecifiedException extends Exception {
	
	public ParserNotSpecifiedException(String fileExtention) {
		super("Parser is not specified for file extention " 
				+ fileExtention + " in dataFormatsMapping");
	}


}
