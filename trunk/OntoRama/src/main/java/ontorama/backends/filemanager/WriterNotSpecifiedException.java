package ontorama.backends.filemanager;

@SuppressWarnings("serial")
public class WriterNotSpecifiedException extends Exception {
	
	public WriterNotSpecifiedException(String fileExtention) {
		super("Writer is not specified for file extention " 
				+ fileExtention + " in dataFormatsMapping");
	}
}
