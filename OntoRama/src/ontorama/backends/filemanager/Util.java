package ontorama.backends.filemanager;

import java.io.File;

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
}
