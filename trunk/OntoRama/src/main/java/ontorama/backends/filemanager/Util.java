package ontorama.backends.filemanager;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import ontorama.conf.DataFormatMapping;

/**
 * @author nataliya
 */
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
			if (element.getFileExtention().equals(extension)) {
				return element;
			}
		}
		return null;
	}
	
	
	public static String getParserForFile (List<DataFormatMapping> dataFormatsMapping, File file) 
								throws ParserNotSpecifiedException {
		String extention = Util.getExtension(file);
		DataFormatMapping mapping = Util.getMappingForExtension(dataFormatsMapping,extention);
		if (mapping == null) {
			throw new ParserNotSpecifiedException(extention);
		}
		if (mapping.getParserName() == null) {
			throw new ParserNotSpecifiedException(extention);
		}
		return mapping.getParserName();
	}
	

	public static String getWriterForFile (List<DataFormatMapping> dataFormatsMapping, File file) 
								throws WriterNotSpecifiedException {
		String extention = Util.getExtension(file);
		DataFormatMapping mapping = Util.getMappingForExtension(dataFormatsMapping,extention);
		if (mapping == null) {
			throw new WriterNotSpecifiedException(extention);
		}
		if (mapping.getWriterName() == null) {
			throw new WriterNotSpecifiedException(extention);
		}
		return mapping.getWriterName();
	}
	
}
