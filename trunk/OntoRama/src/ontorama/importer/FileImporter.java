package ontorama.importer;

import java.io.File;
import javax.swing.JFileChooser;

import org.tockit.events.EventBroker;

import ontorama.OntoramaConfig;
import ontorama.backends.Backend;
import ontorama.backends.filemanager.Util;
import ontorama.backends.filemanager.gui.FileBackendFileFilter;
import ontorama.conf.DataFormatMapping;
import ontorama.model.graph.Graph;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.query.QueryEngine;
import ontorama.ontotools.query.QueryResult;
import ontorama.ui.ErrorDialog;
import ontorama.ui.OntoRamaApp;
import ontorama.ui.events.QueryEndEvent;

/**
 * @author nataliya
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FileImporter implements Importer {
	private EventBroker _eventBroker;
	
	private String _sourcePackageName = "ontorama.ontotools.source.FileSource";
	
	public FileImporter (EventBroker eventBroker) {
		_eventBroker = eventBroker;
	}

	/** 
	 * @see ontorama.importer.Importer#doImport()
	 */
	public void doImport() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(new FileBackendFileFilter(OntoramaConfig.getDataFormatsMapping()));
		
		int returnValue = fileChooser.showOpenDialog(OntoRamaApp.getMainFrame());
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			DataFormatMapping mapping = Util.getMappingForFile(OntoramaConfig.getDataFormatsMapping(),file);
			System.out.println("FileImporter::loadFile, mapping = " + mapping);
				
			if ((mapping == null) || (mapping.getParserName() == null)) {
				/// @todo need to throw a 'parser not specified exception' here?
				ErrorDialog.showError(OntoRamaApp.getMainFrame(), "Error", "There is no parser specified for this file type ");
				return;
			}
	
			String parserPackageName = mapping.getParserName();		
			System.out.println("FileImporter::parserName = " + parserPackageName);
//			GeneralQueryEvent queryEvent = new GeneralQueryEvent(new Query());
//			_eventBroker.processEvent(queryEvent);
			getResult(parserPackageName, file);
		}
	}

	/**
	 * @see ontorama.importer.Importer#getName()
	 */
	public String getName() {
		String str = "File";
		return str;
	}
	
	private void getResult(String parserPackageName, File file) {
		try {
			QueryEngine qe = new QueryEngine( _sourcePackageName, parserPackageName,file.getAbsolutePath());
			QueryResult qr = qe.getQueryResult(new Query());
			Backend backend = OntoramaConfig.getBackend();
			Graph newGraph = backend.createGraph(qr, _eventBroker);
			_eventBroker.processEvent(new QueryEndEvent(newGraph));
		}
		catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError(OntoRamaApp.getMainFrame(), e, "Error Importing from a file", e.getMessage());
		}
	}

}
