package ontorama.backends.filemanager;

import java.io.File;
import javax.swing.JFileChooser;

import org.tockit.events.EventBroker;

import ontorama.OntoramaConfig;
import ontorama.backends.Importer;
import ontorama.backends.filemanager.gui.FileBackendFileFilter;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.query.QueryEngine;
import ontorama.ui.ErrorDialog;
import ontorama.ui.OntoRamaApp;
import ontorama.ui.events.QueryEngineThreadStartEvent;

/**
 * @author nataliya
 *
 */
public class FileImporter implements Importer {
	private EventBroker _eventBroker;
	
	public FileImporter (EventBroker eventBroker) {
		_eventBroker = eventBroker;
		System.out.println("FileImporter event broker = " + _eventBroker);
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

			String parserPackageName;
			try {
				parserPackageName = Util.getParserForFile(OntoramaConfig.getDataFormatsMapping(),file);
				System.out.println("\nFileImporter::parserName = " + parserPackageName + "\n");
				QueryEngine qe = new QueryEngine( FileBackend.sourcePackageName , parserPackageName,file.getAbsolutePath());
				QueryEngineThreadStartEvent event = new QueryEngineThreadStartEvent(qe, new Query());
				_eventBroker.processEvent(event);
			}
			catch (ParserNotSpecifiedException e) {
				e.printStackTrace();
				ErrorDialog.showError(OntoRamaApp.getMainFrame(), "Error reading file", e.getMessage());
			}	
			catch (Exception e) {
				e.printStackTrace();
				ErrorDialog.showError(OntoRamaApp.getMainFrame(), e, "Error Importing from a file", e.getMessage());
			}
		}
	}

	/**
	 * @see ontorama.importer.Importer#getName()
	 */
	public String getName() {
		String str = "File";
		return str;
	}
	
}
