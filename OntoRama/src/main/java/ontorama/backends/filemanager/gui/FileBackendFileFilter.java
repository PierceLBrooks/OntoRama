package ontorama.backends.filemanager.gui;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.filechooser.FileFilter;

import ontorama.backends.filemanager.Util;
import ontorama.conf.DataFormatMapping;

/**
 * @author nataliya
 */
public class FileBackendFileFilter extends FileFilter {
	
	private final String _filterDescription = "Ontorama readable files";
	
	private final Collection<DataFormatMapping>  _dataFormatsMapping;
	
	
	/**
	 * Constructor for FileBackendFileFilter.
	 */
	public FileBackendFileFilter(Collection<DataFormatMapping> dataFormatsMapping) {
		_dataFormatsMapping = dataFormatsMapping;
	}

	/**
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
    public boolean accept(File file) {
		
		if (file.isDirectory()) {
			return true;
		}
		
		String extension = Util.getExtension(file);
		if (extension == null) {
			return false;
		}
		
		Iterator<DataFormatMapping> it = _dataFormatsMapping.iterator();
		while (it.hasNext()) {
			DataFormatMapping mapping = it.next();
			String fileExtension = mapping.getFileExtension();
			if (extension.equals(fileExtension)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
    public String getDescription() {
		return _filterDescription;
	}


}
