package ontorama.importer;

import java.io.File;
import javax.swing.JFileChooser;

import org.tockit.events.EventBroker;

import ontorama.OntoramaConfig;
import ontorama.backends.Backend;
import ontorama.backends.filemanager.ParserNotSpecifiedException;
import ontorama.backends.filemanager.Util;
import ontorama.backends.filemanager.gui.FileBackendFileFilter;
import ontorama.model.graph.Graph;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.query.QueryEngine;
import ontorama.ontotools.query.QueryResult;
import ontorama.ui.ErrorDialog;
import ontorama.ui.OntoRamaApp;
import ontorama.ui.events.GraphIsLoadedEvent;

/**
 * @author nataliya
 *
 */
public class FileImporter implements Importer {
	private EventBroker _eventBroker;
	
	private String _sourcePackageName = "ontorama.ontotools.source.FileSource";
	
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
//				GeneralQueryEvent queryEvent = new GeneralQueryEvent(new Query());
//				_eventBroker.processEvent(queryEvent);
				getResult(parserPackageName, file);
			}
			catch (ParserNotSpecifiedException e) {
				e.printStackTrace();
				ErrorDialog.showError(OntoRamaApp.getMainFrame(), "Error reading file", e.getMessage());
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
	
	private void getResult(String parserPackageName, File file) {
		try {
			QueryEngine qe = new QueryEngine( _sourcePackageName, parserPackageName,file.getAbsolutePath());
			QueryResult qr = qe.getQueryResult(new Query());
			Backend backend = OntoramaConfig.getBackend();
			System.out.println("FileBackend::getResult, backend = " + backend);
			Graph newGraph = backend.createGraph(qr, _eventBroker);
			_eventBroker.processEvent(new GraphIsLoadedEvent(newGraph));
		}
		catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError(OntoRamaApp.getMainFrame(), e, "Error Importing from a file", e.getMessage());
		}
	}

}
