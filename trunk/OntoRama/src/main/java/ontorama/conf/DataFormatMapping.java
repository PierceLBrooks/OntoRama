package ontorama.conf;

import ontorama.ontotools.parser.Parser;
import ontorama.ontotools.writer.ModelWriter;

/**
 * This class is responsible for mapping file types to corresponding writers,
 * readers, file extensions, etc.
 */
public class DataFormatMapping {
	
	/** 
	 * Name of this mapping
	 */
	private final String name;
	
	/**
	 * Corresponding file extension.
	 */
	private final String fileExtension;
	
	private final Parser parser;

	private ModelWriter _writer;
	
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

	public ModelWriter getWriter() {
		return _writer;
	}
	
	public void setWriter(ModelWriter writer) {
		_writer = writer;
	}
	
	@Override
	public String toString() {
		String str = "DataFormatMapping: name = " + name + ", extension = " + fileExtension + ", parser = " + parser.getClass().getName();
		if (_writer != null) {
			str = str + ", writer = " + _writer.getClass().getName();
		}
		return str;
	}
}
