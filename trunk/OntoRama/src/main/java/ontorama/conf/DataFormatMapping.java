package ontorama.conf;

import ontorama.ontotools.parser.Parser;

/**
 * This class is responsible for mapping file types to corresponding writers,
 * readers, file extensions, etc.
 */
public class DataFormatMapping {
	
	/** 
	 * Name of this mapping
	 */
	private String name;
	
	/**
	 * Corresponding file extension.
	 */
	private String fileExtension;
	
	private Parser parser;

	/**
	 * Fully qualified writer name
	 * note: using names for now because QueryEngine expects it.
	 */
	private String _writerName;
	
	public DataFormatMapping(String name, String fileExtension, Parser parser) {
		this.name = name;
		this.fileExtension = fileExtension;
		this.parser = parser;
	}
	
	public String getFileExtension() {
		return fileExtension;
	}

	public String getName() {
		return name;
	}
	
	public Parser getParser() {
		return parser;
	}

	public String getWriterName() {
		return _writerName;
	}
	
	public void setWriterName(String writerName) {
		_writerName = writerName;
	}
	
	public String toString() {
		String str = "DataFormatMapping: name = " + name + ", extension = " + fileExtension + ", parser = " + parser.getClass().getName();
		if (_writerName != null) {
			str = str + ", writer = " + _writerName;
		}
		return str;
	}
}
