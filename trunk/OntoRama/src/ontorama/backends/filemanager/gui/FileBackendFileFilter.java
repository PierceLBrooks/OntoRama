package ontorama.backends.filemanager.gui;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.filechooser.FileFilter;

import ontorama.backends.DataFormatMapping;
import ontorama.backends.filemanager.FileBackend;
import ontorama.backends.filemanager.Util;

/**
 * @author nataliya
 */
public class FileBackendFileFilter extends FileFilter {
	
	private String _filterDescription = "Ontorama readable files";
	
	private Collection  _dataFormatsMapping;
	
	
	/**
	 * Constructor for FileBackendFileFilter.
	 */
	public FileBackendFileFilter(FileBackend fileBackend) {
		_dataFormatsMapping = fileBackend.getDataFormats();
	}

	/**
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File file) {
		
		if (file.isDirectory()) {
			return true;
		}
		
		String extension = Util.getExtension(file);
		if (extension == null) {
			return false;
		}
		
		Iterator it = _dataFormatsMapping.iterator();
		while (it.hasNext()) {
			DataFormatMapping mapping = (DataFormatMapping) it.next();
			String fileExtension = mapping.getFileExtention();
			if (extension.equals(fileExtension)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription() {
		return _filterDescription;
	}


}
