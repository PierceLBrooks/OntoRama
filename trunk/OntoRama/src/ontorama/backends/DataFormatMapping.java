package ontorama.backends;

/**
 * This class is responsible for mapping file types to corresponding writers,
 * readers, file extentions, etc.
 * @author nataliya
 */
public class DataFormatMapping {
	
	/** 
	 * name of this mapping
	 */
	private String _name;
	
	/**
	 * corresponding file extention.
	 */
	private String _fileExtention;
	
	/**
	 * Fully qualified parser name
	 * note: using names for now because QueryEngine expects it.
	 *	 */
	private String _parserName;

	/**
	 * Fully qualified writer name
	 * note: using names for now because QueryEngine expects it.
	 */
	private String _writerName;
	

	/**
	 * Constructor for DataFormatMapping.
	 */
	public DataFormatMapping(String name, String fileExtention, String parserName, String writerName) {
		_name = name;
		_fileExtention = fileExtention;
		_parserName = parserName;
		_writerName = writerName;
	}

	/**
	 * Returns the fileExtention.
	 * @return String
	 */
	public String getFileExtention() {
		return _fileExtention;
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Returns the parserName.
	 * @return String
	 */
	public String getParserName() {
		return _parserName;
	}

	/**
	 * Returns the writerName.
	 * @return String
	 */
	public String getWriterName() {
		return _writerName;
	}
	
}
