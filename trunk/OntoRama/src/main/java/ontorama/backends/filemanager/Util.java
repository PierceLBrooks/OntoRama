package ontorama.backends.filemanager;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import ontorama.conf.DataFormatMapping;
import ontorama.ontotools.parser.Parser;

public class Util {

	/**
	 * Get the extension of a file.
	 */
	public static String getExtension(File f) {
		return getExtension(f.getName());
	}
	
	public static String getExtension (String filename) {
		String ext = null;
		int i = filename.lastIndexOf('.');

		if (i > 0 &&  i < filename.length() - 1) {
			ext = filename.substring(i+1).toLowerCase();
		}
		return ext;
	}
	
	private static DataFormatMapping getMappingForExtension (List<DataFormatMapping> dataFormatsMapping, String extension) {
		Iterator<DataFormatMapping> it = dataFormatsMapping.iterator();
		while (it.hasNext()) {
			DataFormatMapping element = it.next();
			if (element.getFileExtension().equals(extension)) {
				return element;
			}
		}
		return null;
	}
	
	public static Parser getParserForFile (List<DataFormatMapping> dataFormatsMapping, File file) 
								throws ParserNotSpecifiedException {
		String extension = Util.getExtension(file);
		DataFormatMapping mapping = Util.getMappingForExtension(dataFormatsMapping,extension);
		if (mapping == null) {
			throw new ParserNotSpecifiedException(extension);
		}
		if (mapping.getParser() == null) {
			throw new ParserNotSpecifiedException(extension);
		}
		return mapping.getParser();
	}
	
	public static String getWriterForFile (List<DataFormatMapping> dataFormatsMapping, File file) 
								throws WriterNotSpecifiedException {
		String extension = Util.getExtension(file);
		DataFormatMapping mapping = Util.getMappingForExtension(
				dataFormatsMapping, extension);
		if (mapping == null) {
			throw new WriterNotSpecifiedException(extension);
		}
		if (mapping.getWriterName() == null) {
			throw new WriterNotSpecifiedException(extension);
		}
		return mapping.getWriterName();
	}
}
