package ontorama.backends;

import java.util.List;

import javax.swing.JMenu;

/**
 * Backend interface
 * @author nataliya
 */
/* Methods we want:
Node createNode(...) // instead of constructor
Edge createEdge(...) // instead of constructor
Collection getExportFormats() // returns (writer,format name, extension..)
Parser getParser() // ???
Graph search(Query)
List getPanels()
JMenu getMenu()
		 */
public interface Backend {
	
	public List getPanels();
	public JMenu getMenu();


}
