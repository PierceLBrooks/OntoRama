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
	
	private static DataFormatMapping getMappingForExtension (List dataFormatsMapping, String extension) {
		Iterator it = dataFormatsMapping.iterator();
		while (it.hasNext()) {
			DataFormatMapping element = (DataFormatMapping) it.next();
			if (element.getFileExtention().equals(extension)) {
				return element;
			}
		}
		return null;
	}
	
	public static DataFormatMapping getMappingForFile (List dataFormatsMapping, File file) {
		String filename = file.getAbsolutePath();
		System.out.println("Util::getMappingForFile file = " + filename);
		String extension = Util.getExtension(file);
		DataFormatMapping mapping = Util.getMappingForExtension(dataFormatsMapping,extension);
		System.out.println("Util::getMappingForFile, mapping = " + mapping);
		return mapping;
	}

	
}
