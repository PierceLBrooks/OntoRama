package ontorama.backends;

/**
 * @author nataliya
 */
public interface Importer {
	
	/**
	 * name of the importer (appears as a label on the importer menu).
	 * @return String
	 */
	public String getName();
	
	/**
	 * this method will open necessary dialog
	 */
	public void doImport();
	
	

}
